package michaelpowell.takehome;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

public class MonitorThread extends Thread {

  private static final String LOG_TAG = "MonitorThread";

  // Handler msg.what flags
  public static final int MSG_CMD_ABSOLUTE = 0x02;
  public static final int MSG_CMD_RELATIVE = 0x01;

  // Command structure
  public static final int CMD_ABSOLUTE = 0x02;
  public static final int CMD_RELATIVE = 0x01;

  // Bundle flag for byte array
  private static final String EXTRA_BYTE_ARRAY = "extra_byte_array";

  private Handler mainThreadHandler;

  public MonitorThread(Handler mainThreadHandler) {
    this.mainThreadHandler = mainThreadHandler;
  }

  @Override
  public void run() {

    byte[] absoluteBuffer = new byte[3];
    byte[] relativeBuffer = new byte[6];
    int colorInt;
    Message msg;
    try {
      BufferedInputStream bufferedInputStream = connectToServer();

      while (true) {
        switch(bufferedInputStream.read()) {
          case CMD_ABSOLUTE:

            bufferedInputStream.read(absoluteBuffer, 0, 3);
            colorInt = (0xFF << 24) | (absoluteBuffer[0] << 16) | (absoluteBuffer[1] << 8) | absoluteBuffer[2];
            msg = mainThreadHandler.obtainMessage(MSG_CMD_ABSOLUTE, colorInt, -1);
            mainThreadHandler.sendMessage(msg);

            /*
            Log.i(LOG_TAG, "abs[0]:" + String.format("%02X", absoluteBuffer[0]));
            Log.i(LOG_TAG, "abs[1]:" + String.format("%02X", absoluteBuffer[1]));
            Log.i(LOG_TAG, "abs[2]:" + String.format("%02X", absoluteBuffer[2]));
            */

            break;
          case CMD_RELATIVE:
            bufferedInputStream.read(relativeBuffer, 0, 6);

            int dr = (relativeBuffer[0] << 8) | relativeBuffer[1];
            int dg = (relativeBuffer[2] << 8) | relativeBuffer[3];
            int db = (relativeBuffer[4] << 8) | relativeBuffer[5];

            int[] offsets = new int[] {dr, dg, db};

            msg = mainThreadHandler.obtainMessage(MSG_CMD_RELATIVE, offsets);
            mainThreadHandler.sendMessage(msg);

            /*
            Log.i(LOG_TAG, "rel[0|1]:" + String.format("%02X", (relativeBuffer[0] << 8) | relativeBuffer[1]));
            Log.i(LOG_TAG, "rel[2|3]:" + String.format("%02X", (relativeBuffer[2] << 8) | relativeBuffer[3]));
            Log.i(LOG_TAG, "rel[4|5]:" + String.format("%02X", (relativeBuffer[4] << 8) | relativeBuffer[5]));
            */
            break;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Return an integer representing a color.
   * @param buffer a byte array containing red, green and blue components of a color
   */
  private int convertToColorInt(byte[] buffer) {
    return (0xFF << 24) | (buffer[0] << 16) | (buffer[1] << 8) | buffer[2];
  }

  private int convertToOffsetInt(byte[] buffer) {
        return ((0xFF << 24) |
            (buffer[0] << 20) |
            (buffer[1] << 16) |
            (buffer[2] << 12) |
            (buffer[3] << 8) |
            (buffer[4] << 4) |
            (buffer[5])
        );

  }

  private BufferedInputStream connectToServer() throws IOException {
    Socket socket = new Socket("10.0.0.18", 1234);
    return new BufferedInputStream(socket.getInputStream());
  }

}

