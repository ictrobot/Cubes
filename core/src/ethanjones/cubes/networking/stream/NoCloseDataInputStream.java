package ethanjones.cubes.networking.stream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCloseDataInputStream extends DataInputStream {

  public NoCloseDataInputStream(InputStream in) {
    super(in);
  }

  @Override
  public void close() throws IOException {
    // no close
  }
}
