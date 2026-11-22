public class Deck {
    private Card[] cards;
    private int nextCard;

    public Deck() {
        cards = new Card[52];
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        int index = 0;
        for (String suit : suits) {
            for (String rank : ranks) {
                cards[index++] = new Card(suit, rank);
            }
        }
        nextCard = 0;
    }

    public void displayDeck() {
        for (Card card : cards) {
            System.out.println(card);
        }
    }

    public Card drawCard() {
        if (nextCard < cards.length) {
            return cards[nextCard++];
        } else {
            throw new IllegalStateException("No more cards in the deck.");
        }
    }

    public int cardsRemaining() {
        return cards.length - nextCard;
    }

    public void shuffle() {
        for (int i = cards.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
        nextCard = 0; // Reset nextCard after shuffling
    }
}
