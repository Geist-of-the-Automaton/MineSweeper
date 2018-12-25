// by Auden Childress

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MineSweeper extends JFrame implements ActionListener 
{
	public static final boolean Is_JAR = false;
	
	private static final byte singleDimension = 10;
	private space board[][] = new space[singleDimension][singleDimension];
	private byte safeSpaces = 0;
	enum diffs {easy, medium, hard};
	diffs d = diffs.medium;
	private boolean rainbowMode = false;
	
    public MineSweeper () 
    {
    	setWindowProperties ();
    	setComponentProperties ();
    }
    
    private void setWindowProperties () 
    {
    	setIconImage (Toolkit.getDefaultToolkit().getImage ("MSicon.attribute"));
    	setSize(450,500);
    	setLayout(new GridLayout (singleDimension, singleDimension));
    	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible (true);
		getContentPane().setBackground (Color.lightGray);
		if (Is_JAR) 
			setSize(getWidth() + 10, getHeight() + 10);
		setResizable (false);
    }
    
	private void setComponentProperties () 
	{
		JMenuBar bar = new JMenuBar ();
		JMenu downMenu = new JMenu ("Options");
		bar.add(downMenu);
		String downMenuItems[] = {"Reset", "View Board", "Difficulty", "About", "Exit"};
		for (String name : downMenuItems) 
		{
			if (name.equals("Difficulty")) 
			{
				JMenu it = new JMenu (name);
				String diffs[]= {"Over Easy", "Scrambled", "Hard Boiled"};
				for (String d : diffs) 
				{
					JMenuItem i = new JMenuItem(d);
					i.addActionListener(this);
					it.add(i);
				}
				downMenu.add(it);
			}
			else
			{
				JMenuItem it = new JMenuItem (name);
				it.addActionListener (this);
				if (name.equals ("Exit")) 
					downMenu.addSeparator ();
				downMenu.add (it);
			}
		}
		bar.setVisible (true);
		setJMenuBar (bar);
		boardSetup ();
	}
	
	private void boardSetup () 
	{
		setTitle (" Mine Sweeper - Medium");
		for (byte y = 0; y < singleDimension; y++)
			for (byte x = 0; x < singleDimension; x++) 
			{
				board[y][x] = new space();
				add(board[y][x]);
			}
		getContentPane().doLayout();
		reset ();
	}
	
	private void reset () 
	{
		resetBoard ();
		countSurrounding ();
	}
	
	private void resetBoard () 
	{
		safeSpaces = 0;
		for (space[] arr : board)
			for (space it : arr)
				it.reset ();
	}
	
	private void countSurrounding () 
	{
		for (int Y = 0; Y < board.length; Y++)
			for (int X = 0; X < board[Y].length; X++)
				for (byte vert = 0; vert < 3; vert++)
					for (byte hori = 0; hori < 3; hori++) {
						try 
						{
							if (rainbowMode)
								board[Y][X].setBackground(new Color (255-25*Y, 25 * (Y+X) / 2, 0+25*X, 255));
							if (board[Y-1+vert][X-1+hori].isMine)
								board[Y][X].surroundingMines++;
						} 
						catch (Exception any) 
						{/*out of bounds*/}
					}
	}
	
	public void theEndGame (boolean endGameStatus) 
	{
		for (space[] arr : board)
			for (space it : arr)
				if (it.isMine)
					it.reveal (true);
		String text = endGameStatus ? "You won! Play again?" : "You lost. Play again?";
		int choice = JOptionPane.showConfirmDialog (this, text, "Results", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.NO_OPTION)
			System.exit (0);
		if (endGameStatus && d == diffs.hard)
			rainbowMode = true;
		reset ();
	}
	
    public static void main (String... cheese) 
    {
    	try 
    	{
			UIManager.setLookAndFeel ("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
    	catch (Exception any) 
    	{/*UI not found*/}
    	EventQueue.invokeLater (() -> 
    	{
            new MineSweeper ();
        });
    }
    
	@Override
	public void actionPerformed (ActionEvent evt) 
	{
		String Pressed = evt.getActionCommand ();
		switch (Pressed) {
		case "Over Easy":
			d = diffs.easy;
			setTitle (" Mine Sweeper - Easy");
			reset();
			break;
		case "Scrambled":
			d = diffs.medium;
			setTitle (" Mine Sweeper - Medium");
			reset();
			break;
		case "Hard Boiled":
			d = diffs.hard;
			setTitle (" Mine Sweeper - Hard");
			reset();
			break;
		case "View Board":
			for (space[] arr : board)
				for (space it : arr)
					it.reveal (true);
			theEndGame (false);
			break;
		case "About":
			JOptionPane.showOptionDialog(this, "This was made by Auden Childress.\n Thanks for playing!", "About", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, null, null);
			break;
		case "Reset":
			reset ();
			break;
		case "Exit":
			System.exit (0);
		}
	}
	
	private class space extends JButton implements ActionListener, MouseListener
	{
		private byte surroundingMines;
		private boolean isMine;
		private boolean hidden = true;
		
		space () 
		{
			super ();
			addActionListener (this);
			addMouseListener(this);
			this.reset ();
		}
		
		public void reset () 
		{	
			hidden = true;
			Random rng = new Random();
			isMine = rng.nextBoolean();
			if (d == diffs.medium)
				isMine = isMine && rng.nextBoolean();
			else if (d == diffs.easy)
				isMine = isMine && rng.nextBoolean() && rng.nextBoolean();
			if (!isMine)
				safeSpaces++;
			surroundingMines = 0;
			setBackground (Color.white);
			setText ("");
		}
		
		@Override
		public void actionPerformed (ActionEvent arg0) 
		{
			reveal(false);
		}
		
		public void reveal (boolean atEndGame) 
		{
			if (isMine && hidden) 
			{
				setBackground (Color.red);
				if (!atEndGame)
					theEndGame (false);
			}
			else if (hidden)
			{
				hidden = false;
				setBackground(new Color(192+63*((surroundingMines-4)/4), 255-(31*surroundingMines)-surroundingMines+surroundingMines/8, 32+32*((surroundingMines-4)/4), 255));
				setText (surroundingMines+"");
				safeSpaces--;
				if (safeSpaces == 0 && !atEndGame)
					theEndGame (true);
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) 
		{
			if (SwingUtilities.isRightMouseButton(arg0) && hidden)
			{
				hidden = false;
				if (isMine) 
					setBackground (Color.red);
				else
					theEndGame (false);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) 
		{
			if (hidden)
				setBackground(Color.gray);
		}

		@Override
		public void mouseExited(MouseEvent arg0) 
		{
			if (hidden)
				setBackground(Color.white);
		}

		// UNUSED BUT REQUIRED VIA IMPLEMENTATION
		@Override public void mousePressed  (MouseEvent arg0) { }
		@Override public void mouseReleased (MouseEvent arg0) { }
	}
}