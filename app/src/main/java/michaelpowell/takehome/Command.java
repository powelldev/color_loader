package michaelpowell.takehome;

import android.graphics.Color;

public class Command {

  Type type;

  int color;
  int[] colorOffset;

  Command(int absoluteColorInt) {
    type = Type.ABSOLUTE;
    color = absoluteColorInt;
  }

  Command(int[] offsets) {
    type = Type.RELATIVE;
    colorOffset = offsets;
  }
  enum Type {
    ABSOLUTE, RELATIVE
  }

  @Override
  public String toString() {
    switch(type) {
      case ABSOLUTE:
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return "r: " + r + " g: " + g + " b: " + b;
      case RELATIVE:
        return "r: " + colorOffset[0] +
               "g: " + colorOffset[1] +
               "b: " + colorOffset[2];
      default: // Shouldn't happen
        return "";
    }
  }
}
