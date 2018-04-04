package ethanjones.cubes.core.performance;

import java.io.*;
import java.text.DecimalFormat;

public class PerformanceAnalysis {
  public static final String[] padding = new String[256];
  private static DecimalFormat msFormatter = new DecimalFormat("0.0000");

  static {
    padding[0] = "";
    for (int i = 1; i < padding.length; i++) {
      padding[i] = padding[i - 1] + "  ";
    }
  }

  public static void main(String[] args) {
    if (args.length != 1) throw new IllegalArgumentException("Excepted file as argument");
    File file = new File(args[0]);
    try {
      run(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void run(File file) throws IOException {
    if (!file.exists()) throw new IllegalArgumentException("File does not exist");
    if (file.isDirectory()) throw new IllegalArgumentException("Path is a directory");

    FileInputStream fileInputStream = null;
    PrintWriter out = null;
    try {
      fileInputStream = new FileInputStream(file);
      DataInputStream data = new DataInputStream(new BufferedInputStream(fileInputStream));

      if (!(data.readUnsignedByte() == 0xEE && data.readUnsignedByte() == 0xCE)) {
        throw new IllegalArgumentException("Not a Cubes Performance file");
      }
      out = new PrintWriter(file.getAbsolutePath() + ".txt");
      while (data.readUnsignedByte() != 0xFF) {
        read(data, out, 0);
      }
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (Exception ignored) {

        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (Exception ignored) {

        }
      }
    }
  }

  private static void read(DataInputStream in, PrintWriter out, int i) throws IOException {
    String tag = in.readUTF();
    String extra = in.readUTF();
    if (!extra.isEmpty()) tag += " " + extra;

    long start = in.readLong();
    long end = in.readLong();
    long diff = end - start;
    double diffMS = ((double) diff) / 1000000d;
    out.println(padding[i] + tag + " " + msFormatter.format(diffMS) + "ms"); // (" + diff + "ns)");
    while (in.readUnsignedByte() != 0xFF) {
      read(in, out, i + 1);
    }
  }
}
