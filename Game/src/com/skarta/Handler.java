package com.skarta;

import java.io.*;

public class Handler implements Runnable
{
    Camera camera;//Display for the game
    MainMenu mainMenu;
    PauseMenu pauseMenu;
    Background background;
    Player[] players = new Player[3];

    int[][] foreground = new int[Key.levelArrayRows][Key.levelArrayColumns];//the entire layout of the level map
    Enemy[] enemies = new Enemy[Key.levelArrayRows * Key.levelArrayColumns];
    Boss[] bosses = new Boss[3];

    private boolean running;
    private int currentLevel;
    private String levelBackground;
    private long creditsInterval = 0;

    public void setCreditsInterval(long creditsInterval) {
        this.creditsInterval = creditsInterval;
    }

    Thread logicThread;

    Handler(String title)
    {
        //objects and variables initialized
        Platform.loadSpriteSheet();//all the different types of platform sprites are loaded
        mainMenu = new MainMenu(this);
        pauseMenu = new PauseMenu(this);
        MusicPlayer musicPlayer = new MusicPlayer();//plays background music based on gameState
        background = new Background();
        players[0] = new Ahsan();
        players[1] = new Sarosh();
        players[2] = new Tayyab();
        camera = new Camera(this);
        //JFrame where the game runs
        new Window(Key.getDisplayResolutionWidth(), Key.getDisplayResolutionHeight(), title, camera);//new window created in which the game will be played

        //all the different threads are started
        camera.start();
        musicPlayer.start();
        logicThread = new Thread(this);
        start();
    }

    synchronized void start()
    {
        running = true;
        logicThread.start();
    }

    @Override
    public void run()//contains the main game loop
    {
        while (running)//loop runs until the game is running
        {
            long timeDelay = System.currentTimeMillis();//used to make the thread sleep for a specified period of time
            collision(players[Player.currentCharacter]);//checks player collision with other objects

            //checks if player goes on to the next level
            if (!Key.bossFight && Key.gameState == 0)
            {
                if(currentLevel < Key.numberOfLevels - 1 && Player.displayXcor > Key.rightWall)
                {
                    Key.gameState = 5;
                    currentLevel++;
                    levelSetUp();
                    try {
                        HelperFunctions.saveGame(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(currentLevel == Key.numberOfLevels - 1)// Game has finished
                {
                    if(System.currentTimeMillis() - creditsInterval > 500 && creditsInterval != 0) // Waits extra 500 millisecond if Game has finished
                    {
                        Key.gameState = 8; //Rolling Credits state
                        Player.reset(camera.getXcor());
                        MusicPlayer.reset();
                        creditsInterval = 0;
                    }
                }
            }

            //camera can only move on levels that aren't boss fights
            if(currentLevel % 2 == 0)
            {
                checkCameraMove();
            }

            //calls the update function of all the enemies in the foreground array and checks for their collision
            for (int[] row : foreground)
            {
                for (int entry : row)
                {
                    if (entry >= 1225)
                    {
                        bosses[entry - 1225].update();
                        collision(bosses[entry - 1225]);
                    }
                    else if (entry >= 25)
                    {

                        if (enemies[entry - 25].cycle < enemies[entry - 25].hitRightCycle)
                        {
                            collision(enemies[entry - 25]);
                        }
                        enemies[entry - 25].updateCycle(foreground);
                    }
                }
            }

            //if characters Ahsan or Tayyab are doing their special attacks, their cycles are not updated as that animation needs to finish first
            if (!((Player.currentCharacter == 0 || Player.currentCharacter == 2) && Player.specialAbility))
            {
                if(!Player.cycleUpdated)
                {
                    players[Player.currentCharacter].updateCycle();
                }
            }

            //if the player dies, the level is reset
            if (Player.life == 0)
            {
                if (camera.fade.getYcor() == Key.screenHeight)
                {
                    //if the fade effect hasn't started yet, make the game state 6 which starts the fade effect in the camera class
                    Key.gameState = 6;
                }
                else if (camera.fade.getYcor() < - Key.screenHeight / 2)
                {
                    //if the fade effect has covered the screen, reset the level
                    Player.life = 4;
                    levelSetUp();
                }
            }

            timeDelay = System.currentTimeMillis() - timeDelay;//calculates the time it took to go through one entire iteration
            if(timeDelay > Key.timeDelayInHandler)
            {
                timeDelay = Key.timeDelayInHandler;//if more time is passed to run the loop than the delay variable in Key class allows, then don't make the thread sleep
            }
            try
            {
                //make the thread sleep so that the total time for the iteration becomes to the timeDelayInHandler variable in key class
                Thread.sleep(Key.timeDelayInHandler - timeDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void collision (Character character)
    {
        // the character provided, update coordinates of hitbox and check collision
        character.updateHitBoxCoordinates();
        character.collide(foreground, camera.getXcor());
    }

    //to check if the camera needs to move
    void checkCameraMove()
    {
        if(!Player.collisionChecked)
        {
            Player.collisionChecked = true;
            if (Player.displayXcor > Key.screenWidth * 2 / 3 && camera.getXcor() < Key.absoluteHorizontalLimit)
            {
                cameraShift();
            }
            else if (Player.displayXcor < Key.screenWidth / 3 && camera.getXcor() > 0)
            {
                cameraShift();
            }
        }
    }

    private void cameraShift() {
        int movedBy = Player.effectiveDx; // distance the camera should be moved.
        if (camera.getXcor() + Player.effectiveDx < 0)
        {
            movedBy = -camera.getXcor();
        }
        else if(camera.getXcor() + Player.effectiveDx > Key.absoluteHorizontalLimit)
        {
            movedBy = Key.absoluteHorizontalLimit - camera.getXcor();
        }
        camera.shift(movedBy);
        background.move(background.calculateSpeed(camera.getXcor(), movedBy));
        Player.updateDisplayCoordinates(camera.getXcor());
    }

    void levelSetUp()
    {
        try
        {
            //the current level file is loaded and all the variables are set according to what's stored inside the file
            int backgroundXcor = HelperFunctions.loadGame(this);//loads the appropriate level file based on the currentLevel variable
            background.reset(levelBackground, backgroundXcor);//resets the background image and coordinates
            Key.bossFight = currentLevel % 2 == 1;//all odd numbered levels are boss fights
            Player.reset(camera.getXcor());//player is reset with absolute x coordinate relative to camera's x coordinate in relation to the foreground array
            MusicPlayer.reset();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    int getCurrentLevel()
    {
        return currentLevel;
    }
    String getLevelBackground()
    {
        return levelBackground;
    }
    void setCurrentLevel(int level)
    {
        currentLevel = level;
    }
    void setLevelBackground(String levelBackground)
    {
        this.levelBackground = levelBackground;
    }
}