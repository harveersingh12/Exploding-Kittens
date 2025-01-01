package Controller.Players;

import Controller.ClientServer.Server;
import Controller.Game;
import Model.Card;
import Model.CardType;
import Model.Hand;
import Model.Deck;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 * Class for human game player, based on basic properties
 * of the Player class.
 * @invariant player has a name and hand
 * @invariant player draws and plays cards from one deck
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class HumanPlayer implements Player {
    private Hand hand;
    private String name;


    /**
     * @param name is the name of the player.
     * Player is constructed with just a name,
     * deck is set through the playCard() method
     */
    public HumanPlayer(String name){
        this.name = name;
    }


    /**
     * @param name, name of the player
     * @param deck, deck from which player draws and gets their hand
     */
    public HumanPlayer(String name, Deck deck){
        this(name);
        this.hand = new Hand(deck);
    }


    /**
     * @requires hand is not null
     * @return hand of the player
     */
    @Override
    public Hand getHand(){
        return hand;
    }


    /**
     * @requires name is not null
     * @return name of the player
     */
    @Override
    public String getName(){
        return name;
    }


    /**
     * @param deck is the game deck from which the draw pile is created.
     * Players draw cards from the draw pile depending on the type of card
     * that they play. The player's hand is updated and displayed after every
     * turn so that they can see what they drew.
     * @requires drawn card, deck and draw pile are not null
     * @return the card drawn by the player
     */
    @Override
    public Card drawCard(Deck deck){
        if(deck.getDrawPile() != null){
            Card drawnCard = deck.drawCard();
            if(drawnCard != null) {
                hand.addCard(drawnCard);
            }
            Server.askPlayer(this,"Player " + name + ", you drew the card " + drawnCard.getName() +"."+
                    "\nHand updated:\n" + hand);
            return drawnCard;
        }return null;
    }


    /**
     * Players have the ability to play a card or pass their turn
     * (by saying 'no') to the next person. To do so, they must select
     * a card to remove from their hand and add to the discard pile.
     * This can be a single card or a double/triple combo. Depending on
     * the card type the corresponding action is executed.
     * @requires player to provide a card from their hand or to respond with 'no'
     * @ensures player is asked for input as long as they don't say 'no'
     * @ensures player plays a valid combination of cards
     * @ensures player can only play card(s) from their hand
     * @return the chosen card(s) from the player's hand stored in a list.
     */
    @Override
    public ArrayList<Card> playCard() {
        Server.askPlayer(this, name + ", do you want to play a card?\n" + "yes - type the card name\n" + "no - type DRAW_CARD");
        Server.askPlayer(this, hand.toString());
        ArrayList<Card> result;

        while (true) {
            if(hand.isCursed()){
                Server.askPlayer(this,name +  ", do you want to play a card?\n" + "yes - the card will be chosen randomly\n" + "no - type DRAW_CARD");
                Server.askPlayer(this, hand.toString());
                ArrayList<Card> cursedResult = new ArrayList<>();

                String input = Server.receiveInput(this);
                if(input.equals("DRAW_CARD")){
                    return null;
                }else{
                    Random random = new Random();
                    int randomIndex = random.nextInt(hand.getCards().size()-1);
                    cursedResult.add(hand.getCards().get(randomIndex));
                    hand.getCards().remove(randomIndex);
                    return cursedResult;
                }
            }
            String input = Server.receiveInput(this);

            if (input.equals("DRAW_CARD")) {
                return null;
            }

            String[] splitInput = input.split(",");

            if(Game.flags.contains(Game.PARTY_FLAG)){
                if(splitInput.length == 5) {
                    return fiveCardCombo(splitInput);
                }
            }
            result = new ArrayList<>();
            boolean validCombination = true;

            for (String param : splitInput) {
                Card card = findCardByName(param);

                if (card != null) {
                    if (result.isEmpty() || card.getType() == result.get(0).getType() ||
                            (card.getType() == CardType.FERAL_CAT && isCatCard(result.get(0)))
                            || isCatCard(card) && result.get(0).getType() == CardType.FERAL_CAT){
                        result.add(card);
                    } else {
                        validCombination = false;
                        break;
                    }
                } else {
                    validCombination = false;
                    break;
                }
            }

            if (validCombination && result.size() == splitInput.length) {
                for (Card card : result) {
                    hand.getCards().remove(card);
                }
                break;
            } else {
                Server.askPlayer(this,"Enter a valid combination of cards of the same type. Try again:");
            }
        }

        return result;
    }

    /**
     * Tests whether a card is a cat card or not. Created for the implementation
     * of the FERAL CAT card type, which can be used in combination with any cat card.
     * @param card, the card to be tested for its type
     * @return true if the card is one of the cat cards
     */
    public boolean isCatCard(Card card) {
        return card.getType() == CardType.BEARD_CAT || card.getType() == CardType.CATTERMELON ||
                card.getType() == CardType.TACO_CAT || card.getType() == CardType.RAINBOW_RALPHING ||
                card.getType() == CardType.HAIRY_POTATO;
    }

    /**
     * Verifies that the list of cards played is in accordance
     * with the rules. If it does, the five-card combo is called
     * in the Game class and the player chooses a card to take from
     * the discard pile.
     * @param input, the played card list
     * @return a list of five unique card types, excluding streaking/exploding kittens
     * (or null if the player decides to draw)
     */

    private ArrayList<Card> fiveCardCombo(String[] input) {
        ArrayList<Card> result = new ArrayList<>();

        do {
            result.clear();
            CardType currentType = null;
            for (String s : input) {
                Card card = findCardByName(s);
                if (card != null) {
                    if (card.getType() != currentType && card.getType()!=CardType.EXPLODING_KITTEN
                            && card.getType()!=CardType.STREAKING_KITTEN) {
                        result.add(card);
                        currentType = card.getType();
                    }
                }
            }
            if (result.size() == 5) {
                return result;
            }
            Server.askPlayer(this,"Enter five cards in your hand that are not of the same type " +
                    "(cannot be streaking/exploding kitten)");
            String askAgain = Server.receiveInput(this);
            if (askAgain.equals("no")) {
                return null;
            }
            input = askAgain.split(",");

        } while (true);
    }


    /**
     * @param cardName, the String name of the card
     * that needs to be found
     * @requires card name is valid and in the player's hand
     * @return the card of type Card if the card name
     * is found in the player's hand
     */
    @Override
    public Card findCardByName(String cardName) {
        for (Card card : hand.getCards()) {
            if (card.getName().equals(cardName)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Checks the player's hand for a nope card
     * @return whether a nope card is present
     *
     */
    @Override
    public boolean hasNopeCard() {
        for (Card card : hand.getCards()) {
            if (card.getType() == CardType.NOPE) {
                return true;
            }
        }
        return false;
    }
    /**
     * This method attempts to find a nope card and if
     * successful, removes the nope card from the player's hand
     * @requires found card is not null
     */
    @Override
    public void playNopeCard() {
        Card nope = hand.findCard(CardType.NOPE);
        if (nope != null) {
            hand.removeCard(nope);
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return The name of the object as a string.
     */
    @Override
    public String toString(){
        return getName();
    }
}