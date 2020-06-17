package com.skarta;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Ahsan extends Player
{
    private final BufferedImage[][] attack;

    private static final String path = "/ahsan.png";
    private static final String attackPath = "/attack.png";
    private static final String slimeSoundFile = "slime.wav";
    private static final String attackSoundFile = "attack.wav";

    private static final int horizontalSpeed = 8;
    private static final int jump = 16;
    private static final int cycleCount = 11;

    private static final int slimeSwitchCycle = 7;
    static final int slimeCycle = 9;
    private static final int humanHitBoxYcor = 30;

    private static boolean down = false;
    private static boolean isSlime;
    private static boolean canSwitch = true;
    private static boolean cycleOpposite = false; // Used for fixing a problem with slime changing direction whenever we switch between Slime and Ahsan
    private static boolean downLock = false;
    private static int attackCycle;

    Ahsan()
    {
        speed = horizontalSpeed;
        jumpPower = jump;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        attack = HelperFunctions.makeSpritesheet(attackPath, 2, 5, 200, 200);
        hitBoxWidth = 38;
        hitBoxHeight = 90;
    }

    @Override
    void specialRender(Graphics g)
    {
        if (!isSlime && specialAbility && !downLock)
        {
            HelperFunctions.drawImage(g, attack[attackCycle][specialFrameCount / 2], displayXcor - 60, displayYcor - 90);
        }
    }

    @Override
    void specialTick()
    {
        if (!isSlime)
        {
            if (specialAbility && !downLock)
            {
                specialFrameCount++;
                if (specialFrameCount == 1)
                {
                    attackCycle = cycle % 2;
                    renderLock = true;
                    MusicPlayer.playSound(attackSoundFile, -15, 0);
                }
                else if (specialFrameCount == 10)
                {
                    specialAbility = false;
                    cycle = rightJumpCycle + (attackCycle % 2);
                    specialFrameCount = 0;
                    renderLock = false;
                }
            }
            else if (down) {
                downLock = true;
            }
            if(downLock)
            {
                specialFrameCount++;

                if (specialFrameCount == 1)
                {
                    if(cycle != rightJumpCycle && cycle != leftJumpCycle)
                    {
                        cycleOpposite = false;
                        cycle =  slimeSwitchCycle + cycle % 2;
                    }
                    else
                    {
                        cycleOpposite = true;
                        cycle =  slimeSwitchCycle + ((cycle + 1)  % 2);
                    }
                    frame = 0;
                    MusicPlayer.playSound(slimeSoundFile, -15, 0);
                }
                else if (specialFrameCount == 2)
                {
                    specialFrameCount = 4;
                    frame = 4;
                }
                else if (specialFrameCount == 5)
                {
                    specialFrameCount = 0;
                    cycle = slimeCycle + (cycle + 1) % 2;
                    frame = 0;
                    isSlime = true;
                    downLock = false;
                }
            }
        }
        if(!down && isSlime)
        {
            downLock = true;
        }
        if (isSlime && downLock)
        {
            if(canSwitch)
            {
                specialFrameCount++;
                if (specialFrameCount == 1)
                {
                    if(cycleOpposite)
                    {
                        cycle = slimeSwitchCycle + (cycle + 1) % 2;
                    }
                    else
                    {
                        cycle = slimeSwitchCycle + cycle % 2;
                    }

                    frame = 8;
                }
                else if (specialFrameCount == 2)
                {
                    specialFrameCount = 4;
                    frame = 12;
                }
                else if (specialFrameCount == 5)
                {
                    specialFrameCount = 0;
                    cycle = rightJumpCycle + cycle % 2;
                    isSlime = false;
                    downLock = false;
                    MusicPlayer.playSound(slimeSoundFile, -15, 0);
                }
            }
            else
            {
                cycle = slimeCycle + cycle % 2;
            }
        }
    }

    @Override
    void updateHitBoxCoordinates()
    {
        if (isSlime)
        {
            hitBoxHeight = 35;
            hitBoxWidth = 65;
        }
        else
        {
            hitBoxWidth = 38;
            hitBoxHeight = 94;
        }
        hitBoxXcor = (width - hitBoxWidth) / 2;
        hitBoxYcor = height - hitBoxHeight;
    }

    @Override
    void specialUpdateCycle()
    {
        if (!isSlime && down && isGlitching)
        {
            down = false;
            cycle = rightJumpCycle;
        }

    }

    @Override
    void resetCycles()
    {
        specialAbility = false;
        isSlime = false;
    }

    static void specialCollisionChecks(int[][] foreground)
    {
        if (isSlime)
        {
            int toBeChecked = foreground[(displayYcor + humanHitBoxYcor) / Key.platformHeight][(absoluteXcor + hitBoxXcor + hitBoxWidth / 2) / Key.platformWidth]; // The Platform at the head of Ahsan's Human hitBox
            canSwitch = toBeChecked == 0 || (toBeChecked >= 19 && toBeChecked != 21);
        }
    }

    static boolean getIsSlime()
    {
        return isSlime;
    }
    static boolean getCanSwitch()
    {
        return canSwitch;
    }
    static boolean getDownInverted()
    {
        return !down;
    }

    static void setSlime()
    {
        Ahsan.isSlime = false;
    }
    static void setDown(boolean down)
    {
        Ahsan.down = down;
    }
}