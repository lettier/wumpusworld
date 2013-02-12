/******************************************

WUMPUS WORLD CLIENT.

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
import java.awt.CheckboxGroup;
import java.awt.Checkbox;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.Random;

import java.util.Date;

import java.io.*;
import java.net.*;

import java.lang.Thread;

import java.lang.Integer;


///// MAIN ///////////////////////////////////////////////////////////////////

public class wumpusworldclient extends JFrame implements KeyListener, FocusListener, MouseListener, ItemListener
{

	public wumpusworldclient( String IP ) 
	{
		
		setLayout( new BorderLayout( ) );
		
		game_control = new control_client( ); 
		game_board = new board_client();

		game_board.addFocusListener(this);
		game_control.addFocusListener(this);
		
		//game_board.setFocusable(true);
		//game_control.setFocusable(true);
		
		game_board.addKeyListener( this );
		
		game_control.addMouseListener( this );  
       	game_board.addMouseListener( this ); 
       	
       	game_control.ai.addItemListener( this );
       	game_control.user.addItemListener( this );
       	
       	game_board.setVisible( false );

 		add( game_control, "North");		
       	add( game_board, "Center" );  
       	
       	game_control.requestFocusInWindow();
       	game_control.requestFocus();

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 523, 656 );
		setLocationRelativeTo( null );
		setTitle( "Wumpus World | LETTiER" );
		setVisible( true );
		
		// LOG FILE
		
		try
		{
			File file = new File( "client.log" );
 
			// if file doesnt exists, then create it
			if ( !file.exists( ) )
			{
				file.createNewFile( );
			}
			else if ( file.exists( ) )
			{
				file.delete();
				
				file = new File( "client.log" );
			}
			
			fstream = new FileWriter( file, true );
  			fout = new BufferedWriter( fstream );
  		}
  		catch ( IOException e )
  		{
  			System.out.println( "DEBUG: Could not open log file. Does it exist?" );
  		}
		
		//NETWORK
		
		try 
		{
			socket = new Socket( IP, 5555 );
			out = new PrintWriter( socket.getOutputStream( ), true );
			in = new BufferedReader( new InputStreamReader( socket.getInputStream( ) ) );
		} 
		catch ( UnknownHostException e ) 
		{
			System.err.println( "DEBUG: Unkown host.");
			System.exit( 1 );
		} 
		catch ( IOException e ) 
		{
			System.err.println( "DEBUG: Couldn't get I/O for the connection to host." );
			System.exit( 1 );
		}
		
		ai_thread = new Thread( "run_ai") 
		{
			public void run()
			{ 	
				run_ai( );
			}
		};
		
		comm_thread = new Thread( "comm") 
		{
			public void run()
			{ 	
				communicate( );
			}
		};		
	}
	
	public void communicate( )
	{
		System.out.println( "DEBUG: Commnicating with server..." );
		
		try
		{
		
			fout.write( " ***** Connected to server at " + socket.getRemoteSocketAddress().toString() + "\n" );
			fout.newLine( );
		}
		catch ( IOException e ) {}
		
		inputLine = "";
		outputLine = "";

		
		try
		{	
			while ( ( inputLine = in.readLine( ) ) != null ) 
			{   
	
				System.out.println( "DEBUG: Server said \"" + inputLine + "\"." );
				
				fout.write( "Server said \"" + inputLine + "\"." );
				fout.newLine( );
				
				if( inputLine.substring( 0, 1 ).equals( "~" ) )
				{
					game_board.robo_y = Integer.parseInt( inputLine.substring( 1, 2 ) );
					game_board.robo_x = Integer.parseInt( inputLine.substring( 2, 3 ) );
					
					for ( int row = 0; row < game_board.BOARD_SIZE; row++ ) // CLEAR OUT PREVIOUS ROBOT POSITIONS
					{
						for ( int col = 0; col < game_board.BOARD_SIZE; col++ )
						{
							game_board.robot_placement[row][col] = 0;
						}
					}
					
					for ( int row = 0; row < game_board.BOARD_SIZE; row++ ) // CLEAR OUT PREVIOUS GOLD POSITIONS WHEN THE ROBOT HAS THE GOLD
					{
						for ( int col = 0; col < game_board.BOARD_SIZE; col++ )
						{
							game_board.gold_placement[row][col] = 0;
						}
					}
					
					game_board.mask[game_board.robo_y][game_board.robo_x] = 1;					
					
					game_board.pit_placement[game_board.robo_y][game_board.robo_x]         = Integer.parseInt( inputLine.substring(  3,  4 ) );
					game_board.wumpus_placement[game_board.robo_y][game_board.robo_x]      = Integer.parseInt( inputLine.substring(  4,  5 ) );
					game_board.dead_wumpus_placement[game_board.robo_y][game_board.robo_x] = Integer.parseInt( inputLine.substring(  5,  6 ) );
					game_board.gold_placement[game_board.robo_y][game_board.robo_x]        = Integer.parseInt( inputLine.substring(  6,  7 ) );
					game_board.breezy_placement[game_board.robo_y][game_board.robo_x]      = Integer.parseInt( inputLine.substring(  7,  8 ) );
					game_board.smelly_placement[game_board.robo_y][game_board.robo_x]      = Integer.parseInt( inputLine.substring(  8,  9 ) );
					game_board.glittery_placement[game_board.robo_y][game_board.robo_x]    = Integer.parseInt( inputLine.substring(  9, 10 ) );
					game_board.robot_placement[game_board.robo_y][game_board.robo_x]       = Integer.parseInt( inputLine.substring( 10, 11 ) );
					
					game_board.game_lost = ( ( Integer.parseInt( inputLine.substring( 11, 12 ) ) == 1 ) ? true : false );					
					
					if ( game_board.gold_placement[game_board.robo_y][game_board.robo_x] == 1 ) game_board.has_gold = true;
					
					if ( game_board.pit_placement[game_board.robo_y][game_board.robo_x] == 1 ) game_board.game_lost = true;
					if ( game_board.wumpus_placement[game_board.robo_y][game_board.robo_x] == 1 ) game_board.game_lost = true;
					
					if ( game_board.has_gold && game_board.robo_y == game_board.HOME_Y && game_board.robo_x == game_board.HOME_X ) game_board.game_won = true;
					
					game_board.repaint( );
				}
		
				if ( game_board.game_lost || game_board.game_won )
				{
					break;
				}
		
			}
			System.out.println( "DEBUG: Game over, closing socket..." );
			
			fout.write( "Game over, buh bye." );
			fout.newLine( );
			
			fout.close();
			out.close();
			in.close();
			socket.close();
			try
			{
				Thread.sleep( 4000 );
			}
			catch( InterruptedException x ) {}
			System.exit( 0 );
		}
		catch( IOException e)
		{
			System.out.println( "DEBUG: Could not get inputLine from server." );
		}		
	}
	

	public void keyPressed( KeyEvent e ) 
	{
	
		if ( !game_board.game_lost && !game_board.game_won && !game_control.ai_yes && game_board.start_game )
		{
			outputLine = "";
			
			int sleep = 100;
			
			switch ( e.getKeyCode() ) 
			{
				case KeyEvent.VK_LEFT:  					
					outputLine = "move_left";
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					//game_board.move_left( );
					break;
				case KeyEvent.VK_UP: 					
					outputLine = "move_up";
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					//game_board.move_up( );
					break;
				case KeyEvent.VK_RIGHT: 
					outputLine = "move_right";
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					//game_board.move_right( );
					break;
				case KeyEvent.VK_DOWN:
					outputLine = "move_down";
					out.println( outputLine ); 
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {} 
					//game_board.move_down( );
					break;
				case KeyEvent.VK_SPACE:
					if ( game_board.arrows == 0 ) break;
					outputLine = "shoot";
					game_board.arrows -= 1;
					if ( game_board.arrows < 0 ) game_board.arrows = 0;
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					//game_board.shoot( );
					break;
				case KeyEvent.VK_R:
					outputLine = "rotate";
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					break;
			}
		}
		
		//System.out.println( "DEBUG: Key pressed." );
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)  
	{	
	}
	
	public void focusGained( FocusEvent e )
	{
		System.out.println( "DEBUG: " + e.getComponent().getClass().getName() + " gained focus.");
	}
	
	public void focusLost(FocusEvent e)
	{
		System.out.println( "DEBUG: " + e.getComponent().getClass().getName() + " lost focus." );
	}
	
	public void mousePressed( MouseEvent e ) 
	{
		
	}

	public void mouseReleased( MouseEvent e ) 
	{

	}

	public void mouseEntered( MouseEvent e ) 
	{

	}

	public void mouseExited( MouseEvent e ) 
	{

	}

	public void mouseClicked( MouseEvent e )
	{
		if ( e.getSource() == game_board )
		{
			game_board.requestFocusInWindow();
		}
		
		System.out.println( "DEBUG: " + e.getSource() );
	}
	
	public void itemStateChanged( ItemEvent e )
	{
		if ( e.getSource() == game_control.ai || e.getSource() == game_control.user )
		{
			game_control.ai.setVisible( false );
			game_control.user.setVisible( false );
			
			game_board.setVisible( true );
			
			game_board.requestFocusInWindow();
			
			game_board.repaint();
			
			game_board.start_game = true;
			
			if ( game_control.user_yes )
			{
				game_board.show_user_controls = true;
				comm_thread.start();
			}	
			else if ( game_control.ai_yes )
			{
				ai_thread.start();
				comm_thread.start();				
			}		
		}
	}
	
	public void run_ai( )
	{
		
		int flip_flop = new Random( new Date().getTime() ).nextInt( 2 ) ; 
		
		ArrayList<Integer> stack = new ArrayList<Integer>();
		boolean solution_exists = false;
		
		int[][] marked = new int[game_board.BOARD_SIZE][game_board.BOARD_SIZE];
		int[][] nodes = new int[game_board.BOARD_SIZE][game_board.BOARD_SIZE];
		int[][] relationships = new int[game_board.BOARD_SIZE*game_board.BOARD_SIZE][game_board.BOARD_SIZE*game_board.BOARD_SIZE];
		
		int node_count = 0;
		
		int sleep = 3000;
		
		for ( int row = 0; row < game_board.BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < game_board.BOARD_SIZE; col++ )
			{
				marked[row][col] = 0;
			}
		}
		
		for ( int row = 0; row < game_board.BOARD_SIZE; row++ ) // GENERATE NODE "NAMES"
		{
			for ( int col = 0; col < game_board.BOARD_SIZE; col++ )
			{
				nodes[row][col] = node_count;
				node_count += 1;
			}
		}
		
		for ( int row = 0; row < game_board.BOARD_SIZE*game_board.BOARD_SIZE; row++ )
		{
			for ( int col = 0; col < game_board.BOARD_SIZE*game_board.BOARD_SIZE; col++ )
			{
				relationships[row][col] = 0;
			}
		}
		
		for ( int row = 0; row < game_board.BOARD_SIZE; row++ ) // GENERATE RELATIONSHIP MATRIX
		{
			for ( int col = 0; col < game_board.BOARD_SIZE; col++ )
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
		
		stack.add( 0, nodes[game_board.BOARD_SIZE-1][0 ] );
		marked[game_board.BOARD_SIZE-1][0 ] = 1;
		
		try
		{
			Thread.sleep( sleep );
		}
		catch( InterruptedException x ) {}
		
		

		
		while( !stack.isEmpty() ) // DFS ALGO BEGIN
		{
			int node = stack.get( 0 );					
			
			if( game_board.smelly_placement[ ( int ) node / 8 ][ ( int ) node % 8 ] == 1 )
			{
				if ( game_board.arrows != 0 )
				{
					outputLine = "shoot";
					out.println( outputLine );
					try
					{
						Thread.sleep( sleep );
					}
					catch( InterruptedException x ) {}
					
					game_board.arrows -= 1;
					if ( game_board.arrows < 0 ) game_board.arrows = 0;
				}
			}
			
			
			if( game_board.gold_placement[ ( int ) node / 8 ][ ( int ) node % 8 ] == 1 ) // GOLD?
			{
				outputLine = "move_to_" + game_board.BOARD_SIZE*( game_board.BOARD_SIZE - 1 ); // GO TO HOME NODE
				out.println( outputLine );
				try
				{
					Thread.sleep( sleep );
				}
				catch( InterruptedException x ) {}				
				break;
			}
			else // NO, SO ADD IT'S NEIGHBORS BUT ONLY IF THEY ARE NOT UNMASKED AND HAVEN'T ALREADY BEEN MARKED
			{
				boolean added = false;
				
				if ( flip_flop == 0 ) 
				{
					for ( int i = 0 ; i < game_board.BOARD_SIZE*game_board.BOARD_SIZE ; i++ )
					{
						if ( relationships[node][i] == 1 && game_board.mask[ ( int ) i / 8 ][ (int) i % 8 ] != 1 && marked[ ( int ) i / 8 ][ (int) i % 8 ] != 1 )
						{
							stack.add( 0,  nodes[ ( int ) i / 8 ][ (int) i % 8 ] );
							marked[ ( int ) i / 8 ][ (int) i % 8 ] = 1;
							added = true;
						}
					}
				}
				else // ADD RELATIONSHIPS IN REVERSE TO STACK
				{
					for ( int i = ( game_board.BOARD_SIZE*game_board.BOARD_SIZE - 1 ) ; i >= 0 ; i-- )
					{
						if ( relationships[node][i] == 1 && game_board.mask[ ( int ) i / 8 ][ (int) i % 8 ] != 1 && marked[ ( int ) i / 8 ][ (int) i % 8 ] != 1 )
						{
							stack.add( 0,  nodes[ ( int ) i / 8 ][ (int) i % 8 ] );
							marked[ ( int ) i / 8 ][ (int) i % 8 ] = 1;
							added = true;
						}
					}
				}
				
				if ( !added ) stack.remove( 0 );
			}
			
			node = stack.get( 0 );
			
			outputLine = "move_to_" + node;	
			out.println( outputLine );			
			try
			{
				Thread.sleep( sleep );
			}
			catch( InterruptedException x ) {}	
		}
	}

	public static void main( String[] args )
	{
		new wumpusworldclient( args[0] );
	}
	
	board_client game_board;
	control_client game_control;
	
	Thread ai_thread;
	
	Thread comm_thread;
	
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	
	String inputLine, outputLine;
	
	FileWriter fstream;
	BufferedWriter fout;
}

///// CONTROL ///////////////////////////////////////////////////////////////////

class control_client extends JPanel implements ItemListener
{
	CheckboxGroup cbg;   	
	Checkbox ai;
	Checkbox user;
	
	boolean ai_yes = false;
	boolean user_yes = false;
	boolean selected_already = false;
	
	public control_client( )
	{
		cbg   = new CheckboxGroup();       	
		ai    = new Checkbox( " Use AI for the robot.", cbg, false);
		user  = new Checkbox( " You control the robot.", cbg, false);	
		
		ai.addItemListener( this );
		user.addItemListener( this );
		
		add( ai );
		add( user );		
	}
	
	public void itemStateChanged( ItemEvent e ) 
	{
		if ( !selected_already )
		{
			if( e.getSource() == ai ) { ai_yes = true; user_yes = false; System.out.println( "DEBUG: AI control was selected."); }
			else if ( e.getSource() == user ) { user_yes = true; ai_yes = false; System.out.println( "DEBUG: User control was selected."); }
			selected_already = true;
		}
		else
		{
			if ( ai_yes )
			{
				ai.setState( true );
				user.setState( false );
			}
			else if ( user_yes )
			{
				ai.setState( false );
				user.setState( true );
			}
		}
	}
}

///// BOARD ///////////////////////////////////////////////////////////////////

class board_client extends JPanel
{

	Image question;
	Image wumpus;
	Image dead_wumpus;
	Image robot_up;
	Image robot_down;
	Image robot_left;
	Image robot_right;
	Image gold;
	Image pit;

	public board_client()
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
		
		System.out.println( "DEBUG: board_client() created.");
		

	}

	public void paint( Graphics g ) 
	{
		System.out.println( "DEBUG: paint() called.");
		
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
				
				if ( mask[i][j] == 0 ) g2d.drawImage( question, x, y, null );				
						
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
			g.setColor( Color.BLACK );
			
			g2d.drawString("ROBOT LCD:", 5, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 30 );
			g2d.drawString("Currently sensing:" + senses, 10, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 50 );
			g2d.drawString("Arrows Left:" + arrows, 10, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 70 );
			
			if ( show_user_controls )
			{
				g2d.drawString("USER CONTROLS:", 290, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 30 );
				g2d.drawString("Move - Arrow Left Up Right Down", 300, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 50 );
				g2d.drawString("Shoot - Spacebar", 300, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 70 );
				g2d.drawString("Rotate - R", 300, ( ( BOARD_SIZE  ) * TILE_SIZE ) + 90 );
			}
			
			g.setColor( Color.LIGHT_GRAY );
		}
	}
	
	public void get_senses( )
	{
		senses = "";
		
		if ( breezy_placement[robo_y][robo_x] == 1 )   senses += " -a breeze- ";
		if ( smelly_placement[robo_y][robo_x] == 1 )   senses += " -a horrible smell- ";
		if ( glittery_placement[robo_y][robo_x] == 1 ) senses += " -glitter- ";
		
	}
	
	public void move_left( )
	{
		System.out.println( "DEBUG: Moving left." );
		
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
		
		mask[robo_y][robo_x]= 1;
		
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
		
		System.out.println( "DEBUG: Moving up." );
		
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
		
		mask[robo_y][robo_x]= 1;
		
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
		
		System.out.println( "DEBUG: Moving right." );
		
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
		
		mask[robo_y][robo_x]= 1;
		
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
		System.out.println( "DEBUG: Moving down." );
		
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
		
		mask[robo_y][robo_x]= 1;
		
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
		
		System.out.println( "DEBUG: Shooting." );
		
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
		catch( ArrayIndexOutOfBoundsException e ){ /* System.out.println( "DEBUG: Out of bounds."); */  }
		
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
				
	int[][] mask =  {
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {0,0,0,0,0,0,0,0},
					 {1,0,0,0,0,0,0,0}
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
	
	static final int BOARD_SIZE  = 8;	
	static final int TILE_SIZE   = 64;	

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
	boolean show_user_controls = false;
	boolean start_game = false;
	
	int wumpus_dead   = 0;
}
