import org.omg.PortableServer.THREAD_POLICY_ID;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Program Created by Subbaramaiah Mettu
 *
 * Note: This program is just a GUI without implementation. Game logic is not complete yet.
 *
 * This program is a card fight game where two players can play together. This game is similar to trading cards game where
 * you have to take turns in playing a card and oompare each card stats and determine the winner for the round.
 * Player who scored most points wins the game
 *
 */
public class CardFight {

    private static AudioStream theStream;

    public static void main(String[] args){

        //creating two players and starting a mock server
        Player p0 = new Player();

        GameInterface player0 = new GameInterface(p0);

        try{

            //music from https://www.dl-sounds.com/royalty-free/power-bots-loop/ all credits goes to them and its open source music, just used it for project purpose
            //audio, got this resource from stack overflow all credits to https://stackoverflow.com/questions/8979914/audio-clip-wont-loop-continuously
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\nagaraju\\IdeaProjects\\cardFight\\src\\Power Bots Loop.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            Thread.sleep(10000);
        }
        catch(Exception e){e.printStackTrace();}


    }

}
