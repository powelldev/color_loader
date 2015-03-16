package michaelpowell.takehome;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;


public class MainActivity extends ActionBarActivity {

  private static final String LOG_TAG = "MICHAEL_DEBUG";
  Handler mHandler = new Handler();
  BufferedInputStream bufferedInputStream;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }


  @Override
  protected void onResume() {
    super.onResume();
    new Thread(monitorPortRunnable).start();
  }

  private Runnable monitorPortRunnable = new Runnable() {

    @Override
    public void run() {

      byte[] absoluteBuffer = new byte[3];
      byte[] relativeBuffer = new byte[6];
      try {
        bufferedInputStream = connectToServer();

        while (true) {
          switch(bufferedInputStream.read()) {
            case 0x02:
              bufferedInputStream.read(absoluteBuffer, 0, 3);
              Log.i(LOG_TAG, "[0]: " + (absoluteBuffer[0] & 0x00FF));
              Log.i(LOG_TAG, "[1]: " + (absoluteBuffer[1] & 0x00FF));
              Log.i(LOG_TAG, "[2]: " + (absoluteBuffer[2] & 0x00FF));
              break;
            case 0x01:
              bufferedInputStream.read(relativeBuffer, 0, 6);
              break;
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  };

  private BufferedInputStream connectToServer() throws IOException {
    Socket socket = new Socket("10.0.0.18", 1234);
    return new BufferedInputStream(socket.getInputStream());
  }

}

