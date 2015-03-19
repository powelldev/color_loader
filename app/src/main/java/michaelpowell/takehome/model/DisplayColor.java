package michaelpowell.takehome.model;

import android.graphics.Color;

/* Calculates a color based on commands sent to it. */
public class DisplayColor {
  int currentAbsolute = 0x7F7F7F;
  int currentColor = 0x7F7F7F;

  public void addCommand(Command command) {
    switch (command.type) {
      case ABSOLUTE:
        setCurrentAbsolute(command.color);
        break;
      case RELATIVE:
        addRelativeOffset(new ColorOffset(command.colorOffset));
        break;
    }
  }

  public void setCurrentAbsolute(int abosoluteColor) {
    this.currentAbsolute = abosoluteColor;
    this.currentColor = abosoluteColor;
  }
  public void addRelativeOffset(ColorOffset offset) {
    int r = Color.red(currentColor) + offset.deltaR;
    int g = Color.green(currentColor) + offset.deltaG;
    int b = Color.blue(currentColor) + offset.deltaB;
    currentColor = Color.rgb(r, g, b);
  }

  public void removeRelativeOffset(ColorOffset offset) {
    int r = Color.red(currentColor) - offset.deltaR;
    int g = Color.green(currentColor) - offset.deltaG;
    int b = Color.blue(currentColor) - offset.deltaB;
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

  public static class ColorOffset {
    public ColorOffset(int[] offsets) {
      deltaR = offsets[0];
      deltaG = offsets[1];
      deltaB = offsets[2];
    }
    public int deltaR;
    public int deltaG;
    public int deltaB;
  }

}
