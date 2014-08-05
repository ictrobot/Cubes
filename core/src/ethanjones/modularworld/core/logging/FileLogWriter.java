package ethanjones.modularworld.core.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileLogWriter implements LogWriter {

  FileOutputStream fileOutputStream;
  PrintStream printStream;

  public FileLogWriter(File file) {
    try {
      file.createNewFile();
      fileOutputStream = new FileOutputStream(file);
      printStream = new PrintStream(fileOutputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void log(LogLevel level, String tag, String message) {
    writeToFile(SysOutLogWriter.getString(level, tag, message));
    printStream.flush();
  }

  @Override
  public void log(LogLevel level, String tag, String message, Throwable throwable) {
    log(level, tag, message);
    throwable.printStackTrace(printStream);
    printStream.println();
    printStream.flush();
  }

  public void writeToFile(String string) {
    printStream.println(string);
  }

  @Override
  public void dispose() {
    try {
      printStream.close();
      fileOutputStream.close();
    } catch (Exception e) {

    }
  }
}
