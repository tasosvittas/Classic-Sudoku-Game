package sudokugame;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SudokuGame extends JFrame {

        private static JFrame newFrame;
        private JPanel panel1;
        private ArrayList<Player>players = new ArrayList<Player>();
        JTextField usernameText;
        String currentPlayer;
        
	boolean isStarted = false;
	int prevBoard[][] = new int[9][9];
        private Connection conn =null;
        
	
        final JLabel guiSudokuLabel = new JLabel("Sudoku Game");
	final JButton startButton = new JButton("Start");
	final JButton submitButton = new JButton("Submit");
        final JButton changePlayer = new JButton("Change Player");
	final JTextField grid[][] = new JTextField[9][9];

	public void gameOver(){
		prevBoard = SudokuSolver.solve(prevBoard);
		boolean isFine = true;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(grid[i][j].getText().equals("")){
					isFine = false;
					break;
				}
				else if(Integer.parseInt(grid[i][j].getText()) != prevBoard[i][j]){
					isFine = false;
					break;
				}
			}
		}
		if(isFine && isStarted)
                {   
                    Player p = new Player(currentPlayer);
                    JOptionPane.showMessageDialog(null, "You Won.");
                    scoreSelect(p);
                }

                else
                {
                    JOptionPane.showMessageDialog(null, "You Lose.");
                }
                
		isStarted = false;
		startButton.setText("Start");
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				grid[i][j].setText("");
				grid[i][j].setEditable(false);
			}
		}
	}

	public SudokuGame(String title) 
        {
            validationForm(); 
	}
        
        public void createDb()
        {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:player.s3db");
                String sql = "CREATE TABLE IF NOT EXISTS player (\n"
                    + "	username text PRIMARY KEY,\n"
                    + "	score integer NOT NULL\n"
                    + ");";
                Statement st=conn.createStatement();
                st.execute(sql);
             
            } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
       
        public void addPlayer(Player p)
        {
            try {
                String sql = "INSERT INTO player(username,score) VALUES(?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, p.getUserName());
                pstmt.setInt(2, 0);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
        public void scoreSelect(Player p)
        {
            try{
                String  sql = "SELECT * FROM player where username='"+p.getUserName()+"'";
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                String takeString = rs.getString("username");
                Integer score = rs.getInt("score");
                score++;
                String name = takeString;
                winScore(score,name);
           } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
        
        public void winScore(Integer score, String name)
        {
            try {
                String sql = "UPDATE player SET score=? where username='"+name+"'";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, score);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
        
        public void checkDb(Player p)
        {
            try {
                String  sql = "SELECT * FROM player where username='"+p.getUserName()+"'";
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                int icount=0;
                while(rs.next()) icount++;
                if(icount == 0)
                {
                    addPlayer(p);
                    initialize();
                }
                else 
                {
                    initialize();
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
        
        public void showPlayerStatus(Player p)
        {
            guiSudokuLabel.setText("");
            try
            {
                String  sql = "SELECT * FROM player where username='"+p.getUserName()+"'";
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                String takeString = rs.getString("username");
                Integer score = rs.getInt("score");
                
                String t= takeString+", Score: "+score;
                guiSudokuLabel.setText(guiSudokuLabel.getText()+t+"\n");
                
            }catch(SQLException ex) {
                JOptionPane.showMessageDialog(null,"Sql exception "+ex.getMessage());
            }
        }
          
        void validationForm()
        {
            JFrame newFrame = new JFrame();
            newFrame.setLayout(new GridLayout());
            newFrame.setVisible(true);
            newFrame.setResizable(false);
            newFrame.setSize(600,300);
            newFrame.setTitle("Sudoku Game");
            panel1 = new JPanel();
            newFrame.add(panel1);
            panel1.setLayout(new GridLayout(5,1));
            panel1.setVisible(true);
            panel1.setBounds(300,100,400,300);
            JLabel playerNameLabel = new JLabel("Username: ");
            panel1.add(playerNameLabel);
            JTextField usernameText = new JTextField("Player Username");
            panel1.add(usernameText);
   
            JButton loginButton = new JButton("Login / Sign Up");
            loginButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent ae) 
                        {
                            createDb();
                            Player p = new Player(usernameText.getText());
                            players.add(p); 
                            currentPlayer = usernameText.getText();
                            checkDb(p);
                            newFrame.dispose();
                        }
                    });
            
            panel1.add(loginButton);
            panel1.setVisible(true);
            newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

	private void initialize() 
        {
		newFrame = new JFrame();
		newFrame.setBounds(100, 100, 668, 438);
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newFrame.getContentPane().setLayout(null);
                newFrame.setVisible(true);
		int h = 12, w = 13, hi = 39, wi = 37;

		for(int i = 0; i < 9 ; i++){
			if(i%3 == 0 && i!=0) w += 13;

			for(int j = 0; j < 9; j++){
				if(j%3 == 0 && j!=0) h += 11;

				grid[i][j] = new JTextField();
				grid[i][j].setColumns(10);
				grid[i][j].setBounds(h, w, 38, 37);
				newFrame.getContentPane().add(grid[i][j]);
				h += hi;
			}
			h = 12;
			w += wi;
		}

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				grid[i][j].setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.BOLD, 22));
				grid[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				grid[i][j].setEditable(false);
			}
		}
                
		submitButton.setVisible(false);
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameOver();
			}
		});
		submitButton.setFont(new Font("Calibri Light", Font.BOLD, 18));
		submitButton.setBounds(435, 240, 155, 37);
		newFrame.getContentPane().add(submitButton);
                
                Player p = new Player(currentPlayer);
                showPlayerStatus(p);
                guiSudokuLabel.setFont(new Font("Calibri Light", Font.BOLD, 16));
                guiSudokuLabel.setBounds(435, 15, 200, 16);
                newFrame.getContentPane().add(guiSudokuLabel);
                
		JLabel difficultyLabel = new JLabel("Select difficulty:");
		difficultyLabel.setFont(new Font("Calibri Light", Font.BOLD, 16));
		difficultyLabel.setBounds(435, 70, 155, 24);
		newFrame.getContentPane().add(difficultyLabel);

		final JRadioButton easyButton = new JRadioButton("Easy");
		easyButton.setFont(new Font("Calibri Light", Font.BOLD, 13));
		easyButton.setBounds(435, 103, 127, 25);
		newFrame.getContentPane().add(easyButton);

            	final JRadioButton mediumButton = new JRadioButton("Medium");
		mediumButton.setFont(new Font("Calibri Light", Font.BOLD, 13));
		mediumButton.setBounds(435, 133, 127, 25);
		newFrame.getContentPane().add(mediumButton);

		final JRadioButton hardButton = new JRadioButton("Hard");
		hardButton.setFont(new Font("Calibri Light", Font.BOLD, 13));
		hardButton.setBounds(435, 163, 127, 25);
		newFrame.getContentPane().add(hardButton);

		ButtonGroup bg = new ButtonGroup();
		bg.add(easyButton);
		bg.add(mediumButton);
		bg.add(hardButton);
		bg.setSelected(mediumButton.getModel(), true);
                newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                changePlayer.setBounds(435, 310, 155, 37);
		newFrame.getContentPane().add(changePlayer);
		changePlayer.setFont(new Font("Calibri Light", Font.BOLD, 15));
                changePlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
                        {
                            validationForm();
                            newFrame.dispose();                          
			}
		});
                
                
		startButton.setBounds(435, 206, 155, 37);
		newFrame.getContentPane().add(startButton);
		startButton.setFont(new Font("Calibri Light", Font.BOLD, 18));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(isStarted){
					isStarted = false;
					prevBoard = SudokuSolver.solve(prevBoard);
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 9; j++){
							grid[i][j].setEditable(false);
							grid[i][j].setText(Integer.toString(prevBoard[i][j]));
						}
					}
					startButton.setText("Start");
					submitButton.setVisible(false);
				}
				else{
					int difficulty = 1; 
					if(easyButton.isSelected()) difficulty = 0;
					else if (hardButton.isSelected()) difficulty = 2;
					else difficulty = 1;


					int board[][] = new int[9][9];
					do{
						board = SudokuGenerator.generate(difficulty);
					}while(board[0][0] == -1);
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 9; j++){
							prevBoard[i][j] = board[i][j];
						}
					}
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 9; j++){
							if(board[i][j] != 0){
								grid[i][j].setText(Integer.toString(board[i][j]));
							}
							else {
								grid[i][j].setText("");
								grid[i][j].setEditable(true);
							}
						}
					}
					submitButton.setVisible(true);
					startButton.setText("Give up!");
					isStarted = true;
				}
			}
		});
	}
}