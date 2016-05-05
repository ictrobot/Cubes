package ethanjones.cubes.item;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

  public String id;

  protected TextureRegion texture;

  public Item(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id.toLowerCase();
  }

  public void loadGraphics() {
    this.texture = Assets.getPackedTextureFromID(id, "item");
  }

  public TextureRegion getTextureRegion() {
    return texture;
  }

  public String getName() {
    return Localization.get("item." + id.replaceFirst(":", "."));
  }

  public void onButtonPress(int button, ItemStack itemStack, Player player, int stack) {
  }

  public int getStackCountMax() {
    return 64;
  }
}
