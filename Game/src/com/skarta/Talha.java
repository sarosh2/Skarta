package com.skarta;

import java.awt.*;
import java.util.Random;

public class Talha extends Boss
{
    private static final String path = "/talha.png";
    private static final String dashPath = "/talhaDash.png";
    private static final String shockPath = "/talhaShock.png";
    private static final String attackSoundFile = "/talhaAttack.wav";
    private static final String dashSoundFile = "/talhaDash.wav";

    private static final int dashHeight = 200;
    private static final int dashWidth = 110;

    private static final int shockWidth = 220;
    private static final int shockHeight = 120;

    private static final int standRightCycle = 0;
    private static final int standLeftCycle = 1;
    private static final int transitionRightCycle = 2;
    private static final int transitionLeftCycle = 3;
    private static final int ballCycle = 4;

    private static final int cycleCount = 5;
    private static final int framesPerCycle = 4;
    private static final int width = 90;
    private static final int height = 120;

    private static final int horizontalSpeed = 40;
    private static final int verticalSpeed = 20;

    private static final int noCollisionChecks = 5;

    private int direction;

    public Talha(int foregroundRow, int foregroundColumn, Handler handler)
    {
        super(foregroundColumn, handler);
        life = 4;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        specialFeature = HelperFunctions.makeSpritesheet(dashPath, 1, framesPerCycle, dashWidth, dashHeight);
        projectiles = new Projectile[2];
        projectiles[0] = new Projectile(shockPath, shockWidth, shockHeight, 1, 2, 1, false, false,15, 120, shockHeight - 40, 25);
        projectiles[1] = new Projectile(shockPath, shockWidth, shockHeight, 0, 2, 1, false, false,15, 120, shockHeight - 40, 75);
        this.foregroundRow = foregroundRow;
        displayYcor = (foregroundRow - 1) * Key.platformHeight;
        direction = 1;
        damageSoundFile = "/talhaDamage.wav";
        deathSoundFile = "/talhaDeath.wav";
        damageMicroSecondPosition = 0;
        deathMicroSecondPosition = 0;
        volume = -15;
    }

    @Override
    void update()
    {
        if (Player.displayXcor - displayXcor > 0)
        {
            direction = 0;
        }
        else
        {
            direction = 1;
        }

    }

    @Override
    void updateHitBoxCoordinates()
    {
        if(cycle == ballCycle && isSpecialFeature)
        {
            hitBoxWidth = 50;
            hitBoxHeight = 160;
            hitBoxXcor = - (dashWidth - width) / 2 + (dashWidth - hitBoxWidth) / 2;
            hitBoxYcor = + height - dashHeight + (dashHeight - hitBoxHeight) / 2;
        }
        else if(cycle == ballCycle)
        {
            hitBoxWidth = 50;
            hitBoxHeight = 50;
            hitBoxXcor = (width - hitBoxWidth) / 2;
            hitBoxYcor = (height - hitBoxHeight) / 2;
        }
        else
        {
            hitBoxWidth = 30;
            hitBoxHeight = 100;
            hitBoxXcor = (width - hitBoxWidth) / 2;
            hitBoxYcor = height - hitBoxHeight;
        }

    }

    @Override
    void move()
    {
        absoluteXcor += dx;
        displayYcor += dy;
        collisionChecked = false;
    }

    @Override
    void collide(int[][] foreground, int cameraXcor)
    {
        if(!collisionChecked)
        {
            if (displayYcor > Key.floor)
            {
                displayYcor = (foregroundRow - 1) * Key.platformHeight;
                change();
            }

            if(absoluteXcor + hitBoxXcor + hitBoxWidth > Key.rightWall)
            {
                absoluteXcor = Key.rightWall - (hitBoxXcor + hitBoxWidth);
                change();
            }
            else if(absoluteXcor + hitBoxXcor < Key.leftWall)
            {
                absoluteXcor = Key.leftWall - (hitBoxXcor);
                change();
            }
            for (Projectile projectile: projectiles)
            {
                if (!projectile.isDestroyed())
                {
                    projectile.collide(foreground);
                }
            }

            for(int i = 0; i <= noCollisionChecks; i++)
            {
                updatePaths(noCollisionChecks, i);

                if(collisionWithPlayer(foreground, playerXcorPath[i], playerYcorPath[i], bossXcorPath[i], bossYcorPath[i]))
                {
                    break;
                }

            }
            collisionChecked = true;
        }

    }

    @Override
    public void render(Graphics g)
    {
        if (isSpecialFeature && cycle == ballCycle)
        {
            HelperFunctions.drawImage(g, specialFeature[0][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor - (dashWidth - width) / 2, displayYcor + height - dashHeight);
        }
        else if (!renderLock)
        {
            HelperFunctions.drawImage(g, spritesheet[cycle][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor, displayYcor);
        }

        for (Projectile projectile: projectiles)
        {
            if (projectile.isNotLastFrameShown())
            {
                projectile.render(g);
                if(projectile.isDestroyed())
                {
                    projectile.setLastFrameShown(true);
                }
            }
        }
    }

    @Override
    public void tick()
    {
        if (frame + 1 >= framesPerCycle * Key.numberOfFramesRepeatedPerCycle || cycle == transitionRightCycle + direction)
        {
            change();
        }
        else
        {
            frame++;
        }
        tickChecks();
    }

    @Override
    void change()
    {
        Random random = new Random();
        if (cycle <= standLeftCycle)
        {
            if (random.nextInt() % 2 == 0)
            {
                cycle = transitionRightCycle + direction;
            }
            else
            {
                dx = horizontalSpeed * (int)Math.pow(-1, direction);
                cycle = ballCycle;
                MusicPlayer.playSound(dashSoundFile, volume, 0);
            }
        }
        else if (cycle <= transitionLeftCycle && !isSpecialFeature)
        {
            dy = - verticalSpeed * 2;
            cycle = ballCycle;
        }
        else if (cycle == ballCycle && !isSpecialFeature)
        {
            if (dx != 0)
            {
                dx = 0;
                cycle = standRightCycle + direction;
            }
            else
            {
                dy = verticalSpeed * 3;
                isSpecialFeature = true;
            }
        }
        else
        {
            dx = 0;
            dy = 0;
            if (cycle <= transitionLeftCycle)
            {
                cycle = standRightCycle + direction;
                isSpecialFeature = false;
                projectiles[0].launch(attackSoundFile, absoluteXcor, displayYcor + 10, horizontalSpeed, 0);
                projectiles[1].launch(attackSoundFile, absoluteXcor - 150,displayYcor + 10, -horizontalSpeed, 0); // ....
            }
            else
            {
                cycle = transitionRightCycle + direction;
            }
        }
        frame = 0;
    }


    void updateDisplayCoordinates(int cameraXcor)
    {
        displayXcor = absoluteXcor - cameraXcor;
        for (Projectile projectile: projectiles)
        {
            if(projectile.isNotLastFrameShown())
            {
                projectile.updateDisplayCoordinates(cameraXcor);
            }
        }
    }
}