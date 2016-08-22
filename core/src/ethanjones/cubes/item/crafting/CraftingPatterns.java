package ethanjones.cubes.item.crafting;

public class CraftingPatterns {

  public static final boolean[][][] patterns = new boolean[][][]{
          null,
          {
                  {true, false, false, false, false, false, false, false, false},
                  {false, true, false, false, false, false, false, false, false},
                  {false, false, true, false, false, false, false, false, false},
                  {false, false, false, true, false, false, false, false, false},
                  {false, false, false, false, true, false, false, false, false},
                  {false, false, false, false, false, true, false, false, false},
                  {false, false, false, false, false, false, true, false, false},
                  {false, false, false, false, false, false, false, true, false},
                  {false, false, false, false, false, false, false, false, true},
          },
          null,
          null,
          {
                  {true, true, false, true, true, false, false, false, false},
                  {false, true, true, false, true, true, false, false, false},
                  {false, false, false, true, true, false, true, true, false},
                  {false, false, false, false, true, true, false, true, true},
          },
          null,
          null,
          null,
          null,
          {
                  {true, true, true, true, true, true, true, true, true},
          },
  };

}
