
package dotsandboxes;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DotSample extends JFrame implements MouseMotionListener, MouseListener {

	public int DOT_NUMBER;
	public int PLAYER_NUMBERS;
	public int numberTemp;
	
	public static boolean KEY_ROOM = false;
	private static boolean start = false;
	private static Socket socket;
	String serverAddress;
	static JFrame frame = new JFrame("Dots and Boxes");
	JButton startGameBtn = new JButton("START");

	JTextArea messageArea = new JTextArea();
	JScrollPane sp = new JScrollPane(messageArea);
	private static DotSample client;
	private static String colorList;
	private static String playerName;
	private static String playerColor;
	private static String[] opponentColor;
	private static String[] nameColor;
	private static int yourNumber;
	

	public static final int DOT_GAP = 24; // The space between each dot
	public static final int DOT_SIZE = 4; // The length of the sides of the square dot

	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = 2;
	public static final int PLAYER_THREE = 3;
	public static final int PLAYER_FOUR = 4;
	private static HashMap<Integer, String> players = new HashMap<Integer, String>();
	private static HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
	private static Scanner in;
	private static PrintWriter out;
	private ConnectionSprite[] horizontalConnections; // Array for all the ConnectionSprites that horizontally connect
														// dots
	private ConnectionSprite[] verticalConnections; // Array for all the ConnectionSprites that vertically connect dots
	private BoxSprite[] boxes; // Array for all the BoxSprites
	private Sprite[] dots; // Array for all the dots

	private Dimension dim; // Window dimensions

	private int clickx; // Holds the x coordinate of mouse click
	private int clicky; // Holds the y coordinate of mouse click

	private int mousex; // Holds the x coordinate of the mouse location
	private int mousey; // Holds the y coordinate of the mouse location

	private int centerx; // x coordinate of the center of the gameboard
	private int centery; // y coordinate of the center of the gameborad

	private int side; // Length of the sides of the square gameboard
	private int space; // Length of 1 dot + 1 connection

	private int activePlayer; // Holds the current player
	private boolean isBlockMove = false;
	private final JButton btnQuit = new JButton("QUIT");
	
	private final JButton btnPlay = new JButton("PLAY");

	private final JTextField inputName = new JTextField();

	private final JLabel labelInputName = new JLabel("Name");
	private final JLabel labelInputColor = new JLabel("Color");

	private final JRadioButton rdbtnRed = new JRadioButton("RED");
	private final JRadioButton rdbtnGreen = new JRadioButton("GREEN");
	private final JRadioButton rdbtnYellow = new JRadioButton("YELLOW");
	private final JRadioButton rdbtnBlue = new JRadioButton("BLUE");
	private int checkFrameVisible = 0;
	private String defaultMessage = "Choose a display name:";
	private String defaultMessageColor = "Choose a display color(";
	public DotSample(String serverAddress) {
		inputName.setColumns(10);
		//Customize button play
		
	ImageIcon imageIconPlay = new ImageIcon("/home/nhyen/project/DnB/Desktop/candy.png");
	    Image imagePlay = imageIconPlay.getImage(); // transform it 
	    Image newimgPlay = imagePlay.getScaledInstance(20, 20,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
	    imageIconPlay = new ImageIcon(newimgPlay);  // transform it back
	    btnPlay.setIcon(imageIconPlay);
	    btnPlay.setFont(new Font("Serif",Font.BOLD,20));
	    btnPlay.setBackground(new Color(102, 0, 204));//import java.awt.Color;
	    btnPlay.setForeground(Color.WHITE);
	    btnPlay.setFocusPainted(false);
//	    btnPlay.setBorderPainted(false);
//	    btnPlay.setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue)); //Four Colors Outer Bevel
	    //Customize button quit
	    ImageIcon imageIconQuit = new ImageIcon("/home/nhyen/project/DnB/Desktop/mint.png");
	    Image imageQuit = imageIconQuit.getImage(); // transform it 
	    Image newimgQuit = imageQuit.getScaledInstance(20, 20,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
	    imageIconQuit = new ImageIcon(newimgQuit);  // transform it back
	    btnQuit.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (JOptionPane.showConfirmDialog(frame, 
	    	            "Are you sure you want to quit the game?", "Quit game?", 
	    	            JOptionPane.YES_NO_OPTION,
	    	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	    	            System.exit(0);
	    	        }
	    	}
	    });
	    
	    btnQuit.setIcon(imageIconQuit);
	    btnQuit.setFont(new Font("Serif",Font.BOLD,20));
	    btnQuit.setBackground(new Color(102, 0, 204));//import java.awt.Color;
	    btnQuit.setForeground(Color.WHITE);
	    btnQuit.setFocusPainted(false);
	    
	  //Customize button start
	  		ImageIcon imageIconStart = new ImageIcon("/home/nhyen/project/DnB/Desktop/watermelon.png");
	  	    Image imageStart = imageIconStart.getImage(); // transform it 
	  	    Image newimgStart = imageStart.getScaledInstance(20, 20,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
	  	    imageIconStart = new ImageIcon(newimgStart);  // transform it back
	  	    startGameBtn.setIcon(imageIconStart);
	  	  	startGameBtn.setFont(new Font("Serif",Font.BOLD,20));
	  	  	startGameBtn.setBackground(new Color(102, 0, 204));//import java.awt.Color;
	  	  	startGameBtn.setForeground(Color.WHITE);
	  	  	startGameBtn.setFocusPainted(false);
	    
//		btnPlay.setIcon(new ImageIcon("C:\\Users\\nhyen.RD\\Desktop\\candy.png"));
		this.serverAddress = serverAddress;
		setSize(900, 900);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		getContentPane().setLayout(null);
		setVisible(false);

		//Update game ui
//		frame.getContentPane().setLayout(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon("/home/nhyen/project/DnB/Desktop/imgbackground.jpg"));

        frame.setContentPane(label);
        frame.getContentPane().setLayout(new FlowLayout());

        ImageIcon imageIcon = new ImageIcon("/home/nhyen/project/DnB/Desktop/textlogo.png");
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(400, 120,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);  // transform it back
        JLabel imagelabel = new JLabel(imageIcon,JLabel.CENTER);
        imagelabel.setBounds(100,10,400,120);
		label.setLayout(null);
        label.add(imagelabel);
		
        
        ImageIcon imageIconCopyright = new ImageIcon("/home/nhyen/project/DnB/Desktop/copyright.png");
  	    Image imageCopyright = imageIconCopyright.getImage(); // transform it 
  	    Image newimgCopyright = imageCopyright.getScaledInstance(20, 20,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
  	    imageIconCopyright = new ImageIcon(newimgCopyright);  // transform it back
        JLabel copyright = new JLabel("Copyright: Group 8888 / All rights reserved",JLabel.CENTER);
        copyright.setIcon(imageIconCopyright);
        copyright.setBounds(100,500,400,120);
        label.add(copyright);

        
		btnPlay.setLayout(null);
		btnPlay.setBounds(220, 200, 150, 50);
		btnQuit.setBounds(220, 300, 150, 50);
		startGameBtn.setBounds(220, 200, 150, 50);
		startGameBtn.setVisible(false);

//		frame.getContentPane().add(startGameBtn);
		label.add(btnPlay);
		label.add(btnQuit);
		label.add(startGameBtn);

		frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		

		messageArea.append("Waiting for other players..." + "\n");
        messageArea.setBounds(130, 300, 350, 100);
        sp.setBounds(130, 300, 350, 100);
		messageArea.setEditable(false);
		messageArea.setBackground(Color.BLACK);
		messageArea.setForeground(Color.WHITE);
		sp.setVisible(false);
		label.add(sp);

//		messageArea.setBackground(Color.PINK);
//		frame.getContentPane().add(messageArea);
//		frame.pack();

		
        frame.setVisible(true);

	}

	private Color getPlayerColor(String color) {
		switch (color) {
		case "BLUE":
			return Color.BLUE;
		case "YELLOW":
			return Color.YELLOW;
		case "GREEN":
			return Color.GREEN;
		case "PINK":
			return Color.PINK;
		default:
			return null;
		}
	}

	private void getPlayerNameAndColor(String[] nameColors) {
		for (int i = 0; i < nameColors.length; i++) {
			String[] arr = nameColors[i].split("=");
			Color color = getPlayerColor(arr[1]);
			players.put(i + 1, arr[0]);
			colors.put(i + 1, color);
	
		}
	}

	private void loadProperties() {
		// Initialize fields

		clickx = 0;
		clicky = 0;
		mousex = 0;
		mousey = 0;

		dim = getSize();
		centerx = dim.width / 2;
		centery = (dim.height) / 2;

		side = DOT_NUMBER * DOT_SIZE + (DOT_NUMBER - 1) * DOT_GAP; // There is one less connection than dot per side
		space = DOT_SIZE + DOT_GAP;
	}

	private void loadConnections() {

		horizontalConnections = new ConnectionSprite[(DOT_NUMBER - 1) * DOT_NUMBER];
		verticalConnections = new ConnectionSprite[(DOT_NUMBER - 1) * DOT_NUMBER];

		/*
		 *
		 * There are two ways to cycle through the Connections, Boxes, and Dots grids.
		 * This way uses only 1 for loop and keeps track of the current row and column
		 * number in colsx, rowsx, colsy, rowsy. colsx and rowsx track the columns and
		 * rows for the horizontalConnections while colsy and rowsy track the columns
		 * and rows for the vertical connections. The reason to have different fields
		 * for vertical and horizontal connections is so that both grids will be filled
		 * in left to right and then top to bottom (rows first then columns). This makes
		 * it easier to match the connection up to box or boxes it borders. Simple
		 * setting colsy=rowsx and rowsy=colsx will put the vertical connections on the
		 * correct place on the screen but they won't match up to the boxes correctly.
		 *
		 */

		for (int i = 0; i < horizontalConnections.length; i++) {
			int colsx = i % (DOT_NUMBER - 1);
			int rowsx = i / (DOT_NUMBER - 1);
			int horx = centerx - side / 2 + DOT_SIZE + colsx * space;
			int hory = centery - side / 2 + rowsx * space;
			horizontalConnections[i] = ConnectionSprite.createConnection(ConnectionSprite.HORZ_CONN, horx, hory);

			int colsy = i % DOT_NUMBER;
			int rowsy = i / DOT_NUMBER;
			int vertx = centerx - side / 2 + colsy * space;
			int verty = centery - side / 2 + DOT_SIZE + rowsy * space;
			verticalConnections[i] = ConnectionSprite.createConnection(ConnectionSprite.VERT_CONN, vertx, verty);
		}
	}

	private void loadBoxes() {

		/*
		 *
		 * loadBoxes cycles through the box grid the way loadConnection does. There is
		 * oneless box per side than dot per side.
		 *
		 */

		boxes = new BoxSprite[(DOT_NUMBER - 1) * (DOT_NUMBER - 1)];

		for (int i = 0; i < boxes.length; i++) {
			int cols = i % (DOT_NUMBER - 1);
			int rows = i / (DOT_NUMBER - 1);

			int boxx = centerx - side / 2 + DOT_SIZE + cols * space;
			int boxy = centery - side / 2 + DOT_SIZE + rows * space;

			ConnectionSprite[] horConn = new ConnectionSprite[2];
			horConn[0] = horizontalConnections[i];
			horConn[1] = horizontalConnections[i + (DOT_NUMBER - 1)];

			ConnectionSprite[] verConn = new ConnectionSprite[2]; // This only works if the verticalConnections were put
																	// into the array rows then columns
			verConn[0] = verticalConnections[i + rows];
			verConn[1] = verticalConnections[i + rows + 1];

			boxes[i] = BoxSprite.createBox(boxx, boxy, horConn, verConn);
		}
	}

	private void loadDots() {

		/*
		 *
		 * loadDots cycles through the dot grid differently than the loadConnections and
		 * loadBoxes methods cycle through the connections and boxes grids. The loadDots
		 * cycles through the dot grid with two for loops. It doesn't matter what order
		 * the dots are loaded into the dots array since they are for visual purposes
		 * only. The body of the loop also contains the code to actually build the dots
		 * shape.
		 *
		 */

		dots = new Sprite[DOT_NUMBER * DOT_NUMBER];
		for (int rows = 0; rows < DOT_NUMBER; rows++) {
			for (int cols = 0; cols < DOT_NUMBER; cols++) {
				Sprite dot = new Sprite();
				dot.width = DOT_SIZE;
				dot.height = DOT_SIZE;
				dot.x = centerx - side / 2 + cols * space;
				dot.y = centery - side / 2 + rows * space;
				dot.shape.addPoint(-DOT_SIZE / 2, -DOT_SIZE / 2);
				dot.shape.addPoint(-DOT_SIZE / 2, DOT_SIZE / 2);
				dot.shape.addPoint(DOT_SIZE / 2, DOT_SIZE / 2);
				dot.shape.addPoint(DOT_SIZE / 2, -DOT_SIZE / 2);
				int index = rows * DOT_NUMBER + cols;
				dots[index] = dot;
			}
		}
	}

	private void startNewGame() {
		if(activePlayer != yourNumber) {
			isBlockMove = true;
		}
		loadConnections();
		loadBoxes();
	}

	public ConnectionSprite getConnection(int x, int y) {

		// Get the connection that encloses point (x, y) or return null if there isn't
		// one

		for (int i = 0; i < horizontalConnections.length; i++) {
			if (horizontalConnections[i].containsPoint(x, y)) {
				return horizontalConnections[i];
			}
		}

		for (int i = 0; i < verticalConnections.length; i++) {
			if (verticalConnections[i].containsPoint(x, y)) {
				return verticalConnections[i];
			}
		}

		return null;
	}

	private boolean[] getBoxStatus() {
		boolean[] status = new boolean[boxes.length];

		for (int i = 0; i < status.length; i++) {
			status[i] = boxes[i].isBoxed();
		}

		return status;
	}

	private int[] calculateScores() {
		int[] scores = { 0, 0, 0, 0 };
		
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isBoxed() && boxes[i].player != 0) {
				scores[boxes[i].player - 1]++;
			}
		}

		return scores;
	}

	public boolean makeConnection(ConnectionSprite connection) {
		boolean newBox = false;

		boolean[] boxStatusBeforeConnection = getBoxStatus(); // The two boolean arrays are used to see if a new box was
																// created after the connection was made

		connection.connectionMade = true;

		boolean[] boxStatusAfterConnection = getBoxStatus();

		for (int i = 0; i < boxes.length; i++) {
			if (boxStatusAfterConnection[i] != boxStatusBeforeConnection[i]) {
				newBox = true;
				boxes[i].player = activePlayer;
			}
		}

		if (!newBox) { // Allow the current player to go again if he made a box
			activePlayer = activePlayer + 1;
			if (activePlayer == players.size() + 1)
				activePlayer = 1;
		}
		if(activePlayer == yourNumber) {
			isBlockMove = false;
		}

		checkForGameOver();

		return newBox;
	}
	
	private int findWinner(int[] scores) {
		int winnerNumber = 0;
		int maxScore = 0;
		for(int i =0; i<PLAYER_NUMBERS;i++) {
			if(scores[i]>maxScore) {
				maxScore = scores[i];
				winnerNumber = i;
			}
		}
		if(winnerNumber+1>players.size()) return 1;
		return winnerNumber+1;
	}

	private void checkForGameOver() {
		int[] scores = calculateScores();
		int scoreSum = 0;
		for (int i = 0; i < PLAYER_NUMBERS; i++) {
			scoreSum += scores[i];
		}
		if (scoreSum == ((DOT_NUMBER - 1) * (DOT_NUMBER - 1))) {
			out.println("GAMEOVER");
			int winnerNumber = findWinner(scores);
			String title = (winnerNumber == yourNumber)?"Congrats!!! You are the winner\n":"Oh no!!! You lose. Try again to get higher score\n";
			int choice = 0;
			switch (PLAYER_NUMBERS) {
			case 2:
				choice = JOptionPane.showConfirmDialog(this, title+ players.get(PLAYER_ONE) + ": " + scores[0] + "\n" + players.get(PLAYER_TWO) + ": " + scores[1],
						"Result",JOptionPane.OK_OPTION,
	    	            JOptionPane.QUESTION_MESSAGE);

				break;
			case 3:
				choice = JOptionPane.showConfirmDialog(this, title + players.get(PLAYER_ONE) + ": " + scores[0] + "\n" + players.get(PLAYER_TWO) + ": " + scores[1]
						+ "\n" + players.get(PLAYER_THREE) + ": " + scores[2],
				"Result",JOptionPane.OK_OPTION,
	    	            JOptionPane.QUESTION_MESSAGE);

				break;
			case 4:
				choice = JOptionPane.showConfirmDialog(this, title+players.get(PLAYER_ONE) + ": " + scores[0] + "\n" + players.get(PLAYER_TWO) + ": " + scores[1]
						+ "\n" + players.get(PLAYER_THREE) + ": " + scores[2] + "\n" + players.get(PLAYER_FOUR)
						+ ": " + scores[3],
				"Result",JOptionPane.OK_OPTION,
	    	            JOptionPane.QUESTION_MESSAGE);
				break;
			default: break;
			}
			if(choice == JOptionPane.OK_OPTION) {
				getContentPane().setLayout(null);
				checkFrameVisible = 1;
				setVisible(false);
				frame.setVisible(true);
//				if(players.size() < 2) startGameBtn.setEnabled(false);
				out.println("GAMEOVER");
				sp.setVisible(false);
				startGameBtn.setVisible(false);
				btnPlay.setVisible(true);
				btnQuit.setVisible(true);
			}
			else {
				out.println("GAMEOVER");
	            System.exit(0);
			}
		}
	}

	private void handleClick() {
		ConnectionSprite connection = getConnection(clickx, clicky);
		if (connection == null)
			return;

		if (!connection.connectionMade) {
			makeConnection(connection);

		}
		out.println("MOVE " + activePlayer + "," + clickx + "," + clicky);
		repaint();
	}

	public void mouseMoved(MouseEvent event) {
		if(!isBlockMove) {
			mousex = event.getX();
			mousey = event.getY();
			repaint();
		}
		
	}

	public void mouseDragged(MouseEvent event) {
		if(!isBlockMove) {
			mouseMoved(event);
		}

	}

	public void mouseClicked(MouseEvent event) {
		System.out.println("active player: "+activePlayer+isBlockMove);
		if(!isBlockMove) {
			clickx = event.getX();
			clicky = event.getY();

			handleClick();
		}
		
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	private void paintBackground(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, dim.width, dim.height);
	}

	private void paintDots(Graphics g) {
		for (int i = 0; i < dots.length; i++) {
			dots[i].render(g);
		}
	}

	private void paintConnections(Graphics g) {
		for (int i = 0; i < horizontalConnections.length; i++) {
			if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_ONE) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_ONE);

				} else {
					horizontalConnections[i].color = Color.WHITE;
				}

			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_TWO) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_TWO);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_THREE) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_THREE);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_FOUR) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_FOUR);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			}
