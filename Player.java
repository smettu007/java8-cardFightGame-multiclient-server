import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
/**
 * Created by subbabramaiah mettu
 *
 * purpose of this class is to create a game player with instance variables such as score, card seleced,
 * cards count, players turn etc
 */
public class Player {
    //currently public to make testing easier but will be private eventually
    //public variables
    private int cardNo, cardsCount;
    private int playerID;
    private Card playerCard;
    private Card opponentCard;
    private Card selectedCard;

    //instance variables
    private boolean isPlaying;
    private int score;
    private ArrayList<Card> cards;
    private static final Card[] packOfCards = {
       new Card("dino", 10, "Strength", "Intelligence",1),
       new Card("zeus", 8, "Intelligence", "Agility",2),
       new Card("achilles", 10, "Agility", "Strength",3),
       new Card("aqua", 9, "Neutral", "none",4)

    };

    public Player() {

        playerID = 0;
        cardsCount = 4;
        selectedCard = null;
        opponentCard = new Card("Opponent Card",0,"none","none",0);
        playerCard = new Card("your card",0,"none","none",0);
        cards = new ArrayList<Card>();
        score = 0;
        generateCards();
    }

    /**
     * method to return the card of the played based on the index
     * @param index index of the card
     * @return
     */
    public Card getCard(int index) {

        return packOfCards[index];
    }

public void setPlayerID(int id){

        playerID = id;
}
    /**
     * heper method gives the score of the player
     * @return returns the score of the player
     */
    public int getScore() {
        return score;
    }
    public int getPlayerID(){

        return playerID;
    }


    /**
     * sethod to set thecurrent card selected by the player and its card number
     * @param card card selected by the player
     * @param cardNum card no of the selected card
     */
    public void setSelectedCard(Card card, int cardNum){

        selectedCard = card;
        if(selectedCard !=null){
            selectedCard.setName(card.getName(),card.getPower(),card.getType(),card.getWeakness());
            cardNo = cardNum;
        }

    }

    /**
     * returns the card that user currently selected but not yet played
     * @return card that is currently selected
     */
    public Card getSelectedCard(){

        return selectedCard;
    }

    /**
     * methdo which will return the card no that player selected currently
     * @return card no in the cards array
     */
    public int getCardNo(){

        return cardNo;
    }

    /**
     * method to reduce the cards count by -1 after each turn
     */
    public void decrementCardsCount(){

        cardsCount--;
    }

    /**
     * method to return the current cards owned by the player
     * @return
     */
    public int getCardsCount(){

        return cardsCount;
    }

    /**
     * just a helper method to set the cards count
     * @param value count of the cards
     */
    public void setCardsCount(int value) {

        cardsCount = value;
    }

    /**
     * set the opponent card that was played by him and update the deck
     * @param card
     */
    public void setOpponentCard(Card card){

        opponentCard.setName(card.getName(),card.getPower(),card.getType(),card.getWeakness());
    }

    /**
     * method to set the player card that he wants to play on the deck for challenge
     * @param card card that you want ot set on the deck to challenge the opponent
     */
    public void setPlayerCard(Card card){

        playerCard.setName(card.getName(),card.getPower(),card.getType(),card.getWeakness());
    }

    /**
     * method to return the card of the player that he played to challenge the opponent
     * @return
     */
    public Card getPlayerCard(){

        return playerCard;
    }

    /**
     * methdod to return the opponent card of this player
     * @return return opponents card that he played
     */
    public Card getOpponentCard(){

        return opponentCard;
    }

    /**
     * methdo to increment the score of the player
     */
    public void setScore(){

        score++;
    }

    /**
     * method to change the replace the card in index that used wants
     * @param index index of the card
     * @param card card to replace with
     */
    public void setCard(int index, Card card) {

        cards.set(index, card);
    }

    /**
     * helper method to return the cards of the player
     * @return
     */
    public ArrayList<Card> getCards() {

        return cards;
    }

    /**
     * methdod to see if its current players turn or not
     * @return returns boolean value, true if its his turn false if not
     */
    public boolean getIsPlaying() {

        return isPlaying;
    }

    /**
     * methdo to set the playing  status of the player
     * @param status bollean, true if its current player turn, false if not
     */
    public void setIsPlaying(boolean status) {

        isPlaying = status;
    }

    /**
     * methdo to generate cards of the player, can be random p11
     */
    public void generateCards() {


        cards.add(new Card("dino", 10, "Strength", "Intelligence",1));
        cards.add(new Card("zeus", 8, "Intelligence", "Agility",2));
        cards.add(new Card("achilles", 10, "Agility", "Strength",3));
        cards.add(new Card("aqua", 9, "Neutral", "none",4));

        //cards.get(0).setName("zeus", 8, "Intelligence", "Agility");

    }

    /**
     * method to reset the player instance variables if game resets or new game starts
     */
    public void resetProps(){

        this.cardsCount = 4;
        this.selectedCard = null;
        this.isPlaying  = false;
        this.score = 0;
        generateCards();

    }


}
