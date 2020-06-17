package com.skarta;

import java.awt.*;

public class Abdullah extends Enemy
{
    private static final String path = "/abdullah.png";
    private static final String projectilePath = "/abdullahProjectile.png";
    private static final String projectileSoundFile = "/throw.wav";

    private static final int horizontalSpeed = 5;
    private static final int cycleCount = 6;
    private static final int framesPerCycle = 4;
    private static final int width = 80;
    private static final int height = 110;
    private static final int projectileDimensions = 60;
    private static final int throwRightCycle = 2;
    private static final int noBoxesToCheck = 10; // How many Platforms far Abdullah can see
    private final Projectile projectile;

    Abdullah(int foregroundRow, int foregroundColumn, int enemyNo)
    {
        super(foregroundRow, foregroundColumn, enemyNo);
        speed = horizontalSpeed;
        dx = speed;
        hitRightCycle = 4;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        projectile = new Projectile(projectilePath, projectileDimensions, projectileDimensions, 0, 1, framesPerCycle, false, false,10, 40, 40);
        damageSoundFile = "abdullahHit.wav";
        volume = -18;
        damageMicroSecondPosition = 750000;
    }

    @Override
    public void render(Graphics g)
    {
        if(displayXcor + hitBoxXcor + hitBoxWidth > 0 && displayXcor + hitBoxXcor < Key.screenWidth)
        {
            HelperFunctions.drawImage(g, spritesheet[cycle][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor, displayYcor);
        }
        if (projectile.isNotLastFrameShown())
        {
            projectile.render(g);
            if(projectile.isDestroyed())
            {
                projectile.setLastFrameShown(true);
            }
        }

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
        else if (cycle >= throwRightCycle && cycle < hitRightCycle && frame + 1 == 2 * Key.numberOfFramesRepeatedPerCycle)
        {
            cycle = walkRightCycle + cycle % 2;
            frame = 0;
            projectile.launch(projectileSoundFile, absoluteXcor, displayYcor, 10 * (int) Math.pow(-1, cycle % 2), 0);
        }
        else
        {
            frame++;
        }
        if(cycle < hitRightCycle)
        {
            move();
        }

        if (!projectile.isDestroyed())
        {
            projectile.tick();
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

        if (cycle < throwRightCycle && projectile.isDestroyed() && Math.abs(Player.displayYcor - displayYcor) < 20 && !sightCollision(foreground))
        {
            if(Player.displayXcor == displayXcor) // Division by zero check
            {
                cycle = throwRightCycle + cycle % 2;
                frame = 0;
            }
            else if ((int) Math.pow(-1, cycle % 2) == (Player.displayXcor - displayXcor) / Math.abs(Player.displayXcor - displayXcor))
            {
                cycle = throwRightCycle + cycle % 2;
                frame = 0;
            }
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
            if (!projectile.isDestroyed())
            {
                projectile.collide(foreground);
            }

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
        if (projectile.isNotLastFrameShown())
        {
            projectile.updateDisplayCoordinates(cameraXcor);
        }
    }

    boolean sightCollision(int[][] foreground)
    {
        boolean playertoLeft = Player.absoluteXcor < absoluteXcor;
        int toBeChecked;
        for(int horizontalPlatformsCounter = 0; horizontalPlatformsCounter <= noBoxesToCheck; horizontalPlatformsCounter++ )
        {
            if(playertoLeft)
            {
                if((absoluteXcor/Key.platformWidth - horizontalPlatformsCounter) * Key.platformWidth < Player.absoluteXcor) // if Player was to the left of Abdullah, and now the Player is to the right of the Platform to be checked
                {
                    return false;
                }
                toBeChecked = foreground[(displayYcor + projectileDimensions / 2) / Key.platformHeight][absoluteXcor/Key.platformWidth - horizontalPlatformsCounter]; // projectileDimensions =  Projectile's Height
            }
            else
            {
                if((absoluteXcor/Key.platformWidth + horizontalPlatformsCounter) * Key.platformWidth > Player.absoluteXcor) // if Player was to the right of Abdullah, and now the Player is to the left of the Platform to be checked
                {
                    return false;
                }
                toBeChecked = foreground[(displayYcor + projectileDimensions / 2) / Key.platformHeight][absoluteXcor/Key.platformWidth + horizontalPlatformsCounter]; // projectileDimensions =  Projectile's Height
            }
            if(toBeChecked!= 0 && toBeChecked < 19)
            {
                return true;
            }
        }
        return true;
    }
}