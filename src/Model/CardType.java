package Model;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Enum containing all card types of the game.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public enum CardType {
   ATTACK(4,2, 3, 0),
   TARGETED_ATTACK(0, 2, 3, 0),
   SEE_THE_FUTURE(5, 3, 3, 0),
   SHUFFLE(4,2,4, 0),
   SKIP(4, 4, 6, 0),
   FAVOR(4,2,4, 0),
   NOPE(5,4,5, 0),
   HAIRY_POTATO(4,3,4,0),
   TACO_CAT(4,3,4, 0),
   RAINBOW_RALPHING(4,3,4,0),
   BEARD_CAT(4,3,4, 0),
   CATTERMELON(4,3,4,0),
   ALTER_THE_FUTURE(0,2,4, 0),
   DRAW_FROM_THE_BOTTOM(0,3,4, 0),
   FERAL_CAT(0,2,4, 0),
   SUPER_SKIP(0,0,0,1),
   x5_SEE_THE_FUTURE(0,0, 0,1),
   x5_ALTER_THE_FUTURE(0,0,0,1),
   SWAP_TOP_AND_BOTTOM(0,0,0,3),
   GARBAGE_COLLECTION(0,0,0,1),
   CATOMIC_BOMB(0,0,0,1),
   MARK(0,0,0,3),
   CURSE_OF_THE_CAT_BUTT(0,0,0,2),
   STREAKING_KITTEN(0,0,0,1),

   DEFUSE(6, 3, 7, 0), //done
   EXPLODING_KITTEN(4, 2, 7, 1); //done

   private final int STANDARD_AMOUNT;
   private final int PARTY_PACK_AMOUNT_PAW;
   private final int PARTY_PACK_AMOUNT_NO_PAW;
   private final int STREAKING_KITTENS_AMOUNT;


   /**
    * static variables labeling the game type with the
    * index for each element in the list that comes from getAmount().
    */
   public static final int POSITION_STANDARD = 0;
   public static final int POSITION_PARTY_PAW = 1;
   public static final int POSITION_PARTY_NO_PAW = 2;
   public static final int POSITION_PARTY_TOTAL = 3;
   public static final int POSITION_EXTENSION = 4;;


   /**
    * Gets the number of cards for each card type for each version of the game.
    * When adding these cards to a deck, one of the indices in the list can be chosen,
    * providing only the amount required for the flag that is active.
    * @return result, the list containing the number of cards for a card type in each version
    * of the game
    */
   public ArrayList<Integer> getAmount(){
      ArrayList<Integer> result = new ArrayList<>();
      Collections.addAll(result, STANDARD_AMOUNT, PARTY_PACK_AMOUNT_PAW, PARTY_PACK_AMOUNT_NO_PAW,
              PARTY_PACK_AMOUNT_NO_PAW + PARTY_PACK_AMOUNT_PAW, STREAKING_KITTENS_AMOUNT);
      return result;
   }


   /**
    * Creates a card type with a parameter for the number of cards of the type in every game variety
    * @param standardAmount, number of cards in the base game
    * @param partyPackAmountPaw, number of cards in the party pack with less than four players
    * @param partyPackAmountNoPaw, number of cards in the party pack with four to seven players
    * @param streakingKittensAmount, number of cards in the streaking kittens pack
    */
   CardType(int standardAmount, int partyPackAmountPaw, int partyPackAmountNoPaw, int streakingKittensAmount){
      this.STANDARD_AMOUNT = standardAmount;
      this.PARTY_PACK_AMOUNT_PAW = partyPackAmountPaw;
      this.PARTY_PACK_AMOUNT_NO_PAW = partyPackAmountNoPaw;
      this.STREAKING_KITTENS_AMOUNT = streakingKittensAmount;
   }
}