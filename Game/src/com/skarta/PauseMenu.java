package com.skarta;
import java.awt.*;
import java.io.IOException;

public class PauseMenu
{
    Handler handler;
    private final Icons artboard;
    private final Icons controlButton;
    private final Icons resumeButton;
    private final Icons exitButton;
    private final Icons buttonSelection;
    private int selected = 0;

    PauseMenu(Handler handler)
    {
        //Initialising Icons: Giving them appropriate coordinates, widths and heights
        artboard = new Icons("/pauseMenuArtboard 9 copy 7.png", 530, 690,695,195);
        resumeButton = new Icons("/pauseMenuResumeButton.png", 290, 55,825,530);
        controlButton = new Icons("/pauseMenucontrolButton.png", 290, 55,825,590);
        exitButton = new Icons("/pauseMenuExitButton.png", 290, 55,825,650);
        buttonSelection = new Icons("/pauseMenuButtonSelection.png", 300, 60,825,650);
        this.handler = handler;
    }

    void render(Graphics g)
    {
        // Rendering every Icon
        updateSelected();
        artboard.render(g);
        buttonSelection.render(g);
        resumeButton.render(g);
        controlButton.render(g);
        exitButton.render(g);
    }

    private void updateSelected()
    {
        //updating Ycor of buttonSelection Icon according to which Icon has been selected
        buttonSelection.setYCor(530 + selected * 60);
    }

    void selectionDone()
    {
        if (selected == 0)
        {
            Player.right = false;
            Player.left = false;
            Player.up = false;
            Player.switchWithA = false;
            Player.switchWithD = false;
            Key.gameState = 0;
            Ahsan.setDown(false);
        }
        else if (selected == 1)
        {
            Key.gameState = 4;
        }
        else if (selected == 2)
        {
            Key.gameState = 7;
            try {
                HelperFunctions.saveGame(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MusicPlayer.reset();
        }

    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected)
    {
        this.selected = selected;
    }

}