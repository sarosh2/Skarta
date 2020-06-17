package com.skarta;

/*
Player Cycles:
0 - walking rightward
1 - walking leftward
2 - jumping rightward
3 - jumping leftward
4 - standing facing right
5 - standing facing left
 */

import java.awt.*;

public abstract class Player extends Character
{
    int jumpPower;

    static final String jumpSoundFile = "jump.wav";
    static final String landSoundFile = "landing.wav";
    static final String switchSoundFile = "switch.wav";
    static final String damageSoundFile = "damage.wav";
    static final String deathSoundFile = "death.wav";

    static final int width = Key.characterWidth;
    static final int height = Key.characterHeight;

    static final int rightWalkCycle = 0;
    static final int leftWalkCycle = 1;
    static final int rightJumpCycle = 2;
    static final int leftJumpCycle = 3;
    static final int rightStandCycle = 4;
    static final int leftStandCycle = 5;
    static final int glitchFrames = 6;

    static final int framesPerCycle = Key.characterFramesPerCycle;
    static final int framesRepeated = Key.numberOfFramesRepeatedPerCycle;
    static final int glitchDurationInFrames = 6;


    static int startingXcor;
    static int startingYcor;

    static int displayXcor;
    static int displayYcor;
    static int absoluteXcor;
    static int foregroundRow;
    static int foregroundColumn;
    static int dx = 0;
    static int dy = 0;
    static int lastDy = 0;
    static int lastDx = 0;
    static int effectiveDx = 0;
    static int effectiveDy = 0;

    static int currentCharacter = 0;
    static int cycle = rightStandCycle;
    static int frame = 0;
    static int specialFrameCount = 0;
    static int life = Key.playerMaxLife;
    static long damageStart;

    static int hitBoxXcor;
    static int hitBoxYcor;
    static int hitBoxWidth;
    static int hitBoxHeight;


    static boolean specialAbility = false;
    static boolean isGlitching = false;
    static boolean onGround = false;
    static boolean right = false;
    static boolean left = false;
    static boolean up = false;
    static boolean land = false;
    static boolean switchWithA = false;
    static boolean switchWithD = false;
    static boolean dyLock = false;
    static boolean switchLock = false;
    static boolean hurt = false;
    static boolean renderLock = false;
    static boolean collisionChecked = false;
    static boolean switchLeft = false;
    static boolean cycleUpdated = false;

    abstract void specialRender(Graphics g);
    abstract void specialTick();
    abstract void specialUpdateCycle();
    abstract void resetCycles();

    @Override
    public void render(Graphics g)
    {
        if (!renderLock)
        {
            HelperFunctions.drawImage(g, spritesheet[cycle][frame / framesRepeated], displayXcor, displayYcor);
        }

        if (isGlitching)
        {
            HelperFunctions.drawImage(g, spritesheet[glitchFrames][cycle % 2], displayXcor, displayYcor);
        }
        specialRender(g);
    }

    @Override
    public void tick()
    {
        frame++;

        if (frame == 15 && (cycle == rightJumpCycle || cycle == leftJumpCycle))
        {
            frame = 14;
        }
        else if (frame / framesRepeated >= framesPerCycle)
        {
            frame = 0;
        }

        if (hurt)
        {
            renderLock = !renderLock; // switches in rendering to give blinking effect
            if (System.currentTimeMillis() - damageStart > 1000)
            {
                hurt = false;
                renderLock = false;
            }
        }

        if (isGlitching)
        {
            specialFrameCount++;

            if (specialFrameCount == 1)
            {
                switchLock = true;
                MusicPlayer.playSound(switchSoundFile, -15, 0);
            }
            else if (specialFrameCount == glitchDurationInFrames / 2)
            {
                if (switchLeft)
                {
                    currentCharacter = (currentCharacter + 2) % 3;
                }
                else
                {
                    currentCharacter = (currentCharacter + 1) % 3;
                }
            }
            else if (specialFrameCount == glitchDurationInFrames)
            {
                specialFrameCount = 0;
                isGlitching = false;
                switchWithA = false;
                switchWithD = false;
                switchLock = false;
            }
        }
        else
        {
            specialTick();
        }
        move();
    }

    @Override
    void move()
    {
        absoluteXcor += dx;
        lastDy = 0;
        if (!dyLock)
        {
            displayYcor += dy;
            lastDy = dy;
        }

        lastDx = dx;

        if (!onGround && !Tayyab.wallStick)
        {
            dy += Key.gravity;
        }
        collisionChecked = false;
    }

