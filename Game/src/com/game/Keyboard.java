package com.game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter
{
    Object ahsan;

    public Keyboard (Object ahsan)
    {
        this.ahsan = ahsan;
    }
    public void keyPressed (KeyEvent e)
    {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT)
        {
            ahsan.dx = ahsan.speed;
        }
        else if (key == KeyEvent.VK_LEFT)
        {
            ahsan.dx = -ahsan.speed;
        }
        else if (key == KeyEvent.VK_UP)
        {
            ahsan.dy = -ahsan.speed * 5;
            ahsan.inAir = true;
        }
    }

    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT)
        {
            ahsan.dx = 0;
        }
    }
}
