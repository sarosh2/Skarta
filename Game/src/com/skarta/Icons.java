package com.skarta;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Icons implements Animated
{
    private Image image;
    private int xCor;
    private int yCor;

    Icons(String path, int width, int height, int xCor, int yCor)
    {
        this.xCor = xCor;
        this.yCor = yCor;
        try
        {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = HelperFunctions.getScaledImage(image, width, height); // Gets scaled version of Icon
    }

    @Override
    public void render(Graphics g)
    {
        HelperFunctions.drawImage(g, image, xCor, yCor);
    } // Renders The Icon

    @Override
    public void tick()
    {
        yCor -= 50;
    }

    int getYcor()
    {
        return yCor;
    }
    void setYCor(int yCor) {
        this.yCor = yCor;
    }

    void setXCor(int xCor) {
        this.xCor = xCor;
    }
    int getXCor() {return xCor;}
}