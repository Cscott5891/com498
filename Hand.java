public class Hand {
    private Card[] cards;
    private int cardCount;

    public Hand() {
        cards = new Card[52];
        cardCount = 0;
    }

    public void addCard(Card card) {
        if (cardCount < cards.length) {
            cards[cardCount++] = card;
        } else {
            throw new IllegalStateException("Hand is full.");
        }
    }

    public int getTotal() {
        int totalValue = 0;
        for (int i = 0; i < cardCount; i++) {
            totalValue += cards[i].getValue();
        }
        return totalValue;
    }

    public int countAces() {
        int aceCount = 0;
        for (int i = 0; i < cardCount; i++) {
            if (cards[i].getRank().equals("A")) {
                aceCount++;
            }
        }
        return aceCount;
    }

    public int getBestTotal(int target) {

        int totalValue = getTotal();
        int aceCount = countAces();

        int bestTotal = totalValue;
        int bestDistance = Math.abs(target - totalValue);

        for (int i = 0; i < aceCount; i++) {

            // Change one Ace from 11 to 1.
            totalValue -= 10;
            int distance = Math.abs(target - totalValue);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestTotal = totalValue;
            }
        }

        return bestTotal;
    }
}

