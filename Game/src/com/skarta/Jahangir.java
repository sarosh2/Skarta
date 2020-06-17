package com.skarta;

import java.awt.*;
import java.util.Random;

public class Jahangir extends Boss
{
    private static final String path = "/jahangir.png";
    private static final String attackPath = "/jahangirAttack.png";
    private static final String projectilePath = "/jahangirProjectile.png";
    private static final String projectileSoundFile = "throw.wav";
    private static final String attackSoundFile = "/jahangirAttack.wav";

    private static final int attackDimensions = 290;
    private static final int projectileDimensions = 350;

    private static final int disappearRightCycle = 2;
    private static final int appearRightCycle = 4;

    private static final int cycleCount = 6;
    private static final int framesPerCycle = 4;
    private static final int width = 160;
    private static final int height = 240;

    private static final int noCollisionChecks = 5;

    private boolean shouldAttack = false;
    private boolean viableBehind;
    private boolean viableFront;
    private int randPosX = 0;
    private int behindTeleportXcor;
    private int frontTeleportXcor;
    private int teleportYcor;


    public Jahangir(int foregroundRow, int foregroundColumn, Handler handler)
    {
        super(foregroundColumn, handler);
        life = 5;
        numberOfCycles = cycleCount;
        spritesheet = HelperFunctions.makeSpritesheet(path, numberOfCycles, framesPerCycle, width, height);
        specialFeature = HelperFunctions.makeSpritesheet(attackPath, 2, framesPerCycle, attackDimensions, attackDimensions);
        projectiles = new Projectile[1];
        projectiles[0] = new Projectile(projectilePath, projectileDimensions, projectileDimensions, 0, 2, 4, true, true, 137, 75, 75, 137);
        this.foregroundRow = foregroundRow;
        displayYcor = (foregroundRow - 3) * Key.platformHeight;
        damageSoundFile = "/jahangirDamage.wav";
        deathSoundFile = "/jahangirDeath.wav";
        damageMicroSecondPosition = 0;
        deathMicroSecondPosition = 0;
        volume = -15;
    }

    @Override
    void update()
    {

    }

    @Override
    void updateHitBoxCoordinates()
    {

        if(!isSpecialFeature)
        {
            hitBoxHeight = 120;
            hitBoxWidth = 30;
            hitBoxXcor = (width - hitBoxWidth) / 2;
            hitBoxYcor = 100;
        }
        else if ((frame / 3) < 3)
        {
            hitBoxHeight = 120;
            hitBoxWidth = 30;
            hitBoxYcor = - (attackDimensions - height) / 2 - 10 + 135;
            hitBoxXcor = - (attackDimensions - width) / 2 + 130;
        }
        else if (cycle % 2 == 0)
        {
            hitBoxHeight = 120;
            hitBoxWidth = 140;
            hitBoxYcor = - (attackDimensions - height) / 2 - 10 + 135;
            hitBoxXcor = - (attackDimensions - width) / 2 + 130;
        }
        else
        {
            hitBoxHeight = 120;
            hitBoxWidth = 140;
            hitBoxYcor = - (attackDimensions - height) / 2 - 10 + 135;
            hitBoxXcor = - (attackDimensions - width) / 2 + 20;
        }

    }

    @Override
    void move()
    {
        displayXcor += dx;
        displayYcor += dy;
        collisionChecked = false;
        viableBehind = false;
        viableFront = false;
    }

    @Override
    void collide(int[][] foreground, int cameraXcor)
    {
        if(!collisionChecked)
        {
            Random random = new Random();
            if(!projectiles[0].isDestroyed())
            {
                projectiles[0].collide(foreground);
            }

            for(int i = 0; i <= noCollisionChecks; i++)
            {
                updatePaths(noCollisionChecks, i);

                if(collisionWithPlayer(foreground, playerXcorPath[i], playerYcorPath[i], bossXcorPath[i], bossYcorPath[i]))
                {
                    break;
                }

            }

            behindTeleportXcor = Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth/2 - 100 * (int) Math.pow(-1, Player.cycle % 2) - (hitBoxXcor + hitBoxWidth/2);
            frontTeleportXcor = Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth/2 - 100 * (int) Math.pow(-1, (Player.cycle + 1) % 2) - (hitBoxXcor + hitBoxWidth/2);
            teleportYcor = Player.displayYcor + Player.height - hitBoxYcor - hitBoxHeight;

            viableBehind = viable(behindTeleportXcor, teleportYcor, foreground) && Player.onGround; // Checks if it is viable to teleport Behind Player
            viableFront = viable( frontTeleportXcor, teleportYcor, foreground) && Player.onGround; // Checks if it is viable to teleport in front of Player
            if(!viableFront && !viableBehind)
            {
                do
                { // Calculates A random valid Position to teleport to
                    randPosX = Math.abs(random.nextInt() % 17 + 1) * 100;

                }while(!(viable(randPosX , (19 * Key.platformHeight) - (hitBoxYcor + hitBoxHeight - 5) , foreground)) || isColliding(Player.absoluteXcor, Player.displayYcor, randPosX, (19 * Key.platformHeight) - (hitBoxYcor + hitBoxHeight - 5)));
            }

            collisionChecked = true;
        }

    }

