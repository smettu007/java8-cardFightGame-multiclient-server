import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.security.krb5.SCDynamicStoreConfig;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Created By subbaramaiah mettu
 *
 * The purpose of this class is to receive two sockets that server created and handle them appropriately on a thread.
 * This class will ahndle all the protocols receive by two clients and will send proper responses based on the protocols
 * This class will decide who won and can the player quit or start a new game or not
 */
public class HandleASession implements Runnable, Protocol {

    private Socket player1;
    private Socket player2;
    private int player1Score, player2score;
    private int player1Response, player2Response;

    private static final Card[] packOfCards = {
            new Card("dino", 10, "Strength", "Intelligence", 1),
            new Card("zeus", 8, "Intelligence", "Agility", 2),
            new Card("achilles", 10, "Agility", "Strength", 3),
            new Card("aqua", 9, "Neutral", "none", 4)
    };
    private Card player1Card, player2Card;
    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;


    public HandleASession(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1Score = 0;
        player2score = 0;
        // Initialize cells

    }

    public void run() {

        try {
            try {
                // Create data input and output streams
                fromPlayer1 = new DataInputStream(player1.getInputStream());
                toPlayer1 = new DataOutputStream(player1.getOutputStream());
                fromPlayer2 = new DataInputStream(player2.getInputStream());
                toPlayer2 = new DataOutputStream(player2.getOutputStream());

                // Write anything to notify player 1 to start
                // This is just to let player 1 know to start
                toPlayer1.writeInt(1);
                toPlayer1.flush();

                executeCmds();


            } finally {
                player1.close();
                player2.close();
            }
        } catch (IOException ex) {
            System.err.println(ex + " yay");
        }

    }//run

    /**
     * Execute all commands until the QUIT command is received from the client,
     * i.e. continuously serve the client.
     * If there is an unknown command, then stop, do not continue.
     */
    private void executeCmds() throws IOException {

        // start listening to client's requests and respond to them
        boolean done = false;
        while (!done) {


            //System.out.println("cards count  " + cardsCount);
            int player1 = fromPlayer1.readInt();
            System.out.println("received Something1 " + player1);


            //player 1 handling
            if (player1 == PLAYER1) {
                int player1Action = fromPlayer1.readInt();
                switch (player1Action) {

                    case PLAY_CARD: //play the card
                        int player1CardNo = fromPlayer1.readInt();
                        toPlayer2.writeInt(SHOW_CARD);
                        toPlayer2.writeInt(player1CardNo);
                        toPlayer2.flush();
                        player1Card = packOfCards[player1CardNo];
                        System.out.println("sent to  player 2 , player 1 cardNo: " + player1CardNo);
                        break;

                    case SET_SCORE: //
                        //get p1 score
                        int pScore = fromPlayer1.readInt();
                        player1Score = pScore;
                        System.out.println("player1 score: " + player1Score);
                        break;
                    case QUIT: //send response to player 2 that he won

                        SendPlayer2Response(QUIT_WIN);
                        SendPlayer1Response(QUIT);
                        break;

                    case RESET: //send reset protocol to both players

                        SendPlayer1Response(RESET);
                        SendPlayer1Response(SET_PLAYER1);
                        break;
                    case NEW_GAME: //send new game protocol to player 2 asking if he likes

                        SendPlayer2Response(NEW_GAME);
                        break;
                    case YES: //if player 1 likes to newgame then send reset responses and set player 1 an d2 again

                        SendPlayer2Response(RESET);
                        SendPlayer2Response(SET_PLAYER2);
                        SendPlayer1Response(RESET);
                        SendPlayer1Response(SET_PLAYER1);
                        break;

                    case NO: //no from player 2 to l palyer1
                        SendPlayer2Response(NO_NEW_GAME);
                        break;

                }

            }

            int player2 = fromPlayer2.readInt();
            System.out.println("received Something2 " + player2);

            //player 2 handling
            if (player2 == PLAYER2) {

                int player2Action = fromPlayer2.readInt();
                switch (player2Action) {

                    //after player 2 turn calculate score and wait
                    case PLAY_CARD:
                        int player2CardNo = fromPlayer2.readInt();
                        toPlayer1.writeInt(SHOW_CARD);
                        toPlayer1.writeInt(player2CardNo);
                        toPlayer1.flush();
                        player2Card = packOfCards[player2CardNo];

                        //check who won the round or the game
                        checkWinner();

                        System.out.println("sent to  player 1 , player 2 cardNo: " + player2CardNo);
                    break;

                    case QUIT:

                        SendPlayer1Response(QUIT_WIN);
                        SendPlayer2Response(QUIT);
                        break;

                    case SET_SCORE:

                        //get p2 score
                        int pScore = fromPlayer2.readInt();
                        player2score = pScore;
                        System.out.println("player2 score: " + player2score);

                        //once we have all scores then evaluate winner
                        sendWinLose();
                        break;
                    case RESET:

                        SendPlayer2Response(RESET);
                        SendPlayer2Response(SET_PLAYER2);
                        break;
                    case NEW_GAME: //ask if opponentwants to play gamr

                        SendPlayer1Response(NEW_GAME);
                        break;
                    case YES:
                        SendPlayer1Response(RESET);
                        SendPlayer1Response(SET_PLAYER1);
                        SendPlayer2Response(RESET);
                        SendPlayer2Response(SET_PLAYER2);

                        break;

                    case NO: //no from player 2 to l palyer1
                        SendPlayer1Response(NO_NEW_GAME);
                        break;


                }
            }

        } // while
    }

