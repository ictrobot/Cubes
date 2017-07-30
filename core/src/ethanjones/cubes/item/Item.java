package ethanjones.cubes.item;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.input.ClickType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

  public String id;
  public int intID;

  protected TextureRegion texture;

  public Item(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id.toLowerCase();
  }

  public void loadGraphics() {
    this.texture = Assets.getPackedTextureFromID(id, "item");
  }

  public TextureRegion getTextureRegion(int meta) {
    return texture;
  }

  public String getName(int meta) {
    String s = "item." + id.replaceFirst(":", ".");
    return Localization.getFirstAvailable(s + "." + meta, s);
  }

  public boolean onButtonPress(ClickType type, ItemStack itemStack, Player player, int stack) {
    return false;
  }

  public int getStackCountMax() {
    return 64;
  }

  @Override
  public String toString() {
    return id;
  }
}