//                else { horizontalConnections[i].color=Color.YELLOW;
//                }

			horizontalConnections[i].render(g);
		}

		for (int i = 0; i < verticalConnections.length; i++) {

			if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_ONE) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					if (activePlayer == PLAYER_ONE) {
						verticalConnections[i].color = colors.get(PLAYER_ONE);
					}
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_TWO) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_TWO);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_THREE) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_THREE);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_FOUR) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_FOUR);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			}

			verticalConnections[i].render(g);

		}
	}
	
	private void paintReceivedConnections(Graphics g) {
		for (int i = 0; i < horizontalConnections.length; i++) {
			if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_ONE) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_ONE);

				} else {
					horizontalConnections[i].color = Color.WHITE;
				}

			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_TWO) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_TWO);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_THREE) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_THREE);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			} else if (!horizontalConnections[i].connectionMade && activePlayer == PLAYER_FOUR) {
				if (horizontalConnections[i].containsPoint(mousex, mousey)) {
					horizontalConnections[i].color = colors.get(PLAYER_FOUR);
				} else {
					horizontalConnections[i].color = Color.WHITE;
				}
			}
//                else { horizontalConnections[i].color=Color.YELLOW;
//                }

			horizontalConnections[i].render(g);
		}

		for (int i = 0; i < verticalConnections.length; i++) {

			if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_ONE) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					if (activePlayer == PLAYER_ONE) {
						verticalConnections[i].color = colors.get(PLAYER_ONE);
					}
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_TWO) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_TWO);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_THREE) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_THREE);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			} else if (!verticalConnections[i].connectionMade && activePlayer == PLAYER_FOUR) {
				if (verticalConnections[i].containsPoint(mousex, mousey)) {
					verticalConnections[i].color = colors.get(PLAYER_FOUR);
				} else {
					verticalConnections[i].color = Color.WHITE;
				}
			}

			verticalConnections[i].render(g);

		}
	}
	

	public void paintBoxes(Graphics g) {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isBoxed()) {
				if (boxes[i].player == PLAYER_ONE) {
					boxes[i].color = colors.get(PLAYER_ONE);
				} else if (boxes[i].player == PLAYER_TWO) {
					boxes[i].color = colors.get(PLAYER_TWO);
				} else if (boxes[i].player == PLAYER_THREE) {
					boxes[i].color = colors.get(PLAYER_THREE);
				} else {
					boxes[i].color = colors.get(PLAYER_FOUR);
				}
			} else {
				boxes[i].color = Color.WHITE;
			}

			boxes[i].render(g);
		}
	}
	
	public String getColorString(Color color) {
		if(Color.BLUE.equals(color)) return "Blue";
		else if(Color.PINK.equals(color)) return "Pink";
		else if(Color.YELLOW.equals(color)) return "Yellow";
		else if(Color.GREEN.equals(color)) return "Green";
		else return "";
	}

	public void paintStatus(Graphics g) {
		int[] scores = calculateScores();
		String status = "It is player " + players.get(activePlayer) + "'s turn";
		if(yourNumber!=activePlayer) {
			isBlockMove = true;
		}
		else {
			isBlockMove = false;
		}		
		String status2 = players.get(PLAYER_ONE)+" - "+ getColorString(colors.get(PLAYER_ONE)) + " : " + scores[0];
		String status3 = players.get(PLAYER_TWO) + " - "+ getColorString(colors.get(PLAYER_TWO)) + ": " + scores[1];
		String status4 = players.get(PLAYER_THREE) + " - "+ getColorString(colors.get(PLAYER_THREE)) + ": " + scores[2];
		String status5 = players.get(PLAYER_FOUR) +" - "+ getColorString(colors.get(PLAYER_FOUR)) +  ": " + scores[3];
		// Color currentColor=(activePlayer==PLAYER_ONE) ? PLAYER_ONE_COLOR :
		// PLAYER_TWO_COLOR ;
		// g.setColor(currentColor);
		g.setColor(Color.BLACK);
		g.drawString(status, dim.width / 2 - 50, 100);

		g.setColor(Color.BLACK);
		g.drawString(status2, 10, 50);

		g.setColor(Color.BLACK);
		g.drawString(status3, 10, dim.height - 20);
		if (PLAYER_NUMBERS == 3) {
			g.setColor(Color.BLACK);
			g.drawString(status3, dim.width - 70, 50);
		} else if (PLAYER_NUMBERS == 4) {
			g.setColor(Color.BLACK);
			g.drawString(status4, dim.width - 70, 50);

			g.setColor(Color.BLACK);
			g.drawString(status5, dim.width - 70, dim.height - 20);
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		// The double buffer technique is not really necessarry because there is no
		// animation

		Image bufferImage = createImage(dim.width, dim.height);
		Graphics bufferGraphics = bufferImage.getGraphics();

		paintBackground(bufferGraphics);
		paintDots(bufferGraphics);
		paintConnections(bufferGraphics);
		paintBoxes(bufferGraphics);
		paintStatus(bufferGraphics);

		g.drawImage(bufferImage, 0, 0, null);
	}
	
	private String getUserName(String message) {
		String response = JOptionPane.showInputDialog(frame, message, "Username selection",
				JOptionPane.PLAIN_MESSAGE);
		if ((response != null) && (response.length() > 0)) {
			return response;
		}
		else if(response.length() == 0) return "";
		return null;
	}

	private String getColor(String message) {
		String response = JOptionPane.showInputDialog(frame, message + colorList + "):",
				"Color selection", JOptionPane.PLAIN_MESSAGE);
		if ((response != null) && (response.length() > 0)) {
			return response.toUpperCase();
		}
		else if(response.length() == 0) return "";
		return null;
	}

	private int getDotSize() {
		try {
			String response = JOptionPane.showInputDialog(frame, "Enter the number of dots in Rows and Colums:",
					"Dots size selection", JOptionPane.PLAIN_MESSAGE);
			if ((response != null) && (response.length() > 0)) {
				return Integer.parseInt(response);
			}
			else if(response.length() == 0) return 0;
			else {
				return -1;
			}
		}
		catch(Exception e) {
			System.out.println("GetdotSize-Exception: "+e);
			return 0;
		}
		
	}
	
	private void startGame(String activePlayerStr) {
		getPlayerNameAndColor(nameColor);
		
		for (Entry<Integer, String> entry : players.entrySet()) {
            if (entry.getValue().equals(activePlayerStr)) {
                activePlayer = entry.getKey();
            }
            if(entry.getValue().equals(playerName)) {
            	yourNumber = entry.getKey();
            }
        }
		
		loadProperties();

		loadDots();

		startNewGame();
        setVisible(true);

	}
	
	private int getKey(HashMap<Integer, String> map, String value) {
	    for (Entry<Integer,String> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return 0;
	}

	private void run() throws IOException {
		try {
			socket = new Socket(serverAddress, 5000);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			startGameBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					out.println("STARTGAME");
				}
			});
			btnPlay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					out.println("PLAYCHOICE");
				}
			});
