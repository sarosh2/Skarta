package com.skarta;

import java.awt.*;
import java.util.Random;

public class Mutahar extends Boss
{
    private static final String path = "/mutahar.png";
    private static final String projectilePath = "/mutaharProjectile.png";
    private static final String shieldPath = "/mutaharShield.png";
    private static final String attackSoundFile = "/mutaharAttack.wav";
    private static final String projectileSoundFile = "throw.wav";

    private static final int cycleCount = 2;
    private static final int framesPerCycle = 4;

    private static final int width = 90;
    private static final int height = 120;
    private static final int shieldDimensions = 250;
    private static final int projectileDimensions = 100;

    private static final int horizontalSpeed = 25;

    private static final int flyRightCycle = 0;
    private static final int flyLeftCycle = 1;

    private static final int noCollisionChecks = 10;

    private int waitTime;
    private boolean isProjectile;
    private boolean horizontallyCollided;
    private boolean verticallyCollided;

    public Mutahar(int foregroundRow, int foregroundColumn, Handler handler)
    {
        super(foregroundColumn,handler);
        life = 3;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        specialFeature = HelperFunctions.makeSpritesheet(shieldPath, 1, framesPerCycle, shieldDimensions, shieldDimensions);
        projectiles = new Projectile[3];
        projectiles[0] = new Projectile(projectilePath, projectileDimensions, projectileDimensions, 0, 1, framesPerCycle, true, false, 25, 50, 50);
        projectiles[1] = new Projectile(projectilePath, projectileDimensions, projectileDimensions, 0, 1, framesPerCycle, true, false, 25, 50, 50);
        projectiles[2] = new Projectile(projectilePath, projectileDimensions, projectileDimensions, 0, 1, framesPerCycle, true, false, 25, 50, 50);
        this.foregroundRow = foregroundRow;
        displayYcor = (foregroundRow - 1) * Key.platformHeight;
        waitTime = 0;
        isProjectile = false;
        damageSoundFile = "/mutaharDamage.wav";
        deathSoundFile = "/mutaharDeath.wav";
        damageMicroSecondPosition = 970000;
        deathMicroSecondPosition = 0;
        volume = -10;
    }

    public void update()
    {
        if (Player.displayXcor - displayXcor > 0)
        {
            cycle = flyRightCycle;
        }
        else
        {
            cycle = flyLeftCycle;
        }

    }

    @Override
    public void updateHitBoxCoordinates()
    {
        if(isSpecialFeature)
        {
            hitBoxWidth = 200;
            hitBoxHeight = 200;
            hitBoxXcor = - (shieldDimensions - width) / 2 + (shieldDimensions - hitBoxWidth) / 2;
            hitBoxYcor =  - (shieldDimensions - height) / 2 + (shieldDimensions - hitBoxHeight) / 2;
        }
        else
        {
            hitBoxWidth = 30;
            hitBoxHeight = 100;
            hitBoxXcor = (width - hitBoxWidth) / 2;
            hitBoxYcor = (height - hitBoxHeight) / 2;
        }
    }

    @Override
    public void move()
    {
        absoluteXcor += dx;
        displayYcor += dy;
        verticallyCollided = false;
        horizontallyCollided = false;
        collisionChecked = false;
    }

