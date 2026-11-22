import java.util.Scanner;

public class Game {
    private final Scanner input = new Scanner(System.in);
    private final Player[] players = new Player[1000];
    private int playerCount = 0;
    private final int[][] headToHeadWins = new int[1000][1000];
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    public void menu() {
        int choice;
        do {
            System.out.println(" Playload Pursuit Menu: ");
            System.out.println("1. Play Match");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Run simulation");
            System.out.println("4. Compare two players");
            System.out.println("5. Search player history");
            System.out.println("6. List players with > x match wins");
            System.out.println("7. Exit");
            choice = readInt("Enter your choice: ", 1, 7);

            if (choice == 1) playMatch();
            else if (choice == 2) leaderboard();
            else if (choice == 3) simulation();
            else if (choice == 4) comparePlayers();
            else if (choice == 5) searchHistory();
            else if (choice == 6) filterWins();
        } while (choice != 7);
        System.out.println("Exiting the game. Goodbye!");
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int value = input.nextInt();
                input.nextLine();
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine();
            }
        }
    }

    private String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = input.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.println("Name cannot be empty.");
        }
    }

    private int findPlayer(String name) {
        for (int i = 0; i < playerCount; i++) {
            if (players[i].getName().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    private int getOrCreatePlayer(String name) {
        int index = findPlayer(name);
        if (index != -1) return index;
        if (playerCount >= players.length) throw new IllegalStateException("Player storage full.");
        players[playerCount] = new Player(name);
        return playerCount++;
    }

    private int randomInt(int min, int max) {
        if (min > max) throw new IllegalArgumentException("Minimum exceeds maximum.");
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private void playMatch() {
        String first = readName("First player name: ");
        int a = getOrCreatePlayer(first);
        System.out.println("Second player: 1. Human 2. AI");
        boolean bot = readInt("Choose: ", 1, 2) == 2;

        String second;
        if (bot) {
            do {
                second = "BotPlayer" + randomInt(1, 10);
            } while (players[a].getName().equalsIgnoreCase(second));
        } else {
            do {
                second = readName("Second player name: ");
                if (players[a].getName().equalsIgnoreCase(second)) {
                    System.out.println("Players must have different names.");
                }
            } while (players[a].getName().equalsIgnoreCase(second));
        }

        int b = getOrCreatePlayer(second);
        runMatch(a, b, false, bot, true);
    }

    // Both players share one shuffled deck per round.
    private void runMatch(int a, int b, boolean botA, boolean botB, boolean show) {
        Player p1 = players[a];
        Player p2 = players[b];
        p1.resetMatchScore();
        p2.resetMatchScore();
        int target = RuleConfig.INITIAL_TARGET;
        int first = randomInt(0, 1);

        for (int round = 1; round <= RuleConfig.ROUNDS_PER_MATCH; round++) {
            Deck deck = new Deck();
            deck.shuffle();
            p1.resetHand();
            p2.resetHand();

            if (show) System.out.println("\nROUND " + round + " | Target: " + target);

            int firstIndex = first == 0 ? a : b;
            int secondIndex = first == 0 ? b : a;
            boolean firstBot = first == 0 ? botA : botB;
            boolean secondBot = first == 0 ? botB : botA;

            int firstGuess = firstBot
                    ? randomInt(RuleConfig.SIM_MIN_CARDS, Math.min(26, deck.cardsRemaining()))
                    : readInt(players[firstIndex].getName() + " cards to draw (1-" + Math.min(26, deck.cardsRemaining()) + "): ",
                              1, Math.min(26, deck.cardsRemaining()));
            deal(players[firstIndex], deck, firstGuess, target, show);

            int remaining = deck.cardsRemaining();
            int secondGuess;
            if (secondBot) {
                secondGuess = randomInt(RuleConfig.SIM_MIN_CARDS, Math.max(RuleConfig.SIM_MIN_CARDS, remaining));
            } else {
                secondGuess = readInt(players[secondIndex].getName() + " cards to draw (1-" + remaining + "): ",
                                      1, remaining);
            }
            deal(players[secondIndex], deck, secondGuess, target, show);

            int total1 = p1.getHand().getBestTotal(target);
            int total2 = p2.getHand().getBestTotal(target);
            if (show) {
                System.out.println("Scores so far: " + p1.getName() + "=" + p1.getTotalScore()
                        + ", " + p2.getName() + "=" + p2.getTotalScore());
            }

            if (total1 < target && total2 < target) target += RuleConfig.TARGET_SHIFT;
            else if (total1 > target && total2 > target) target -= RuleConfig.TARGET_SHIFT;
            first = 1 - first;
        }

        if (show) {
            System.out.println("\nFINAL: " + p1.getName() + "=" + p1.getTotalScore()
                    + " | " + p2.getName() + "=" + p2.getTotalScore());
        }

        boolean p1Won = p1.getTotalScore() < p2.getTotalScore();
        boolean p2Won = p2.getTotalScore() < p1.getTotalScore();
        p1.recordMatch(p1Won);
        p2.recordMatch(p2Won);
        if (p1Won) headToHeadWins[a][b]++;
        if (p2Won) headToHeadWins[b][a]++;

        if (show) {
            if (p1Won) System.out.println("Winner: " + p1.getName());
            else if (p2Won) System.out.println("Winner: " + p2.getName());
            else System.out.println("It's a tie!");
        }
    }

    private void deal(Player player, Deck deck, int count, int target, boolean show) {
        if (show) System.out.println(player.getName() + " draws " + count + " cards:");
        for (int i = 0; i < count; i++) player.getHand().addCard(deck.drawCard());

        int total = player.getHand().getBestTotal(target);
        int score = scoreCalculator.calculateScore(player.getHand(), target);
        player.addScore(score);
        if (show) System.out.println("Total: " + total + " | Score: " + score);
    }

    private void leaderboard() {
        if (playerCount == 0) {
            System.out.println("No players have played yet.");
            return;
        }

        int[] order = new int[playerCount];
        for (int i = 0; i < playerCount; i++) order[i] = i;
        for (int i = 0; i < playerCount - 1; i++) {
            for (int j = 0; j < playerCount - 1 - i; j++) {
                if (players[order[j]].getWins() < players[order[j + 1]].getWins()) {
                    int temp = order[j];
                    order[j] = order[j + 1];
                    order[j + 1] = temp;
                }
            }
        }

        System.out.println("\nLeaderboard:");
        for (int i = 0; i < playerCount; i++) {
            Player p = players[order[i]];
            System.out.println((i + 1) + ". " + p.getName() + " | wins=" + p.getWins()
                    + " | played=" + p.getMatchesPlayed());
        }
    }

    private void searchHistory() {
        String name = readName("Search name: ");
        int index = findPlayer(name);
        if (index == -1) {
            System.out.println("Player not found.");
            return;
        }
        Player p = players[index];
        System.out.printf("%s | matches=%d | wins=%d | win percentage=%.2f%%n",
                p.getName(), p.getMatchesPlayed(), p.getWins(), p.getWinPercentage());
    }

    private void comparePlayers() {
        int a = findPlayer(readName("First player: "));
        int b = findPlayer(readName("Second player: "));
        if (a == -1 || b == -1) {
            System.out.println("One or both players not found.");
            return;
        }
        Player p1 = players[a];
        Player p2 = players[b];
        System.out.printf("%s: played=%d, wins=%d, win percentage=%.2f%%n",
                p1.getName(), p1.getMatchesPlayed(), p1.getWins(), p1.getWinPercentage());
        System.out.printf("%s: played=%d, wins=%d, win percentage=%.2f%%n",
                p2.getName(), p2.getMatchesPlayed(), p2.getWins(), p2.getWinPercentage());
        System.out.println("Head-to-head: " + p1.getName() + " won " + headToHeadWins[a][b]
                + " times, " + p2.getName() + " won " + headToHeadWins[b][a] + " times.");
    }

    private void filterWins() {
        int threshold = readInt("Show players with more than how many wins? ", 0, 1000);
        boolean found = false;
        for (int i = 0; i < playerCount; i++) {
            if (players[i].getWins() > threshold) {
                System.out.println(players[i].getName() + ": " + players[i].getWins() + " wins");
                found = true;
            }
        }
        if (!found) System.out.println("No matching players.");
    }

    private void simulation() {
        int matches = readInt("Number of simulation matches: ", 1, 1000);
        for (int i = 0; i < matches; i++) {
            int n1 = randomInt(1, 10);
            int n2;
            do {
                n2 = randomInt(1, 10);
            } while (n2 == n1);
            int a = getOrCreatePlayer("BotPlayer" + n1);
            int b = getOrCreatePlayer("BotPlayer" + n2);
            runMatch(a, b, true, true, false);
        }
        System.out.println("Simulation complete.");
        leaderboard();
    }
}
