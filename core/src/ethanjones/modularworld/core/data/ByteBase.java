package ethanjones.modularworld.core.data;

import java.io.*;

public abstract class ByteBase {

  protected abstract void writeData(DataOutput output) throws IOException;

  protected abstract void readData(DataInput input) throws IOException;

  protected abstract ByteBase clone(ByteMode mode);

  public abstract byte getID();

  public final ByteMode mode;

  public ByteBase(ByteMode mode) {
    this.mode = mode;
  }

  public static ByteBase read(DataInput input) throws IOException {
    byte b = input.readByte();
    byte type = (byte) (b % ByteMode.MODE_BASE);
    ByteMode mode = ByteMode.read(b, input);
    ByteBase bb = getTag(type, mode);
    bb.readData(input);
    return bb;
  }

  public static void write(ByteBase bb, DataOutput output) throws IOException {
    ByteMode.write(bb, output);
    bb.writeData(output);
  }

  // Byte Array

  public static ByteBase decompress(byte[] bytes) throws IOException {
    return decompress(new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes))), true);
  }


  public static ByteBase decompress(DataInputStream input, boolean close) throws IOException {
    ByteBase bb;

    try {
      bb = ByteBase.read(input);
    } finally {
      if (close) input.close();
    }

    return bb;
  }

  public static byte[] compress(ByteBase bb) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compress(bb, new DataOutputStream(baos), true);
    return baos.toByteArray();
  }

  public static void compress(ByteBase bb, DataOutputStream output, boolean close) throws IOException {
    try {
      ByteBase.write(bb, output);
    } finally {
      if (close) output.close();
    }
  }

  // File
  public static void write(ByteBase bb, File file) throws IOException {
    DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

    try {
      ByteBase.write(bb, output);
      ;
    } finally {
      output.close();
    }
  }

  public static ByteBase read(File file) throws IOException {
    DataInputStream input = new DataInputStream(new FileInputStream(file));
    ByteBase bb;

    try {
      bb = ByteBase.read(input);
    } finally {
      input.close();
    }

    return bb;
  }

  public static ByteBase getTag(byte type, ByteMode mode) {
    switch (type % ByteMode.MODE_BASE) {
      case 0:
        return new ByteData(mode);
      case 1:
        return new ByteByte(mode);
      case 2:
        return new ByteShort(mode);
      case 3:
        return new ByteInteger(mode);
      case 4:
        return new ByteLong(mode);
      case 5:
        return new ByteFloat(mode);
      case 6:
        return new ByteDouble(mode);
      case 8:
        return new ByteBoolean(mode);
      case 7:
        return new ByteString(mode);
      case 10:
        return new ByteList(mode);
      case 14:
        return new ByteMap(mode);
    }
    // case 15:
    return new ByteEnd(mode);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ByteBase) {
      ByteBase bb = (ByteBase) o;
      if (this.getID() == bb.getID() && mode == bb.mode) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final ByteBase clone() {
    return clone(mode);
  }
}
