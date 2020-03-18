package com.game;

import javax.swing.*;

public class Screen extends JFrame//creates a window to play the game in
{
    public Screen (int width, int height, String title, Game game)
    {
        super(title);//creates a JFrame with the given title
        setSize(width, height);//setting the dimensions of the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);//the window closes when the close button is pressed
        setVisible(true);//to project the window onto the screen
        setResizable(false);
        add(game);
        game.start();
    }
}
