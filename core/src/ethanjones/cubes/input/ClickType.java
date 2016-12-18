package ethanjones.cubes.input;

public enum ClickType {
  // android only mine and place
  // mine is long press
  // place is tap and server sets back to none after processing
  none(-1), mine(0), place(1), desktopMiddleButton(2), desktopBackButton(3), desktopForwardButton(4);
  
  public final int num;
  
  ClickType(int i) {
    num = i;
  }
  
  public static ClickType type(int i) {
    switch (i) {
      case 0:
        return mine;
      case 1:
        return place;
      case 2:
        return desktopMiddleButton;
      case 3:
        return desktopBackButton;
      case 4:
        return desktopForwardButton;
      default:
        return none;
    }
  }
}
