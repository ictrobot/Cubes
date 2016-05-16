package ethanjones.cubes.networking.stream;

import java.io.ByteArrayOutputStream;

public class DirectByteArrayOutputStream extends ByteArrayOutputStream {

  public DirectByteArrayOutputStream() {
    this(262144); //256 kib
  }

  public DirectByteArrayOutputStream(int size) {
    super(size);
  }

  public byte[] buffer() {
    return buf;
  }

  public int count() {
    return count;
  }
}
