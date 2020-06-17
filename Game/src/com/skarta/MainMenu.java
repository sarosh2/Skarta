package com.skarta;

import java.awt.*;

public class MainMenu
{
    private final Icons background;
    private final Icons continueIcon;
    private final Icons newGameIcon;
    private final Icons exitIcon;
    private final Icons menuLogo;
    private final Icons selectedIcon;
    private final Icons controlsIcon;
    private int selected = 1;

    Handler handler;

    MainMenu(Handler handler)
    {
        //Initialising Icons: Giving them appropriate coordinates, widths and heights
        this.handler = handler;
        background = new Icons("/MENU BACKGROUND.png", 1920,1080, 0, 0);
        menuLogo = new Icons("/MENU LOGO.png", 900, 400, 510,168);
        continueIcon = new Icons("/CONTINUE BUTTON.png", 394, 74, 763,550);
        newGameIcon = new Icons("/NEW GAME  BUTTON.png", 394, 74, 763,630);
        controlsIcon = new Icons("/bigger-control-button.png", 394, 74, 763,710);
        exitIcon = new Icons("/EXIT BUTTON.png", 394, 74, 763,790);
        selectedIcon = new Icons("/SECLECTED BUTTON.png", 394, 74, 763,710);
    }

    void render(Graphics g)
    {
        // Rendering every Icon
        updateSelected();
        background.render(g);
        menuLogo.render(g);
        selectedIcon.render(g);
        continueIcon.render(g);
        newGameIcon.render(g);
        controlsIcon.render(g);
        exitIcon.render(g);
    }

    private void updateSelected()
    {
        //updating Ycor of Selected Icon according to which Icon has been selected
        selectedIcon.setYCor(550 + selected * 80);
    }

    void selectionDone()
    {
        if (selected == 0)
        {
            //Here is where we Load the old game
            Key.gameState = 5;
            handler.setCurrentLevel(8);//the save file is technically the 8th level file
            handler.levelSetUp();
        }
        else if (selected == 1)
        {
            Key.gameState = 5;
            handler.setCurrentLevel(0);
            handler.levelSetUp();
        }
        else if (selected == 2)
        {
            Key.gameState = 2;
        }
        else if (selected == 3)
        {
            System.exit(0);
        }
    }

    int getSelected()
    {
        return selected;
    }
    void setSelected(int selected)
    {
        this.selected = selected;
    }
}