    void updateCycle()
    {
        if (land)
        {
            MusicPlayer.playSound(landSoundFile, -12, 0);
            onGround = true;
            Tayyab.lastWall = -1;
            Sarosh.doubleJumpLock = false;
            dy = 0;
            if (!Ahsan.getIsSlime())
            {
                if (dx == 0)
                {
                    cycle = 4 + (cycle % 2);
                }
                else
                {
                    cycle = cycle % 2;
                }
            }
            land = false;
        }

        specialUpdateCycle();

        if (up && !Ahsan.getIsSlime())
        {
            if (onGround || Tayyab.wallStick)
            {
                MusicPlayer.playSound(jumpSoundFile, -15, 0);
                dy = -jumpPower;
                onGround = false;
                Tayyab.wallStick = false;
                cycle = rightJumpCycle + (cycle % 2);
                frame = 0;
                up = false;
            }

        }

        if ((switchWithA || switchWithD) && !switchLock && Ahsan.getCanSwitch())
        {
            isGlitching = true;
            specialFrameCount = 0;
            resetCycles();
            switchLeft = switchWithA;
        }

        if((left && right) || (!left && !right))
        {
            Tayyab.wallStickInputDelayed = 0;
            dx = 0;
            if (onGround && Ahsan.getDownInverted() && !Ahsan.getIsSlime())
            {
                cycle = rightStandCycle + (cycle % 2);
            }
        }
        else if(right)
        {
            dx = speed;
            if (onGround && Ahsan.getDownInverted() && !Ahsan.getIsSlime())
            {
                cycle = rightWalkCycle;
            }
            else if (cycle <= leftStandCycle && cycle % 2 == 1 || (this instanceof Ahsan && cycle == Ahsan.slimeCycle + 1))
            {
                cycle--;
            }
            else if (cycle == Tayyab.wallStickLeftCycle && Tayyab.wallStick)
            {
                if(Tayyab.wallStickInputDelayed >= Key.timesInputDelayed)
                {
                    Tayyab.wallStick = false;
                    Tayyab.wallStickInputDelayed = 0;
                }
                else
                {
                    Tayyab.wallStickInputDelayed++;
                    dx = 0;
                }
            }
        }
        else
        {
            dx = -speed;
            if (onGround && Ahsan.getDownInverted() && !Ahsan.getIsSlime())
            {
                cycle = leftWalkCycle;
            }
            else if(cycle <= leftStandCycle && cycle % 2 == 0 || (this instanceof Ahsan && cycle == Ahsan.slimeCycle))
            {
                cycle++;
            }
            else if (cycle == Tayyab.wallStickRightCycle && Tayyab.wallStick)
            {
                if(Tayyab.wallStickInputDelayed >= Key.timesInputDelayed)
                {
                    Tayyab.wallStick = false;
                    Tayyab.wallStickInputDelayed = 0;
                }
                else
                {
                    Tayyab.wallStickInputDelayed++;
                    dx = 0;
                }
            }
        }
        cycleUpdated = true;
    }

    @Override
    void collide(int[][] foreground, int cameraXcor)
    {
        if (!collisionChecked) // Only checks Collision if it had not already been checked after moving
        {
            effectiveDx = lastDx; // Initialises effectiveDx by lastDx moved by the Player.
            if (absoluteXcor < Key.leftWall) // Check so that Player cant go back
            {
                absoluteXcor = Key.leftWall;
                effectiveDx = 0;
            }
            if (displayYcor  + hitBoxYcor < Key.ceiling) // Check so that Player cant go above screen
            {
                displayYcor = Key.ceiling - hitBoxYcor;
                dy = 0;
            }
            if (displayYcor > Key.floor) // Check so that Player cant go below screen
            {
                displayYcor = Key.floor;
            }

            if (lastDy > 5 || lastDy < - 20)  // Checks if Vertical Collisions should be given priority
            {
                downwardCollision(foreground);
                upwardCollision(foreground);
                horizontalCollision(foreground);
            }
            else
            {
                horizontalCollision(foreground);
                downwardCollision(foreground);
                upwardCollision(foreground);
            }

            if (this instanceof Ahsan)
            {
                Ahsan.specialCollisionChecks(foreground); // Calls Ahsan's slimeCollision checks
            }
            else if(Sarosh.bounce) // if Sarosh had bounced from Spikes due to his shield being broken
            {
                // Double Collision Checks Occur as regular collision can't deal with such high movement speed
                displayYcor -= Sarosh.shieldBounce / 2;
                lastDy = - Sarosh.shieldBounce / 2;
                Sarosh.bounce = false;
                collide(foreground, cameraXcor);
            }

            if(absoluteXcor + hitBoxXcor + hitBoxWidth > Key.rightWall && Key.bossFight) // Checks so that Player can't move out of the screen in boss fights
            {
                effectiveDx -= absoluteXcor - (Key.rightWall - (hitBoxXcor + hitBoxWidth));
                absoluteXcor = Key.rightWall - (hitBoxXcor + hitBoxWidth);
            }
            updateDisplayCoordinates(cameraXcor);
            cycleUpdated = false; //As Collision has been checked again cycle needs to be updated again
        }
    }

