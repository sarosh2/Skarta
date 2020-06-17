package com.skarta;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Projectile implements Animated
{
    private final int framesPerCycle;
    int cycleCount;
    private int xCor;
    private int yCor;
    private int absoluteXcor;
    private int cycle;
    private int frame;
    private int dx;
    private int dy;
    private int lastDx;
    private int lastDy;
    private int speed;
    private int hitBoxXcor;
    private int hitBoxYcor;
    private int hitBoxHeight;
    private int hitBoxWidth;
    private boolean tracker;
    private boolean destroyed;
    private final boolean explosive;
    private boolean lastFrameShown;
    private boolean hasExploded;

    public void setLastFrameShown(Boolean lastFrameShown)
    {
        this.lastFrameShown = lastFrameShown;
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }

    public boolean isNotLastFrameShown()
    {
        return !lastFrameShown;
    }



    static final int noCollisionChecks = 5;
    BufferedImage[][] spriteSheet;

    Projectile(String path, int width, int height, int cycle, int cycleCount, int framesPerCycle, boolean tracker, boolean explosive, int hitBoxYcor, int hitBoxWidth, int hitBoxHeight)
    {
        xCor = 1920;
        absoluteXcor = 1920;
        yCor = 1080;
        dx = 0;
        dy = 0;
        speed = 0;
        this.tracker = tracker;
        this.cycleCount = cycleCount;
        this.framesPerCycle = framesPerCycle;
        this.cycle = cycle;
        this.hitBoxHeight = hitBoxHeight;
        this.hitBoxWidth = hitBoxWidth;
        this.hitBoxYcor = hitBoxYcor;
        this.hitBoxXcor = (width - hitBoxWidth) / 2;
        frame = 0;
        destroyed = true;
        this.explosive = explosive;
        lastFrameShown = true;
        hasExploded = false;
        spriteSheet = HelperFunctions.makeSpritesheet(path, cycleCount, framesPerCycle, width, height);
    }

    Projectile(String path, int width, int height, int cycle, int cycleCount, int framesPerCycle, boolean tracker, boolean explosive, int hitBoxYcor, int hitBoxWidth, int hitBoxHeight, int hitBoxXcor)
    {
        this(path, width, height, cycle, cycleCount, framesPerCycle, tracker, explosive, hitBoxYcor, hitBoxWidth, hitBoxHeight);
        this.hitBoxXcor = hitBoxXcor;
    }

    @Override
    public void render(Graphics g)
    {
        HelperFunctions.drawImage(g, spriteSheet[cycle][frame / Key.numberOfFramesRepeatedPerCycle], xCor, yCor);
    }

    @Override
    public void tick()
    {

        if ((frame + 1)/ Key.numberOfFramesRepeatedPerCycle >= framesPerCycle)
        {
            frame = 0;

        }
        else
        {
            frame++;
        }

        if (explosive)
        {
            explode();
        }

        if (tracker)
        {
            track();
        }
        move();
    }

    void move()
    {
        absoluteXcor += dx;
        yCor += dy;
        lastDx = dx;
        lastDy = dy;
    }

    void launch(String soundFilePath, int absoluteXcor, int yCor, int dx, int dy)
    {
        this.absoluteXcor = absoluteXcor;
        this.yCor = yCor;
        this.dx = dx;
        this.dy = dy;
        speed = (int) Math.pow(dx * dx + dy * dy, 0.5);
        destroyed = false;
        lastFrameShown = false;
        hasExploded = false;
        if(explosive)
        {
            cycle = 0;
        }
        lastDx = 0;
        lastDy = 0;
        MusicPlayer.playSound(soundFilePath, -15, 0);
    }

    void track()
    {
        dx += (Player.displayXcor - xCor) / speed;
        dy += Key.gravity;
        int mod = (int)Math.pow(dx * dx + dy * dy, 0.5);
        dx *= (double) speed / mod;

    }

    void explode()
    {
        if(hasExploded && cycle !=1)
        {
            dx = 0;
            dy = 0;
            cycle = 1;
            frame = 0;
            setHitBox(50,50, 250, 250);
            tracker = false;
        }
        else if (cycle == 1 && (frame + 1)/ Key.numberOfFramesRepeatedPerCycle >= framesPerCycle)
        {
            tracker = true;
            destroyed = true;
        }
    }

    void setHitBox(int hitBoxXcor, int hitBoxYcor, int hitBoxWidth, int hitBoxHeight)
    {
        this.hitBoxXcor = hitBoxXcor;
        this.hitBoxYcor = hitBoxYcor;
        this.hitBoxHeight = hitBoxHeight;
        this.hitBoxWidth = hitBoxWidth;
    }

    void collide(int[][] foreground)
    {
        int[] playerXcorPath = new int[noCollisionChecks + 1];
        int[] playerYcorPath = new int[noCollisionChecks + 1];
        int[] projectileXcorPath = new int[noCollisionChecks + 1];
        int[] projectileYcorPath = new int[noCollisionChecks + 1];
        for(int i = 0; i <= noCollisionChecks; i++)
        {
            //Setting up Path taken by Projectile and Player
            Boss.updatePaths(noCollisionChecks, i, projectileXcorPath, absoluteXcor, lastDx, projectileYcorPath, yCor, lastDy, playerXcorPath, playerYcorPath);

            if(playerCollision(playerXcorPath[i], playerYcorPath[i], projectileXcorPath[i], projectileYcorPath[i]))
            {
                absoluteXcor = projectileXcorPath[i];
                yCor = projectileYcorPath[i];
                if(!explosive)
                {
                    destroyed = true;
                }
                else
                {
                    hasExploded = true;
                }
                break;
            }
            if(!hasExploded)
            {
                if (platformCollision(foreground, projectileXcorPath[i], projectileYcorPath[i]))
                {
                    absoluteXcor = projectileXcorPath[i];
                    yCor = projectileYcorPath[i];
                    if(!explosive)
                    {
                        destroyed = true;
                    }
                    else
                    {
                        hasExploded = true;
                    }
                    break;
                }
            }

        }


    }

    private boolean playerCollision(int playerXcor, int playerYcor, int projectileXcor, int projectileYcor)
    {
        if(isColliding(playerXcor, playerYcor, projectileXcor, projectileYcor))
        {
            boolean moveRight = dx > 0;
            if(Player.currentCharacter == 1 && Player.specialAbility) // Sarosh has his shield
            {
                Sarosh.shieldHit(false, moveRight, !moveRight, false);
            }
            else
            {
                Player.hit(moveRight, !moveRight, false);
            }
            return true;
        }
        return false;
    }

    private boolean isColliding(int playerXcor, int playerYcor, int projectileXcor, int projectileYcor)
    {
        boolean returnValue = ((playerXcor + Player.hitBoxXcor >= projectileXcor + hitBoxXcor && playerXcor + Player.hitBoxXcor <= projectileXcor + hitBoxXcor + hitBoxWidth) || (playerXcor + Player.hitBoxXcor + Player.hitBoxWidth >= projectileXcor + hitBoxXcor && playerXcor + Player.hitBoxXcor + Player.hitBoxWidth <= projectileXcor + hitBoxXcor + hitBoxWidth)) && ((playerYcor + Player.hitBoxYcor >= projectileYcor + hitBoxYcor && playerYcor + Player.hitBoxYcor <= projectileYcor + hitBoxYcor + hitBoxHeight) || (playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 >= projectileYcor + hitBoxYcor && playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 <= projectileYcor + hitBoxYcor + hitBoxHeight));
        returnValue = returnValue || ((projectileXcor + hitBoxXcor >= playerXcor + Player.hitBoxXcor && projectileXcor + hitBoxXcor <= playerXcor + Player.hitBoxXcor + Player.hitBoxWidth) ||  (projectileXcor + hitBoxXcor + hitBoxWidth >= playerXcor + Player.hitBoxXcor && projectileXcor + hitBoxXcor + hitBoxWidth<= playerXcor + Player.hitBoxXcor + Player.hitBoxWidth))&& ((projectileYcor + hitBoxYcor >= playerYcor + Player.hitBoxYcor && projectileYcor + hitBoxYcor <= playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4) || (projectileYcor + hitBoxYcor + hitBoxHeight >= playerYcor + Player.hitBoxYcor && projectileYcor + hitBoxYcor + hitBoxHeight <= playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4));
        return returnValue;
    }

    private boolean platformCollision(int[][] foreground, int projectileXcor, int projectileYcor)
    {
        int toBeChecked;
        if((projectileYcor + hitBoxYcor + hitBoxHeight >  Key.platformHeight * 20) || (projectileYcor + hitBoxYcor < Key.ceiling) || (projectileXcor + hitBoxXcor + hitBoxWidth >= Key.platformWidth * 60) || (projectileXcor + hitBoxXcor<= 0))
        {
            return true;
        }
        // Returns true if any part of the hitBox collides with a Platform
        for(int horizontalPixelCounter = hitBoxXcor; horizontalPixelCounter <= hitBoxXcor + hitBoxWidth; horizontalPixelCounter+= hitBoxWidth/2)
        {
            for(int verticalPixelCounter = hitBoxYcor; verticalPixelCounter <= hitBoxYcor + hitBoxHeight; verticalPixelCounter+= hitBoxHeight/2)
            {
                toBeChecked = foreground[(projectileYcor + verticalPixelCounter)/Key.platformHeight][(projectileXcor + horizontalPixelCounter)/Key.platformWidth]; // Platform Type
                if(toBeChecked != 0 && toBeChecked < 19) // Regular Platform
                {
                    return true;
                }
                else if(toBeChecked == 19) // Bottom Spikes
                {
                    if((projectileYcor + verticalPixelCounter) % Key.platformHeight > Key.platformHeight - Key.spikesHeight) // Special Limit Check for Spikes
                    {
                        return true;
                    }
                }
                else if(toBeChecked == 20) // Right Spikes
                {
                    if((projectileXcor + horizontalPixelCounter) % Key.platformWidth < Key.spikesWidth) // Special Limit Check for Spikes
                    {
                        return true;
                    }
                }
                else if(toBeChecked == 21) // Top Spikes
                {
                    if((projectileYcor + verticalPixelCounter) % Key.platformHeight < Key.spikesHeight) // Special Limit Check for Spikes
                    {
                        return true;
                    }
                }
                else if(toBeChecked == 22) // Left Spikes
                {
                    if((projectileXcor + horizontalPixelCounter) % Key.platformWidth >  Key.platformWidth - Key.spikesWidth) // Special Limit Check for Spikes
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void updateDisplayCoordinates(int cameraXcor)
    {
        xCor = absoluteXcor - cameraXcor;
    }
}