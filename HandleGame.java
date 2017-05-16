import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import static java.util.Collections.*;

/** 
      Defines the thread class for handling a game for two players.
      @author ZIWEN LI
      @version 1.0
*/
public class HandleGame implements Runnable, MemoryGameConstants 
{
   // Sockets for players
   private Socket player1;
   private Socket player2;
	
   private ArrayList<Integer> cardNumbers;
		
   private DataInputStream fromPlayer1;
   private DataOutputStream toPlayer1;
   private DataInputStream fromPlayer2;
   private DataOutputStream toPlayer2;
		
   // Stores the first player who gets the first matching cards
   private int gotFirstPoint;
		
   /** 
           Construct a thread per game.
           @param player1 the player who joins the game first
           @param player2 the 2nd player
      */
   public HandleGame(Socket player1, Socket player2)
   {
      this.player1 = player1;
      this.player2 = player2;
      cardNumbers = new ArrayList<Integer>();
      gotFirstPoint = -1;
			
      // Initializes the game board
      init();
   }
		
   /**
            Shuffles cards.
      */
   public void init()
   {
      for (int i = 0; i <= 1; i++)
      {
         for (int j = 0; j < PAIRS; j++)
         {
            cardNumbers.add(j);
         }
      }
      shuffle(cardNumbers);
      
      // For cheating
      String matrix = "";
      for  (int i = 0; i < cardNumbers.size(); i++)
      {
         matrix += cardNumbers.get(i) + " ";
         if ((i + 1) % COLS == 0)
         {
            matrix += "\n";
         }
      }
      System.out.println(matrix);
   }
		
