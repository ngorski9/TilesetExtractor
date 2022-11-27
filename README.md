# TilesetExtractor

A program that allows you to input a screenshot from a game made with a tilemap. It will extract a tileset from that image.

For games where there is a tilemap laid over a background image, you can also upload a background image an it will ignore all of the colors in the background image when extracting the tileset. However, this can cause problems when the foreground and background share colors, so beware of this.

Note that this will not remove the HUD or any NPCs/objects etc. from the screenshot that you upload. The program has no way of discerning what is an object and what is a tile based on graphics alone.

I've included the source code, as well as a compiled jar file.

### Dependencies

This program uses [Eclipse SWT](https://www.eclipse.org/swt/). When programming the application, I added SWT to eclipse from an Archive file and used windowbuilder, as instructed [here](https://www.eclipse.org/swt/eclipse.php), although I believe that there is a way to reference eclipse from Maven, if that is what you prefer.

Because I am providing a compiled jar file, I am redistributing the binaries for SWT, which is distributed under the [Eclipse Public License](https://www.eclipse.org/legal/epl-2.0/)
