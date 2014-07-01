package ethanjones.modularworld.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TexturePacker {
  static final class Node {
    public Node leftChild;
    public Node rightChild;
    public Rectangle rect;
    public String leaveName;

    public Node(int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName) {
      this.rect = new Rectangle(x, y, width, height);
      this.leftChild = leftChild;
      this.rightChild = rightChild;
      this.leaveName = leaveName;
    }

    public Node() {
      rect = new Rectangle();
    }
  }

  BufferedImage image;
  int padding;
  Node root;
  Map<String, Rectangle> rectangles;

  public TexturePacker(int width, int height, int padding) {
    this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    this.padding = padding;
    this.root = new Node(0, 0, width, height, null, null, null);
    this.rectangles = new HashMap<String, Rectangle>();
  }

  public boolean insertImage(String name, BufferedImage image) {
    if (rectangles.containsKey(name)) throw new RuntimeException("Key with name '" + name + "' is already in map");

    int borderPixels = padding;
    borderPixels <<= 1;
    Rectangle rect = new Rectangle(0, 0, image.getWidth() + borderPixels, image.getHeight() + borderPixels);
    Node node = insert(root, rect);

    if (node == null) return false;

    node.leaveName = name;
    rect = new Rectangle(node.rect);
    rect.width -= borderPixels;
    rect.height -= borderPixels;
    borderPixels >>= 1;
    rect.x += borderPixels;
    rect.y += borderPixels;
    rectangles.put(name, rect);

    Graphics2D g = this.image.createGraphics();
    g.drawImage(image, rect.x, rect.y, null);
    g.dispose();

    return true;
  }

  private Node insert(Node node, Rectangle rect) {
    if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
      Node newNode = null;

      newNode = insert(node.leftChild, rect);
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

  public BufferedImage getImage() {
    return image;
  }

  public Map<String, Rectangle> getRectangles() {
    return rectangles;
  }
}
