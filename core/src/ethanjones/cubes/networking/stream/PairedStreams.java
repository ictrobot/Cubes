package ethanjones.cubes.networking.stream;

import java.io.*;

public class PairedStreams {

  public final DirectByteArrayOutputStream output;
  public final PairedInput input;

  public final DataOutputStream dataOutput;
  public final DataInputStream dataInput;

  public PairedStreams() {
    this.output = new DirectByteArrayOutputStream();
    this.input = new PairedInput(output);

    this.dataOutput = new NoCloseDataOutputStream(output);
    this.dataInput = new NoCloseDataInputStream(input);
  }

  public void reset() {
    output.reset();
  }

  public void updateInput() {
    input.update();
  }

  private static class PairedInput extends ByteArrayInputStream {
    private final DirectByteArrayOutputStream os;

    public PairedInput(DirectByteArrayOutputStream os) {
      super(os.buffer(), 0, os.count());
      this.os = os;
    }

    private void update() {
      pos = 0;
      mark = 0;
      buf = os.buffer();
      count = os.count();
    }
  }
}
