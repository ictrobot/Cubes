package ethanjones.modularworld.core.logging.loggers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileLogWriter extends TextLogWriter {

  public static File file;
  FileOutputStream fileOutputStream;
  PrintStream printStream;

  public FileLogWriter(File file) {
    FileLogWriter.file = file;
    try {
      file.createNewFile();
      fileOutputStream = new FileOutputStream(file);
      printStream = new PrintStream(fileOutputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void dispose() {
    try {
      printStream.close();
      fileOutputStream.close();
    } catch (Exception e) {

    }
  }

  @Override
  protected void println(String string) {
    printStream.println(string);
  }
}
