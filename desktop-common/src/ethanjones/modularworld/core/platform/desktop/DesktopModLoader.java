package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.mod.ModLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopModLoader implements ModLoader {

  ExternalJarLoader externalJarLoader = new ExternalJarLoader();

  public class ExternalJarLoader extends URLClassLoader {

    public ExternalJarLoader() {
      super(new URL[0]);
    }

    public void addFile(FileHandle fileHandle) throws MalformedURLException {
      URL url = fileHandle.file().toURI().toURL();
      super.addURL(url);
    }

  }

  @Override
  public boolean supports(Type type) {
    return type == Type.jar;
  }

  @Override
  public void load(FileHandle file) throws Exception {
    externalJarLoader.addFile(file);
  }

  @Override
  public Class<?> loadClass(String file, String className) throws Exception {
    return externalJarLoader.loadClass(className);
  }
}
