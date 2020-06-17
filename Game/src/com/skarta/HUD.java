package com.skarta;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD implements Animated
{
    private final BufferedImage[][] selectedCharacter;
    private final BufferedImage[][] lifeCounter;
    private final BufferedImage[][] powers;

    private final int selectorHeight = 160;

    private final int powerWidth = 55;
    private final int powerHeight = 60;
    static final int maxPowerFrame = 60;
    static int[] powerFrames = {maxPowerFrame, maxPowerFrame, maxPowerFrame};
    private static boolean refilling = false;

    static boolean isRefilling()
    {
        return refilling;
    }
    static void setRefilling(boolean refilling)
    {
        HUD.refilling = refilling;
    }

    HUD ()
    {
        String characterPath = "/HUDCharacters.png";
        int selectorWidth = 250;
        selectedCharacter = HelperFunctions.makeSpritesheet(characterPath, 3, 1, selectorWidth, selectorHeight);
        String lifePath = "/HUDLife.png";
        int lifeHeight = 45;
        lifeCounter = HelperFunctions.makeSpritesheet(lifePath, 5, 1, selectorWidth, lifeHeight);
        String powerPath = "/HUDPowers.png";
        powers = HelperFunctions.makeSpritesheet(powerPath, 3, 4, powerWidth, powerHeight);
    }

    @Override
    public void render(Graphics g)
    {
        for (int character = 0; character < powers.length; character++)
        {
            HelperFunctions.drawImage(g, powers[character][powerFrames[character] / (maxPowerFrame / (powers[0].length - 1))], 48 + (powerWidth - 3) * character, 20);
        }
        HelperFunctions.drawImage(g, selectedCharacter[Player.currentCharacter][0], 0, powerHeight);
        HelperFunctions.drawImage(g, lifeCounter[Player.life][0], 0, selectorHeight + powerHeight - 10);

    }

    @Override
    public void tick()
    {
        if (powerFrames[2] < maxPowerFrame)
        {
            powerFrames[2]++;
        }
        if (powerFrames[1] < maxPowerFrame && refilling)
        {
            powerFrames[1]++;
        }
        if(powerFrames[1] >= maxPowerFrame)
        {
            refilling = false;
        }
        if(!refilling)
        {
            powerFrames[1] = Sarosh.shieldLife * maxPowerFrame / 3;
        }
    }

    static void reset()
    {
        for(int i = 0; i<3; i++)
        {
            HUD.powerFrames[i] = HUD.maxPowerFrame;
        }
        refilling = false;
    }
}