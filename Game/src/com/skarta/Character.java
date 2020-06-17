package com.skarta;

import java.awt.image.BufferedImage;

public abstract class Character implements Animated
{
    BufferedImage[][] spritesheet;
    int speed;// horizontal speed of character
    int numberOfCycles;

    int foregroundColumn;
    int foregroundRow;
    int displayXcor;
    int displayYcor;

    abstract void updateHitBoxCoordinates();
    abstract void move();
    abstract void collide(int[][] foreground, int cameraXcor);
}