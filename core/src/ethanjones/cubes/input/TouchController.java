package ethanjones.cubes.input;

import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketClick;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

public class TouchController extends InputAdapter {
  
  public static final long TAP_TIME = 200L;
  
  private boolean down = false;
  private int pointer;
  private long timestamp;
  private boolean mine = false;
  
  public boolean touchDown (int screenX, int screenY, int pointer, int button) {
    if (!Cubes.getClient().player.isNoClipInBlock() && !down) {
      this.pointer = pointer;
      this.timestamp = System.currentTimeMillis();
      down = true;
      return true;
    }
    return false;
  }
  
  public boolean touchUp (int screenX, int screenY, int pointer, int button) {
    if (down && this.pointer == pointer) {
      this.down = false;
      this.mine = false;
      long diff = System.currentTimeMillis() - timestamp;
      if (diff < TAP_TIME) {
        NetworkingManager.sendPacketToServer(new PacketClick(ClickType.place));
        DesktopController.handleClick(ClickType.place);
      } else {
        NetworkingManager.sendPacketToServer(new PacketClick(ClickType.none));
      }
      return true;
    }
    return false;
  }
  
  public void update() {
    if (down) {
      long diff = System.currentTimeMillis() - timestamp;
      if (diff >= TAP_TIME) {
        if (!mine) {
          NetworkingManager.sendPacketToServer(new PacketClick(ClickType.mine));
          DesktopController.handleClick(ClickType.mine);
          mine = true;
        }
      }
    }
    
    if (mine) {
      float dX = (float) Gdx.input.getDeltaX() / (float) Gdx.graphics.getWidth() / Gdx.graphics.getDeltaTime();
      float dY = (float) Gdx.input.getDeltaY() / (float) Gdx.graphics.getHeight() / Gdx.graphics.getDeltaTime();
      float delta = dX * dX + dY * dY;
      mine = delta <= 2f;
    }
  }
  
  public void tick() {
    ItemTool.mine(Cubes.getClient().player, mine);
  }
}