//			while (KEY_ROOM) {
//				int choice = JOptionPane.showConfirmDialog(frame, "Start new game");
//				if (choice == 0) {
//					KEY_ROOM = false;
//					out.println("STARTGAME");
//				}
//			}
			while (in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println("Message from server: "+line);
				if (line.startsWith("USERNAME")) {
					//send username to server to validate
					String input = getUserName(defaultMessage);
					if(input == null) out.println("HANDLECANCELBTN");
					else out.println("USERNAMESELECTED "+input);
				}
				else if(line.startsWith("ERRORNAME")) {
					String errorMessage = "Error! You must specify other name\n";
					String input = getUserName(errorMessage+defaultMessage);
					out.println("USERNAMESELECTED "+input);
				}
					
				else if (line.startsWith("COLORLIST")) {
					//get the list of colors
					colorList = line.substring(10);
					colorList = colorList.substring(0, colorList.length() - 1);
				} else if (line.startsWith("ASKDOTSIZE")) {
					//send the number of dots in case of keyroom
					int dotnum = 0;
					while(dotnum == 0) {
						dotnum = getDotSize();
						
					}
					if(dotnum == -1) {
						out.println("HANDLECANCELBTN");
					}
					else out.println("DOTSIZERES "+dotnum);
				} else if (line.startsWith("COLOR")) {
					//enter your color choice 
					String color = getColor(defaultMessageColor);
					if(color == null) out.println("HANDLECANCELBTN");
					else out.println("COLORCHOICE "+color);
				}
				else if(line.startsWith("ERRORCOLOR")) {
					String errorMessageColor = "Error! You must specify other color\n";
					String color = getColor(errorMessageColor+defaultMessageColor);
					if(color == null) out.println("HANDLECANCELBTN");
					else out.println("COLORCHOICE "+color);
				}
				else if (line.startsWith("DOTSIZE")) {
					//get the number of dots of match from server
					this.DOT_NUMBER = Integer.parseInt(line.substring(8));
				} else if (line.startsWith("KEYROOM")) {
					//specify the keyroom of the match
					Client.KEY_ROOM = true;
					startGameBtn.setVisible(true);
					startGameBtn.setEnabled(false);
					messageArea.append("You are keyroom!" + "\n");
				} else if (line.startsWith("NAMEACCEPTED")) {
					//get your valid input name
					playerName = line.substring(13);
					frame.setTitle("Player - " + playerName);
					sp.setVisible(true);
				}
				else if(line.startsWith("ACCEPTEDCOLOR")){
					playerColor = line.substring(14);
					btnQuit.setVisible(false);
					btnPlay.setVisible(false);
					
				}else if (line.startsWith("PLAYERS")) {
					String players = line.substring(8);
					players = players.substring(1, players.length() - 1);
					nameColor = players.split(", ");
					if (nameColor.length >= 2)
						startGameBtn.setEnabled(true);
				} else if (line.startsWith("MESSAGE")) {
					messageArea.append(line.substring(8) + "\n");
					messageArea.append("Waiting for keyroom started..." + "\n");
				}
				else if(line.startsWith("LEAVING")) {
					String leavingPlayer = line.substring(8,line.length()-9);
					int selectedKey = getKey(players,leavingPlayer);
					players.remove(selectedKey);
					colors.remove(selectedKey);
					messageArea.append(line.substring(8) + "\n");
					if(players.size()<2) startGameBtn.setEnabled(false);
					if(checkFrameVisible == 1) break;
					if(numberTemp == 2 ) {
						String title = "Congrats!!! You are the winner because other players have left\n";
						int choiceOption = JOptionPane.showConfirmDialog(this, title,
									"Result",JOptionPane.OK_CANCEL_OPTION,
				    	            JOptionPane.QUESTION_MESSAGE);
						if(choiceOption == JOptionPane.OK_OPTION) {
							getContentPane().setLayout(null);
							checkFrameVisible = 1;
							setVisible(false);
							frame.setVisible(true);
//							if(players.size() < 2) startGameBtn.setEnabled(false);
							sp.setVisible(false);
							startGameBtn.setVisible(false);
							btnPlay.setVisible(true);
							btnQuit.setVisible(true);
						}
						else {
				            System.exit(0);
						}
						out.println("GAMEOVER");
					}
					else {
						numberTemp--;
						int j = 1;
						for (int i = 1; i <= 4; i++) {
							if(players.get(i)!=null){
								players.put(j, players.get(i));
								colors.put(j, colors.get(i));
								j++;
							}
					
						}
						if(activePlayer>players.size()) {
							activePlayer = 1;
						}
						
						out.println("DECREASENUMBER "+selectedKey);
						

//						repaint();
					}
				}
				else if(line.startsWith("DECREASENUMBER")) {
					String selectedKeyStr = line.substring(15);
					if(yourNumber>Integer.parseInt(selectedKeyStr)) {
						System.out.println("keyyyyy: "+selectedKeyStr);
						yourNumber--;
					}
					if(activePlayer == yourNumber) isBlockMove = false;
					repaint();
				}
				else if (line.startsWith("START")) {
					PLAYER_NUMBERS = nameColor.length;
					numberTemp = PLAYER_NUMBERS;
					String activePlayerStr = line.substring(6);
					checkFrameVisible = 0;
					frame.setVisible(false);
					client.setVisible(true);
					client.setTitle(playerName);
					startGame(activePlayerStr);
				} else if(line.startsWith("MOVE")){
					Image bufferImage = createImage(dim.width, dim.height);
					Graphics bufferGraphics = bufferImage.getGraphics();
					
					String move = line.substring(5);
					String[] arr = move.split(",");
					mousex = Integer.parseInt(arr[1]);
					mousey = Integer.parseInt(arr[2]);
					paintReceivedConnections(bufferGraphics);
					
					ConnectionSprite connection = getConnection(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));

					if(connection==null)
			    		return;

			    	if(!connection.connectionMade) {
			    		makeConnection(connection);

			    	}
					repaint();
				}
			}
		}
		catch(UnknownHostException e) {
			System.out.println(e);
            JOptionPane.showMessageDialog(null, "No active shares found on this IP!");
			System.exit(0);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		finally {
			setVisible(false);
			dispose();
			socket.close();
		}
	}
	
	public static String getIpAddress(JFrame frame) {
		String response = JOptionPane.showInputDialog(frame, "Enter server ip address", "Server IP Address",
				JOptionPane.PLAIN_MESSAGE);
		if ((response != null) && (response.length() > 0)) {
			return response;
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		String address = getIpAddress(frame);
		if(address == null) System.exit(0);
		client = new DotSample(address);
		client.run();

	}

}