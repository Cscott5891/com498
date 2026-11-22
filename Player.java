public class Player {
    private String name;
    private Hand hand;
    private int totalScore;
    private int matchesPlayed;
    private int wins;

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.totalScore = 0;
        this.matchesPlayed = 0;
        this.wins = 0;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void addScore(int score) {
        totalScore += score;
    }

    public void resetHand() {
        hand = new Hand();
    }

    public void resetMatchScore() {
        totalScore = 0;
    }

    public void recordMatch(boolean won) {
        matchesPlayed++;
        if (won) {
            wins++;
        }
    }

    public double getWinPercentage() {
        if (matchesPlayed == 0) {
            return 0;
        }
        return 100.0 * wins / matchesPlayed;
    }
}