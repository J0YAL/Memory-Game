/**
     An interface that stores the game constants.
     @author ZIWEN LI
     @version 1.0
*/
public interface MemoryGameConstants
{
   // The default port to server
   int PORT = 8888;
	
   // Number of rows of cards
   int ROWS = 4;

   // Number of columns of cards
   int COLS = 4;
	
   // Number of pairs of cards
   int PAIRS = ROWS * COLS / 2;
	
   // How many milliseconds to expose unmatching cards
   int MOMENT = 1000;	
	
   // Player 1
   int PLAYER1 = 1181;
	
   // Player 2
   int PLAYER2 = 1160;
   
   // Signal for two matching cards
   int MATCHING = 1;
   
   // Signal for two unmatching cards
   int UNMATCHING = 0;
	
   // Player 1 winning signal
   int PLAYER1_WON = 1;
	
   // Player 2 winning signal
   int PLAYER2_WON = 2;
   
   // The player is ready
   int START = 100;
   
   // A player quits in the middle of a game
   int QUIT = 99;
   
   // Game on signal
   int CONTINUE = 101;
   
   // The other player is on the move
   int FREEZE = 105;
   
   //~ String[] COMMANDS = {
      //~ "QUIT" , /* 0 */
      //~ "FREEZE", /*1*/
      //~ "CONTINUE"/*2*/
   //~ };
}