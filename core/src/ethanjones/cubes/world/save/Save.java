package ethanjones.cubes.world.save;

import com.badlogic.gdx.files.FileHandle;

public class Save {
  public String name;
  public FileHandle fileHandle;

  @Override
  public String toString() {
    return name;
  }
}
