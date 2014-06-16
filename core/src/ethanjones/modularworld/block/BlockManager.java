package ethanjones.modularworld.block;

import ethanjones.modularworld.block.factory.BlockFactory;

import java.util.ArrayList;

public class BlockManager {

  ArrayList<BlockFactory> factories = new ArrayList<BlockFactory>();

  public void register(BlockFactory factory) {
    factories.add(factory);
  }

  public void newMappings() {
    for (int i = 1; i <= factories.size(); i++) {
      factories.get(i).numID = i;
    }
  }

  public void readMappings() {

  }
}
