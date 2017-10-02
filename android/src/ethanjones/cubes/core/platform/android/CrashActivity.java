package ethanjones.cubes.core.platform.android;

import ethanjones.cubes.core.system.Debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CrashActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crash);

    String s = getIntent().getStringExtra("ethanjones.cubes.core.platform.android.LOG_FILE");

    TextView text = (TextView) findViewById(R.id.editText);
    text.setText(Debug.getLogString(s));
  }

}
