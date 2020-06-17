package com.skarta;

import java.awt.*;

public abstract class Enemy extends Character
{
    static final int walkRightCycle = 0;
    static final int walkLeftCycle = 1;

    String damageSoundFile;

    float volume;
    long damageMicroSecondPosition;

    int hitRightCycle;
    int cycle;
    int frame;
    
    int dx;
    int dy;
    int absoluteXcor;
    int hitBoxWidth;
    int hitBoxHeight;
    int hitBoxXcor;
    int hitBoxYcor;
    boolean die;
    boolean collisionChecked;
    
    int enemyNo;

    Enemy(int foregroundRow, int foregroundColumn, int enemyNo)
    {
        this.enemyNo = enemyNo;
        dy = 0;
        hitBoxWidth = 30;
        hitBoxHeight = 90;
        this.foregroundRow = foregroundRow;
        this.foregroundColumn = foregroundColumn;
        displayXcor = foregroundColumn * Key.platformWidth;
        displayYcor = (foregroundRow - 1) * Key.platformHeight;
        cycle = 0;
        frame = 0;
        die = false;
        absoluteXcor = foregroundColumn * Key.platformWidth;
    }

    abstract void updateDisplayCoordinates(int cameraXcor);
    abstract void updateCycle(int[][] foreground);

    void hit()
    {
        if (cycle < hitRightCycle)
        {
            cycle = hitRightCycle + cycle % 2;
            frame = 0;
            MusicPlayer.playSound(damageSoundFile, volume, damageMicroSecondPosition);
        }
    }

