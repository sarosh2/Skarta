package com.skarta;

import java.awt.image.BufferedImage;

public abstract class Boss extends Character
{
    int hitBoxXcor;
    int hitBoxYcor;
    int hitBoxWidth;
    int hitBoxHeight;

    int dx;
    int dy;
    int absoluteXcor;

    String damageSoundFile;
    String deathSoundFile;

    int[] playerXcorPath = new int[11];
    int[] playerYcorPath = new int[11];
    int[] bossXcorPath = new int[11];
    int[] bossYcorPath = new int[11];

    Projectile[] projectiles;
    BufferedImage[][] specialFeature;
    static int life;
    int cycle;
    int frame;
    long damageMicroSecondPosition;
    long deathMicroSecondPosition;
    long damageStart;
    float volume;
    boolean collisionChecked;
    boolean isSpecialFeature;
    boolean renderLock;

    Handler handler;

    public Boss(int foregroundColumn , Handler handler)
    {
        this.handler = handler;
        this.foregroundColumn = foregroundColumn;
        absoluteXcor = foregroundColumn * Key.platformWidth;
        cycle = 1;
        frame = 0;
        dx = 0;
        dy = 0;
        damageStart = 0;
        renderLock = false;
        isSpecialFeature = false;
    }

    void hit(int[][] foreground)
    {
        if (System.currentTimeMillis() - damageStart > 1000)
        {
            if (life > 1)
            {
                life--;
                renderLock = true;
                MusicPlayer.playSound(damageSoundFile, volume, damageMicroSecondPosition);
                damageStart = System.currentTimeMillis();
            }
            else
            {
                MusicPlayer.playSound(deathSoundFile, volume, deathMicroSecondPosition);
                foreground[foregroundRow][foregroundColumn] = 0;
                if(handler.getCurrentLevel() != 7)
                {
                    Key.bossFight = false;
                    MusicPlayer.reset();
                }
                else if (this instanceof Mutahar)
                {
                    handler.setCreditsInterval(System.currentTimeMillis());
                    Key.bossFight = false;
                    MusicPlayer.reset();
                }
                else if (this instanceof Talha)
                {
                    handler.bosses[2] = new Jahangir(foregroundRow, foregroundColumn + 1, handler);
                    handler.foreground[foregroundRow][foregroundColumn + 1] = 1227;
                    handler.bosses[2].damageStart = System.currentTimeMillis() - 500;
                    handler.bosses[2].renderLock = true;
                }
                else  // Jahangir Baba Hae
                {
                    handler.bosses[1] = new Mutahar(foregroundRow - 2, foregroundColumn, handler);
                    handler.foreground[foregroundRow - 2][foregroundColumn] = 1226;
                    handler.bosses[1].damageStart = System.currentTimeMillis() - 500;
                    handler.bosses[1].renderLock = true;
                }


            }
        }
    }

    abstract void update();
    abstract void change();

    void tickChecks()
    {
        move();

        for (Projectile projectile: projectiles)
        {
            if (!projectile.isDestroyed())
            {
                projectile.tick();
            }
        }

        if (System.currentTimeMillis() - damageStart <= 1000)
        {
            renderLock = !renderLock;
        }
        else
        {
            renderLock = false;
        }
    }

    void updatePaths(int noCollisionChecks, int updateNo) // Calculates where Boss and Player was after (updateNo/noCollisionChecks) frames.
    {
        updatePaths(noCollisionChecks, updateNo, bossXcorPath, absoluteXcor, dx, bossYcorPath, displayYcor, dy, playerXcorPath, playerYcorPath);
    }

    static void updatePaths(int noCollisionChecks, int updateNo, int[] bossXcorPath, int absoluteXcor, int dx, int[] bossYcorPath, int displayYcor, int dy, int[] playerXcorPath, int[] playerYcorPath) {
        bossXcorPath[updateNo] = absoluteXcor - (int)(dx * (noCollisionChecks - updateNo)/(noCollisionChecks * 1.0));
        bossYcorPath[updateNo] = displayYcor - (int)(dy * (noCollisionChecks - updateNo)/(noCollisionChecks * 1.0));
        playerXcorPath[updateNo] = Player.absoluteXcor - Player.effectiveDx + (int)(Player.lastDx * updateNo/(noCollisionChecks * 1.0));
        if(Player.lastDx > 0)
        {
            if(playerXcorPath[updateNo] > Player.absoluteXcor)
            {
                playerXcorPath[updateNo] = Player.absoluteXcor;
            }
        }
        else
        {
            if(playerXcorPath[updateNo] < Player.absoluteXcor)
            {
                playerXcorPath[updateNo] = Player.absoluteXcor;
            }
        }

        playerYcorPath[updateNo] = Player.displayYcor - Player.effectiveDy + (int)(Player.lastDy * updateNo/(noCollisionChecks * 1.0));

        if(Player.lastDy> 0)
        {
            if(playerYcorPath[updateNo] > Player.displayYcor)
            {
                playerYcorPath[updateNo] = Player.displayYcor;
            }
        }
        else
        {
            if(playerYcorPath[updateNo] < Player.displayYcor)
            {
                playerYcorPath[updateNo] = Player.displayYcor;
            }
        }
    }