    @Override
    public void render(Graphics g)
    {
        if (isSpecialFeature)
        {
            HelperFunctions.drawImage(g, specialFeature[cycle][frame / Key.numberOfFramesRepeatedPerCycle], displayXcor - (attackDimensions - width) / 2, displayYcor - (attackDimensions - height) / 2 - 10);
        }
        else if (!renderLock)
        {
            HelperFunctions.drawImage(g, spritesheet[cycle][frame / 3], displayXcor, displayYcor);
        }
        if (projectiles[0].isNotLastFrameShown())
        {
            projectiles[0].render(g);
            if(projectiles[0].isDestroyed())
            {
                projectiles[0].setLastFrameShown(true);
            }
        }
    }

    @Override
    public void tick()
    {
        if (frame + 1 >= framesPerCycle * 3)
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
        if (!isSpecialFeature)
        {
            if (cycle < disappearRightCycle)
            {
                if (random.nextInt() % 2 == 0)
                {
                    cycle = disappearRightCycle + cycle % 2;
                }
                else if (projectiles[0].isDestroyed())
                {
                    projectiles[0].setHitBox(137, 137, 75, 75);
                    projectiles[0].launch(projectileSoundFile, absoluteXcor - (projectileDimensions - width) / 2, displayYcor - (projectileDimensions - height) / 2, 10 * (int)Math.pow(-1, cycle % 2), -20);
                }
            }
            else if (cycle < appearRightCycle)
            {
                if(viableBehind)
                {
                    absoluteXcor = behindTeleportXcor;
                    displayYcor = teleportYcor;
                    shouldAttack = true;
                }
                else if(viableFront)
                {
                    absoluteXcor = frontTeleportXcor;
                    displayYcor = teleportYcor;
                    shouldAttack = true;
                }
                else
                {
                    shouldAttack = false;
                    displayYcor = (19 * Key.platformHeight) - (hitBoxYcor + hitBoxHeight - 5);
                    absoluteXcor = randPosX;
                }

                cycle = appearRightCycle;
                if(Player.absoluteXcor < absoluteXcor)
                {
                    cycle++;
                }
            }
            else
            {
                if(shouldAttack)
                {
                    isSpecialFeature = true;
                    MusicPlayer.playSound(attackSoundFile, volume, 0);
                }
                isSpecialFeature = shouldAttack;
                cycle = cycle % 2;
            }
        }
        else
        {
            isSpecialFeature = false;
        }
        frame = 0;
    }


    private boolean viable(int bossXcor, int bossYcor, int[][] foreground)
    {
        int toBeChecked;
        if(bossXcor < 0)
        {
            return false;
        }
        // Checks if Upper 2/3 parts of Jahangir is not colliding with anything
        for(int horizontalPixelCounter = hitBoxXcor; horizontalPixelCounter <= hitBoxXcor + hitBoxWidth; horizontalPixelCounter+= hitBoxWidth/2)
        {
            for(int verticalPixelCounter = hitBoxYcor; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 2/3; verticalPixelCounter+= hitBoxHeight/3)
            {
                toBeChecked = foreground[(bossYcor + verticalPixelCounter)/Key.platformHeight][(bossXcor + horizontalPixelCounter)/Key.platformWidth];
                if(toBeChecked != 0 && toBeChecked < 19)
                {
                    return false;
                }
                else if(toBeChecked == 19) // Bottom Spikes
                {
                    if((bossYcor + verticalPixelCounter) % Key.platformHeight > Key.platformHeight - Key.spikesHeight)
                    {
                        return false;
                    }
                }
                else if(toBeChecked == 20) // Left Spikes
                {
                    if((bossXcor + horizontalPixelCounter) % Key.platformWidth < Key.spikesWidth)
                    {
                        return false;
                    }
                }
                else if(toBeChecked == 21) // Top Spikes
                {
                    if((bossYcor + verticalPixelCounter) % Key.platformHeight < Key.spikesHeight)
                    {
                        return false;
                    }
                }
                else if(toBeChecked == 22)// Right Spikes
                {
                    if((bossXcor + horizontalPixelCounter) % Key.platformWidth >  Key.platformWidth - Key.spikesWidth)
                    {
                        return false;
                    }
                }
            }
        }

        //checks if feet are planted on the ground
        toBeChecked = foreground[(bossYcor + hitBoxYcor + hitBoxHeight)/Key.platformHeight][(bossXcor + hitBoxXcor + hitBoxWidth/2)/Key.platformWidth];
        return toBeChecked != 0 && toBeChecked < 19;
    }

    void updateDisplayCoordinates(int cameraXcor)
    {
        displayXcor = absoluteXcor - cameraXcor;
        if(projectiles[0].isNotLastFrameShown())
        {
            projectiles[0].updateDisplayCoordinates(cameraXcor);
        }
    }
}