package com.skarta;
import javax.swing.*;

public class Window extends JFrame//creates a window to play the game in
{
    Window(int width, int height, String title, Camera camera)
    {
        super(title);//creates a JFrame with the given title
        add(camera);
        setUndecorated(true);
        setResizable(false);
        setSize(width, height);//setting the dimensions of the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);//the window closes when the close button is pressed
        setVisible(true);//to project the window onto the camera
    }
}