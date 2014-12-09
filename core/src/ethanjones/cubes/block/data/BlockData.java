package ethanjones.cubes.block.data;

public class BlockData {

  private final BlockAttributes blockAttributes;
  public int[] data;

  public BlockData(BlockAttributes blockAttributes) {
    this.blockAttributes = blockAttributes;
    this.data = new int[blockAttributes.attributes.size()];
    for (int i = 0; i < data.length; i++) {
      data[i] = blockAttributes.attributes.get(i).getDefault();
    }
  }

  public <T> void setAttribute(Attribute<T> attribute, T object) {
    data[blockAttributes.attributes.indexOf(attribute)] = attribute.getAttribute(object);
  }

  public void setAttribute(int attributeNum, Object object) {
    data[attributeNum] = blockAttributes.attributes.get(attributeNum).getAttribute(object);
  }

  public void setAttribute(int attributeNum, int num) {
    data[attributeNum] = num;
  }

  public <T> T getAttribute(Attribute<T> attribute) {
    return attribute.getAttribute(data[blockAttributes.attributes.indexOf(attribute)]);
  }

  public Object getAttribute(int attributeNum) {
    return blockAttributes.attributes.get(attributeNum).getAttribute(data[attributeNum]);
  }

  public String toString() {
    String str = "[";
    for (int i = 0; i < data.length; i++) {
      Attribute attribute = blockAttributes.attributes.get(i);
      str += attribute.getName() + "=" + attribute.getAttribute(data[i]).toString();
      if (i != data.length - 1) str += ", ";
    }
    return str + "]";
  }
}
