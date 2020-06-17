package com.skarta;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer implements Runnable
{
    Thread musicThread;
    boolean running;
    /*
    as there are many instances where the music player needs to be reset, different sound files to be played by different characters and their interactions,
    and since there is only one music player throughout the game,
    we felt it was easier if the methods used by music player were allowed access to as static methods.
    */

    //variables to calculate time the duration the individual sound files have been running for
    private static long bgThemeTime;
    private static long bgEffectsTime;
    private static long bossThemeTime;
    private static long mainMenuTime;

    //the sound files played constantly throughout the game
    private static Clip bgTheme;
    private static Clip bgEffects;
    private static Clip bossTheme;
    private static Clip mainMenuTheme;

    //the duration of all the individual music files in milli seconds
    private static final long bgEffectsDuration = 52000;
    private static final long bgThemeDuration = 149000;
    private static final long bossThemeDuration = 317000;

    MusicPlayer()
    {
        //initializing all the time variables and music files
        bgThemeTime = 0;
        bgEffectsTime = 0;
        bossThemeTime = 0;
        mainMenuTime = 0;

        bgTheme = makeSoundFFile("bgTheme.wav", -15);
        bgEffects = makeSoundFFile("bgMusic.wav", -10);
        bossTheme = makeSoundFFile("bossTheme.wav", 0);
        mainMenuTheme = makeSoundFFile("menuTheme.wav", 0);
    }

    synchronized void start()
    {
        musicThread = new Thread(this);
        running = true;
        musicThread.start();
    }

    @Override
    public void run()
    {
        while (running)
        {
            //if the game is being played, the music can either be regular level or boss fight music
            if (Key.gameState == 0)
            {
                if (Key.bossFight)
                {
                    //time == 0 dictates that the clip needs to be started
                    if (bossThemeTime == 0)
                    {
                        bossTheme.setMicrosecondPosition(0);
                        bossTheme.start();
                        bossThemeTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - bossThemeTime >= bossThemeDuration)//if the audio file has reached the end of it's duration, it is restared
                    {
                        bossThemeTime = 0;
                        bossTheme.stop();
                    }
                    //a similar code structure is used for all the different sound files
                }
                else
                {
                    if (bgEffectsTime == 0)
                    {
                        bgEffects.setMicrosecondPosition(0);
                        bgEffects.start();
                        bgEffectsTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - bgEffectsTime >= bgEffectsDuration)
                    {
                        bgEffectsTime = 0;
                        bgEffects.stop();
                    }

                    if (bgThemeTime == 0)
                    {
                        bgTheme.setMicrosecondPosition(0);
                        bgTheme.start();
                        bgThemeTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - bgThemeTime >= bgThemeDuration)
                    {
                        bgThemeTime = 0;
                        bgTheme.stop();
                    }
                }
            }
            else if (Key.gameState == 1 || Key.gameState == 8)
            {
                //state where main menu is running
                if (mainMenuTime == 0)
                {
                    mainMenuTheme.setMicrosecondPosition(0);
                    mainMenuTheme.start();
                    mainMenuTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - mainMenuTime >= bgEffectsDuration)
                {
                    mainMenuTime = 0;
                    mainMenuTheme.stop();
                }
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //function used throughout the game to play a file at a certain volume starting from a certain position in micro seconds
    static void playSound(String fileName, float volume, long microSecondPosition)
    {
        Clip file = makeSoundFFile(fileName, volume);
        assert file != null;
        file.setMicrosecondPosition(microSecondPosition);
        file.start();
    }

    //loads a .wav file and sets it to a specific volume
    static Clip makeSoundFFile(String fileName, float volume)
    {
        fileName = "Music/" + fileName;
        File music = new File(fileName);

        AudioInputStream audio;
        try {
            audio = AudioSystem.getAudioInputStream(music);
            AudioFormat format = audio.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audio);
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);

            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void reset()
    {
        //all the sound files are stopped and reset to their original states
        bgEffectsTime = 0;
        bgThemeTime = 0;
        bossThemeTime = 0;
        mainMenuTime = 0;

        bgEffects.stop();
        bgTheme.stop();
        bossTheme.stop();
        mainMenuTheme.stop();

        bgEffects.setMicrosecondPosition(0);
        bgTheme.setMicrosecondPosition(0);
        bossTheme.setMicrosecondPosition(0);
        mainMenuTheme.setMicrosecondPosition(0);
    }
}