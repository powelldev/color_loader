# color_loader
Android coding challenge for Pebble

Given a python server script that connects to a port and spits out a command each second. 

Two types of commands
a) three 8 bit unsigned bytes
b) three 16 bit signed bytes

Interpret (a) as an RGB value command.
Interpret (b) as 3 signed integers representing an offset to apply to RGB values

Any number of (b) may be selected. Only one (a) may be active at a time

If (a) is selected, display that color.
For each (b) selected, apply that offset to the current color.

New commands are automatically selected.
Allow the user to manually select commands.
