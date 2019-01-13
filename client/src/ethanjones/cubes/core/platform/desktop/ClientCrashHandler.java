package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.system.Branding;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientCrashHandler {
  
  private static final String HEADER = "==================================================\nCUBES CRASH [" + Branding.VERSION_FULL + "]\n==================================================\n\n";
  
  public static boolean handle(String msg) {
    try {
      final Frame f = new Frame("Cubes Crash");
      f.setSize(600, 800);
      f.setLayout(null);
      final TextArea textArea = new TextArea();
      textArea.setBounds(10, 40, f.getWidth() - 20, f.getHeight() - 50);
      textArea.setText(HEADER + msg);
      textArea.setEditable(false);
      f.add(textArea);
  
      f.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          textArea.setBounds(10, 40, f.getWidth() - 20, f.getHeight() - 50);
        }
      });
  
      f.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent we) {
          f.dispose();
        }
      });
  
      f.setVisible(true);
      System.out.println("Opened crash dialog");
      return false;
    } catch (Exception e) {
      System.out.println("Failed to open crash dialog");
      e.printStackTrace();
      return true;
    }
  }
  
}
