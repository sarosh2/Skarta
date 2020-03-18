package com.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Object
{

    protected int xcor;//x coordinate of object
    protected int ycor;//y coordinate of object
    protected int speed;// horizontal speed of object
    protected int width;
    protected int height;
    protected int dx;// horizontal speed of object
    protected int dy;//vertical speed of object
    private Image image;//image of the object
    int gravity = 1;
    boolean inAir = false;

    public Object(String path, int xcor, int ycor, int speed, int width, int height)
    {
        //initializing the object's coordinates and speed
        this.xcor = xcor;
        this.ycor = ycor;
        this.speed = speed;
        this.width = width;
        this.height = height;

        //adding the image of the object
        try
        {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public void render(Graphics g) {
        g.drawImage(image, xcor, ycor, null);
    }

    public void move()
    {
        xcor += dx;
        ycor += dy;

        if (dy != 0 || inAir)
        {
            dy += gravity;
        }
    }
}
