package michaelpowell.takehome.utils;

public class ColorUtils {
  /**
   * Converts absolute command's byte array an argb color integer
   */
  public static int bufferToInt(byte[] buffer, int size) {
    if (null == buffer || buffer.length != size) {
      throw new IllegalArgumentException("Buffer must be of size: " + size);
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
  public static int[] bufferToInts(byte[] buffer, int size) {
    if (null == buffer || buffer.length != size) {
      throw new IllegalArgumentException("Buffer must be of size: " + size);
    }
    int dr = (buffer[0] << 8) | buffer[1];
    int dg = (buffer[2] << 8) | buffer[3];
    int db = (buffer[4] << 8) | buffer[5];

    return new int[] {dr, dg, db};
  }
}
