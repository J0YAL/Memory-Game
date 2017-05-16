import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.net.*;


/**
     A game player.
     @author ZIWEN LI
     @version 1.0
*/
public class Client extends JFrame implements Runnable, MemoryGameConstants
{
	public static final int FRAME_WIDTH = 660;
	public static final int FRAME_HEIGHT = 800;
	
	private boolean myTurn;
   
	// Which player am i
	private int me;
   
    private Sound audio;

    // For sound effect
    private boolean mute;
	
    // Receives the card numbers from the server
	private int[] cardNumbers;
	private Card[] cards;
   
    // A socket to the server
    private Socket socket;

    // Components of the game board
	private JButton quitButton;
    private JButton startButton;
    private JButton muteButton;
	private JLabel pointsLabel;
    private JLabel playerLabel;
    private JTextArea messageArea;
	
	// The selected two cards by the current move
	private int posOfFirstTry;
	private int posOfSecondTry;
    private int countOfMove;
    private int points;
   
	// Input and output streams from/to server
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	// Continue to play
	private boolean continueToPlay;
	
	// Host name or ip
	private String host;
  
	/**
        Constructs a game board with 16 cards.
    */
	public Client(String host)
	{
		myTurn = false;
      me = 0;
      cardNumbers = new int[ROWS * COLS];
		cards = new Card[ROWS * COLS];
      posOfFirstTry = -1;
      posOfSecondTry = -1;
      countOfMove = 0;
      points = 0;
		this.host = host;
      continueToPlay = true;
      audio = new Sound();
      mute = false;
      
		// Gets cards arrangement from the server
		init();
      buildGUI();
		
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
   
    /** 
        Sends the card position to server
    */
   class ClickListener extends MouseAdapter 
   {
      public void mousePressed(MouseEvent e) 
      {
         if (myTurn) 
         {
            for(int i = 0; i < cards.length; i++)
            {
               // Sends the signal only when the card's face is up
               if (e.getSource() == cards[i] && !cards[i].isFaceUp())
               {
                  if (countOfMove == 0)
                  {
                     posOfFirstTry = i;
                     countOfMove = 1;
                  }
                  else if (countOfMove == 1)
                  {
                     posOfSecondTry = i;
                     messageArea.setText("Waiting for the other player to move");
                     countOfMove = 0;
                     myTurn = false;
                  }
                  try 
                  {
                     sendMove(i);
                  }
                   catch (Exception ex) 
                  {
                     ex.printStackTrace();
                  }
                  cards[i].reveal();
                  if (!mute)
                  {
                     audio.playMove();
                  }
               }
            }
         }
      }
   }
   
	/**
	     Fills the game board with cards.
	*/
	public void init()
	{
		for (int i = 0; i < cards.length; i++)
		{
			cards[i] = new Card();
         cards[i].addMouseListener(new ClickListener());
		}
	}

    /**
	    Connect to the server.
	*/
	public void connectToServer() 
	{
		try
		{
			// Creates a socket to connect to the server
			socket = new Socket(host, PORT);
			
			// Creates an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Creates an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception e) 
		{
			System.err.println(e);
		}
   }
   
    /**
	    Start a thread for the game.
	*/
   public void startGame()
   {
      // Controls the game on a separate thread
		Thread t = new Thread(this);
		t.start();
   }
	
    /**
	    Runs the game.
	*/
	public void run() 
	{
		try 
		{
            // Which player am I
			int player = fromServer.readInt();
            me = player;
         
		    // Click on the button when player is ready to play
            startButton.setEnabled(true);
			
            // Receives the position from the server for each card on the board 
			for (int i = 0; i < cardNumbers.length; i++)
			{
				cardNumbers[i] = fromServer.readInt();
			}
			
            // Loads up the pic for each card
			for (int i = 0; i < cards.length; i++)
			{
				cards[i].setCardNumber(cardNumbers[i]);
				cards[i].setImage("images/" + cardNumbers[i] + ".jpg");
			}
			
			// Am I player 1 or 2 ?
			if (player == PLAYER1) 
			{
				playerLabel.setText("Player 1");
				messageArea.setText("Waiting for player 2 to join");

				// Receive startup notification from the server
            // Whatever read is ignored
				fromServer.readInt(); 

				// The other player has joined
				messageArea.setText("Player 2 has joined. You start first");

				// It is my turn
				myTurn = true;
			}
			else if (player == PLAYER2) 
			{
				playerLabel.setText("Player 2");
				messageArea.setText("Waiting for player 1 to move");
			}
			
            // Keeps receiving commands and result from the server
			while (continueToPlay) 
			{
            startButton.setEnabled(false);
            
				if (player == PLAYER1) 
				{
               // To cover two cards back or not
               receiveFinalMove();
               // Receive commands or results from the server               
					receiveInfoFromServer(); 
               receiveFirstMove();
               receiveSecondMove();
               receiveFinalMove();
               receiveInfoFromServer();
				}
				else
				{
               receiveFirstMove();
               receiveSecondMove();
               receiveFinalMove();
               receiveInfoFromServer(); 
               receiveFinalMove();
               receiveInfoFromServer();
				}
			}
		}
		catch (Exception e) 
		{
           System.err.println(e);
		}
      finally
      {
         if (!mute)
         {
            audio.playGameOver();
         }
        cleanup();
        return;
      }
	}
   
    /** 
	    Closes all connections.
	*/
   public void cleanup()
   {
      try
      { 
         socket.close(); 
         toServer.close(); 
         fromServer.close();
      }
      catch (Exception e) 
      {
         System.err.println(e);
      }
   }
 
	/** 
	    Send the player's move to the server.
        @param pos the position of the card
	*/
   public void sendMove(int pos) 
		         throws IOException 
	{
      toServer.writeInt(pos); 
      toServer.flush();
	}
   
    /**
        Receives the other player's first move.
    */
    public void receiveFirstMove() 
		         throws IOException 
	{
		// Gets the other player's move
		int posOfFirstTry = fromServer.readInt();
      if (posOfFirstTry == QUIT)
      {
         continueToPlay = false;
         startButton.setEnabled(true);
         myTurn = false;
			messageArea.setText("The other player has quit, so you won!");
      }
      else 
      {
         this.posOfFirstTry = posOfFirstTry;
		   cards[posOfFirstTry].reveal();
      }
	}
   
   /**
            Receives the other player's second move.
      */
	public void receiveSecondMove() 
		         throws IOException 
	{
		int posOfSecondTry = fromServer.readInt();
      if (posOfSecondTry == QUIT)
      {
         continueToPlay = false;
         startButton.setEnabled(true);
         myTurn = false;
			messageArea.setText("The other player has quit, so you won!");
      }
      else
      {
         this.posOfSecondTry = posOfSecondTry;
		   cards[posOfSecondTry].reveal();
      }
	}
   
    /**
        The server decides if both cards match.
    */
	public void receiveFinalMove() 
		   throws IOException 
	{
		int matching = fromServer.readInt();
      points = fromServer.readInt();
      pointsLabel.setText("Points: " + points);
      // Covers the both cards back up
      if (matching == UNMATCHING) 
      {
         cards[posOfFirstTry].cover();
		   cards[posOfSecondTry].cover();
         repaint();
      }
	}
   
    /** 
	    Receives the commands from the server 
	*/
    public void receiveInfoFromServer() 
		   throws IOException 
	{
		// Receives game status
		int status = fromServer.readInt();

		if (status == PLAYER1_WON) 
		{
			// Player 1 won, stop playing
			continueToPlay = false;
         startButton.setEnabled(true);
         myTurn = false;
			if (me == PLAYER1)
			{
				messageArea.setText("You won!");
			}
			else if(me == PLAYER2)
			{
				messageArea.setText("You lose.");
			}
      }
		else if (status == PLAYER2_WON) 
		{
			// Player 2 won, stop playing
			continueToPlay = false;
         startButton.setEnabled(true);
         myTurn = false;
			if (me == PLAYER1)
			{
				messageArea.setText("You lose.");
			}
			else if(me == PLAYER2)
			{
				messageArea.setText("You won!");
			}
		}
		else if (status == CONTINUE)
		{
			messageArea.setText("Your turn");
			myTurn = true;
		}
      else if (status == FREEZE)
      {
         messageArea.setText("Waiting for the other player to move");
      }
	}

    /**
        Builds a GUI
    */
   public void buildGUI()
   {
      pointsLabel = new JLabel("Points: 0");
      playerLabel = new JLabel("Player");
      
      startButton = new JButton("start");
      ActionListener startListener = new StartGameListener();
      startButton.addActionListener(startListener);
      
      quitButton = new JButton("Quit");
      ActionListener quitListener = new ExitGameListener();
      quitButton.addActionListener(quitListener);
      
      muteButton = new JButton("Sound on");
      ActionListener muteListener = new MuteListener();
      muteButton.addActionListener(muteListener);
      
      JPanel controlPanel = new JPanel();
      final int COMPONENTS = 5;
		controlPanel.setLayout(new GridLayout(COMPONENTS, 1));
      controlPanel.add(startButton);
		controlPanel.add(muteButton);
		controlPanel.add(quitButton);
      controlPanel.add(playerLabel);
      controlPanel.add(pointsLabel);
      
      final int AREA_ROWS = 7;
      final int AREA_COLUMNS = 50;
      messageArea = new JTextArea(AREA_ROWS, AREA_COLUMNS);
      messageArea.setEditable(false);
      
      JPanel msgPanel = new JPanel();
		msgPanel.add(messageArea);
      
      setLayout(new BorderLayout());
      add(createCardGrid(), BorderLayout.NORTH);
      add(msgPanel, BorderLayout.EAST);
      add(controlPanel, BorderLayout.WEST);
   }
   
    /**
        Sets up a game grid and places cards.
        @return a game board with cards
    */
	public JPanel createCardGrid()
	{
		JPanel cardPanel = new JPanel(new GridLayout(ROWS, COLS));
		for (Card c : cards)
		{
			cardPanel.add(c);
		}
		return cardPanel;
	}
   
    /**
        A class to exit the game.
    */
   class ExitGameListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         // A player can only quit during his turn
         if (myTurn)
         {   
            startButton.setEnabled(true);
            myTurn = false;
            try
            {
               toServer.writeInt(QUIT);
               toServer.flush();
            }
            catch (Exception ex) 
            {
               ex.printStackTrace();
            }
            continueToPlay = false;
            messageArea.setText("You hit the 'Quit' button, so you lost!");
         }
      }
   }
   
