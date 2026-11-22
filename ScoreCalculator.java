
public class ScoreCalculator {

    public int calculateScore(Hand hand, int target) {

        int total = hand.getBestTotal(target);
        int difference = Math.abs(target - total);

        if (difference == 0) {
            return -4; // Perfect hit bonus
        } else if (total > target) {
            return difference * 3; // Overshoot penalty
        } else {
            return difference; // Undershoot penalty
        }
    }

}