    /**
     * method to determine the winner of the roundand the game
     * @throws IOException
     */
    public void checkWinner() throws IOException{

        //player 1 won
        if (compareCardwinner(player1Card, player2Card) == 1) {

            //notify player 2 lost round
            SendPlayer2Response(PLAYER_LOSTROUND);

            // Notify player 1 won round
            SendPlayer1Response(PLAYER_WONROUND);

            //give score to player 1
            SendPlayer1Response(GIVE_SCORE);

            //freeze player frames for 4 sec
            waitPlayers();

            //hide player cards
            hidePlayerCards();

            int showWinner = fromPlayer2.readInt();

            //if both players have 0 cards player 2 will send show winner request

            if (showWinner == SHOW_WINNER) {
                getScores();
            } else {
                SendPlayer1Response(SET_PLAYER1);
                SendPlayer2Response(SET_PLAYER2);
            }

        } else if (compareCardwinner(player1Card, player2Card) == 2) {

            // Notify player 2 won round
            SendPlayer2Response(PLAYER_WONROUND);

            //notify player 1 lost round
            SendPlayer1Response(PLAYER_LOSTROUND);

            //give score to player 2
            SendPlayer2Response(GIVE_SCORE);

            //freeze player frames for 4 sec
            waitPlayers();

            //hide player cards
            hidePlayerCards();
            int showWinner = fromPlayer2.readInt();
            //if both players have 0 cards player 2 will send show winner request

            if (showWinner == SHOW_WINNER) {
                getScores();
            } else {
                SendPlayer1Response(SET_PLAYER1);
                SendPlayer2Response(SET_PLAYER2);
            }
        } else {

            //send draw response to player 2
            SendPlayer2Response(DRAW);

            //send draw response to player 1
            SendPlayer1Response(DRAW);

            //freeze player frames for 4 sec
            waitPlayers();

            //hide player cards
            hidePlayerCards();
            int showWinner = fromPlayer2.readInt();

            //if both players have 0 cards player 2 will send show winner request
            if (showWinner == SHOW_WINNER) {
                getScores();

            }
            //if player 2 has more than 0 cards he will send continue game request
            else {
                SendPlayer1Response(SET_PLAYER1);
                SendPlayer2Response(SET_PLAYER2);
            }
        }

    }

    /**
     * methhdo to send who won or lost
     * @throws IOException
     */
    public void sendWinLose() throws IOException {

        //if player 1 is winner
        if (decideWinner(player1Score, player2score) == 1) {

            SendPlayer2Response(LOSER);
            SendPlayer1Response(WINNER);
        }
        //if game is drawn
        else if (decideWinner(player1Score, player2score) == 0) {

            SendPlayer2Response(DRAW_GAME);
            SendPlayer1Response(DRAW_GAME);
        }
        //if player 2 is the winner
        else {
            SendPlayer1Response(LOSER);
            SendPlayer2Response(WINNER);
        }
    }

    /**
     * methdo to decide the winner and return the player number who ever wins
     * @param p1Score player 1 score
     * @param p2Score player 2 score
     * @return returns who won or 0 if drawn
     */
    public int decideWinner(int p1Score, int p2Score) {

        if (p1Score > p2Score) {
            return 1;
        } else if (p1Score == p2Score) {
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * get the scores of player 1 and 2
     * @throws IOException
     */
    public void getScores() throws IOException {
        toPlayer1.writeInt(GET_SCORE);
        toPlayer1.flush();
        toPlayer2.writeInt(GET_SCORE);
        toPlayer2.flush();
    }

    /**
     * helper method to send player 2 response with protocol
     * @param protocol protocol
     * @throws IOException
     */
    public void SendPlayer2Response(int protocol) throws IOException {


        if (protocol == SET_PLAYER2) {

            toPlayer2.writeInt(protocol);
            toPlayer2.flush();

            toPlayer2.writeInt(PLAYER2);
            toPlayer2.flush();

        } else {
            toPlayer2.writeInt(protocol);
            toPlayer2.flush();
        }


    }

    /**
     *  method to send protocols to player 1
     * @param protocol protocol
     * @throws IOException
     */
    public void SendPlayer1Response(int protocol) throws IOException {
        if (protocol == SET_PLAYER1) {

            toPlayer1.writeInt(protocol);
            toPlayer1.flush();

            toPlayer1.writeInt(PLAYER1);
            toPlayer1.flush();

        } else {
            toPlayer1.writeInt(protocol);
            toPlayer1.flush();

        }


    }

    /**
     * method to send both players to wait
     * @throws IOException
     */
    public void waitPlayers() throws IOException {

        toPlayer1.writeInt(WAIT);
        toPlayer1.flush();

        toPlayer2.writeInt(WAIT);
        toPlayer2.flush();

    }

    /**
     * methdo to hide both players cards
     * @throws IOException
     */
    public void hidePlayerCards() throws IOException {


        toPlayer1.writeInt(HIDE);
        toPlayer1.flush();
        toPlayer2.writeInt(HIDE);
        toPlayer2.flush();

    }

    /**
     * method to check the card types and determne whose card ispowerful
     * @param player1 player1 card
     * @param player2 player 2 card
     * @return retuns who ever wins 1 if player 1, 2 if player2 , 0 if drawn
     */
    public int compareCardwinner(Card player1, Card player2) {

        int winner = 0;

        if ((player1.getType() == "Strength" && player2.getType() == "Intelligence") ||
                (player1.getType() == "Intelligence" && player2.getType() == "Agility") ||
                (player1.getType() == "Agility" && player2.getType() == "Strength")) {

            player1.decreaePower();
            player2.increasePower();

            if (player1.getPower() > player2.getPower()) {

                winner = 1;
            } else {

                winner = 2;
            }
        } else {

            if (player1.getPower() > player2.getPower()) {

                winner = 1;
            } else if (player1.getPower() == player2.getPower()) {

                winner = 0;
            } else {
                winner = 2;
            }
        }
        return winner;

    }

}
