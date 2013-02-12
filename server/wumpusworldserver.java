/******************************************

WUMPUS WORLD SERVER.

CREATED BY DAVID LETTIER.

******************************************/


import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javax.swing.JFrame;

import java.util.*;

import java.awt.Dimension;

import java.awt.event.*;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import java.net.*;
import java.io.*;

import java.lang.Thread;

import java.lang.Integer;


///// MAIN ///////////////////////////////////////////////////////////////////

public class wumpusworldserver extends JFrame implements KeyListener
{

	public wumpusworldserver()
	{
		game_board = new board_server();
       	add( game_board );      	
       	
       	this.addKeyListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize( 523, 656 );
		setLocationRelativeTo( null );
		setTitle( "Wumpus World | LETTiER" );
		setVisible( true );
		
		// LOG FILE
		
		try
		{
			File file = new File( "server.log" );
 
			// if file doesnt exists, then create it
			if ( !file.exists( ) )
			{
				file.createNewFile( );
			}
			
			fstream = new FileWriter( file, true );
  			fout = new BufferedWriter( fstream );
  		}
  		catch ( IOException e )
  		{
  			System.out.println( "DEBUG: Could not open log file. Does it exist?" );
  		}
		
		// NETWORKING
		
		try 
		{
			serverSocket = new ServerSocket( 5555 );
		} 
		catch ( IOException e ) 
		{
			System.out.println( "DEBUG: Could not listen on port: 5555!" );
			System.exit( -1 );
		}
		
		
		try 
		{
			System.out.println( "DEBUG: Waiting for client..." );
			clientSocket = serverSocket.accept();
			System.out.println( "DEBUG: Got a client!" );
			
			fout.write( "\n ***** New game started with client from " + clientSocket.getRemoteSocketAddress().toString() + "\n" );
			fout.newLine( );
		} 
		catch ( IOException e ) 
		{
			System.err.println( "DEBUG: Accept failed." );
			System.exit( 1 );
		}
		
		try 
		{		
			out = new PrintWriter( clientSocket.getOutputStream(), true );
			in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream( ) ) );
		}
		catch ( IOException e ) 
		{
			System.out.println( "DEBUG: Could not create writer and reader." );
		}		
		
		comm_thread = new Thread( "comm") 
		{
			public void run()
			{ 	
				communicate( );
			}
		};
		
		comm_thread.start();
	}
	
	public void communicate( )
	{
		System.out.println( "DEBUG: Commnicating with client..." );
		
		inputLine = "";
		outputLine = "Hello, welcome to Wumpus World.";
		out.println( outputLine );
		
		// INITIAL ITEM PLACEMENT MESSAGE
		
		outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 ) ;
		out.println( outputLine );
		
		try
		{	
			while ( ( inputLine = in.readLine( ) ) != null ) 
			{   

				System.out.println( "DEBUG: Client said " + inputLine + "." );
				
				if ( inputLine.equals( "move_left" ) )
				{
					game_board.move_left( );
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 ) ;
					out.println( outputLine );
				}
				else if ( inputLine.equals( "move_up" ) )
				{
					game_board.move_up( );
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 );
					out.println( outputLine );
				}
				else if ( inputLine.equals( "move_right" ) )
				{
					game_board.move_right( );
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 ) ;
					out.println( outputLine );
				}
				else if ( inputLine.equals( "move_down" ) )
				{
					game_board.move_down( );
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 ) ;
					out.println( outputLine );
				}
				else if ( inputLine.equals( "shoot" ) )
				{
					game_board.shoot( );
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 );
					out.println( outputLine );
				}
				else if ( inputLine.equals( "rotate" ) )
				{
					game_board.robot_placement[game_board.robo_y][game_board.robo_x] += 1;
					if ( game_board.robot_placement[game_board.robo_y][game_board.robo_x] == 5 ) game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 1;
					game_board.update();
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 );
					out.println( outputLine );
				}
				else if ( inputLine.substring( 0 , 8 ).equals( "move_to_" ) )
				{
					int node = Integer.parseInt( inputLine.substring( 8 , inputLine.length() ) );
					
					int old_x = game_board.robo_x;
					int old_y = game_board.robo_y;
					
					game_board.robo_y = ( int ) node / 8;
					game_board.robo_x = ( int ) node % 8;
					
					if ( old_x - game_board.robo_x == 1 )
					{
						game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 1; // MOVE LEFT
					}
					else if ( old_x - game_board.robo_x == -1 )
					{
						game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 3; // MOVE RIGHT
					}
					else if ( old_y - game_board.robo_x == game_board.BOARD_SIZE )
					{
						game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 2; // MOVE UP
					}
					else if ( old_y - game_board.robo_x == -game_board.BOARD_SIZE )
					{
						game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 4; // MOVE DOWN
					}
					else
					{
						game_board.robot_placement[game_board.robo_y][game_board.robo_x] = 2; // CAN'T FIGURE OUT CORRECT STANCE SO USE UP
					}	
					
					game_board.robot_placement[old_y][old_x] = 0;	// REMOVE OLD ROBOT'S POSITION
					
					if ( game_board.pit_placement[game_board.robo_y][game_board.robo_x] == 1 ) game_board.game_lost = true;
					if ( game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] == 1 ) game_board.game_lost = true;	
					
					if ( game_board.gold_placement[game_board.robo_y][game_board.robo_x] == 1 ) 
					{
						game_board.has_gold = true;
						game_board.gold_placement[old_y][old_x] = 0;
						game_board.gold_placement[game_board.robo_y][game_board.robo_x] = 1;
					}
					
					if ( game_board.has_gold )
					{
						game_board.gold_placement[old_y][old_x] = 0;
						game_board.gold_placement[game_board.robo_y][game_board.robo_x] = 1;
					}
					
					if ( game_board.has_gold && game_board.robo_y == game_board.HOME_Y && game_board.robo_x == game_board.HOME_X ) game_board.game_won = true;			
					
					outputLine = "~" + game_board.robo_y + game_board.robo_x + game_board.pit_placement[game_board.robo_y][game_board.robo_x] + game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] +  game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] + game_board.gold_placement[game_board.robo_y][game_board.robo_x] + game_board.breezy_placement[game_board.robo_y][game_board.robo_x] + game_board.smelly_placement[game_board.robo_y][game_board.robo_x] + game_board.glittery_placement[game_board.robo_y][game_board.robo_x] + game_board.robot_placement[game_board.robo_y][game_board.robo_x] + ( ( game_board.game_lost ) ? 1 : 0 );
					out.println( outputLine );
					
					game_board.repaint( );					
				}		
				
				outputLine = "Client said \"" + inputLine + "\".";
								
				out.println( outputLine );
				
				fout.write( outputLine );
				fout.newLine( );
		
				if ( game_board.game_lost || game_board.game_won )
				{					
					fout.write( "Client lost: " + game_board.game_lost + " Client won: " + game_board.game_won );
					fout.newLine( );
					
					break;
				}
		
			}
			System.out.println( "DEBUG: Game over, closing socket..." );
			
			fout.write( "Game over, buh bye." );
			fout.newLine( );
			
			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();
			fout.close();
			try
			{
				Thread.sleep( 4000 );
			}
			catch( InterruptedException x ) {}
			System.exit( 0 );
		}
		catch( IOException e )
		{
			System.out.println( "DEBUG: Could not get inputLine from client." );
		}
				
	}

	public void keyPressed( KeyEvent e ) 
	{
	
		if ( !game_board.game_lost && !game_board.game_won )
		{
			/*
			switch ( e.getKeyCode() ) 
			{
				case KeyEvent.VK_LEFT:  
					game_board.move_left( );
					break;
				case KeyEvent.VK_UP:  
					game_board.move_up( );
					break;
				case KeyEvent.VK_RIGHT: 
					game_board.move_right( );
					break;
				case KeyEvent.VK_DOWN:  
					game_board.move_down( );
					break;
				case KeyEvent.VK_SPACE:
					game_board.shoot( );
					break;
			}
			*/
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)  
	{
	}

	public static void main( String[] args )
	{
		new wumpusworldserver( );
	}
	
	board_server game_board;
	
	ServerSocket serverSocket = null;
	Socket clientSocket = null;
	PrintWriter out;
	BufferedReader in;
	
	Thread comm_thread;
	
	String inputLine, outputLine;
	
	FileWriter fstream;
	BufferedWriter fout;
}


