package ethanjones.cubes.graphics.assets;

public enum AssetType {
  block, font, hud, item, json, language, world;

  private PackedTextureSheet packed = null;

  void setPackedTextureSheet(PackedTextureSheet packed) {
    this.packed = packed;
  }

  public PackedTextureSheet getPackedTextureSheet() {
    return packed;
  }
}