    void platformCollision(int[][] foreground)
    {
        int toBeChecked;
        int foregroundColumn;
        boolean rightCollision = false;
        if(dx>0)
        {
            foregroundColumn = (absoluteXcor + hitBoxXcor + hitBoxWidth) / Key.platformWidth; // The current column that the Character is colliding with
            for(int verticalPixelCounter = hitBoxYcor + hitBoxHeight / 3; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 2/3; verticalPixelCounter += 20) // Iterates Vertically to check different points of hitboxes
            {
                toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn]; // Platform Type
                if (toBeChecked != 0 && toBeChecked != 25 + enemyNo) // A platform or another enemy's foreground row and Column is placed there
                {
                    if(toBeChecked == 22) // Right Spikes
                    {
                        if((absoluteXcor + hitBoxXcor + hitBoxWidth) % Key.platformWidth > Key.platformWidth - Key.spikesWidth) // Checks Limit of Spikes
                        {
                            absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth) + (Key.platformWidth - Key.spikesWidth); // Moving Appropriate Limit of the character to the Appropriate limit of spikes
                            dx = - speed ; // Changing Direction of the Enemy's movement
                            cycle = walkLeftCycle; // Changing Direction of the Enemy's Image
                            rightCollision = true;
                            break;
                        }
                    }
                    else if(toBeChecked !=20) // if Platform colliding with is not Left Spikes
                    {
                        absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth); // Moving Appropriate Limit of the character to the Appropriate limit of Platforms
                        dx = - speed ; // Changing Direction of the Enemy's movement
                        cycle = walkLeftCycle; // Changing Direction of the Enemy's Image
                        rightCollision = true;
                        break;
                    }

                }
            }

            toBeChecked = foreground[(displayYcor + hitBoxHeight * 2 / 3 + hitBoxYcor) / Key.platformHeight + 1][foregroundColumn];
            if ((toBeChecked ==0 || toBeChecked >= 19)&& !rightCollision)
            {
                absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth);
                dx = - speed; // Changing Direction of the Enemy's movement
                cycle = walkLeftCycle; // Changing Direction of the Enemy's Image
            }
        }
        else
        {
            foregroundColumn = (absoluteXcor + hitBoxXcor) / Key.platformWidth;
            for(int verticalPixelCounter = hitBoxYcor + hitBoxHeight / 3; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 2/3; verticalPixelCounter += 20)
            {
                toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn];
                if (toBeChecked != 0 && toBeChecked != 25 + enemyNo)
                {
                    if(toBeChecked == 20) // Left Spikes
                    {
                        if((absoluteXcor + hitBoxXcor) % Key.platformWidth < Key.spikesWidth) // Checks Limit of Spikes
                        {
                            absoluteXcor = foregroundColumn * Key.platformWidth - hitBoxXcor + Key.spikesWidth; // Moving Appropriate Limit of the character to the Appropriate limit of spikes
                            dx = speed ; // Changing Direction of the Enemy's movement
                            cycle = walkRightCycle; // Changing Direction of the Enemy's Image
                            rightCollision = true;
                            break;
                        }
                    }
                    else if(toBeChecked != 22) // If not Right Spikes
                    {
                        absoluteXcor = (foregroundColumn + 1) * Key.platformWidth - (hitBoxXcor); // Moving Appropriate Limit of the character to the Appropriate limit of Platforms
                        dx = speed ; // Changing Direction of the Enemy's movement
                        cycle = walkRightCycle; // Changing Direction of the Enemy's Image
                        rightCollision = true;
                        break;
                    }
                }
            }
            toBeChecked = foreground[(displayYcor + hitBoxHeight * 2 / 3 + hitBoxYcor) / Key.platformHeight + 1][foregroundColumn]; // Checks Floor's Platform
            if ((toBeChecked ==0 || toBeChecked >= 19) && !rightCollision)
            {
                absoluteXcor = (foregroundColumn + 1) * Key.platformWidth - (hitBoxXcor);
                dx = speed; // Changing Direction of the Enemy's movement
                cycle = walkRightCycle; // Changing Direction of the Enemy's Image
            }
        }
    }

    boolean isColliding()
    {
        if(Player.currentCharacter == 0 && Player.specialAbility)
        {
            return ((absoluteXcor + hitBoxXcor >= Player.absoluteXcor - 60 && absoluteXcor + hitBoxXcor <= Player.absoluteXcor + Player.width + 60) ||  (absoluteXcor + hitBoxXcor + hitBoxWidth >= Player.absoluteXcor - 60 && absoluteXcor + hitBoxXcor + hitBoxWidth<= Player.absoluteXcor + Player.width + 60))&& ((Player.displayYcor + Player.hitBoxYcor >= displayYcor && Player.displayYcor + Player.hitBoxYcor <= displayYcor + hitBoxYcor + hitBoxHeight) || (Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 >= displayYcor && Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 <= displayYcor + hitBoxYcor + hitBoxHeight));
        }
        else
        {
            boolean returnValue = ((Player.absoluteXcor + Player.hitBoxXcor >= absoluteXcor + hitBoxXcor && Player.absoluteXcor + Player.hitBoxXcor <= absoluteXcor + hitBoxXcor + hitBoxWidth) || (Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth >= absoluteXcor + hitBoxXcor && Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth <= absoluteXcor + hitBoxXcor + hitBoxWidth)) && ((Player.displayYcor + Player.hitBoxYcor >= displayYcor + hitBoxYcor && Player.displayYcor + Player.hitBoxYcor <= displayYcor + hitBoxYcor + hitBoxHeight) || (Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 >= displayYcor + hitBoxYcor && Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 <= displayYcor + hitBoxYcor + hitBoxHeight));
            returnValue = returnValue || ((absoluteXcor + hitBoxXcor >= Player.absoluteXcor + Player.hitBoxXcor && absoluteXcor + hitBoxXcor <= Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth) ||  (absoluteXcor + hitBoxXcor + hitBoxWidth >= Player.absoluteXcor + Player.hitBoxXcor && absoluteXcor + hitBoxXcor + hitBoxWidth<= Player.absoluteXcor + Player.hitBoxXcor + Player.hitBoxWidth))&& ((displayYcor + hitBoxYcor >= Player.displayYcor + Player.hitBoxYcor && displayYcor + hitBoxYcor <= Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4) || (displayYcor + hitBoxYcor + hitBoxHeight >= Player.displayYcor + Player.hitBoxYcor && displayYcor + hitBoxYcor + hitBoxHeight <= Player.displayYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4));
            return returnValue;
        }
    }

    void collisionWithPlayer()
    {
        if(isColliding() && cycle < hitRightCycle)
        {
            boolean moveRight = Player.dx < 0 || (Player.dx == 0 && dx > 0);
            if(Player.currentCharacter == 0 && Player.specialAbility) // Ahsan is Attacking
            {
                hit();
            }
            else if(Player.currentCharacter == 1 && Player.specialAbility) // Sarosh has his shield equipped
            {
                Sarosh.shieldHit(false, moveRight, !moveRight, false);
            }
            else
            {
                Player.hit(moveRight, !moveRight, false);
            }
        }
    }

    void renderWithCheck(Graphics g) // Calls Render if Enemy is on Screen
    {
        if(displayXcor + hitBoxXcor + hitBoxWidth > 0 && displayXcor + hitBoxXcor < Key.screenWidth)
        {
            render(g);
        }
    }
}