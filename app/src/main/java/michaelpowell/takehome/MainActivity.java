package michaelpowell.takehome;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

  private DisplayColor displayColor = new DisplayColor();

  // TODO: allow user to enter IP address of server!
  private static final String LOG_TAG = "MainActivity";
  Handler mHandler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
      switch (msg.what) {
        case MonitorThread.MSG_CMD_ABSOLUTE:
          int colorInt = msg.arg1;
          displayColor.removeAllRelative(colorInt);
          displayColor.setCurrentAbsolute(colorInt);
          updateView(displayColor.getCurrentColor());
          //color = colorInt;
          //Log.i(LOG_TAG, "Absolute command: " + Integer.toHexString(colorInt));
          break;
        case MonitorThread.MSG_CMD_RELATIVE:
          int[] offsets = (int[]) msg.obj;
          displayColor.addRelativeOffset(offsets[0], offsets[1], offsets[2]);
          updateView(displayColor.getCurrentColor());
          /*
          Log.i(LOG_TAG, "" + offsets[0] + " " + offsets[1] + " " + offsets[2]);
          int r = Color.red(color) + offsets[0];
          int g = Color.green(color) + offsets[1];
          int b = Color.blue(color) + offsets[2];
          Log.i(LOG_TAG, "r: " + r + " g: " + g + " b: " + b);
          color = Color.rgb(r, g, b);
          */
          break;
      }
      return false;
    }
  });

  private void updateView(int color) {
    mTextView.setText(displayColor.toString());
    mTextView.setTextColor(color);
  }

  class DisplayColor {
    int currentAbsolute = 0xFF7F7F7F;
    int currentColor = 0xFF7F7F7F;

    public void setCurrentAbsolute(int abosoluteColor) {
      this.currentAbsolute = abosoluteColor;
      this.currentColor = abosoluteColor;
    }
    public void addRelativeOffset(int dr, int dg, int db) {
      int r = Color.red(currentColor) + dr;
      int g = Color.green(currentColor) + dg;
      int b = Color.blue(currentColor) + db;
      currentColor = Color.rgb(r, g, b);
    }

    public void removeRelativeOffset(int dr, int dg, int db) {
      int r = Color.red(currentColor) - dr;
      int g = Color.green(currentColor) - dg;
      int b = Color.blue(currentColor) - db;
      currentColor = Color.rgb(r, g, b);
    }

    public void removeAllRelative(int absoluteColor) {
      currentColor = absoluteColor;
    }

    public int getCurrentColor() {
      return currentColor;
    }

    @Override
    public String toString() {
      int r = Color.red(currentColor);
      int g = Color.green(currentColor);
      int b = Color.blue(currentColor);
      return "r: " + r + " g: " + g + " b: " + b;
    }
  }
  TextView mTextView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTextView = (TextView) findViewById(R.id.textview);
  }


  @Override
  protected void onResume() {
    super.onResume();
    new MonitorThread(mHandler).start();
  }

}

