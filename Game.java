import java.util.Scanner;
public class Game {
    private Scanner input = new Scanner(System.in);
    private Player[] players = new Player[1000];
    private int playerCount = 0;
    private int[][] headToHeadWins = new int[1000][1000]; // headToHeadWinners[i][j] = number of times player i has beaten player j
    private ScoreCalculator scoreCalculator = new ScoreCalculator();

    public void menu() {
        int choice = 0;
        while (choice != 7) {
            System.out.println(" Playload Pursuit Menu: ");
            System.out.println("1. Play Match");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Run simulation");
            System.out.println("4. Compare two players");
            System.out.println("5. Search player history");
            System.out.println("6. List players with > x match wins");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = readInt("Choose: ", 1, 7);

            if (choice == 1) {
                playMatch();
            } else if (choice == 2) {
                leaderboard();
            } else if (choice == 3) {
                simulation();
            } else if (choice == 4) {
                comparePlayers();
            } else if (choice == 5) {
                comparePlayers();
            } else if (choice == 6) {
                searchHistory();
            } else if (choice == 7) {
                filterWins();
            }
        }
        System.out.println("Exiting the game. Goodbye!");
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int value = input.nextInt();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                input.next(); // Clear the invalid input
            }
        }
        private String readNames (String prompt){
            String name = "";
            while (name.length() == 0) {
                System.out.print(prompt);
                name = input.nextLine().trim();
            }
            return name;
        }
        private int findPlayers (String name){
            for (int i = 0; i < playerCount; i++) {
                if (players[i].getName().equalsIgnoreCase(name))
                    return i;
            }
            return -1; // Player not found
        }

        private int getOrCreatePlayer (String name){
            for (int i = 0; i < playerCount; i++) {
                if (players[i].getName().equalsIgnoreCase(name))
                    return i; // Player found, return index
            }
            return -1; // Player not found
        }
        private int getOrCreatePlayer (String name){
            int index = findPlayers(name);
            if (index != -1)
                return index; // Player found, return index
            if (playerCount >= players.length) throw new IllegalStateException("Player storage full.");
            players[playerCount] = new Player(name);
            return playerCount++; // Return new player's index and increment count
        }
        private int randomInt ( int min, int max){
            return (int) (Math.random() * (max - min + 1)) + min;
        }
        private void playMatch () {
            String first = readName("First plaer name: ");
            int a = getOrCreatePlayer(first);
            System.out.println("Second player: 1. Human 2. AI");
            boolean bot = readInt("Choose: ", 1, 2) == 2;
            String second = bot ? "Bot Player" + randomInt(1, 10) : readNames("Second player name: ");
            while (players[a].getName().equalsIgnoreCase(second)) {
                System.out.println("Players must have different names.");
                second = bot ? "BotPlayer" + randomInt(1, 10) : readNames("Second Player name: ");
            }
            int b = getOrCreatePlayer(second);
            runMatch(a, b, false, bot, true);
        }
        //Both players share one shuffled deck per round.
        private void runMatch(int a, int b, boolean botA, boolean botB, boolean show){
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

                if (show) System.out.println("\nROUND " + round + "| Target: " + target);

                int firstIndex = first == 0 ? a : b;
                int secondIndex = first == 0 ? b : a;
                boolean firstBot = first == 0 ? botA : botB;
                boolean secondBot = first == 0 ? botB : botA;
                int firstGuess = firstBot ? randomInt(RuleConfig.SIM_MIN_CARDS,
                        : readInt(players[firstIndex].getName() + " Cards to draw (1-26): ", 1, 26);
                deal(players[firstIndex], deck, firstGuess, target, show);

                int secondGuess;
                if (secondBot) { secondGuess = firstGuess + RuleConfig.SIM_MIN_CARDS, RuleConfig.SIM_OFFSET;
                } else {
                    secondGuess = readInt(players[secondIndex].getName() + " Cards to draw (1-", deck.cardsRemaining() + "): ", 1, deck.cardsRemaining());
                }
                deal(players[secondIndex], deck, secondGuess, target, show);

                int score1 = scoreCalculator.calculateScore(p1.getHand().getBestTotal(target);
                int score2 = scoreCalculator.calculateScore(p2.getHand().getBestTotal(target);
                if (show) {
                    System.out.println("Scores so far: " + p1.getName() + "=" + p1.getTotalScore() + ", " + p2.getName() + "=" + p2.getTotalScore());
                }
                if(total1 < target && total2 < target) target += RuleConfig.TARGET_SHIFT;
                else if (total1 > target && total2 > target) target -= RuleConfig.TARGETSHIFT;
                first = 1 - first;

            }
            if (show) System.out.print("\nFINAL: " + p1.getName() + "=" + p1.getTotalScore( + " | " + p2.getName() + "=" + p2.getTotalScore()));
            boolean p1Won = p1.getTotalScore() < p2.getTotalScore();
            boolean p2Won - p2.getTotalScore() < p1.getTotalScore();
            p1.recordMatch(p1Won);
            p2.recordMatch(p2Won);
            if (p1Won) headToHeadWins[a][b]++;
            if (p2Won) headToHeadWins[a][b]++;
            if (show){
                if(p1Won) System.out.println("Winner: " +p1.getName());
                else if (p2Won) System.out.println("Winner: " + p2.getName());
                else System.out.println("It's a tie!");
            }
        }

        private void deal(Player player, Deck deck, int count, int target, boolean show) {
            if (show) System.out.println(player.getName() + " draws " + count + " cards:");
            for (int i = 0; i < count; i++) player.getHand().addCard(deck.drawCard());
            int total = player.getHand().getBestTotal(target);
            int score = calculator.calculateScore(player.getHand(), target);
            player.addScore(score);
            if (show)  {
                System.out.println("Total: " + total + ", Score: " + score);
            }
        }


    }
}