    @Override
    public void collide(int[][] foreground, int cameraXcor)
    {
        if(!collisionChecked)
        {
            if (absoluteXcor + hitBoxXcor + hitBoxWidth > Key.rightWall)
            {
                absoluteXcor = Key.rightWall - (hitBoxXcor + hitBoxWidth);
                dx = -dx;
            }
            else if (absoluteXcor < Key.leftWall)
            {
                absoluteXcor = Key.leftWall + 5;
                dx = -dx;
            }

            if (displayYcor + hitBoxYcor + hitBoxHeight >= Key.screenHeight)
            {
                displayYcor = Key.screenHeight - (hitBoxYcor + hitBoxHeight) - 25;
            }
            else if (displayYcor < Key.ceiling)
            {
                displayYcor = Key.ceiling;
                dy = -dy;
            }

            for (Projectile projectile : projectiles)
            {
                if (!projectile.isDestroyed())
                {
                    projectile.collide(foreground);
                }
            }

            boolean collidedWithPlayer = false;
            for(int i = 0; i <= noCollisionChecks; i++)
            {
                updatePaths(noCollisionChecks, i);

                if(!collidedWithPlayer)
                {
                    collidedWithPlayer = collisionWithPlayer(foreground, playerXcorPath[i], playerYcorPath[i], bossXcorPath[i], bossYcorPath[i]);
                }

                if(platformCollision(foreground, bossXcorPath[i], bossYcorPath[i]))
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
        if (!renderLock)
        {
            HelperFunctions.drawImage(g, spritesheet[cycle][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor, displayYcor);
        }

        if (isSpecialFeature)
        {
            HelperFunctions.drawImage(g, specialFeature[0][frame / Key.numberOfFramesRepeatedPerCycle],displayXcor - (shieldDimensions - width) / 2, displayYcor - (shieldDimensions - height) / 2);
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
        if (frame + 1 >= framesPerCycle * Key.numberOfFramesRepeatedPerCycle)
        {
            change();
            if (waitTime >= 4)
            {
                waitTime = 0;
            }
            else
            {
                waitTime++;
            }
        }
        else
        {
            frame++;
        }
        tickChecks();
    }

    @Override
    public void change()
    {
        Random random = new Random();
        if (!isSpecialFeature && !isProjectile)
        {
            if (random.nextInt() % 2 == 0)
            {
                dx = 30;
                dy = 30;
                isSpecialFeature = true;
                MusicPlayer.playSound(attackSoundFile, -10, 800000);
            }
            else
            {
                isProjectile = true;
            }
        }
        else if (isSpecialFeature)
        {
            if (waitTime == 0)
            {
                dx = 0;
                dy = 0;
                isSpecialFeature = false;
            }
            else
            {
                switch (random.nextInt() % 2) {
                    case 0 -> dx = -dx;
                    case 1 -> dy = -dy;
                }
                MusicPlayer.playSound(attackSoundFile, -10, 500000);
            }
        }
        else
        {
            if (waitTime == 0)
            {
                isProjectile = false;
            }
            else
            {
                for (Projectile projectile : projectiles)
                {
                    if (projectile.isDestroyed())
                    {
                        projectile.launch(projectileSoundFile, absoluteXcor, displayYcor, horizontalSpeed * (int) Math.pow(-1, cycle % 2), -horizontalSpeed / 4);
                        break;
                    }
                }
            }
        }
        frame = 0;
    }


    boolean platformCollision(int[][] foregorund, int bossXcor, int bossYcor)
    {
        boolean returnValue;
        returnValue = horizontalPlatformCollision(foregorund, bossXcor, bossYcor);
        returnValue = returnValue && verticalPlatformCollision(foregorund, bossXcor, bossYcor);
        return returnValue; // Returns True only if both types of Platform Collision has occurred;
    }

    boolean horizontalPlatformCollision(int[][] foreground, int bossXcor, int bossYcor)
    {
        if(horizontallyCollided)
        {
            return true;
        }
        else
        {
            int foregroundColumn;
            int toBeChecked;
            //rightwardCollision
            if(dx>0)
            {
                foregroundColumn = (bossXcor + hitBoxXcor + hitBoxWidth) / Key.platformWidth; // The column in which Collision is to be checked
                for (int verticalPixelCounter = hitBoxYcor + hitBoxHeight / 4; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 3 / 4; verticalPixelCounter += hitBoxHeight / 4)  // Iterates to check different Vertical points for collision
                {
                    toBeChecked = foreground[(bossYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn]; // Platform Type
                    if (toBeChecked != 0 && toBeChecked < 19) // Regular Platform
                    {
                        dx = -dx; // Changing Directions
                        absoluteXcor = (foregroundColumn * Key.platformWidth) - (hitBoxXcor + hitBoxWidth); // Moving Appropriate Limit of Character to appropriate limit of the Platform
                        return horizontallyCollided = true;
                    }
                    else if (toBeChecked == 22) // Right Spikes
                    {
                        if ((bossXcor + hitBoxXcor + hitBoxWidth) % Key.platformWidth >= Key.platformWidth - Key.spikesWidth) // Special Limit Check for Spikes
                        {
                            dx = -dx; // Changing Directions
                            absoluteXcor = (foregroundColumn * Key.platformWidth) - (hitBoxXcor + hitBoxWidth) + (Key.platformWidth - Key.spikesWidth); // Moving Appropriate Limit of Character to appropriate limit of the Spikes
                            return horizontallyCollided = true;
                        }
                    }
                }
            }
            //leftwardCollision
            else if(dx<0)
            {
                foregroundColumn = (bossXcor + hitBoxXcor) / Key.platformWidth; // The column in which Collision is to be checked
                for(int verticalPixelCounter = hitBoxYcor + hitBoxHeight /4; verticalPixelCounter<= hitBoxYcor + hitBoxHeight * 3/4 ; verticalPixelCounter+= hitBoxHeight/4) // Iterates to check different Vertical points for collision
                {
                    toBeChecked = foreground[(bossYcor + verticalPixelCounter)/Key.platformHeight][foregroundColumn]; // Platform Type
                    if(toBeChecked !=0 && toBeChecked < 19) // Regular Platform
                    {
                        dx = -dx; // Changing Directions
                        absoluteXcor = (foregroundColumn + 1) * Key.platformWidth - (hitBoxXcor); // Moving Appropriate Limit of Character to appropriate limit of the Platform
                        return horizontallyCollided = true;
                    }
                    else if(toBeChecked == 20) // Left Spikes
                    {
                        if((bossXcor + hitBoxXcor) % Key.platformWidth <= Key.spikesWidth) // Special Limit Check for Spikes
                        {
                            dx = -dx; // Changing Directions
                            absoluteXcor = (foregroundColumn * Key.platformWidth) - (hitBoxXcor) + (Key.spikesWidth); // Moving Appropriate Limit of Character to appropriate limit of the Spikes
                            return horizontallyCollided = true;
                        }
                    }
                }
            }
            else
            {
                return horizontallyCollided = true;
            }
            return false;
        }
    }

    boolean verticalPlatformCollision(int[][] foreground, int bossXcor, int bossYcor)
    {
        if(verticallyCollided)
        {
            return true;
        }
        else
        {
            int foregroundRow;
            int toBeChecked;
            //downwardCollision
            if(dy>0)
            {
                foregroundRow = (bossYcor + hitBoxYcor + hitBoxHeight)/ Key.platformHeight;
                for(int horizontalPixelCounter = hitBoxXcor + hitBoxWidth / 4; horizontalPixelCounter <= hitBoxXcor + hitBoxWidth * 3 / 4; horizontalPixelCounter+= hitBoxWidth / 4)
                {
                    toBeChecked = foreground[foregroundRow][(bossXcor + horizontalPixelCounter)/Key.platformWidth]; // Platform Type
                    if(toBeChecked !=0 && toBeChecked < 19)
                    {
                        dy = - dy; // Changing Directions
                        displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight); // Moving Appropriate Limit of Character to appropriate limit of the Platform
                        return verticallyCollided = true;
                    }
                    else if(toBeChecked == 19) // Bottom Spikes
                    {
                        if((bossYcor + hitBoxYcor + hitBoxHeight) % Key.platformHeight >= Key.platformHeight - Key.spikesHeight) // Special Limit Check for Spikes
                        {
                            dy = - dy; // Changing Directions
                            displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight) + (Key.platformHeight - Key.spikesHeight); // Moving Appropriate Limit of Character to appropriate limit of the Spikes
                            return verticallyCollided = true;
                        }
                    }
                }
            }
            //upwardCollision
            else if(dy<0)
            {
                foregroundRow = (bossYcor + hitBoxYcor)/ Key.platformHeight;
                for(int horizontalPixelCounter = hitBoxXcor + hitBoxWidth / 4; horizontalPixelCounter <= hitBoxXcor + hitBoxWidth * 3 / 4; horizontalPixelCounter+= hitBoxWidth / 4)
                {
                    toBeChecked = foreground[foregroundRow][(bossXcor + horizontalPixelCounter)/Key.platformWidth];  // Platform Type
                    if(toBeChecked !=0 && toBeChecked < 19) // Regular Platform
                    {
                        dy = - dy; // Changing Directions
                        displayYcor = (foregroundRow + 1) * Key.platformHeight - (hitBoxYcor); // Moving Appropriate Limit of Character to appropriate limit of the Platform
                        return verticallyCollided = true;
                    }
                    else if(toBeChecked == 21) // Top Spikes
                    {
                        if((bossYcor + hitBoxYcor) % Key.platformHeight <= Key.spikesHeight) // Special Limit Check for Spikes
                        {
                            dy = - dy; // Changing Directions
                            displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor) + (Key.spikesHeight); // Moving Appropriate Limit of Character to appropriate limit of the Spikes
                            return verticallyCollided = true;
                        }
                    }
                }
            }
            else
            {
                return  verticallyCollided = true;
            }
            return false;
        }
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