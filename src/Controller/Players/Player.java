package Controller.Players;

import Model.Card;
import Model.Hand;
import Model.Deck;

import java.util.ArrayList;

/**
 * Class containing all shared elements between the HumanPlayer and ComputerPlayer.
 * All players have the ability to play or draw a card from the draw pile in the game.
 * In order to display their respective names and cards, getName() and getHand() have been added.
 */
public interface Player {
    Card drawCard(Deck deck);
    ArrayList<Card> playCard();
    String getName();
    Hand getHand();
    boolean hasNopeCard();
    void playNopeCard();
    String toString();
    Card findCardByName(String cardName);
    boolean isCatCard(Card card);
}