///// BOARD ///////////////////////////////////////////////////////////////////

class board_server extends JPanel {

	Image question;
	Image wumpus;
	Image dead_wumpus;
	Image robot_up;
	Image robot_down;
	Image robot_left;
	Image robot_right;
	Image gold;
	Image pit;

	public board_server()
	{
		ImageIcon x = new ImageIcon(this.getClass().getResource("question.gif"));
		question = x.getImage();
		
		ImageIcon y = new ImageIcon(this.getClass().getResource("wumpus.png"));
		wumpus = y.getImage();
		y = new ImageIcon(this.getClass().getResource("dead_wumpus.gif"));
		dead_wumpus = y.getImage();
		
		ImageIcon z = new ImageIcon(this.getClass().getResource("robot_up.gif"));
		robot_up = z.getImage();
		z = new ImageIcon(this.getClass().getResource("robot_down.gif"));
		robot_down = z.getImage();
		z = new ImageIcon(this.getClass().getResource("robot_left.gif"));
		robot_left = z.getImage();
		z = new ImageIcon(this.getClass().getResource("robot_right.gif"));
		robot_right = z.getImage();
		
		ImageIcon w = new ImageIcon(this.getClass().getResource("gold.png"));
		gold = w.getImage();
		
		ImageIcon v = new ImageIcon(this.getClass().getResource("pit.gif"));
		pit = v.getImage();
		
		generate_board();
	}
	
