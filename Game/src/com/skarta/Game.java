package com.skarta;

//creating the main class Game which contains the main method
public class Game
{
    Handler handler;
    String title = "SKARTA";
    private Game()
    {
        handler = new Handler(title);
    }

    public static void main(String[] args)
    {
        Key.updateDisplayResolution(); // Updating the display resolution of the screen before game starts
        new Game();//new game started in main
    }
}