package com.skarta;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardListener extends KeyAdapter
{
    Player[] players;
    MainMenu mainMenu;
    PauseMenu pauseMenu;

    KeyboardListener(Player[] players, MainMenu mainMenu, PauseMenu pauseMenu)
    {
        this.players = players;
        this.mainMenu = mainMenu;
        this.pauseMenu = pauseMenu;
    }

    @Override
    public void keyPressed (KeyEvent e)
    {
        int key = e.getKeyCode();

        if (Key.gameState == 0) // If we are in game
        {
            if (key == KeyEvent.VK_RIGHT) // Right Arrow Key was Pressed
            {
                Player.right = true;
            }
            else if (key == KeyEvent.VK_LEFT) // Left Arrow Key was Pressed
            {
                Player.left = true;
            }
            else if (key == KeyEvent.VK_UP) // UP Arrow Key was Pressed
            {
                Player.up = true;
            }
            else if (Player.currentCharacter == 0 && key == KeyEvent.VK_DOWN) // Down Arrow Key was Pressed and current Character was Ahsan
            {
                Ahsan.setDown(true);
            }
            else if (key == KeyEvent.VK_A) // A was Pressed
            {
                Player.switchWithA = true;
            }
            else if (key == KeyEvent.VK_S) // S was Pressed
            {
                if (!Ahsan.getIsSlime())
                {
                    Player.specialAbility = true;
                }
            }
            else if (key == KeyEvent.VK_D) // D was Pressed
            {
                Player.switchWithD = true;
            }
            else if(key == KeyEvent.VK_ESCAPE) // ESC was Pressed
            {
                Key.gameState = 3;
            }
        }

        else if(Key.gameState == 1) // if in Game Menu
        {
            if(key == KeyEvent.VK_UP)  // UP Arrow Key was Pressed
                mainMenu.setSelected((mainMenu.getSelected() + 3) % 4); // updating the current Button Selected
            else if(key == KeyEvent.VK_DOWN) // Down Arrow Key was Pressed
                mainMenu.setSelected((mainMenu.getSelected() + 1) % 4); // updating the current Button Selected
            else if(key ==  KeyEvent.VK_ENTER) // Enter was Pressed
                mainMenu.selectionDone(); //Calls function as a button has been pressed
        }

        else if(Key.gameState == 3)
        {
            if(key == KeyEvent.VK_UP) // UP Arrow Key was Pressed
                pauseMenu.setSelected ((pauseMenu.getSelected() + 2) % 3); // updating the current Button Selected
            else if(key == KeyEvent.VK_DOWN)  // Down Arrow Key was Pressed
                pauseMenu.setSelected((pauseMenu.getSelected() + 1) % 3); // updating the current Button Selected
            else if(key ==  KeyEvent.VK_ENTER) // Enter was Pressed
                pauseMenu.selectionDone(); //Calls function as a button has been pressed
            else if(key == KeyEvent.VK_ESCAPE) // ESC was Pressed
            {

                //Now Realeasing All Keys Manually.
                //To avoid Error that might occur if User had Held onto another Key While Pausing the Game
                Player.right = false;
                Player.left = false;
                Player.up = false;
                Player.switchWithA = false;
                Player.switchWithD = false;
                Key.gameState = 0;
                Ahsan.setDown(false);
            }

        }
        else if((Key.gameState == 2 || Key.gameState == 4) && key == KeyEvent.VK_ESCAPE) // Goes back from Controls Menu to the Previous Menu if ESC was Pressed
        {
            Key.gameState--;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (Key.gameState == 0)
        {
            if (key == KeyEvent.VK_RIGHT) // Right Arrow key was released
            {
                Player.right = false;
            }
            else if (key == KeyEvent.VK_LEFT) // Left Arrow key was released
            {
                Player.left = false;
            }
            else if (key == KeyEvent.VK_UP) // Up Arrow key was released
            {
                Player.up = false;
            }
            else if (key == KeyEvent.VK_A) // 'A' was released
            {
                Player.switchWithA = false;
            }
            else if (key == KeyEvent.VK_D) // 'D' was released
            {
                Player.switchWithD = false;
            }
            else if (Player.currentCharacter == 0 && key == KeyEvent.VK_DOWN) // Down Arrow key was released and the player is Ahsan
            {
                Ahsan.setDown(false);
            }
        }

    }
}