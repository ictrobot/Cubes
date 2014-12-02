package ethanjones.cubes.block.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlockAttributes {

  public final List<Attribute> attributes;

  public BlockAttributes(Attribute... attributes) {
    this.attributes = Collections.unmodifiableList(Arrays.asList(attributes));
  }
}
