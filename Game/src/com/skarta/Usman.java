package com.skarta;

import java.awt.*;

public class Usman extends Enemy
{
    private static final String path = "/usman.png";

    private static final int horizontalSpeed = 3;
    private static final int cycleCount = 4;
    private static final int framesPerCycle = 4;
    private static final int width = 80;
    private static final int height = 110;

    Usman(int foregroundRow, int foregroundColumn, int enemyNo)
    {
        super(foregroundRow, foregroundColumn, enemyNo);
        speed = horizontalSpeed;
        dx = speed;
        hitRightCycle = 2;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        damageSoundFile = "usmanHit.wav";
        volume = -15;
        damageMicroSecondPosition = 300000;
    }

    @Override
    public void render(Graphics g)
    {
        HelperFunctions.drawImage(g, spritesheet[cycle][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor, displayYcor);
    }

    @Override
    public void tick()
    {
        if (frame + 1 == Key.numberOfFramesRepeatedPerCycle * framesPerCycle)
        {
            if (cycle >= hitRightCycle)
            {
                die = true;
            }
            frame = 0;
        }
        else
        {
            frame++;
        }
        if(cycle < hitRightCycle)
        {
            move();
        }

    }

    @Override
    void move()
    {
        absoluteXcor += dx;
        collisionChecked = false;
    }

    @Override
    void updateCycle(int[][] foreground)
    {
        if (die)
        {
            foreground[foregroundRow][foregroundColumn] = 0;
        }
    }

    @Override
    void updateHitBoxCoordinates()
    {
        hitBoxXcor = (width - hitBoxWidth) / 2;
        hitBoxYcor = height - hitBoxHeight;
    }

    @Override
    void collide(int[][] foreground, int cameraXcor)
    {
        if(!(collisionChecked && Player.collisionChecked))
        {
            if(absoluteXcor < Key.leftWall)
            {
                dx = speed;
                absoluteXcor = Key.leftWall;
                cycle = walkRightCycle;
            }

            platformCollision(foreground);

            collisionWithPlayer();
            updateDisplayCoordinates(cameraXcor);
            collisionChecked = true;
        }

    }

    @Override
    void updateDisplayCoordinates(int cameraXcor)
    {
        displayXcor = absoluteXcor - cameraXcor;
    }
}