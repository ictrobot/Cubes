package ethanjones.cubes.item;

import ethanjones.cubes.core.id.IDManager;

public class Items {

  public static ItemTool pickaxe;
  public static ItemTool axe;
  public static ItemTool shovel;

  public static void getInstances() {
    pickaxe = (ItemTool) IDManager.toItem("core:pickaxe");
    axe = (ItemTool) IDManager.toItem("core:axe");
    shovel = (ItemTool) IDManager.toItem("core:shovel");
  }
}
