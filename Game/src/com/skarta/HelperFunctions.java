package com.skarta;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.Character;
import java.io.IOException;

public class HelperFunctions
{
    static int loadGame(Handler handler) throws IOException
    {
        String fileName;
        if (handler.getCurrentLevel() == 8)
        {
            fileName = "save.txt";
        }
        else
        {
            fileName = "Levels/level" + handler.getCurrentLevel() + ".txt";
        }
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String currentLine;
        int enemyCount = 0;

        int[] levelCoordinates = new int[4];

        handler.setLevelBackground(bufferedReader.readLine());
        handler.setCurrentLevel(bufferedReader.readLine().charAt(0) - '0');

        for (int coordinates = 0; coordinates < levelCoordinates.length; coordinates++)
        {
            char[] currentCoordinates = bufferedReader.readLine().toCharArray();
            for (int index = 0; index < currentCoordinates.length; index++)
            {
                levelCoordinates[coordinates] += (currentCoordinates[index] - '0') * (int)Math.pow(10, currentCoordinates.length - index - 1);
            }
        }

        handler.camera.setXcor(levelCoordinates[1]);
        Player.startingXcor = levelCoordinates[2];
        Player.startingYcor = levelCoordinates[3];

        for (int row = 0; row < handler.foreground.length; row++)
        {
            if ((currentLine = bufferedReader.readLine()) != null)
            {
                char[] line = currentLine.toCharArray();
                handler.foreground[row] = new int[line.length];
                for (int column = 0; column < line.length; column++)
                {
                    if (line[column] == 'W')
                    {
                        handler.bosses[0] = new Talha(row, column, handler);
                        handler.foreground[row][column] = 1225;
                    }
                    else if (line[column] == 'X')
                    {
                        handler.bosses[1] = new Mutahar(row, column, handler);
                        handler.foreground[row][column] = 1226;
                    }
                    else if (line[column] == 'Y')
                    {
                        handler.bosses[2] = new Jahangir(row, column, handler);
                        handler.foreground[row][column] = 1227;
                    }
                    else if (line[column] == 'U' || line[column] == 'V')
                    {
                        if (line[column] == 'U')
                        {
                            handler.enemies[enemyCount] = new Usman(row, column, enemyCount);
                        }
                        else
                        {
                            handler.enemies[enemyCount] = new Abdullah(row, column, enemyCount);
                        }
                        handler.foreground[row][column] = 25 + enemyCount;
                        enemyCount++;
                    }
                    else if (line[column] >= 'A')
                    {
                        handler.foreground[row][column] = line[column] - 'A' + 10;
                    }
                    else
                    {
                        handler.foreground[row][column] = Character.getNumericValue(line[column]);
                    }
                }
            }
        }

        if (fileName.compareTo("save.txt") == 0)
        {
            Player.life = bufferedReader.readLine().charAt(0) - '0';
        }
        else
        {
            Player.life = Key.playerMaxLife;
        }

        bufferedReader.close();
        fileReader.close();

        return levelCoordinates[0];
    }

    static void saveGame (Handler handler) throws IOException
    {
        String filename = "save.txt";
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        char[][] foreground = new char[Key.levelArrayRows][Key.levelArrayColumns];

        bufferedWriter.write(handler.getLevelBackground() + '\n');
        bufferedWriter.write(String.valueOf(handler.getCurrentLevel()) + '\n');
        bufferedWriter.write(String.valueOf(handler.background.getAbsoluteXcor()) + '\n');
        bufferedWriter.write(String.valueOf(handler.camera.getXcor()) + '\n');
        bufferedWriter.write(String.valueOf(Player.displayXcor) + '\n');
        bufferedWriter.write(String.valueOf(Player.displayYcor) + '\n');

        for (int row = 0; row < foreground.length; row++)
        {
            for (int column = 0; column < foreground[row].length; column++)
            {
                if (handler.foreground[row][column] == 1225)
                {
                    foreground[row][column] = 'W';
                }
                else if (handler.foreground[row][column] == 1226)
                {
                    foreground[row][column] = 'X';
                }
                else if (handler.foreground[row][column] == 1227)
                {
                    foreground[row][column] = 'Y';
                }
                else if (handler.foreground[row][column] >= 25)
                {
                    if (handler.enemies[handler.foreground[row][column] - 25] instanceof Usman)
                    {
                        foreground[row][column] = 'U';
                    }
                    else
                    {
                        foreground[row][column] = 'V';
                    }
                }
                else if (handler.foreground[row][column] >= 10)
                {
                    foreground[row][column] = (char)(handler.foreground[row][column] - 10 + 'A');
                }
                else
                {
                    foreground[row][column] = (char)(handler.foreground[row][column]  + '0');
                }
            }
            bufferedWriter.write(String.valueOf(foreground[row]) + '\n');
        }

        bufferedWriter.write(String.valueOf(Player.life));

        bufferedWriter.close();
        fileWriter.close();
    }

    static BufferedImage[][] makeSpritesheet(String path, int numberOfRows, int numberOfColumns, int width, int height)
    {
        int newWidth;
        int newHeight;
        BufferedImage sheet = null;
        BufferedImage[][] spritesheet;

        spritesheet = new BufferedImage[numberOfRows][numberOfColumns];

        //loading the complete sprite sheet
        try
        {
            sheet = ImageIO.read(HelperFunctions.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        newWidth = (int)Math.ceil(width * Key.getScale());
        newHeight = (int)Math.ceil(height * Key.getScale());

        //cropping the spritesheet
        //loading the walk cycle
        for (int row = 0; row < spritesheet.length; row++)
        {
            for (int column = 0; column < spritesheet[row].length; column++)
            {
                assert sheet != null;
                spritesheet[row][column] = sheet.getSubimage(width * column, height * row, width, height);

            }

        }

        if(Key.getScale() != 1) // Scaled Buffered Images are required
        {
            for (int row = 0; row < spritesheet.length; row++)
            {
                for (int column = 0; column < spritesheet[row].length; column++)
                {
                    BufferedImage scaledImage = new BufferedImage(newWidth , newHeight, BufferedImage.TYPE_INT_ARGB); // Creating an empty Buffered Image
                    Graphics2D graphics2D = scaledImage.createGraphics();
                    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics2D.drawImage(spritesheet[row][column], 0, 0, newWidth, newHeight, null); // Drawing scaled image onto the empty scaled Buffered Image
                    graphics2D.dispose();
                    spritesheet[row][column] = scaledImage; // The new Scaled Buffered Image replaces the unscaled one
                }

            }
        }

        return spritesheet;
    }

    static Image getScaledImage(Image image, int width, int height)
    {
       return image.getScaledInstance((int)Math.ceil(width * Key.getScale()), (int)Math.ceil(height * Key.getScale()), Image.SCALE_SMOOTH);
    }

    static void drawImage(Graphics g, Image image, int xCor, int yCor)
    {
        g.drawImage(image, (int)Math.round(xCor * Key.getScale()), (int)Math.round(yCor * Key.getScale()), null);
    }

}