package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.other.DataParser;

import java.util.HashMap;
import java.util.Map;

public class TexturePacker {
  Pixmap pixmap;
  int padding;
  Node root;
  Map<String, PackRectangle> rectangles;

  public TexturePacker(int width, int height, int padding) {
    this.pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
    this.padding = padding;
    this.root = new Node(0, 0, width, height, null, null, null);
    this.rectangles = new HashMap<String, PackRectangle>();
  }

  public boolean insertImage(String name, Pixmap image) {
    if (rectangles.containsKey(name)) throw new RuntimeException("Key with name '" + name + "' is already in map");

    int borderPixels = padding;
    borderPixels <<= 1;
    PackRectangle rect = new PackRectangle(0, 0, image.getWidth() + borderPixels, image.getHeight() + borderPixels);
    Node node = insert(root, rect);

    if (node == null) return false;

    node.leaveName = name;
    rect = new PackRectangle(node.rect);
    rect.width -= borderPixels;
    rect.height -= borderPixels;
    borderPixels >>= 1;
    rect.x += borderPixels;
    rect.y += borderPixels;
    rectangles.put(name, rect);
    pixmap.drawPixmap(image, rect.x, rect.y);

    return true;
  }

  private Node insert(Node node, PackRectangle rect) {
    if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
      Node newNode = insert(node.leftChild, rect);
      if (newNode == null) newNode = insert(node.rightChild, rect);

      return newNode;
    } else {
      if (node.leaveName != null) return null;

      if (node.rect.width == rect.width && node.rect.height == rect.height) return node;

      if (node.rect.width < rect.width || node.rect.height < rect.height) return null;

      node.leftChild = new Node();
      node.rightChild = new Node();

      int deltaWidth = node.rect.width - rect.width;
      int deltaHeight = node.rect.height - rect.height;

      if (deltaWidth > deltaHeight) {
        node.leftChild.rect.x = node.rect.x;
        node.leftChild.rect.y = node.rect.y;
        node.leftChild.rect.width = rect.width;
        node.leftChild.rect.height = node.rect.height;

        node.rightChild.rect.x = node.rect.x + rect.width;
        node.rightChild.rect.y = node.rect.y;
        node.rightChild.rect.width = node.rect.width - rect.width;
        node.rightChild.rect.height = node.rect.height;
      } else {
        node.leftChild.rect.x = node.rect.x;
        node.leftChild.rect.y = node.rect.y;
        node.leftChild.rect.width = node.rect.width;
        node.leftChild.rect.height = rect.height;

        node.rightChild.rect.x = node.rect.x;
        node.rightChild.rect.y = node.rect.y + rect.height;
        node.rightChild.rect.width = node.rect.width;
        node.rightChild.rect.height = node.rect.height - rect.height;
      }

      return insert(node.leftChild, rect);
    }
  }

  public Pixmap getPixmap() {
    return pixmap;
  }

  public Map<String, PackRectangle> getRectangles() {
    return rectangles;
  }

  public static final class PackRectangle implements DataParser<DataGroup> {

    int x;
    int y;
    int width;
    int height;

    public PackRectangle(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    public PackRectangle() {
      this(0, 0, 0, 0);
    }

    public PackRectangle(PackRectangle packRectangle) {
      this(packRectangle.x, packRectangle.y, packRectangle.width, packRectangle.height);
    }

    @Override
    public DataGroup write() {
      DataGroup dataGroup = new DataGroup();
      dataGroup.setInteger("x", x);
      dataGroup.setInteger("y", y);
      dataGroup.setInteger("width", width);
      dataGroup.setInteger("height", height);
      return dataGroup;
    }

    @Override
    public void read(DataGroup bytebase) {
      this.x = bytebase.getInteger("x");
      this.y = bytebase.getInteger("y");
      this.width = bytebase.getInteger("width");
      this.height = bytebase.getInteger("height");
    }
  }

  static final class Node {
    public Node leftChild;
    public Node rightChild;
    public PackRectangle rect;
    public String leaveName;

    public Node(int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName) {
      this.rect = new PackRectangle(x, y, width, height);
      this.leftChild = leftChild;
      this.rightChild = rightChild;
      this.leaveName = leaveName;
    }

    public Node() {
      rect = new PackRectangle();
    }
  }
}
