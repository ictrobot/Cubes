package ethanjones.modularworld.core.platform.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class AndroidLauncher extends AndroidApplication {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new AndroidCompatibility(this).startModularWorld();
  }
}
