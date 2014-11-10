package ethanjones.cubes.core.platform.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.Launcher;

public class AndroidLauncher extends AndroidApplication implements Launcher {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new AndroidCompatibility(this).startCubes();
  }

  @Override
  public void onBackPressed() {
    ((AndroidCompatibility) Compatibility.get()).back = true;
  }

}
