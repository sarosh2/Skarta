package com.skarta;

import java.awt.*;

public class Tayyab extends Player
{
    static final String path = "/tayyab.png";
    static final int horizontalSpeed = 12;
    static final int jump = 18;
    static final int cycleCount = 11;
    static boolean wallStick = false;
    static boolean wallLand = false;
    static int lastWall;
    static int currentWall;

    static final int dashRightCycle = 7;
    static final int dashLeftCycle = 8;
    static final int wallStickRightCycle = 9;
    static final int wallStickLeftCycle = 10;

    static int wallStickInputDelayed = 0;

    Tayyab()
    {
        speed = horizontalSpeed;
        jumpPower = jump;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        hitBoxWidth = 30;
        hitBoxHeight = 90;
    }

    @Override
    void specialRender(Graphics g) { }

    @Override
    void specialTick()
    {
        if (specialAbility && HUD.powerFrames[2] == HUD.maxPowerFrame)
        {
            specialFrameCount++;

            if (specialFrameCount == 1)
            {
                dyLock = true;
                switchLock = true;
                if (cycle % 2 == 0)
                {
                    dx = 58;
                    cycle = dashRightCycle;
                }
                else
                {
                    dx = -58;
                    cycle = dashLeftCycle;
                }
                frame = 2;
                MusicPlayer.playSound("dash.wav", -10, 0);
            }
            else if (frame == 6)
            {
                frame = 10;
            }
            else if (specialFrameCount == 9)
            {
                //onGround = false;
                //wallStick = false;

                dy = 0;

                if (!wallStick)
                {
                    cycle = rightJumpCycle + (cycle + 1) % 2;
                }
                else
                {
                    if (cycle % 2 == 0)
                    {
                        cycle = wallStickRightCycle;
                    }
                    else
                    {
                        cycle = wallStickLeftCycle;
                    }
                }
                dyLock = false;
                switchLock = false;
                specialAbility = false;
                HUD.powerFrames[2] = 0;
                specialFrameCount = 0;
            }
        }
        else
        {
            specialAbility = false;
        }
    }

    @Override
    void updateHitBoxCoordinates()
    {
        hitBoxWidth = 30;
        hitBoxHeight = 90;
        hitBoxXcor = (width - hitBoxWidth) / 2;
        hitBoxYcor = height - hitBoxHeight;
    }

    @Override
    void specialUpdateCycle()
    {
        if (wallLand)
        {
            if(lastWall != currentWall)
            {
                wallStick = true;
                dy = 2;
                if (cycle % 2 == 0)
                {
                    cycle = wallStickRightCycle;
                }
                else
                {
                    cycle = wallStickLeftCycle;
                }
                lastWall = currentWall;
                wallLand = false;
            }
        }

        if (!onGround && !wallStick)
        {
            wallLand = false;
            if (cycle == wallStickRightCycle || cycle == wallStickLeftCycle)
            {

                cycle = rightJumpCycle + (cycle + 1) % 2;
                frame = 11;
            }
        }

    }

    @Override
    void resetCycles()
    {
        specialAbility = false;
        wallStick = false;
    }
}