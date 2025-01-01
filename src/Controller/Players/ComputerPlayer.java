package Controller.Players;

import Model.Card;
import Model.CardType;
import Model.Hand;
import Model.Deck;

import java.util.ArrayList;


/**
 * Class for the computer game player. Contains all the same
 * methods as Human Player since it implements the same interface.
 * Therefore, see the Human Player class for all documentation
 * (except for the playCard()).
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ComputerPlayer implements Player{
    private Card card;
    private String name;
    private Hand hand;

    public ComputerPlayer(String name){
        this.name = name;
    }

    public ComputerPlayer(String name, Deck deck){
        this(name);
        this.hand = new Hand(deck);
    }


    @Override
    public Card drawCard(Deck deck) {
        if(deck.getDrawPile() != null){
            Card drawnCard = deck.drawCard();
            if(drawnCard != null) {
                hand.addCard(drawnCard);
            }
            return drawnCard;
        }return null;
    }

    @Override
    public ArrayList<Card> playCard() {
        ArrayList<Card> result = new ArrayList<>();
        Card cardToPlay = null;
        for (Card card : hand.getCards()) {
            if (card.getType() != CardType.DEFUSE && card.getType() != CardType.NOPE && card.getType() != CardType.CATTERMELON
                    && card.getType() != CardType.BEARD_CAT && card.getType() != CardType.TACO_CAT
                    && card.getType() != CardType.RAINBOW_RALPHING && card.getType() != CardType.HAIRY_POTATO) {
               cardToPlay = card;
                break;
            }
        }
        if(cardToPlay!=null){
        result.add(cardToPlay);
        hand.getCards().remove(cardToPlay);
        return result;
        }else return null;
    }

    @Override
    public boolean hasNopeCard() {
        for (Card card : hand.getCards()) {
            if (card.getType() == CardType.NOPE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void playNopeCard() {
        Card nope = hand.findCard(CardType.NOPE);
        if (nope != null) {
            hand.removeCard(nope);
        }
    }

    @Override
    public String getName(){
        return name;
    }
    @Override
    public Hand getHand() {
        return hand;
    }

    @Override
    public Card findCardByName(String cardName){
        for (Card card : hand.getCards()) {
            if (card.getName().equals(cardName)) {
                return card;
            }
        }
        return null;
    }

    @Override
    public boolean isCatCard(Card card) {
        return card.getType() == CardType.BEARD_CAT || card.getType() == CardType.CATTERMELON ||
                card.getType() == CardType.TACO_CAT || card.getType() == CardType.RAINBOW_RALPHING ||
                card.getType() == CardType.HAIRY_POTATO;
    }


    @Override
    public String toString(){
        return getName();
    }
}
