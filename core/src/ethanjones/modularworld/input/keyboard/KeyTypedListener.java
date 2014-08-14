package ethanjones.modularworld.input.keyboard;

public interface KeyTypedListener {

  public void keyDown(int keycode);

  public void keyUp(int keycode);

  public void keyTyped(char character);

}
