package ethanjones.cubes.core.platform.android;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.Launcher;

import android.os.Bundle;
import android.view.KeyEvent;
import com.badlogic.gdx.backends.android.AndroidApplication;

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

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    int action = event.getAction();
    int keyCode = event.getKeyCode();
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      if (action == KeyEvent.ACTION_DOWN) {
        ((AndroidCompatibility) Compatibility.get()).modifier = true;
      } else if (action == KeyEvent.ACTION_UP) {
        ((AndroidCompatibility) Compatibility.get()).modifier = false;
      }
    }
    return super.dispatchKeyEvent(event);
  }
}