	public void generate_board( )
	{
		do
		{
		
			Random rand = new Random( new Date().getTime() );
			int wumpus_placed = 0;
			int pits_placed = 0;
			int gold_placed = 0;	
			int gold_placed_x = 0;
			int gold_placed_y = 0;
			
			for ( int row = 0; row < BOARD_SIZE; row++) // CLEAR THEM ALL OUT
			{
				for ( int col = 0; col < BOARD_SIZE; col++)
				{
					pit_placement[row][col] = 0;
					wumpus_placement[row][col] = 0;
					gold_placement[row][col] = 0;
					
					breezy_placement[row][col] = 0;
					smelly_placement[row][col] = 0;
					glittery_placement[row][col] = 0;
					
				}
			}	
		
			for ( int row = 0; row < BOARD_SIZE; row++) // PLACE THE PITS
			{
				for ( int col = 0; col < BOARD_SIZE; col++)
				{
					int x = rand.nextInt( 100 );
				
					if ( row == ( BOARD_SIZE - 1 ) && col == 0 ) continue;
				
					if ( x < 20 && x > 0 )
					{
						if ( pits_placed < 9 ) 
						{
							pit_placement[row][col] = PIT;
							pits_placed += 1;				
												
						}
					}
				}
			}
		
			for ( int row = 0; row < BOARD_SIZE; row++) // PLACE THE WUMPUS'S
			{
				for ( int col = 0; col < BOARD_SIZE; col++)
				{
					int x = rand.nextInt( 100 );
				
					if ( row == ( BOARD_SIZE - 1 ) && col == 0 ) continue;
				
					if ( x < 20 && x > 0 )
					{
						if ( wumpus_placed < 2 ) 
						{
							if ( pit_placement[row][col] == PIT ) continue;
							wumpus_placement[row][col] = WUMPUS;
							wumpus_placed += 1;		
										
						}
					}
				}
			}
		
			for ( int row = 0; row < BOARD_SIZE; row++) // PLACE THE GOLD
			{
				for ( int col = 0; col < BOARD_SIZE; col++)
				{
					int x = rand.nextInt( 100 );
				
					if ( row == ( BOARD_SIZE - 1 ) && col == 0 ) continue;
				
					if ( x < 5 && x > 0 )
					{
						if ( gold_placed < 1 ) 
						{
							if ( pit_placement[row][col] == WUMPUS ) continue;
							if ( wumpus_placement[row][col] == WUMPUS ) continue;
							gold_placement[row][col] = GOLD;
							gold_placed += 1;
							gold_placed_x = col;
							gold_placed_y = row;			
											
						}
					}
				}
			}
		
			for ( int row = 0; row < BOARD_SIZE; row++) // REMOVE PITS AND WUMPUS FROM GOLD SPOT
			{
				for ( int col = 0; col < BOARD_SIZE; col++)
				{
					if ( gold_placement[row][col] == GOLD && wumpus_placement[row][col] == WUMPUS )
					{
						wumpus_placement[row][col] = 0;
					}
				
					if ( pit_placement[row][col] == PIT && wumpus_placement[row][col] == WUMPUS )
					{
						pit_placement[row][col] = 0;
					}
				}
			}
			
			//System.out.println( "DEBUG: Solution exists? " + bfs() );
		}
		while( !bfs( ) ); // DOES A SOLUTION EXIST?
		
		for ( int row = 0; row < BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < BOARD_SIZE; col++ )
			{
				if ( pit_placement[row][col] == 1 )
				{
					// PLACE THE BREEZES
							
					try // LEFT Neighbor
					{
						if( pit_placement[row][col-1] != 1 ) breezy_placement[row][col-1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // UP Neighbor
					{
						if( pit_placement[row-1][col] != 1 ) breezy_placement[row-1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // RIGHT Neighbor
					{
						if( pit_placement[row][col+1] != 1 ) breezy_placement[row][col+1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // DOWN Neighbor
					{
						if( pit_placement[row+1][col] != 1 ) breezy_placement[row+1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }	
				}
				
				if ( wumpus_placement[row][col] == 1 )
				{
					// PLACE THE SMELLIES
							
					try // LEFT Neighbor
					{
						if( pit_placement[row][col-1] != 1 ) smelly_placement[row][col-1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // UP Neighbor
					{
						if( pit_placement[row-1][col] != 1 ) smelly_placement[row-1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // RIGHT Neighbor
					{
						if( pit_placement[row][col+1] != 1 ) smelly_placement[row][col+1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // DOWN Neighbor
					{
						if( pit_placement[row+1][col] != 1 ) smelly_placement[row+1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }	
				}
				
				if ( gold_placement[row][col] == 1 )
				{
					// PLACE THE GLITTER
							
					try // LEFT Neighbor
					{
						if ( pit_placement[row][col-1] != 1 ) glittery_placement[row][col-1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // UP Neighbor
					{
						if ( pit_placement[row-1][col] != 1 ) glittery_placement[row-1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // RIGHT Neighbor
					{
						if ( pit_placement[row][col+1] != 1 ) glittery_placement[row][col+1] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }
		
					try // DOWN Neighbor
					{
						if ( pit_placement[row+1][col] != 1 ) glittery_placement[row+1][col] = 1;
					}
					catch( ArrayIndexOutOfBoundsException e ){  }	
				}
			}
		}		
	}
	
	public boolean bfs( )
	{
		ArrayList<Integer> queue = new ArrayList<Integer>();
		boolean solution_exists = false;
		
		int[][] marked = new int[BOARD_SIZE][BOARD_SIZE];
		int[][] nodes = new int[BOARD_SIZE][BOARD_SIZE];
		int[][] relationships = new int[BOARD_SIZE*BOARD_SIZE][BOARD_SIZE*BOARD_SIZE];
		
		int node_count = 0;
		
		for ( int row = 0; row < BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < BOARD_SIZE; col++ )
			{
				marked[row][col] = 0;
			}
		}
		
		for ( int row = 0; row < BOARD_SIZE; row++ ) // GENERATE NODE "NAMES"
		{
			for ( int col = 0; col < BOARD_SIZE; col++ )
			{
				nodes[row][col] = node_count;
				node_count += 1;
			}
		}
		
		for ( int row = 0; row < BOARD_SIZE*BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < BOARD_SIZE*BOARD_SIZE; col++ )
			{
				relationships[row][col] = 0;
			}
		}
		
		for ( int row = 0; row < BOARD_SIZE; row++ ) // GENERATE RELATIONSHIP MATRIX
		{
			for ( int col = 0; col < BOARD_SIZE; col++ )
			{
				try // LEFT Neighbor
				{
					relationships[ nodes[row][col] ][ nodes[row][col-1]  ] = 1;
				}
				catch( ArrayIndexOutOfBoundsException e ){  }
				
				try // UP Neighbor
				{
					relationships[ nodes[row][col] ][ nodes[row-1][col]  ] = 1;
				}
				catch( ArrayIndexOutOfBoundsException e ){  }
				
				try // RIGHT Neighbor
				{
					relationships[ nodes[row][col] ][ nodes[row][col+1]  ] = 1;
				}
				catch( ArrayIndexOutOfBoundsException e ){  }
				
				try // DOWN Neighbor
				{
					relationships[ nodes[row][col] ][ nodes[row+1][col]  ] = 1;
				}
				catch( ArrayIndexOutOfBoundsException e ){  }				 
			}
		}
		
		queue.add( nodes[BOARD_SIZE-1][0 ] );
		marked[BOARD_SIZE-1][0 ] = 1;
		
		while( !queue.isEmpty() ) // BFS ALGO BEGIN
		{
			int node = queue.remove( 0 );
			
			if( gold_placement[ ( int ) node / 8 ][ (int) node % 8 ] == 1 ) // GOLD?
			{
				solution_exists = true;
				break;
			}
			else // NO, SO ADD IT'S NEIGHBORS BUT ONLY IF THEY ARE NOT PITS AND HAVEN'T ALREADY BEEN MARKED
			{
				for ( int i = 0 ; i < BOARD_SIZE*BOARD_SIZE ; i++ )
				{
					if ( relationships[node][i] == 1 && pit_placement[ ( int ) i / 8 ][ (int) i % 8 ] != 1 && marked[ ( int ) i / 8 ][ (int) i % 8 ] != 1 )
					{
						queue.add( nodes[ ( int ) i / 8 ][ (int) i % 8 ] );
						marked[ ( int ) i / 8 ][ (int) i % 8 ] = 1;
					}
				}
			}
		}
		
		for ( int row = 0; row < BOARD_SIZE*BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < BOARD_SIZE*BOARD_SIZE; col++ )
			{
				// System.out.print( relationships[row][col] + " " );
			}
			
			// System.out.print( "\n " );
		}
		
		
		return solution_exists;
	}

	public void paint( Graphics g ) 
	{
		Graphics2D g2d = ( Graphics2D ) g;
		
		g2d.setColor( getBackground() );
		g2d.fillRect( 0, 0, getWidth(), getHeight() );
		
		g2d.setColor( Color.LIGHT_GRAY );
		
		int x, y = 0;
		
		get_senses();

		if ( gold_home )
		{
			game_won = true;
		}
		
		for ( int i = 0 ; i < BOARD_SIZE ; i++ )
		{
			for ( int j = 0 ; j < BOARD_SIZE ; j++ )
			{
				x = j *( TILE_SIZE + 1 );
				y = i *( TILE_SIZE + 1 );
				
				g2d.drawRect( x, y, 64, 64);
				
				g2d.setColor( Color.RED );
				
				if ( breezy_placement[i][j] == 1 )   g2d.drawString( "B",       x + ( TILE_SIZE / 4 ), y + ( TILE_SIZE / 2 ) );
				if ( smelly_placement[i][j] == 1 )   g2d.drawString( "   S",    x + ( TILE_SIZE / 4 ), y + ( TILE_SIZE / 2 ) );
				if ( glittery_placement[i][j] == 1 ) g2d.drawString( "      G", x + ( TILE_SIZE / 4 ), y + ( TILE_SIZE / 2 ) );
				
				g2d.setColor( Color.LIGHT_GRAY );
				
				if ( pit_placement[i][j] == PIT ) g2d.drawImage( pit, x, y, null );
				
				if ( wumpus_placement[i][j] == WUMPUS ) g2d.drawImage( wumpus, x, y, null );
				
				if ( dead_wumpus_placement[i][j] == DEAD_WUMPUS ) g2d.drawImage( dead_wumpus, x, y, null );
				
				if ( gold_placement[i][j] == GOLD ) g2d.drawImage( gold, x, y, null );
				
				
				if ( robot_placement[i][j] == ROBO_LEFT ) g2d.drawImage( robot_left, x, y, null );
				else if ( robot_placement[i][j] == ROBO_UP ) g2d.drawImage( robot_up, x, y, null );
				else if ( robot_placement[i][j] == ROBO_RIGHT ) g2d.drawImage( robot_right, x, y, null );
				else if ( robot_placement[i][j] == ROBO_DOWN ) g2d.drawImage( robot_down, x, y, null );				
			}
		}
		
		if ( game_lost )
		{
			g.setFont( new Font( "Arial", Font.BOLD, 60 ) );
			g2d.setColor( Color.RED );
			g2d.drawString("GAME LOST!", getWidth()/2 - 180, getHeight()/2 - 50 );
		}
		else if ( game_won )
		{
			g.setFont( new Font( "Arial", Font.BOLD, 60 ) );
			g2d.setColor( Color.BLUE );
			g2d.drawString("GAME WON!", getWidth()/2 - 180, getHeight()/2 - 50 );
		}
		else
		{
			g2d.setColor( Color.BLACK );
			g2d.drawString("ROBOT LCD:", 5, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 30 );
			g2d.drawString("Currently sensing:" + senses, 10, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 50 );
			g2d.drawString("Arrows Left:" + arrows, 10, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 70 );
			g2d.setColor( Color.LIGHT_GRAY );
		}
	}
	
	public void get_senses( )
	{
		
		senses = "";
		
		if ( smelly_placement[robo_y][robo_x] == 1  )
		{
			senses += "  -a horrible smell-  ";
		}
		
		if ( breezy_placement[robo_y][robo_x]  == 1  )
		{
			senses += "  -a breeze-  ";
		}
		
		if ( glittery_placement[robo_y][robo_x] == 1   )
		{
			senses += "  -glitter-  ";
		}
		
		
		if ( has_gold && !gold_home )
		{
			senses = "";
		}
		
	}
	
	public void move_left( )
	{
		int next = 0;
		
		try
		{
			next = robot_placement[robo_y][robo_x-1];
		}
		catch( ArrayIndexOutOfBoundsException e ){ game_lost = true; repaint(); return;  }
		
		
		if ( wumpus_placement[robo_y][robo_x-1] == WUMPUS )
		{
			game_lost = true;
		}
		else if ( pit_placement[robo_y][robo_x-1] == PIT )
		{
			game_lost = true;
		}
		else if ( gold_placement[robo_y][robo_x-1] == GOLD )
		{
			has_gold = true;
		}
		
		robot_placement[robo_y][robo_x] = 0;
		
		robot_placement[robo_y][robo_x-1] = 1;
		
		robo_x = robo_x-1;	
		
		if ( has_gold )
		{
			for( int row = 0 ; row < BOARD_SIZE ; row++ )
			{
				for( int col = 0 ; col < BOARD_SIZE ; col++ )
				{
					gold_placement[row][col]= 0;
				}
			}
			
			gold_placement[robo_y][robo_x] = 1;
		}
		
		if ( robo_x == HOME_X && robo_y == HOME_Y && has_gold )
		{
			game_won = true;
		}
		
		repaint();		
	}
	
	public void move_up( )
	{
		int next = 0;
		
		try
		{
			next = robot_placement[robo_y-1][robo_x];
		}
		catch( ArrayIndexOutOfBoundsException e ){ game_lost = true; repaint(); return;  }
		
		
		if ( wumpus_placement[robo_y-1][robo_x] == WUMPUS )
		{
			game_lost = true;
		}
		else if ( pit_placement[robo_y-1][robo_x] == PIT )
		{
			game_lost = true;
		}
		else if ( gold_placement[robo_y-1][robo_x] == GOLD )
		{
			has_gold = true;
		}
				
		robot_placement[robo_y][robo_x] = 0;
		
		robot_placement[robo_y-1][robo_x] = 2;
		
		robo_y = robo_y-1;	
		
		if ( has_gold )
		{
			for( int row = 0 ; row < BOARD_SIZE ; row++ )
			{
				for( int col = 0 ; col < BOARD_SIZE ; col++ )
				{
					gold_placement[row][col]= 0;
				}
			}
			
			gold_placement[robo_y][robo_x] = 1;
		}
		
		if ( robo_x == HOME_X && robo_y == HOME_Y && has_gold )
		{
			game_won = true;
		}
		
		repaint();	
	}
	
	public void move_right( )
	{
		int next = 0;
		
		try
		{
			next = robot_placement[robo_y][robo_x+1];
		}
		catch( ArrayIndexOutOfBoundsException e ){ game_lost = true; repaint(); return;  }
		
		
		if ( wumpus_placement[robo_y][robo_x+1] == WUMPUS )
		{
			game_lost = true;
		}
		else if ( pit_placement[robo_y][robo_x+1] == PIT )
		{
			game_lost = true;
		}
		else if ( gold_placement[robo_y][robo_x+1] == GOLD )
		{
			has_gold = true;
		}
				
		robot_placement[robo_y][robo_x] = 0;
		
		robot_placement[robo_y][robo_x+1] = 3;
		
		robo_x = robo_x + 1;	
		
		if ( has_gold )
		{
			for( int row = 0 ; row < BOARD_SIZE ; row++ )
			{
				for( int col = 0 ; col < BOARD_SIZE ; col++ )
				{
					gold_placement[row][col]= 0;
				}
			}
			
			gold_placement[robo_y][robo_x] = 1;
		}
		
		if ( robo_x == HOME_X && robo_y == HOME_Y && has_gold )
		{
			game_won = true;
		}
		
		repaint();	
	}
	
	public void move_down( )
	{
		int next = 0;
		
		try
		{
			next = robot_placement[robo_y+1][robo_x];
		}
		catch( ArrayIndexOutOfBoundsException e ){ game_lost = true; repaint(); return;  }
		
		
		if ( wumpus_placement[robo_y+1][robo_x] == WUMPUS )
		{
			game_lost = true;
		}
		else if ( pit_placement[robo_y+1][robo_x] == PIT )
		{
			game_lost = true;
		}
		else if ( gold_placement[robo_y+1][robo_x] == GOLD )
		{
			has_gold = true;
		}
				
		robot_placement[robo_y][robo_x] = 0;
		
		robot_placement[robo_y+1][robo_x] = 4;
		
		robo_y = robo_y + 1;	
		
		if ( has_gold )
		{
			for( int row = 0 ; row < BOARD_SIZE ; row++ )
			{
				for( int col = 0 ; col < BOARD_SIZE ; col++ )
				{
					gold_placement[row][col]= 0;
				}
			}
			
			gold_placement[robo_y][robo_x] = 1;
		}
		
		if ( robo_x == HOME_X && robo_y == HOME_Y && has_gold )
		{
			game_won = true;
		}
		
		repaint();	
	}
	
	public void shoot( )
	{
		arrows -= 1;
		
		int next = 0;		
		int next_x = 0;
		int next_y = 0;
		
		if ( arrows < 0 )
		{
			arrows = 0;
			repaint();
			return;
		}
		
		try
		{
			if ( robot_placement[robo_y][robo_x] == 1 && wumpus_placement[robo_y][robo_x-1] == 1 ) // FACING LEFT
			{
				wumpus_placement[robo_y][robo_x-1] = 0;
				dead_wumpus_placement[robo_y][robo_x-1] = 1;
			}
			else if ( robot_placement[robo_y][robo_x] == 2 && wumpus_placement[robo_y-1][robo_x] == 1 ) // FACING UP
			{
				wumpus_placement[robo_y-1][robo_x] = 0;
				dead_wumpus_placement[robo_y-1][robo_x] = 1;
			}
			else if ( robot_placement[robo_y][robo_x] == 3 && wumpus_placement[robo_y][robo_x+1] == 1 ) // FACING RIGHT
			{
				wumpus_placement[robo_y][robo_x+1] = 0;
				dead_wumpus_placement[robo_y][robo_x+1] = 1;
			}
			else if ( robot_placement[robo_y][robo_x] == 4 && wumpus_placement[robo_y+1][robo_x] == 1 ) // FACING DOWN
			{
				wumpus_placement[robo_y+1][robo_x] = 0;
				dead_wumpus_placement[robo_y+1][robo_x] = 1;
			}

		}
		catch( ArrayIndexOutOfBoundsException e ){ /*System.out.println( "DEBUG: Out of bounds.");*/  }
		
		repaint();		
	}
	
	public void update( )
	{
		repaint();
	}
	
	int[][] board =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] pit_placement =   {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] wumpus_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] dead_wumpus_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] gold_placement = {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] breezy_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] smelly_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] glittery_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0}
				};
				
	int[][] robot_placement =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {2,0,0,0,0,0,0,0}
				};
	
	static final int QUESTION    = 0;
	static final int WUMPUS      = 1;
	static final int DEAD_WUMPUS = 1;	
	static final int PIT         = 1;
	static final int GOLD        = 1;
	
	static final int ROBO_LEFT   = 1;
	static final int ROBO_UP     = 2;
	static final int ROBO_RIGHT  = 3;
	static final int ROBO_DOWN   = 4;
	
	static final int WUMPUS_LIMIT = 2;
	static final int PIT_LIMIT    = 10;
	static final int GOLD_LIMIT   = 1;
	
	static final int BOARD_SIZE = 8;	
	static final int TILE_SIZE  = 64;
	
	String senses = " -nothing- ";
	int arrows    = 2;
	
	int robo_x = 0;
	int robo_y = 7;
	
	static final int HOME_X = 0;
	static final int HOME_Y = 7;
	
	boolean game_lost = false;
	boolean has_gold  = false;
	boolean gold_home = false;
	boolean game_won  = false;
	int wumpus_dead   = 0;
}
