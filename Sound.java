import javax.sound.sampled.*;
import java.net.*;

/** 
      Sound was downloaded from "http://themushroomkingdom.net/"
      Defines the thread class for handling a game for two players.
      @author ZIWEN LI
      @version 1.0 
*/

public class Sound
{
   // Directory of the audio filenames for sound effect
   private String fileStart;         
   private String fileGameOver;
   private String fileFlip;
   
   private Clip soundClipStart;
   private Clip soundClipGameOver;
   private Clip soundClipFlip;  

   /**
           Constructs a set of sound effect.
      */
   public Sound()
   {
      fileStart = "sounds/start.wav";         
      fileGameOver = "sounds/gameover.wav";
      fileFlip = "sounds/flip.wav";
      
      try 
      {
         URL url = this.getClass().getClassLoader().getResource(fileGameOver);
         if (url == null) 
         {
            System.err.println("Couldn't find file: " + fileGameOver);
         } 
         else 
         {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            soundClipGameOver = AudioSystem.getClip();
            soundClipGameOver.open(audioIn);
         }
       
         url = this.getClass().getClassLoader().getResource(fileStart);
         if (url == null) 
         {
            System.err.println("Couldn't find file: " + fileStart);
         } 
         else 
         {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            soundClipStart = AudioSystem.getClip();
            soundClipStart.open(audioIn);
         }
         
         url = this.getClass().getClassLoader().getResource(fileFlip);
         if (url == null) 
         {
            System.err.println("Couldn't find file: " + fileFlip);
         } 
         else 
         {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            soundClipFlip = AudioSystem.getClip();
            soundClipFlip.open(audioIn);
         }
      } 
      catch (UnsupportedAudioFileException e) 
      {
         System.err.println("Audio Format not supported!");
      } 
      catch (Exception e) 
      {
         e.printStackTrace();
      }
   }
   
   /**
           For when the game starts.
      */
   public void playStart()
   {
      soundClipStart.start();
      soundClipStart.setFramePosition(0);
   }
   
   /**
           For when the game ends.
      */
   public void playGameOver()
   {
      soundClipGameOver.start();
      soundClipGameOver.setFramePosition(0);
   }
   
   /**
           For when playing a move.
      */   
   public void playMove()
   {
      soundClipFlip.start();
      soundClipFlip.setFramePosition(0);
   }
}