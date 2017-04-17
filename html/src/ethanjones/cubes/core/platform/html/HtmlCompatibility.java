package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.gwt.ExitException;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.Launcher;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

public class HtmlCompatibility extends Compatibility {
  ApplicationListener applicationListener;
  
  HtmlCompatibility(Launcher launcher) {
    super(launcher, ApplicationType.WebGL);
  }
  
  @Override
  protected void run(ApplicationListener applicationListener) {
    this.applicationListener = applicationListener;
  }
  
  @Override
  public int getFreeMemory() {
    return 0;
  }
  
  @Override
  public boolean functionModifier() {
    return false;
  }
  
  @Override
  public void _exit(int status) {
    throw new ExitException();
  }
  
  private final DateTimeFormat format = DateTimeFormat.getFormat("dd-MMM-yy HH:mm:ss");
  
  @Override
  public String timestamp() {
    return format.format(new Date());
  }
  
  @Override
  public String line_separator() {
    return "\n";
  }
}
