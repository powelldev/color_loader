package michaelpowell.takehome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity implements CommandFragment.CommandSelectedListener{

  private static final String LOG_TAG = "MainActivity";
  private DisplayColor displayColor = new DisplayColor();

  // TODO: allow user to enter IP address of server!
  // TODO: something is wrong with the red calculation

  Handler mHandler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
      Command command;
      switch (msg.what) {
        case MonitorThread.MSG_CMD_ABSOLUTE:
          command = new Command(msg.arg1);
          performCommand(command);
          break;
        case MonitorThread.MSG_CMD_RELATIVE:
          command = new Command((int[]) msg.obj);
          performCommand(command);
          break;
        default:
          throw new UnsupportedOperationException("Unknown msg.what: " + msg.what);
      }
      return false;
    }
  });

  // TODO abstract away the image display
  private void performCommand(Command command) {
    mCommandFragment.addCommand(command);
    switch (command.type) {
      case ABSOLUTE:
        onAbsoluteCommand(command.color, true);
        break;
      case RELATIVE:
        onRelativeCommand(command.colorOffset, true);
        break;
    }
  }

  private void removeCommand(Command command) {
    switch (command.type) {
      case ABSOLUTE:
        onAbsoluteCommand(command.color, false);
        break;
      case RELATIVE:
        onRelativeCommand(command.colorOffset, false);
        break;
    }
  }


  private void onAbsoluteCommand(int colorInt, boolean set) {
    displayColor.removeAllRelative(colorInt);
    if (set) {
      displayColor.setCurrentAbsolute(colorInt);
    } else {
      displayColor.setCurrentAbsolute(0x7F7F7F);
    }
    updateView(displayColor);
  }

  private void onRelativeCommand(int[] args, boolean set) {
    DisplayColor.ColorOffset offset = new DisplayColor.ColorOffset(args);
    if (set) {
      displayColor.addRelativeOffset(offset);
    } else {
      displayColor.removeRelativeOffset(offset);
    }
    updateView(displayColor);
  }

  private void updateView(DisplayColor color) {
    mColorImage.setBackgroundColor(color.getCurrentColor());
  }

  private MonitorThread mMonitorThread;
  private CommandFragment mCommandFragment;
  private ImageView mColorImage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCommandFragment = new CommandFragment();
    mColorImage = (ImageView) findViewById(R.id.color_image);
    getSupportFragmentManager().beginTransaction().add(R.id.container_left, mCommandFragment).commit();
  }


  @Override
  protected void onResume() {
    super.onResume();
    mMonitorThread = new MonitorThread(mHandler, "10.0.0.18");
    mMonitorThread.start();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mMonitorThread.stopMonitoring();

  }

  @Override
  public void onCommandChecked(Command command, boolean checked) {
    if (checked) {
      performCommand(command);
    } else {
      removeCommand(command);
    }
  }
}

