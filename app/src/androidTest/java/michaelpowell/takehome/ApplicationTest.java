package michaelpowell.takehome;

import android.app.Application;
import android.graphics.Color;
import android.test.ApplicationTestCase;

import michaelpowell.takehome.utils.ColorUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
  public ApplicationTest() {
    super(Application.class);
  }

  @Override
  protected void runTest() throws Throwable {
    AbsTest();
    RelTest();
  }

  public void AbsTest() throws Exception {
    int color = ColorUtils.bufferToInt(new byte[]{0x7F, 0x7F, 0x7F}, 3);
    assertEquals(Color.red(color), 127);
    assertEquals(Color.blue(color), 127);
    assertEquals(Color.green(color), 127);

    color = ColorUtils.bufferToInt(new byte[]{0x2A, 0x2A, 0x2A}, 3);
    assertEquals(Color.red(color), 42);
    assertEquals(Color.blue(color), 42);
    assertEquals(Color.green(color), 42);
  }

  public void RelTest() throws Exception {
    int[] offsets = ColorUtils.bufferToInts(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, 6);
    assertEquals(offsets[0], 0);
    assertEquals(offsets[1], 0);
    assertEquals(offsets[2], 0);

    offsets = ColorUtils.bufferToInts(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x11, (byte) 0xff, (byte) 0xfa}, 6);
    assertEquals(offsets[0], -1);
    assertEquals(offsets[1], 3);
    assertEquals(offsets[2], -9);
  }

}