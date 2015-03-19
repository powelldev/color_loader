package michaelpowell.takehome.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.EditText;

import michaelpowell.takehome.model.Command;
import michaelpowell.takehome.model.DisplayColor;
import michaelpowell.takehome.MonitorThread;
import michaelpowell.takehome.R;


public class MainActivity extends ActionBarActivity implements CommandFragment.CommandSelectedListener{

  private static final String LOG_TAG = "MainActivity";
  private DisplayColor displayColor = new DisplayColor();

  /** Thread responsible for obtaining messages from the server */
  private MonitorThread mMonitorThread;

  /** Fragment responsible for presenting a list of commands and allowing selection */
  private CommandFragment mCommandFragment;

  /** Fragment responsible for presenting the current color */
  private ColorFragment mColorFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCommandFragment = new CommandFragment();
    mColorFragment = new ColorFragment();
    getSupportFragmentManager().beginTransaction().add(R.id.container_left, mCommandFragment).commit();
    getSupportFragmentManager().beginTransaction().add(R.id.container_right, mColorFragment).commit();
  }


  @Override
  protected void onResume() {
    super.onResume();
    final EditText prompt = new EditText(this);
    new AlertDialog.Builder(this)
        .setTitle("Server Address")
        .setView(prompt)
        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          String server = prompt.getEditableText().toString();
          if (!TextUtils.isEmpty(server)) {
            startMonitorThread(server);
          }
        }
       }).show();
  }

  private void startMonitorThread(String serverAddress) {
    mMonitorThread = new MonitorThread(mHandler, serverAddress);
    mMonitorThread.start();
  }


  @Override
  protected void onPause() {
    super.onPause();
    if (mMonitorThread != null) {
      mMonitorThread.stopMonitoring();
    }

  }

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
      displayColor.setCurrentAbsolute(0xFF7F7F7F);
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
    mColorFragment.setColor(color.getCurrentColor());
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

