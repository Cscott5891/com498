public class Main {
    public static void main(String[] args) {
        Card card1 = new Card("Hearts", "A");
        Card card2 = new Card("Diamonds", "10");
        Card card3 = new Card("Clubs", "5");

        System.out.println(card1); // Output: A of Hearts
        System.out.println(card2); // Output: 10 of Diamonds
        System.out.println(card3); // Output: 5 of Clubs

        System.out.println("Value of card1: " + card1.getValue()); // Output: Value of card1: 11
        System.out.println("Value of card2: " + card2.getValue()); // Output: Value of card2: 10
        System.out.println("Value of card3: " + card3.getValue()); // Output: Value of card3: 5
    }
}
