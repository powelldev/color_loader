package michaelpowell.takehome;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

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

    try {
      bufferedInputStream = connectToServer(mServerIpAddress);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Connection to server failed");
      e.printStackTrace();
      return;
    }

    byte[] absoluteBuffer = new byte[CMD_ABSOLUTE_SIZE];
    byte[] relativeBuffer = new byte[CMD_RELATIVE_SIZE];
    int colorInt;
    Message msg;

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
      int[] offsets = bufferToInts(relativeBuffer);
      Message msg = mMainThreadHandler.obtainMessage(MSG_CMD_RELATIVE, offsets);
      mMainThreadHandler.sendMessage(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processAbsoluteCommand(BufferedInputStream bufferedInputStream, byte[] absoluteBuffer) {
    try {
      bufferedInputStream.read(absoluteBuffer, 0, CMD_ABSOLUTE_SIZE);
      int colorInt = bufferToInt(absoluteBuffer);
      Message msg = mMainThreadHandler.obtainMessage(MSG_CMD_ABSOLUTE, colorInt, -1);
      mMainThreadHandler.sendMessage(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Converts absolute command's byte array an argb color integer
   */
  public static int bufferToInt(byte[] buffer) {
    if (null == buffer || buffer.length != CMD_ABSOLUTE_SIZE) {
      throw new IllegalArgumentException("Buffer must be of size: " + CMD_ABSOLUTE_SIZE);
    }
    return (0xFF << 24) | (buffer[0] << 16) | (buffer[1] << 8) | buffer[2];
  }

  /**
   * Converts relative command's byte array into an int array containing
   * offset values
   * @param buffer
   * @return int array where arr[0] contains red offset
   *                         arr[1] contains green offset
   *                         arr[2] contains blue offset
   */
  public static int[] bufferToInts(byte[] buffer) {
    if (null == buffer || buffer.length != CMD_RELATIVE_SIZE) {
      throw new IllegalArgumentException("Buffer must be of size: " + CMD_RELATIVE_SIZE);
    }
    int dr = (buffer[0] << 8) | buffer[1];
    int dg = (buffer[2] << 8) | buffer[3];
    int db = (buffer[4] << 8) | buffer[5];

    return new int[] {dr, dg, db};
  }

  private BufferedInputStream connectToServer(String server) throws IOException {
    Socket socket = new Socket(server, 1234);
    return new BufferedInputStream(socket.getInputStream());
  }

  public void stopMonitoring() {
    mContinueMonitoring = false;
  }

}

