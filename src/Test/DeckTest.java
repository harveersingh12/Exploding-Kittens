package Test;

import Controller.Players.Player;
import Model.CardType;
import Model.Deck;
import Controller.Game;
import Controller.Players.HumanPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


/**
 * "Model.Deck.java" is one of the most complex classes in our project, since it is the base of the whole game.
 * Without the deck, a game cannot run properly.
 * The "Model.Deck.java" class is also dependent on the classes "Model.Card.java" && "Model.CardType.java".
 *
 *
 * The {@code DeckTest} class contains JUnit tests for the {@code Deck} class.
 * These tests cover various aspects such as the size of the deck, shuffling, drawing cards, and resetting the deck.
 * The deck is a crucial component of the game, and these tests ensure its proper functionality.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 *
 */
public class DeckTest {
    Deck deck;
    Game game;

    /**
     * Calculates the expected size of the deck based on the specified game configuration.
     *
     * @param whatAmount 0 for standard game, 1 for small party, 2 for medium party,
     *                          3 for big party, and 4 for streaking kittens extension.
     * @return The expected size of the deck.
     */
    private int calculateNeededSize(int whatAmount){
    int size = 0;
    Player p1 = new HumanPlayer("player1");
    Player p2 = new HumanPlayer("player 2");
    ArrayList<Player> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    game = new Game(players);

    //add all the cards
    for(CardType type : CardType.values()){
        if(type != CardType.EXPLODING_KITTEN && type != CardType.DEFUSE)
            size += type.getAmount().get(whatAmount);
    }
    //add defuse
    if (Game.numberOfPlayers <= 3)
            size += 2 + Game.numberOfPlayers;
    else
        size += CardType.DEFUSE.getAmount().get(0) - Game.getNumberOfPlayers();
    //add ek
    size += deck.getNumberOfEk();

    //substract the players hands (2* 8 cards)
        size -= 16;
    return size;
    }


    /**
     * Initializes the deck before each test.
     */
    @BeforeEach
    public void createDeck(){
        deck = new Deck();
    }


    /**
     * Tests the size of the deck for a standard game.
     */
    @Test
    public void testDeckStandard(){
        assertEquals(calculateNeededSize(0),game.getDeck().getDrawPile().size());
    }


    /**
     * Tests the size of the deck for a small party game.
     */
    @Test
    public void testDeckSmallParty(){
        assertEquals(calculateNeededSize(1), deck.getDrawPile().size());
    }


    /**
     * Tests the size of the deck for a medium party game.
     */
    @Test
    public void testDeckMediumParty(){
        assertEquals(calculateNeededSize(2), deck.getDrawPile().size());
    }


    /**
     * Tests the size of the deck for a big party game.
     */
    @Test
    public void testDeckBigParty(){
        assertEquals(calculateNeededSize(3), deck.getDrawPile().size());
    }


    /**
     * Tests the size of the deck for an extension game.
     */
    @Test
    public void testDeckStreakingKittens(){
        assertEquals(calculateNeededSize(4), deck.getDrawPile().size());
    }


    /**
     * Tests if shuffling the deck results in a different order.
     */
    @Test
    public void shuffle(){
        Deck shuffledDeck = new Deck();
        shuffledDeck.shuffle();
        assertNotEquals(deck.getDrawPile(), shuffledDeck.getDrawPile());
    }

    /**
     * Tests if drawing a card changes the size of the deck.
     */
    @Test
    public void testDrawCard(){
        Deck origialDeck = new Deck();
        HumanPlayer player = new HumanPlayer("test_player", deck);
        player.drawCard(deck);
        assertNotEquals(deck.getDrawPile().size(), origialDeck.getDrawPile().size());
    }

    /**
     * Tests if resetting the deck results in a different order.
     */
    @Test
    public void testReset(){
        Deck originalDeck = new Deck();
        deck.resetDeck();
        assertNotEquals(deck.getDrawPile(), originalDeck.getDrawPile());
    }
}
