package com.skarta;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sarosh extends Player
{
    BufferedImage[][] shield;
    static boolean shieldBreak = false;
    static boolean doubleJumpLock = false;
    static boolean bounce = false;

    static final String path = "/sarosh.png";
    static final String shieldPath = "/shield.png";
    static final String shieldSoundFile = "shield.wav";

    static final int shieldWidth = 120;
    static final int shieldHeight = 120;
    static final int shieldRows = 2;
    static final int shieldColumns = 3;

    static final int horizontalSpeed = 10;
    static final int jump = 20;
    static final int cycleCount = 7;

    static final int hitBoxChange = 10;

    static final int shieldBounce = 90;

    static int shieldLife;
    static boolean shieldImmune;
    static long shieldDamageStart;

    Sarosh()
    {
        speed = horizontalSpeed;
        jumpPower = jump;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        shield = HelperFunctions.makeSpritesheet(shieldPath, shieldRows, shieldColumns, shieldWidth, shieldHeight);
        hitBoxWidth = 34;
        hitBoxHeight = 98;
        shieldLife = 3;
    }

    @Override
    void specialRender(Graphics g)
    {
        if (specialAbility && HUD.powerFrames[1] >= HUD.maxPowerFrame / 3 && !HUD.isRefilling())
        {
            HelperFunctions.drawImage(g, shield[0][specialFrameCount / 2], displayXcor - 20, displayYcor - 5);
        }
        else if (shieldBreak)
        {
            HelperFunctions.drawImage(g, shield[1][specialFrameCount / 2], displayXcor - 20, displayYcor - 5);
        }
    }

    @Override
    void specialTick()
    {
        if(shieldImmune)
        {
            if(System.currentTimeMillis() - shieldDamageStart > 150)
            {
                shieldImmune = false;
            }
        }
        if (shieldBreak)
        {
            HUD.setRefilling(true);
            specialFrameCount++;
            if (specialFrameCount == 5)
            {
                specialFrameCount = 0;
                specialAbility = false;
            }
            else if (specialFrameCount == 4)
            {
                specialFrameCount = 0;
                HUD.powerFrames[1] = 0;
                shieldBreak = false;
            }
        }
        else if(HUD.isRefilling())
        {
            specialAbility = false;
        }
        else if (specialAbility && HUD.powerFrames[1] >= HUD.maxPowerFrame / 3)
        {
            if (specialFrameCount < 4)
            {
                if (specialFrameCount == 0)
                {
                    MusicPlayer.playSound(shieldSoundFile, -15, 0);
                }
                specialFrameCount++;
            }
        }
    }

    @Override
    void specialUpdateCycle()
    {
        if (up && !onGround && !doubleJumpLock)
        {
            MusicPlayer.playSound(jumpSoundFile, -15, 0);
            dy = -(jumpPower - 2);
            doubleJumpLock = true;
            cycle = rightJumpCycle + (cycle % 2);
            frame = 0;
            up = false;
        }

    }

    @Override
    void updateHitBoxCoordinates()
    {
        hitBoxWidth = 34;
        hitBoxHeight = 98;
        if (cycle % 2 == 0)
        {
            hitBoxXcor = 40;
        }
        else
        {
            hitBoxXcor = 10;
        }

        hitBoxYcor = height - hitBoxHeight;
    }

    @Override
    void resetCycles()
    {
        specialAbility = false;
        shieldBreak = false;
        shieldLife = 3;
        shieldImmune = false;
        HUD.setRefilling(true);
    }

    static void shieldHit(boolean spikesHit, boolean moveRight, boolean moveLeft, boolean moveUp)
    {
        if(!shieldImmune || spikesHit)
        {
            shieldLife--;

            if (moveRight)
            {
                absoluteXcor+= 40;
                lastDx = 40;
            }
            else if (moveLeft)
            {
                absoluteXcor-= 40;
                lastDx = -40;
            }
            else if (moveUp)
            {
                displayYcor -= shieldBounce / 2;
                lastDy = - shieldBounce / 2;
                bounce = true;
            }
            collisionChecked = false;

            if (shieldLife == 0 || spikesHit)
            {
                shieldLife = 3;
                shieldBreak = true;
            }
            shieldDamageStart = System.currentTimeMillis();
            shieldImmune = true;
            MusicPlayer.playSound(shieldSoundFile, -15, 0);
        }
    }
}