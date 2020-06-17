package com.skarta;

/*
Platform SpriteSheet Layout

Floating Platform Left Edge - Floating Platform Middle Section - Floating Platform Right Edge
Floor Sloping Rightward - Floor Flat - Floor Sloping Leftward

 */

import java.awt.*;
import java.awt.image.BufferedImage;

public class Platform
{
    static BufferedImage[][] spritesheet; // SpriteSheet for all Regular Platforms
    static BufferedImage[][] elements; // SpriteSheet for all Spiky Platforms

    static final int spriteRows = Key.platformSpriteRows;
    static final int spriteColumns = Key.platformSprteColumns;
    static final int platformWidth = Key.platformWidth;
    static final int platformHeight = Key.platformHeight;

    static void render(int type, int xcor, int ycor, Graphics g)
    {
        BufferedImage image;
        if (type < 19)
        {
            image = spritesheet[(type - 1) / spriteColumns][(type - 1) % spriteColumns];
        }
        else
        {
            image = elements[(type - 19) / 2][(type - 19) % 2];
        }

        HelperFunctions.drawImage(g, image, xcor, ycor);
    }

    static void loadSpriteSheet()
    {
        spritesheet = HelperFunctions.makeSpritesheet("/platforms.png", spriteRows, spriteColumns, platformWidth, platformHeight);

        elements = HelperFunctions.makeSpritesheet("/elements.png", 2, 2, platformWidth, platformHeight);
    }
}