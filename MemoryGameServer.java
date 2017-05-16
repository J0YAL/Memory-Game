import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import static java.util.Collections.*;

/**
     A server runs multiple games.
     @author ZIWEN LI
     @version 1.0
*/
public class MemoryGameServer extends JFrame implements MemoryGameConstants
{
   // A activity log to user
	private JTextArea serverLog;
	private JScrollPane scrollPane;
	
	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 500;
	
   /**
           Creates a game server.
           @param args no-use argument
      */
	public static void main(String[] args)	
	{
		MemoryGameServer serverFrame = new MemoryGameServer();
	}
	
   /**
           Constructs a game server.
      */
	public MemoryGameServer()
	{
		serverLog = new JTextArea();
		scrollPane = new JScrollPane(serverLog);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("MemoryGameServer");
		setVisible(true);
		
		try
		{
			// Creates a server socket
			ServerSocket serverSocket = new ServerSocket(PORT);
			serverLog.append(new Date() + ": Server started at socket 8888\n");
			
         // Numbers a game room
			int room = 1;
         
			// Whenever two players enter a game room, start a game
			while (true)
			{
				serverLog.append(new Date() + ": Wait for players to enter game room " + room + '\n');
				
				// Connects to player 1
				Socket player1 = serverSocket.accept();
				serverLog.append(new Date() + ": Player 1 entered game room " + room + '\n');
				serverLog.append("Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
				
            DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
            DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
				// Notifies the 1st player is Player 1
				toPlayer1.writeInt(PLAYER1);
            toPlayer1.flush();

				// Connects to player 2
				Socket player2 = serverSocket.accept();
				serverLog.append(new Date() + ": Player 2 entered game room " + room + '\n');
				serverLog.append("Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');
				
            DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());
            DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
				// Notifies the 1st player is Player 2
				toPlayer2.writeInt(PLAYER2);
            toPlayer2.flush();
				
				// Displays this room and increment room number
				serverLog.append(new Date() + ": Start a thread for game room " + room + '\n');
				room++;
            
            // Starts the game when both players hit the start button
            int p1Start = fromPlayer1.readInt();
            int p2Start = fromPlayer2.readInt();
            if (p1Start == START && p2Start == START);
				{
				   // Creates a new thread for two new players
				   HandleGame game = new HandleGame(player1, player2);
				   // Starts the new thread
				   new Thread(game).start();
            }
			}
		}
		catch(IOException e) 
		{
			System.err.println(e);
		}
      finally
      {
         // This is a server that runs forever
         //~ serverSocket.close();
      }
	}
}
