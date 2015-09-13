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
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    //if (level == TRIM_MEMORY_RUNNING_MODERATE || level == TRIM_MEMORY_RUNNING_LOW) {
    //  Debug.lowMemory();
    //} else if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
    //  Debug.criticalMemory();
    //}
  }

  @Override
  public void onBackPressed() {
    ((AndroidCompatibility) Compatibility.get()).back = true;
  }
}