    private void downwardCollision(int[][] foreground)
    {
        int foregroundRow; // The current row that is being checked
        int toBeChecked; // The Platform type that is being currently Checked
        boolean downwardCollision = false;
        //downward collision

        if(lastDy >= 0) // If falling Down, or Staying still vertically
        {
            if(this instanceof Sarosh) //Special Hitbox Changes for Sarosh: His Feet are a little mis-alligned in the Sprites
            {
                if(cycle % 2 == 0)
                {
                    hitBoxXcor -= Sarosh.hitBoxChange;
                }
                else
                {
                    hitBoxXcor += Sarosh.hitBoxChange;
                }
            }
            foregroundRow = (displayYcor + hitBoxYcor + hitBoxHeight - 5) / Key.platformHeight; // The Row at the Player's feet are to be checked
            toBeChecked = foreground[foregroundRow][(absoluteXcor + hitBoxXcor + hitBoxWidth / 2) / Key.platformWidth];
            if (toBeChecked != 0 && toBeChecked < 19) { //If there is a normal Platform at bottom limit
                onGround = true; // Player must be on ground as his feet are embedded in a Platform
                if(lastDy != 0) // If player was Moving down
                {
                    land = true; // Then he should Land: (land also has a sound Effect)
                }
                downwardCollision = true; //As Collision has been detected
                effectiveDy = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5) - (displayYcor - lastDy); // This calculates the dy that the player has effectively moved vertically knowing his last Position.
                displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5) ;// Moving the lower Limit of the Player to the upper Limit of the Platform

            }
            if(this instanceof Sarosh)
            {
                if(cycle % 2 == 0) // Resetting Sarosh's Hitbox
                {
                    hitBoxXcor += Sarosh.hitBoxChange;
                }
                else
                {
                    hitBoxXcor -= Sarosh.hitBoxChange;
                }
            }
            if (downwardCollision)
            {
                foregroundRow--; // Checking the Platform right above the Platform it had collided with
                toBeChecked = foreground[foregroundRow][(absoluteXcor + hitBoxXcor + hitBoxWidth / 2) / Key.platformWidth];
                if(toBeChecked == 19)//Bottom Spikes. This can only happen if the dy was greater than Key.spikesHeight which is 35.
                {
                    dy = 0; // Shouldn't move down
                    if(!Ahsan.getIsSlime())
                    {
                        land = false; //landing should be disabled if Player wasn't a slime(immune to spikes)
                    }
                    effectiveDy -= Key.spikesHeight; // changing the effectiveDy as Player didn't move below Spikes
                    displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5) + (Key.platformHeight - Key.spikesHeight); // Moving bottom limit of Character to upper Limit of Platform
                    checkHit(false, false, true); // Checking if player should be hurt from spikes. If he is hurt he is movedUp
                }
            }
            else if(toBeChecked == 19) //Bottom Spikes(Spikes attached to the top of a Platform)
            {
                if((displayYcor + hitBoxYcor + hitBoxHeight - 5) % Key.platformHeight >= Key.platformHeight - Key.spikesHeight) // checking if Bottom limit of character is greater than Upper limit of spikes
                {
                    onGround = true;
                    dy = 0; // Shouldn't move down
                    downwardCollision = true;
                    effectiveDy = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight -5) + (Key.platformHeight - Key.spikesHeight) - (displayYcor - lastDy);// Calculating how much the character has moved vertically in comparison to last frame
                    displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight -5) + (Key.platformHeight - Key.spikesHeight);// Moving bottom limit of Character to upper Limit of Spikes

                    checkHit(false, false, true); // Checking if player should be hurt from spikes. If he is hurt he is movedUp

                }
            }
            else if(toBeChecked == 20) // Left Spikes(Spikes attached to a Left Wall)
            {
                if((absoluteXcor +  hitBoxXcor + 2) %  Key.platformWidth < (absoluteXcor +  hitBoxXcor + hitBoxWidth / 2) %  Key.platformWidth)// + 2 Jugaari Pixels. Checks if Platform is the same for left limit and middle limit
                {
                    if((absoluteXcor +  hitBoxXcor + 2) %  Key.platformWidth < Key.spikesWidth) // Checks if Left Limit of Character is smaller than right limit of Spikes
                    {
                        if(hurt && Tayyab.wallStick) // Only a sticking and hurt Tayyab should be moved up
                        {
                            effectiveDy = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5) - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                            displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5); // Moving bottom limit of Character to upper Limit of Spikes
                        }


                        checkHit(false, false, true); // Checking if player should be hurt from spikes. If he is hurt he is movedUp

                    }
                }
            }
            else if(toBeChecked == 22) // Right Spikes(Spikes attached to a right Wall)
            {
                if((absoluteXcor +  hitBoxXcor + hitBoxWidth - 2) %  Key.platformWidth > (absoluteXcor +  hitBoxXcor + hitBoxWidth / 2) %  Key.platformWidth)// - 2 Jugaari Pixels. Checks if Platform is the same for right limit and middle limit of character
                {
                    if((absoluteXcor +  hitBoxXcor + hitBoxWidth - 2) %  Key.platformWidth > Key.platformWidth - Key.spikesWidth) // Checks if Right Limit of Character is greater than Left limit of Spikes
                    {
                        if(hurt && Tayyab.wallStick) // Only a sticking and hurt Tayyab should be moved up
                        {
                            effectiveDy = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5) - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                            displayYcor = foregroundRow * Key.platformHeight - (hitBoxYcor + hitBoxHeight - 5); // Moving bottom limit of Character to upper Limit of Spikes
                        }

                        checkHit(false, false, true); // Checking if player should be hurt from spikes. If he is hurt he is movedUp

                    }
                }
            }

        }
        if(!downwardCollision) // In case there is no ground detected Below
        {
            onGround = false;
            if(!Tayyab.wallStick && !((Player.currentCharacter == 0 || Player.currentCharacter == 2) && Player.specialAbility) && !Ahsan.getIsSlime() && cycle< glitchFrames) //If no Special Cycles are being triggered.
            {
                // Jump Cycle is triggered
                if(cycle % 2 == 0) // All rightward Frames are in cycles of Multiple 2
                {
                    cycle = rightJumpCycle;
                }
                else
                {
                    cycle = leftJumpCycle;
                }
            }
        }
    }

    private static void upwardCollision(int[][] foreground)
    {
        //upward collision
        if(lastDy<=0)
        {
            int foregroundRow = (displayYcor + hitBoxYcor) / Key.platformHeight; // The row that is to be checked
            int toBeChecked = foreground[foregroundRow][(absoluteXcor + hitBoxXcor + hitBoxWidth / 2) / Key.platformWidth]; // The Platform type that is to be checked
            if (toBeChecked != 0 && toBeChecked < 19) // If regular Platform at top limit
            {
                dy = 0; // Resetting Vertical Speed
                effectiveDy = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                displayYcor = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor ;// Moving Top Limit of Character Below Bottom Limit of Platform

            }
            else if(toBeChecked == 21) //Top Spikes(Spikes attached to the bottom of a Platform)
            {
                if((displayYcor + hitBoxYcor) % Key.platformHeight < Key.spikesHeight) { //If Top Limit of Character is above bottom limit of spikes
                    dy = 0; // Resetting Vertical Speed
                    effectiveDy = foregroundRow * Key.platformHeight - hitBoxYcor + Key.spikesHeight - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                    displayYcor = foregroundRow * Key.platformHeight - hitBoxYcor + Key.spikesHeight; // Moving Top Limit of Character Below Bottom Limit of Spikes

                    checkHit(false, false, false); //Checking if player should be hurt from spikes.

                }
            }
            else if(toBeChecked == 20) // Left Spikes(Spikes attached to a Left Wall)
            {
                if((absoluteXcor + hitBoxXcor + 2) %  Key.platformWidth < (absoluteXcor + hitBoxXcor + hitBoxWidth / 2) %  Key.platformWidth)// - 2 Jugaari Pixels. Checks if Platform is the same for left limit and middle limit of character
                {
                    if((absoluteXcor + hitBoxXcor + 2) %  Key.platformWidth < Key.spikesWidth) // If left Limit of Character is smaller than right limit of spikes
                    {
                        dy = 0; // Resetting Vertical Speed
                        effectiveDy = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                        displayYcor = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor;// Moving Top Limit of Character Below Bottom Limit of Spikes

                        checkHit(false, false, false); //Checking if player should be hurt from spikes.

                    }
                }
            }
            else if(toBeChecked == 22) // Right Spikes(Spikes attached to a right Wall)
            {
                if((absoluteXcor + hitBoxXcor + hitBoxWidth - 2) %  Key.platformWidth > (absoluteXcor + hitBoxXcor + hitBoxWidth / 2) %  Key.platformWidth)// - 2 Jugaari Pixels. Checks if Platform is the same for right limit and middle limit of character
                {
                    if((absoluteXcor + hitBoxXcor + hitBoxWidth - 2) %  Key.platformWidth > Key.platformWidth - Key.spikesWidth) // If right Limit of Character is greater than left limit of spikes
                    {
                        dy = 0;
                        effectiveDy = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor - (displayYcor - lastDy); // Calculating how much the character has moved vertically in comparison to last frame
                        displayYcor = (foregroundRow + 1) * Key.platformHeight - hitBoxYcor ; // Moving Top Limit of Character Below Bottom Limit of Spikes

                        checkHit(false, false, false); //Checking if player should be hurt from spikes.

                    }
                }
            }

        }
    }

    private static void horizontalCollision(int[][] foreground)
    {
        int toBeChecked;
        boolean rightwardCollision = false;
        //rightward collision
        int foregroundColumn;
        if (lastDx >=0) // If moving towards right or not moving horizontally
        {
            for (int verticalPixelCounter = hitBoxYcor + hitBoxHeight / 6; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 5/6; verticalPixelCounter += 10) // Checks different Parts of hit Box. Moving down Vertically
            {
                foregroundColumn = (absoluteXcor + hitBoxXcor + hitBoxWidth) / Key.platformWidth; // the column that is to be checked
                toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn]; // The Platform type that is being checked
                if (toBeChecked != 0 && toBeChecked < 19) // Regular Platform detected at right Limit of Player
                {
                    rightwardCollision = tayyabCollisionChecks(toBeChecked, foregroundColumn, verticalPixelCounter);
                    effectiveDx = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth) - (absoluteXcor - lastDx);  // Calculating how much the character has moved horizontally in comparison to last frame
                    absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth); // Moving the Character's right limit to the Platforms Left Limit.
                }
                if(rightwardCollision)
                {
                    foregroundColumn--; // Checks the column to the left of the Previous Coloumn
                    toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn]; // The new Platform Type to be checked
                    if(toBeChecked == 22) // Right Spikes(Spikes attached to a right Wall)
                    {
                        effectiveDx -= Key.spikesWidth; // Calculating how much the character has moved horizontally in comparison to last frame
                        absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth) + (Key.platformWidth - Key.spikesWidth); // Moving the Character's right limit to the Spike's Left Limit.
                        Tayyab.wallLand = false; // Tayyab shouldn't wall Land now that he is hurt with spikes
                        checkHit(false, true, false); //Checking if player should be hurt from spikes. Is moved Left if Hurt
                        break;
                    }
                }
                else
                {
                    if(toBeChecked == 22) // Right Spikes(Spikes attached to a right Wall)
                    {
                        if((absoluteXcor + hitBoxXcor + hitBoxWidth) % Key.platformWidth > Key.platformWidth - Key.spikesWidth)
                        {
                            effectiveDx = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth) + (Key.platformWidth - Key.spikesWidth) - (absoluteXcor - lastDx); // Calculating how much the character has moved horizontally in comparison to last frame
                            absoluteXcor = foregroundColumn * Key.platformWidth - (hitBoxXcor + hitBoxWidth) + (Key.platformWidth - Key.spikesWidth); // Moving the Character's right limit to the Spike's Left Limit.
                            dx = 0; // Resetting Horizontal Speed

                            checkHit(false, true, false); //Checking if player should be hurt from spikes. Is moved Left if Hurt

                            break;
                        }
                    }
                    else if(toBeChecked == 19) // Bottom Spikes(Spikes attached to the top of a Platform)
                    {
                        if((displayYcor + verticalPixelCounter) % Key.platformHeight > Key.platformHeight - Key.spikesHeight) // checking if Bottom limit of character is greater than Upper limit of spikes
                        {
                            ahsanSlimeSpikeCheck(foregroundColumn, 0);
                            break;
                        }
                    }
                    else if(toBeChecked == 21) // Top Spikes(Spikes attached to the bottom of a Platform)
                    {
                        if((displayYcor + verticalPixelCounter) % Key.platformHeight < Key.spikesHeight) // checking if top limit of character is above the bottom limit of spikes
                        {
                            ahsanSlimeSpikeCheck(foregroundColumn, 0);
                        }
                    }
                }

                if(rightwardCollision)
                {
                    break;
                }
            }
        }

        boolean leftwardCollision = false;
        //leftward collision
        if (lastDx <=0 && !rightwardCollision)
        {
            for (int verticalPixelCounter = hitBoxYcor + hitBoxHeight / 6; verticalPixelCounter <= hitBoxYcor + hitBoxHeight * 5/6; verticalPixelCounter += 10) // Checks different Parts of hit Box. Moving down Vertically
            {
                foregroundColumn = (absoluteXcor + hitBoxXcor) / Key.platformWidth; // The column that is to be checked
                toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn];// The Platform Type to be checked
                if (toBeChecked != 0 && toBeChecked < 19) // Regular Platform detected at left Limit of Player
                {
                    leftwardCollision = tayyabCollisionChecks(toBeChecked, foregroundColumn, verticalPixelCounter);
                    effectiveDx = (foregroundColumn + 1) * Key.platformWidth - hitBoxXcor - 1 - (absoluteXcor - lastDx); // Calculating how much the character has moved horizontally in comparison to last frame
                    absoluteXcor = (foregroundColumn + 1) * Key.platformWidth - hitBoxXcor - 1; // Moving Character's left Limit to the Platforms Right Limit.
                }
                if(leftwardCollision)
                {
                    foregroundColumn++; // Checking Platform to the Right
                    toBeChecked = foreground[(displayYcor + verticalPixelCounter) / Key.platformHeight][foregroundColumn]; // The new Platform Type
                    if(toBeChecked == 20) // Left Spikes(Spikes attached to a Left Wall)
                    {
                        effectiveDx += Key.spikesWidth; // Calculating how much the character has moved horizontally in comparison to last frame
                        absoluteXcor = foregroundColumn * Key.platformWidth - hitBoxXcor + Key.spikesWidth; // Moving Character's left Limit to the Spikes Right Limit.
                        Tayyab.wallLand = false;  // Tayyab shouldn't wall Land now that he is hurt with spikes
                        checkHit(true, false, false); //Checking if player should be hurt from spikes. Is moved Right if Hurt
                        break;

                    }
                }
                else
                {
                    if(toBeChecked == 20) // Left Spikes(Spikes attached to a Left Wall)
                    {
                        if((absoluteXcor + hitBoxXcor) % Key.platformWidth < Key.spikesWidth)
                        {
                            effectiveDx = foregroundColumn * Key.platformWidth - hitBoxXcor + Key.spikesWidth - (absoluteXcor - lastDx); // Calculating how much the character has moved horizontally in comparison to last frame
                            absoluteXcor = foregroundColumn * Key.platformWidth - hitBoxXcor + Key.spikesWidth; // Moving Character's left Limit to the Spikes Right Limit.
                            dx = 0; // Resetting horizontal Speed

                            checkHit(true, false, false); //Checking if player should be hurt from spikes. Is moved Right if Hurt

                            break;
                        }
                    }
                    else if(toBeChecked == 19) // Bottom Spikes(Spikes attached to the top of a Platform)
                    {
                        if((displayYcor + verticalPixelCounter) % Key.platformHeight > Key.platformHeight - Key.spikesHeight) // checking if bottom limit of character is below the top limit of spikes
                        {
                            ahsanSlimeSpikeCheck(foregroundColumn, 1);
                        }
                    }
                    else if(toBeChecked == 21) // Top Spikes(Spikes attached to the bottom of a Platform)
                    {
                        if((displayYcor + verticalPixelCounter) % Key.platformHeight < Key.spikesHeight) // checking if top limit of character is above the bottom limit of spikes
                        {
                            ahsanSlimeSpikeCheck(foregroundColumn, 1);
                            break;
                        }
                    }
                }

                if(leftwardCollision)
                {
                    break;
                }
            }
        }
        if(!leftwardCollision && !rightwardCollision) //if there was no leftward or rightward Collision, then Tayyab should not stick to a wall
        {
            Tayyab.wallStick = false;
        }
    }

    private static void ahsanSlimeSpikeCheck(int foregroundColumn, int columnAddition) {
        if(Ahsan.getIsSlime() || hurt) //Player to be moved only if Immune to spikes
        {
            effectiveDx = (foregroundColumn + columnAddition) * Key.platformWidth - (hitBoxXcor + hitBoxWidth) - (absoluteXcor - lastDx); // Calculating how much the character has moved horizontally in comparison to last frame
            absoluteXcor = (foregroundColumn + columnAddition) * Key.platformWidth - (hitBoxXcor + hitBoxWidth); // Moving the Character's right limit to the Spike's Left Limit.
            dx = 0; // Resetting Horizontal Speed
        }

        checkHit(false, true, false); //Checking if player should be hurt from spikes. Is moved Left if Hurt
    }

    private static boolean tayyabCollisionChecks(int toBeChecked, int foregroundColumn, int verticalPixelCounter) {
        if (verticalPixelCounter < hitBoxYcor + hitBoxHeight /4 && lastDx!=0 && currentCharacter == 2 && !onGround && !Tayyab.wallStick && (toBeChecked == 4|| toBeChecked == 6||toBeChecked == 9||toBeChecked == 10||toBeChecked == 11||toBeChecked == 12))
        {
            /*                                        One Condition per line is being explained
            If Player's arms have collided. "hitBoxYcor + hitBoxHeight /4" is the approximates location of a player's Upper Arms
            If the Player was actually moving to the right.
            if the current character is Tayyab.
            if the Player is not on the ground.
            if the player is not already sticking to a wall
            if the Platform is among the stickable Platforms
             */
            Tayyab.wallLand = true; //Tayyab should land on the wall
            Tayyab.currentWall = foregroundColumn; // the column of the wall Tayyab is landing on
        }
        dx = 0; // Resetting Horizontal Speed
        return true;
    }

    private static void checkHit(boolean moveRight, boolean moveLeft, Boolean moveUp)
    {
        if(!Ahsan.getIsSlime())
        {
            if(currentCharacter == 1 && specialAbility)
            {
                Sarosh.shieldHit(true,moveRight, moveLeft, moveUp);
            }
            else
            {
                hit(moveRight, moveLeft, moveUp);
            }
        }
    }

    static void reset(int cameraXcor)
    {
        displayXcor = startingXcor;
        absoluteXcor = displayXcor + cameraXcor;
        displayYcor = startingYcor;
        foregroundRow = displayYcor / Key.platformHeight;
        foregroundColumn = absoluteXcor / Key.platformWidth;
        dx = 0;
        dy = 0;
        currentCharacter = 0;
        frame = 0;
        specialFrameCount = 0;

        specialAbility = false;
        isGlitching = false;
        onGround = false;
        right = false;
        left = false;
        up = false;
        land = false;
        switchWithA = false;
        switchWithD = false;
        dyLock = false;
        switchLock = false;
        hurt = false;
        renderLock = false;
        Tayyab.wallStick = false;
        Sarosh.shieldLife = 3;
        cycle = rightStandCycle;
        Ahsan.setSlime();
        Ahsan.setDown(false);
        HUD.reset();
    }

    static void hit(Boolean moveRight, Boolean moveLeft, Boolean moveUp)
    {
        if (life > 0)
        {

            if(!hurt && !Sarosh.shieldImmune)
            {
                life--;
                if (moveRight)
                {
                    absoluteXcor += 40;
                    lastDx = 40;
                }
                else if (moveLeft)
                {
                    absoluteXcor -= 40;
                    lastDx = -40;
                }
                else if (moveUp)
                {
                    displayYcor -= 40;
                    lastDy = - 40;
                }
                collisionChecked = false;
                hurt = true;
                damageStart = System.currentTimeMillis();
                MusicPlayer.playSound(damageSoundFile, -15, 0);
            }
        }
    }

    static void updateDisplayCoordinates(int cameraXcor)
    {
        displayXcor = absoluteXcor - cameraXcor;
    }
}