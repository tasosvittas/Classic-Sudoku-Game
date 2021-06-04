package sudokugame;

public class Player {
    private String userName;
    private int playerScore;
    
    Player(String n)
    {
        userName = n;
    }
    
    Player(String n, int s)
    {
        userName = n;
        playerScore = s;
    }
    
    public void setUserName(String in)
    {
        userName = in;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setPlayerScore(int in)
    {
        playerScore = in;
    }
    
    public int getPlayerScore()
    {
        return playerScore;
    }
    
    public String toString()
    {
        return userName+","+playerScore;
    }
    
}