   /** 
            Runs the game.
      */
   public void run() 
   {
      try
      {
         // Creates data input and output streams
         fromPlayer1 = new DataInputStream(player1.getInputStream());
         toPlayer1 = new DataOutputStream(player1.getOutputStream());
         fromPlayer2 = new DataInputStream(player2.getInputStream());
         toPlayer2 = new DataOutputStream(player2.getOutputStream());
         
         // Sends the card numbers to players to set up the game board
         for (int i : cardNumbers)
         {
            toPlayer1.writeInt(i);
            toPlayer1.flush();
            toPlayer2.writeInt(i);
            toPlayer2.flush();
         }
         
         // Signals player 1 to start
         toPlayer1.writeInt(1);
         toPlayer1.flush();
				
         // Initializes each player's points
         // For player 1
         int points1 = 0;
         // For player 2
         int points2 = 0;
			
         // Game runs on until all the cards are fliped open, or a player quits         
         while (true) 
         {
            // Checks who gets the first point
            if (points1 == 1 && points2 == 0)
            {
               gotFirstPoint = PLAYER1;
            }
            else if (points1 == 0 && points2 == 1)
            {
               gotFirstPoint = PLAYER2;
            }
            
            // For player 1
            
            // Receives the first selected card from player 1
            int posOfFirstCard = fromPlayer1.readInt();
            if (posOfFirstCard == QUIT)
            {
               sendMove(toPlayer2, QUIT);
               return;
            }
            
            // Sends the move to player 2
            sendMove(toPlayer2, posOfFirstCard);
               
            // Receives the second selected card from player 1
            int posOfSecondCard = fromPlayer1.readInt();
            // Receives the signal if player 1 quits
            if (posOfSecondCard == QUIT)
            {
               sendMove(toPlayer2, QUIT);
               return;
            }
               
            // Sends the move to player 2
            sendMove(toPlayer2, posOfSecondCard);
            
            // Exposes two cards for a moment
            try
            {
               Thread.sleep(MOMENT);
            }
            catch (InterruptedException e)
            {
               System.err.println(e);
            }
               
            // Indicates if two cards match
            int matching = UNMATCHING;
					
            // Checkes if two cards match
            if (cardNumbers.get(posOfFirstCard) == cardNumbers.get(posOfSecondCard))
            {
               cardNumbers.set(posOfFirstCard, -1);
               cardNumbers.set(posOfSecondCard, -1);
               matching = MATCHING;
               points1++;
            }
      
            // Covers both players' two cards back up because of unmatching cards
            sendMove(toPlayer1, matching); 
            sendMove(toPlayer1, points1); 
            sendMove(toPlayer2, matching); 
            sendMove(toPlayer2, points2);
					
            // Checks if game is over
            if (isAllOpen())
            {
               if (points1 > points2)
               {
                  sendMove(toPlayer1, PLAYER1_WON); 
                  sendMove(toPlayer2, PLAYER1_WON); 
               }
               else if (points1 == points2)
               {
                  if (gotFirstPoint == PLAYER1)
                  {
                     sendMove(toPlayer1, PLAYER1_WON); 
                     sendMove(toPlayer2, PLAYER1_WON);
                  }
                  else
                  {
                     sendMove(toPlayer1, PLAYER2_WON); 
                     sendMove(toPlayer2, PLAYER2_WON);
                  }
               }
               else
               {
                  sendMove(toPlayer1, PLAYER2_WON); 
                  sendMove(toPlayer2, PLAYER2_WON);
               }
               return;
            }
            else
            {
               sendMove(toPlayer2, CONTINUE); 
               sendMove(toPlayer1, FREEZE);
            }
            
            // Checks who gets the first point
            if (points1 == 1 && points2 == 0)
            {
               gotFirstPoint = PLAYER1;
            }
            else if (points1 == 0 && points2 == 1)
            {
               gotFirstPoint = PLAYER2;
            }
            // For player 2
					
            // Receives the first selected card from player 2
            posOfFirstCard = fromPlayer2.readInt();
            if (posOfFirstCard == QUIT)
            {
               sendMove(toPlayer1, QUIT);
               return;
            }
            
            // Sends the move to player 1
            sendMove(toPlayer1, posOfFirstCard);
               
            // Receives the second selected card from player 2
            posOfSecondCard = fromPlayer2.readInt();
            // Receives the signal if player 1 quits
            if (posOfSecondCard == QUIT)
            {
               sendMove(toPlayer1, QUIT);
               return;
            }
               
            // Sends the move to player 1
            sendMove(toPlayer1, posOfSecondCard);
            
            // Exposes two cards for a moment
            try
            {
               Thread.sleep(MOMENT);
            }
             catch (InterruptedException e)
            {
               System.err.println(e);
            }
            
            // Indicates if two tried cards match
            matching = UNMATCHING;
					
            // Checkes if two cards match
            if (cardNumbers.get(posOfFirstCard) == cardNumbers.get(posOfSecondCard))
            {
               cardNumbers.set(posOfFirstCard, -1);
               cardNumbers.set(posOfSecondCard, -1);
               matching = MATCHING;
               points2++;
            }
               
            // If not matching ,  cover both players' two cards back up because of unmatching cards
            sendMove(toPlayer1, matching); 
            sendMove(toPlayer1, points1); 
            sendMove(toPlayer2, matching); 
            sendMove(toPlayer2, points2);
					
            // Checks if game is over
            if (isAllOpen())
            {
               if (points1 > points2)
               {
                  sendMove(toPlayer1, PLAYER1_WON); 
                  sendMove(toPlayer2, PLAYER1_WON); 
               }
               else if (points1 == points2)
               {
                  if (gotFirstPoint == PLAYER1)
                  {
                     sendMove(toPlayer1, PLAYER1_WON); 
                     sendMove(toPlayer2, PLAYER1_WON);
                  }
                  else
                  {
                     sendMove(toPlayer1, PLAYER2_WON); 
                     sendMove(toPlayer2, PLAYER2_WON);
                  }
               }
               else
               {
                  sendMove(toPlayer1, PLAYER2_WON); 
                  sendMove(toPlayer2, PLAYER2_WON);
               }
               return;
            }
            else
            {
               sendMove(toPlayer1, CONTINUE); 
               sendMove(toPlayer2, FREEZE);
            }
         }
      }
      catch (IOException e) 
      {
         System.err.println(e);
      }
      finally
      {
         try
         {
            fromPlayer1.close();
            toPlayer1.close();
            fromPlayer2.close();
            toPlayer2.close();
         }
         catch (IOException e) 
         {
            System.err.println(e);
         }
      }
   }
		
   /**
           Checks if all the cards are open.
           @return true if all cards are fliped open, otherwise, false
      */
   public boolean isAllOpen()
   {
      // If there's any card with a value != -1, then there's still cards folded
      for (int c : cardNumbers)
      {
         if (c != -1)
         {
            return false;
         }
      }
      return true;
   }
		
   /** 
           Sends the move to player.
           @param out the cocket to player
           @param value the command or card position
      */
   public void sendMove(DataOutputStream out, int value)
				throws IOException 
   {
      out.writeInt(value); 
      out.flush();
   }
}