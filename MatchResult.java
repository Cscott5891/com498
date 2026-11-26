public class MatchResult {
    private String Player1;
    private String Player2;
    private int score1;
    private int score2;
    private String winner;
    private boolean simulated;
public String getPlayer1(){
    return Player1;
}
public String getPlayer2(){
    return Player2;
}
    public MatchResult (String Player1, String player2, int score1, int score2, boolean simulated){
        this.Player1 = Player1;
        this.Player2 = player2;
        this.score1 = score1;
        this.score2 = score2;
        this.simulated = simulated;
        if (score1 < score2){
            this.winner = Player1;
        } else if (score2 < score1){
            this.winner = Player2;
        } else {
            this.winner = "Draw";
        }

    }
    public boolean isSimulated(){
        return simulated;
    }
    public void displayResult() {
        System.out.println(Player1 + ": " + score1 + " | " + Player2 + ": " + score2);

        System.out.println("Winner: " + winner);

        if (simulated){
            System.out.println("Match type: Simulation");
        } else {
            System.out.println("Match type: Manual");
        }
    }
}