    boolean isColliding(int playerXcor, int playerYcor, int bossXcor, int bossYcor)
    {
        if(Player.currentCharacter == 0 && Player.specialAbility && cycle <= 1 && !isSpecialFeature) //Boss is Vulnerable and Ahsan is attacking
        {
            return ((bossXcor + hitBoxXcor >= playerXcor - 60 && bossXcor + hitBoxXcor <= playerXcor + Player.width + 60) ||  (bossXcor + hitBoxXcor + hitBoxWidth >= playerXcor - 60 && bossXcor + hitBoxXcor + hitBoxWidth<= playerXcor + Player.width + 60))&& ((playerYcor + Player.hitBoxYcor >= bossYcor && playerYcor + Player.hitBoxYcor <= bossYcor + hitBoxYcor + hitBoxHeight) || (playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 >= bossYcor && playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 <= bossYcor + hitBoxYcor + hitBoxHeight));
        }
        else
        {
            boolean returnValue = ((playerXcor + Player.hitBoxXcor >= bossXcor + hitBoxXcor && playerXcor + Player.hitBoxXcor <= bossXcor + hitBoxXcor + hitBoxWidth) || (playerXcor + Player.hitBoxXcor + Player.hitBoxWidth >= bossXcor + hitBoxXcor && playerXcor + Player.hitBoxXcor + Player.hitBoxWidth <= bossXcor + hitBoxXcor + hitBoxWidth)) && ((playerYcor + Player.hitBoxYcor >= bossYcor + hitBoxYcor && playerYcor + Player.hitBoxYcor <= bossYcor + hitBoxYcor + hitBoxHeight) || (playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 >= bossYcor + hitBoxYcor && playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4 <= bossYcor + hitBoxYcor + hitBoxHeight));
            returnValue = returnValue || ((bossXcor + hitBoxXcor >= playerXcor + Player.hitBoxXcor && bossXcor + hitBoxXcor <= playerXcor + Player.hitBoxXcor + Player.hitBoxWidth) ||  (bossXcor + hitBoxXcor + hitBoxWidth >= playerXcor + Player.hitBoxXcor && bossXcor + hitBoxXcor + hitBoxWidth <= playerXcor + Player.hitBoxXcor + Player.hitBoxWidth))&& ((bossYcor + hitBoxYcor >= playerYcor + Player.hitBoxYcor && bossYcor + hitBoxYcor <= playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4) || (bossYcor + hitBoxYcor + hitBoxHeight >= playerYcor + Player.hitBoxYcor && bossYcor + hitBoxYcor + hitBoxHeight <= playerYcor + Player.hitBoxYcor + Player.hitBoxHeight * 3.0/4));
            return returnValue;
        }
    }

    boolean collisionWithPlayer(int[][] foreground, int playerXcor, int playerYcor, int bossXcor, int bossYcor)
    {
        if (isColliding(playerXcor, playerYcor, bossXcor, bossYcor))
        {
            boolean moveRight = playerXcor + hitBoxXcor > bossXcor + hitBoxXcor;
            if (Player.currentCharacter == 0 && Player.specialAbility && cycle <= 1 && !isSpecialFeature) // Ahsan is Attacking and Boss is vulnerable
            {
                hit(foreground);
            }

            else if (Player.currentCharacter == 1 && Player.specialAbility) // Sarosh has his shield equipped
            {
                if(this instanceof Mutahar)
                {
                    Sarosh.shieldHit(isSpecialFeature, moveRight, !moveRight, false); // Spikes hit if Mutahar's specialFeature was activated.
                }
                else
                {
                    Sarosh.shieldHit(false, moveRight, !moveRight, false);
                }
            }

            else
            {
                Player.hit(moveRight, !moveRight, false);
            }

            return true;

        }
        return false;
    }


    abstract void updateDisplayCoordinates(int cameraXcor);
}