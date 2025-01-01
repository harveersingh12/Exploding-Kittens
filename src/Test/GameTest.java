package Test;


import Controller.Game;
import Controller.Players.HumanPlayer;
import Controller.Players.Player;
import Model.Card;
import Model.CardType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The {@code GameTest} class contains JUnit tests for the overall game functionality,
 * such as checking the presence of Exploding Kittens (EK) in the draw pile and player hands,
 * the number of cards in players' hands, and the presence of Defuse cards in players' hands.
 *
 * <p>These tests focus on the initial state of the game before any player takes their turn.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class GameTest {
    Game game;
    Player p1;
    Player p2;


    /**
     * Sets up the game and players before each test.
     */
    @BeforeEach
    public void setUp(){
        p1 = new HumanPlayer("player 1");
        p2 = new HumanPlayer("player 2");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        game = new Game(players);
    }


    /**
     * Tests if the number of Exploding Kittens in the draw pile is correct.
     */
    @Test
    public void testEkInGame(){
        int ekCounter = 0;
        for(Card card: game.getDeck().getDrawPile()){
            if(card.getType()== CardType.EXPLODING_KITTEN){
                ekCounter++;
            }
        }
        Assertions.assertEquals(Game.getNumberOfPlayers()-1,ekCounter);
    }


    /**
     * Tests if no player has an Exploding Kitten card in their hand initially.
     */
    @Test
    public void testEkInHand(){
        for(Player player: game.getPlayers()){
            Assertions.assertFalse(player.getHand().containsCardType(CardType.EXPLODING_KITTEN));
        }
    }
}
