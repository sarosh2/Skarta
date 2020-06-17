package com.skarta;

import java.awt.*;

public class Key
{
    static final int levelArrayRows = 20;
    static final int levelArrayColumns = 60;
    static final int screenWidth = 1920;
    static final int screenHeight = 1080;

    static final int numberOfLevels = 8;
    static final int backgroundWidth = 3100;
    static final int backgroundHeight = screenHeight;

    static final int playerMaxLife = 4;
    static final int characterWidth = 80;
    static final int characterHeight = 110;
    static final int characterFramesPerCycle = 4;
    static final int numberOfFramesRepeatedPerCycle = 4;

    static final int platformWidth = 155;
    static final int platformHeight = 54;
    static final int platformSpriteRows = 6;
    static final int platformSprteColumns = 3;

    static final int spikesHeight = 35;
    static final int spikesWidth = 20;

    static final int ceiling = 5;
    static final int floor = screenHeight - platformHeight / 2 - characterHeight;
    static final int leftWall = 5;                                                 //platformWidth / 2 was The Previous
    static final int rightWall = screenWidth - platformWidth / 2;
    static final int absoluteHorizontalLimit = (levelArrayColumns * platformWidth) - screenWidth - platformWidth / 2;

    static final int gravity = 1;
    static final long timeDelayBetweenFrames = 25; //milli seconds
    static final long timeDelayInHandler = 5; // milli seconds

    static final long timesInputDelayed = 1; // Input is delayed by 25ms each time. This is for Tayyab

    private static int displayResolutionWidth;
    private static int displayResolutionHeight;
    private static double scale;

    static int gameState = 1;
    static boolean bossFight = false;
    static void updateDisplayResolution()
    {
        double verticalScale;
        double horizontalScale;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        displayResolutionWidth = screenSize.width;
        displayResolutionHeight = screenSize.height;
        verticalScale = (double)displayResolutionWidth / screenWidth;
        horizontalScale = (double)displayResolutionHeight / screenHeight;
        scale = Math.min(horizontalScale, verticalScale);
    }
    static double getScale()
    {
        return scale;
    }

    static int getDisplayResolutionWidth()
    {
        return displayResolutionWidth;
    }
    static int getDisplayResolutionHeight()
    {
        return displayResolutionHeight;
    }


}