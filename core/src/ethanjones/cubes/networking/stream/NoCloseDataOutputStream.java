package ethanjones.cubes.networking.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NoCloseDataOutputStream extends DataOutputStream {

  public NoCloseDataOutputStream(OutputStream out) {
    super(out);
  }

  @Override
  public void close() throws IOException {
    // no close
  }
}
