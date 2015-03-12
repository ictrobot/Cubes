package ethanjones.cubes.core.logging.loggers;

public class SysOutLogWriter extends TextLogWriter {

  @Override
  public void dispose() {

  }

  @Override
  protected void println(String string) {
    System.out.println(string);
  }
}
