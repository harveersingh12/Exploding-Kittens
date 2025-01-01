package Test;
import Model.Card;
import Model.CardType;
import Model.Hand;
import Model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * The {@code HandTest} class contains JUnit tests for the {@code Hand} class.
 * It focuses on various aspects of the hand, including the number of cards, the presence
 * of specific cards like Defuse, clearing the hand, removing cards, and testing multiple hands.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class HandTest {
    Deck deck;
    Hand hand;


    /**
     * Initializes the deck and hand objects before each test.
     */
    @BeforeEach
    public void initialize(){
        deck = new Deck();
        hand = new Hand(deck);
    }


    /**
     * Tests if the number of cards in the hand is accurate to the one in the real game.
     */
    @Test
    public void numberOfCards(){
        assertEquals(8, hand.getCards().size());
    }


    /**
     * Tests whether the hand contains at least one Defuse card.
     */
    @Test
    public void containsDefuse(){
        int noDefuses = 0;
        for(Card card : hand.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses++;
        assertEquals(1, noDefuses);
    }


    /**
     * Tests the "clearHand" method to ensure the hand becomes empty.
     */
    @Test
    public void testClearHand(){
        hand.clearHand();
        assertTrue(hand.getCards().isEmpty());
    }


    /**
     * Tests the removal of a card from the hand.
     */
    @Test
    public void testRemove(){
        Card card = hand.getCards().get(1);
        hand.removeCard(card);
        assertEquals(7, hand.getCards().size());
    }


    /**
     * Tests the creation of multiple hands (max amount of players in the base-version of the game)
     * and ensures each has at least one Defuse card.
     */
    @Test
    public void test5Hands(){
        Hand hand2 = new Hand(deck);
        Hand hand3 = new Hand(deck);
        Hand hand4 = new Hand(deck);
        Hand hand5 = new Hand(deck);
        int noDefuses1 = 0;
        int noDefuses2 = 0;
        int noDefuses3 = 0;
        int noDefuses4 = 0;
        int noDefuses5 = 0;

        for(Card card : hand.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses1++;
        assertEquals(1, noDefuses1);

        for(Card card : hand2.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses2++;
        assertEquals(1, noDefuses2);

        for(Card card : hand3.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses3++;
        assertEquals(1, noDefuses3);

        for(Card card : hand4.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses4++;
        assertEquals(1, noDefuses4);

        for(Card card : hand5.getCards())
            if(card.getType() == CardType.DEFUSE)
                noDefuses5++;
        assertEquals(1, noDefuses5);
    }
}
