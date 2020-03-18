package com.game;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.concurrent.TimeUnit;

//creating the main class Game which contains the main method
public class Game extends Canvas implements Runnable
{
    public Screen screen;//declaring screen as a variable
    int width = 1000;
    int height = 600;
    int ceiling = 10;
    int leftWall = 15;
    int platformHeight = 50;
    int platformWidth = 250;
    int characterWidth = 40;
    int characterHeight = 60;
    int rightWall = leftWall + width - characterWidth;
    int floor = ceiling + height - platformHeight;
    String title = "Hello";

    boolean running = false;//variable to determine whether game is running or not
    Thread thread;

    public Object background;
    public Object ahsan;
    public Object platforms[] = new Object[5];


    //creating a constructor for the Game class
    private Game()
    {
        background = new Object("/background.jpg", leftWall, ceiling, 0, width, height);
        ahsan = new Object("/ahsan.png", 100, floor - characterHeight, 4, characterWidth, characterHeight);
        platforms[0] = new Object("/platform1.png", leftWall, floor, 0, width, platformHeight);
        platforms[1] = new Object("/platform3.png", leftWall, ceiling, 0, width, platformHeight);

        for (int i = 0; i < platforms.length - 2; i++)
        {
            platforms[i + 2] = new Object("/platform2.png", 100 + (i * 200), 420 - (i * 110), 0, platformWidth, platformHeight);
        }

        this.addKeyListener(new Keyboard(ahsan));
        screen = new Screen(width + 50, height + 80, title, this);//new window created in which the game will be played
    }

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public void run()//contains the main game loop
    {
        while (running)//loop runs until the game is running
        {
            render();
        }
    }

    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        background.render(g);

        for (int i = 0; i < platforms.length; i++)
        {
            platforms[i].render(g);

            landing(platforms[i]);
        }

        ahsan.render(g);

        ahsan.move();

        collision();

        g.dispose();
        bs.show();
    }

    public void landing (Object object)
    {
        if (ahsan.xcor > object.xcor && ahsan.xcor < object.xcor + object.width)
        {
            if (ahsan.ycor > object.ycor - object.height)
            {
                ceiling = object.ycor + 30;
            }
            else
            {
                floor = object.ycor - object.height;
            }
        }
        else
        {
            ahsan.inAir = true;
        }

    }

    public void collision()
    {
        if (ahsan.ycor > floor)
        {
            ahsan.ycor = floor;
            ahsan.inAir = false;
            ahsan.dy = 0;
        }
        else if (ahsan.ycor < ceiling)
        {
            ahsan.ycor = ceiling;
            ahsan.dy = 0;
        }
        else if (ahsan.xcor > rightWall)
        {
            ahsan.xcor = rightWall;
            ahsan.dx = 0;
        }
        else if (ahsan.xcor < leftWall)
        {
            ahsan.xcor = leftWall;
            ahsan.dx = 0;
        }
    }

    public static void main(String[] args) {
        new Game();//new game started in main
    }
}
