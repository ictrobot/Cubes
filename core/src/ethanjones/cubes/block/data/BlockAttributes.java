package ethanjones.cubes.block.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ethanjones.cubes.block.Block;

public class BlockAttributes {

  public final Block block;
  public final List<Attribute> attributes;

  public BlockAttributes(Block block, Attribute... attributes) {
    this.block = block;
    this.attributes = Collections.unmodifiableList(Arrays.asList(attributes));
  }
}
