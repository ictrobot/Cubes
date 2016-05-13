package ethanjones.cubes.core.platform.android;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CrashActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crash);

    String s = getIntent().getStringExtra("ethanjones.cubes.core.platform.android.LOG_FILE");

    TextView text = (TextView) findViewById(R.id.editText);
    text.setText(getLog(s));
  }

  private static String getLog(String file) {
    StringBuilder output = new StringBuilder();
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(file));
      char[] buffer = new char[512];

      while (true) {
        int length = reader.read(buffer);
        if (length == -1) break;
        output.append(buffer, 0, length);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } finally {
      try {
        if (reader != null) reader.close();
      } catch (IOException ignored) {
      }
    }
    return output.toString();
  }

}
