package michaelpowell.takehome;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import michaelpowell.takehome.utils.ColorUtils;

/**
 * Thread to obtain incoming data and send to a subscriber.
 */
public class MonitorThread extends Thread {

  private static final String LOG_TAG = "MonitorThread";

  private boolean mContinueMonitoring = true;

  // Handler msg.what flags
  public static final int MSG_CMD_ABSOLUTE = 0x02;
  public static final int MSG_CMD_RELATIVE = 0x01;

  // Command structure
  private static final int CMD_ABSOLUTE = 0x02;
  private static final int CMD_ABSOLUTE_SIZE = 3;
  private static final int CMD_RELATIVE = 0x01;
  private static final int CMD_RELATIVE_SIZE = 6;

  private Handler mMainThreadHandler;
  private String mServerIpAddress;

  public MonitorThread(Handler mainThreadHandler, String serverIp) {
    mMainThreadHandler = mainThreadHandler;
    mServerIpAddress = serverIp;
  }

  @Override
  public void run() {
    BufferedInputStream bufferedInputStream = null;

    byte[] absoluteBuffer = new byte[CMD_ABSOLUTE_SIZE];
    byte[] relativeBuffer = new byte[CMD_RELATIVE_SIZE];

    try {
      bufferedInputStream = connectToServer(mServerIpAddress);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Connection to server failed");
      e.printStackTrace();
      return;
    }

    try {
      while (mContinueMonitoring) {
        switch (bufferedInputStream.read()) {
          case CMD_ABSOLUTE:
            processAbsoluteCommand(bufferedInputStream, absoluteBuffer);
            break;
          case CMD_RELATIVE:
            processRelativeCommand(bufferedInputStream, relativeBuffer);
            break;
        }
      }

    } catch (IOException e) {
      Log.e(LOG_TAG, "Some issue with stream");
      e.printStackTrace();
    }

  }

  private void processRelativeCommand(BufferedInputStream bufferedInputStream, byte[] relativeBuffer) {
    try {
      bufferedInputStream.read(relativeBuffer, 0, CMD_RELATIVE_SIZE);
      int[] offsets = ColorUtils.bufferToInts(relativeBuffer, CMD_RELATIVE_SIZE);
      Message msg = mMainThreadHandler.obtainMessage(MSG_CMD_RELATIVE, offsets);
      mMainThreadHandler.sendMessage(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processAbsoluteCommand(BufferedInputStream bufferedInputStream, byte[] absoluteBuffer) {
    try {
      bufferedInputStream.read(absoluteBuffer, 0, CMD_ABSOLUTE_SIZE);
      int colorInt = ColorUtils.bufferToInt(absoluteBuffer, CMD_ABSOLUTE_SIZE);
      Message msg = mMainThreadHandler.obtainMessage(MSG_CMD_ABSOLUTE, colorInt, -1);
      mMainThreadHandler.sendMessage(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedInputStream connectToServer(String server) throws IOException {
    Socket socket = new Socket(server, 1234);
    return new BufferedInputStream(socket.getInputStream());
  }

  public void stopMonitoring() {
    mContinueMonitoring = false;
  }

}