    /**
        A class to mute the sound.
    */
   class MuteListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         if (mute == false)
         {
            mute = true;
            muteButton.setText("Sound off");
         }
         else
         {
            mute = false;
            muteButton.setText("Sound on");
         }
      }
   }
   
    /**
        A class to start the game.
    */
   class StartGameListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         try
         {
            if (!mute)
            {
               audio.playStart();
            }
            resetGameBoard();
            connectToServer();
            startGame();
            toServer.writeInt(START);
            toServer.flush();
            messageArea.setText("Ready");
            startButton.setEnabled(false);
         }
         catch (IOException e)
         {
            System.err.println(e);
         }
      }
   }
   
    /**
        Resets the game board.
    */
   public void resetGameBoard()
   {
      myTurn = false;
      me = 0;
      posOfFirstTry = -1;
      posOfSecondTry = -1;
      countOfMove = 0;
      points = 0;
      continueToPlay = true;
      pointsLabel.setText("Points: 0");
      playerLabel.setText("Player");
      for (Card c : cards)
      {
         c.cover();
      }
      repaint();
   }

    /** 
        This main method creates a GUI for a player 
    */
   public static void main(String[] args) 
   {   
      String serverIP = "localhost";
      if (args.length != 0)
      { 
         if (args[0].equalsIgnoreCase("-server"))
         {
            serverIP = args[1];
         }
         else if (args[0].equalsIgnoreCase("-help"))
         {
            System.out.println("help menu");
            return;
         }
         else
         {
            usage();
            return;
         }
      }
      
      JFrame aPlayer = new Client(serverIP);
      aPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      aPlayer.setVisible(true);
   }
   
   public static void usage()
   {
      System.out.println("Usage 1: java Client -server [Server's IP]");
      System.out.println("Usage 2: java Client -help");
   }
}