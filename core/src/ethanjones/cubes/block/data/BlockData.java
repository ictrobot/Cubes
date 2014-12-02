package ethanjones.cubes.block.data;

import java.util.Arrays;

public class BlockData {

  private static final int offset = 128;

  private final BlockAttributes blockAttributes;
  private byte[] data;

  public BlockData(BlockAttributes blockAttributes) {
    this.blockAttributes = blockAttributes;
    this.data = new byte[blockAttributes.attributes.size()];
    Arrays.fill(data, (byte) -offset);
  }

  public <T> void setAttribute(Attribute<T> attribute, T object) {
    data[blockAttributes.attributes.indexOf(attribute)] = (byte) (attribute.getValues().indexOf(object) - offset);
  }

  public void setAttribute(int attributeNum, int listIndex) {
    data[attributeNum] = (byte) (listIndex - offset);
  }

  public <T> T getAttribute(Attribute<T> attribute) {
    return attribute.getValues().get(data[blockAttributes.attributes.indexOf(attribute)] + offset);
  }

  public Object getAttribute(int attributeNum) {
    return blockAttributes.attributes.get(attributeNum).getValues().get(data[attributeNum] + offset);
  }

  public int getAttributeData(int attributeNum) {
    return data[attributeNum] + offset;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String toString() {
    String str = "[";
    for (int i = 0; i < data.length; i++) {
      Attribute attribute = blockAttributes.attributes.get(i);
      str += attribute.getName() + "=" + attribute.getValues().get(data[i] + offset).toString();
      if (i != data.length - 1) str += ", ";
    }
    return str + "]";
  }
}
