import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jdk.nashorn.internal.ir.SetSplitState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Color;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Set;
import java.util.TimerTask;
import javax.swing.border.LineBorder;

/**
 * Created By subbaramaiah mettu
 * <p>
 * The purpose of the class is to create players game interface and connect to game server to start the game. This will connect player one by one
 * This class handles all the protocols received by the server and displays proper responses to the player on GUI
 */
public class GameInterface extends JFrame implements Runnable, Protocol {

    //instance variable
    private JPanel arena;
    private JPanel gameTopPanel;
    private JPanel bottomPanel;
    private JPanel centerPanel;
    private JPanel deck;
    private JPanel gamePanel;
    private JLabel scoreCard;
    private JLabel status;
    private JLabel cardsLeft;
    private JButton playingCard;
    private JButton quitButton;
    private JButton newGameButton;
    private ActionListener listener;
    private Player player;
    private Socket socket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public GameInterface(Player p) {

        player = p;
        buildGUI();
        try {

            openConnection(HOST);
            Thread t1 = new Thread(this);
            t1.start();

        } catch (Exception e) {
            System.out.println("Thread failed");
        }


    }

    public void buildGUI() {

        listener = new ChoiceListener();
        //setTitle("Memory Game player " + player.getPlayerID());
        setSize(800, 800);
        playingCard = new JButton("PLay Card");
        quitButton = new JButton("Quit Game");
        newGameButton = new JButton("New Game");
        quitButton.addActionListener(listener);
        quitButton.setActionCommand("quit");
        newGameButton.addActionListener(listener);
        newGameButton.setActionCommand("newGame");
        generateCards();

        //load the game panel first
        arena = new JPanel();
        gamePanel = new JPanel();
        gameTopPanel = createTopPanel();
        centerPanel = createCenterPanel();
        bottomPanel = createBottomPanel();

        BorderLayout layout = new BorderLayout();

        //to have some gap between rows
        layout.setVgap(50);
        arena.setLayout(layout);
        arena.add(gameTopPanel, BorderLayout.NORTH);
        arena.add(centerPanel, BorderLayout.CENTER);
        arena.add(bottomPanel, BorderLayout.SOUTH);
        add(arena);
        displayCardsCount();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void run() {

        boolean done = false;

        while (!done) {

            try {

                int serverAction = fromServer.readInt();

                switch (serverAction) {

                    case SET_PLAYER1: //set player 1 turn

                        int player1ID = fromServer.readInt();
                        player.setPlayerID(player1ID);
                        setTitle("Memory Game player " + player.getPlayerID());
                        //wait for 4 seconds cocuntly ith freeze frame
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {

                                        setStatus("Status: Its your turn");
                                        player.setIsPlaying(true);

                                    }
                                },
                                5000
                        );

                        break;

                    case SET_PLAYER2: // set player 2 turn

                        int player2ID = fromServer.readInt();
                        player.setPlayerID(player2ID);
                        //wait for 4 seconds cocuntly ith freeze frame
                        setTitle("Memory Game player " + player.getPlayerID());

                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {

                                        player.setIsPlaying(false);
                                        setStatus("Status: wait for player 1 to play");

                                    }
                                },
                                4000
                        );

                        break;

                    case SHOW_CARD: //show opponents card to current player and set the turn

                        int opponentCardNo = fromServer.readInt();
                        player.setOpponentCard(player.getCard(opponentCardNo));
                        player.setIsPlaying(true);

                        setStatus("Status: Its your turn");
                        break;

                    case HIDE: //hide deck cards
                        //wait for 4 seconds cocuntly ith freeze frame
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        setEnabled(true);
                                        System.out.println("player " + player.getPlayerID() + " receive hide request");
                                        player.setPlayerCard(new Card("your card"));
                                        player.setOpponentCard(new Card("Opponent card"));

                                    }
                                },
                                4000
                        );
                        break;

                    case WAIT: // to freeze and unfreeze frames for 4-5 seconds
                        System.out.println("player " + player.getPlayerID() + " receive wait request");
                        //Thread.sleep(4000);
                        waiting();
                        break;

                    case GIVE_SCORE: // to give score to the player if he won

                        //method that will increment the score of the player
                        setScore();
                        break;

                    case PLAYER_WONROUND: // if player wins the round comes from the server
                        setStatus("Status: you won this round");
                        break;

                    case PLAYER_LOSTROUND: //if player looses the round
                        setStatus("Status: you Lost this round");
                        break;

                    case DRAW: //if round is drawn
                        setStatus("Status: Round is drawn, both have equal power");
                        break;

                    case DRAW_GAME: // if the game is drawn

                        setStatus("Status: Game is drawn, both have equal score");
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        try{
                                            sendToServer(player.getPlayerID());
                                            sendToServer(RESET);
                                        }catch (IOException epx){
                                            System.err.println("Error");
                                        }

                                    }
                                },
                                3000
                        );
                        break;

                    case WINNER: // if player wins the game

                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        setStatus("Status: You won the game");
                                    }
                                },
                                5000
                        );

                        //reset the game by default to make my life easy haha!
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        try{
                                            sendToServer(player.getPlayerID());
                                            sendToServer(RESET);
                                        }catch (IOException epx){
                                            System.err.println("Error");
                                        }

                                    }
                                },
                                6000
                        );
                        break;

                    case LOSER: //if player lost
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        setStatus("Status: You lost the game");
                                    }
                                },
                                5000
                        );
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        try{
                                            sendToServer(player.getPlayerID());
                                            sendToServer(RESET);
                                        }catch (IOException epx){
                                            System.err.println("Error");
                                        }

                                    }
                                },
                                6000
                        );

                        break;

                    case GET_SCORE: //send player score to server

                        sendToServer(player.getPlayerID());

                        sendToServer(SET_SCORE);

                        sendToServer(player.getScore());

                        break;

                    case QUIT: //if player choosed to quit the game

                        setStatus("Status: You lost the game");
                        dispose();// close the frame

                        break;

                    case QUIT_WIN: //this is for player 2 if other player quitsthegame

                        setStatus("Status: You won the game opponent left the game");

                        break;

                    case RESET: // protocol to reset the game or for new game purpose
                        resetGame();
                        break;

                    case NEW_GAME: //protocol to start new game askinf for opponents choice
                        newGame();
                        break;

                    case NO_NEW_GAME: //if opponent says no new game then game continues
                        JOptionPane.showMessageDialog(null, "Opponent Declined your offer");
                        break;

                }

            } catch (Exception except) {

               setStatus("Status: Cannot connect to server");
            }
        }
    }

    /***
     * methdod to send server conformation if user wants to play new game with opponent or not
     */
    public void newGame(){

        int reply = JOptionPane.showConfirmDialog(null, "Opponent wants to start a new game?", "New Game?", JOptionPane.YES_NO_OPTION);

        //if yes send opponet yes message and restart the game
        if (reply == JOptionPane.YES_OPTION) {
            try {
                sendToServer(player.getPlayerID());
                sendToServer(YES);

            } catch (IOException exe){

                System.err.println("Sending to server error");
            }
        }else{

            //just send no response to the opponent
            try {
                sendToServer(player.getPlayerID());
                sendToServer(NO);

            } catch (IOException exe){

                System.err.println("Sending to server error");
            }
        }

    }

    /**
     *Method to reset the game
     */
    public void resetGame(){

        // generate new cards for the player
        player.generateCards();

        for(int i = 0; i < player.getCards().size() ;i++){

            //make them visible
            player.getCard(i).setVisible(true);

            player.getCard(i).setBorder(new LineBorder(Color.WHITE));

            //reset properties of the player score,cards count etc...
            player.resetProps();

            //update the default scores and cards count on GUI
            displayCardsCount();
            scoreCard.setText("Your Score: " + player.getScore());
        }

    }
    /**
     * Creates a socket with the GAME_PORT and opens its input
     * and output streams called fromServer and toServer.
     */
    private void openConnection(String serverHost) {
        try {
            this.socket = new Socket(serverHost, PORT);
            this.fromServer = new DataInputStream(socket.getInputStream());
            this.toServer = new DataOutputStream(socket.getOutputStream());
        } catch (SecurityException e) {
            System.err.print("a security manager exists: ");
            System.err.println("its checkConnect doesn't allow the connection");
            System.err.println("without a SERVER, I'm toast ... no point going on so bye, bye");
            setStatus("Status: Server is not allowing the connection due to security reasons");
        } catch (UnknownHostException e) {
            System.err.println("the IP address of the host could not be found...cannot go on, bye");
            setStatus("Status: Ip address of the host could not be found");
        } catch (IOException e) {
            System.err.println("cannot seem to be able to connect to the server \"" + serverHost + "\"");
            System.err.println("without a SERVER, I'm toast ... no point going on so bye, bye");
            setStatus("Status: unable  to connect to the server");
        }

    } // openConnection

    /**
     * class to implement eventlistener for every click on the buttons and on the card
     */
    class ChoiceListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //if current player clicked on card 0
            if (player.getIsPlaying() && e.getSource() == player.getCard(0)) {

                player.setSelectedCard(isCardSelected(player.getCard(0)), 0);
                //player.cardNo = 0;
                System.out.println("CLIENT --> SERVER " + player.getPlayerID() + " : TRY CARD 0 " + player.getSelectedCard().getName());
                String sendToServer = "CLIENT --> SERVER " + player.getPlayerID() + " : TRY CARD 0";


            }
            //if current player clicked on card 1
            else if (player.getIsPlaying() && e.getSource() == player.getCard(1)) {

                // player.selectedCard = isCardSelected(player.getCard(1));
                // player.cardNo = 1;

                player.setSelectedCard(isCardSelected(player.getCard(1)), 1);

                System.out.println("CLIENT --> SERVER " + player.getPlayerID() + " : TRY CARD 1 " + player.getSelectedCard().getName());

            }
            //if current player clicked on card 2
            else if (player.getIsPlaying() && e.getSource() == player.getCard(2)) {

                //player.selectedCard = isCardSelected(player.getCard(2));
                //player.cardNo = 2;
                player.setSelectedCard(isCardSelected(player.getCard(2)), 2);

                System.out.println("CLIENT --> SERVER " + player.getPlayerID() + " : TRY CARD 2 " + player.getSelectedCard().getName());

            }
            //if current player clicked on card 3
            else if (player.getIsPlaying() && e.getSource() == player.getCard(3)) {

                // player.selectedCard = isCardSelected(player.getCard(3));
                //player.cardNo = 3;
                player.setSelectedCard(isCardSelected(player.getCard(3)), 3);

                System.out.println("CLIENT --> SERVER " + player.getPlayerID() + " : TRY CARD 3 " + player.getSelectedCard().getName());

            }
            //if player hits playcard button trigger the function that plays the card on the deck
            else if (e.getSource() == playingCard) {

                try {
                    player.setPlayerCard(player.getSelectedCard());

                } catch (NullPointerException ex) {
                    setStatus("Status: please select a card to play");
                }
                System.out.println("CLIENT --> SERVER " + player.getPlayerID() + " : PLAY CARD  " + player.getPlayerCard().getName());
                playCard();

            }

            //if player wants to quit
            else if("quit".equals(e.getActionCommand())){

                if(!player.getIsPlaying()){
                    setStatus("Status: you cannot quit. Its not your turn");
                }else{

                    // a conformation message to quit or not
                    int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit the new game?", "Quit Game", JOptionPane.YES_NO_OPTION);

                    //if player wants to quit then send a message to server which in return sends a message to player 2
                    if (reply == JOptionPane.YES_OPTION) {
                        try {
                            sendToServer(player.getPlayerID()); //which player is ending request
                            sendToServer(QUIT); // quit protocol status
                            setStatus("Status: You lost");
                        } catch (IOException exe){

                            System.err.println("Sending to server error");
                        }
                    }


                }

            }
            //if player wants to start a new game
            else if("newGame".equals(e.getActionCommand())){

                if(!player.getIsPlaying()){
                    setStatus("Status: you cannot start a new game. Its not your turn");
                }else{
                    int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want new game?", "New Game", JOptionPane.YES_NO_OPTION);

                    //if player says yes it will send a message to player 2 whether to start new game or not
                    if (reply == JOptionPane.YES_OPTION) {
                        try {

                            sendToServer(player.getPlayerID());
                            sendToServer(NEW_GAME);
                            //setStatus("Status: You lost");
                        } catch (IOException exe){

                            System.err.println("Sending to server error");
                        }
                    }


                }

            }

        }
    }


    /**
     * put the player's selected card on the deck
     */
    public void playCard() {

        //check if its player's turn
        if (player.getIsPlaying()) {

            //check if player card that was selected to play is null
            if (player.getSelectedCard() != null) {

                // System.out.println("CLIENT --> SERVER " + player.getPlayerID()  +" : PLAY CARD " + "POWER: " + player.getSelectedCard().getPower() + " TYPE: " + player.getSelectedCard().getType());

                player.getCard(player.getCardNo()).setVisible(false);
                player.setCard(player.getCardNo(), null);
                player.decrementCardsCount();
                displayCardsCount();
                player.setIsPlaying(false);
                player.setSelectedCard(null, 0);
                setStatus("Status: Its your opponents turn");

                //once player played the card its card no is sent to the server so
                //that that card can be displayed on opponent's deck
                try {

                    toServer.writeInt(player.getPlayerID()); //which player
                    toServer.writeInt(PLAY_CARD); //action
                    toServer.writeInt(player.getCardNo()); //what card no
                    toServer.flush();
                    //if player 2 is playing last card that means its time to decide winner
                    if (player.getPlayerID() == 2 && player.getCardsCount() <= 0) {

                        toServer.writeInt(SHOW_WINNER);
                        toServer.flush();
                        System.out.println("Show winner");
                    }
                    //if both players still ahve cards left then do normal playing
                    else if (player.getPlayerID() == 2 && player.getCardsCount() > 0) {
                        toServer.writeInt(DONT_SHOW_WINNER);
                        System.out.println("dont show");
                        toServer.flush();
                    }

                } catch (IOException exception) {

                    System.err.println("Sending failed");
                }


            } else {
                status.setText("Status: select a card to play");
            }
        } else {

            status.setText("Status: Its not your turn");
        }


    }

    /**
     * this method is used to update the cards count of the player once the player plays a card
     */
    public void displayCardsCount() {

        cardsLeft.setText("Cards Left: " + player.getCardsCount());
    }

    /**
     * optional
     * method to set the cards count. Mostly from the server.
     *
     * @param count count of the cards for the player
     */
    public void setCardsCount(int count) {

        player.setCardsCount(count);
    }

    /**
     * This method stops the game frame for a player for 4 secs.
     **/
    public void waiting() {

        //timer to disable the jframe forafter 1 milli second to pause the game frames
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        setEnabled(false);
                    }
                },
                1
        );
        //wait for 4 seconds to unfreeze frame
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        setEnabled(true);
                    }
                },
                4000
        );

    }

    /**
     * Helper function to send messages to server
     * @param protocol protocol to send the server
     * @throws IOException it throws IOEXception
     */
    public void sendToServer(int protocol) throws  IOException{

            toServer.writeInt(protocol);
            toServer.flush();
            System.err.println("Error");
    }

    /**
     * Method to return player cards
     *
     * @return returns arraylist of players cards
     */
    public ArrayList<Card> getCards() {

        return player.getCards();
    }


    /**
     * A method to determine and highlight which card is clicked and selected by the player
     *
     * @param card selected card by the player on the interface
     * @return returns the selected card
     */
    public Card isCardSelected(Card card) {


        card.setBorder(new LineBorder(Color.BLUE));

        //loop to set border of other cards to white
        for (Card c : player.getCards()) {

            if (c != null && !c.equals(card)) {

                c.setBorder(new LineBorder(Color.WHITE));
            }

        }
        return card;
    }

    /**
     * This is to add event listeners to the player cards generated by the player
     */
    public void generateCards() {

        for (int i = 0; i < player.getCardsCount(); i++) {

            player.getCard(i).addActionListener(listener);

        }

        playingCard.addActionListener(listener);
    }

    /**
     * methdo to get the players status
     *
     * @return a boolean value, true if its player turn
     */
    public boolean getIsPlaying() {

        return player.getIsPlaying();
    }

    /**
     * Method to set the status of the player, true if its current player's turn
     *
     * @param status boolean value to set player status
     */
    public void setIsPlaying(boolean status) {

        player.setIsPlaying(status);
    }

    /**
     * This is to keep changing status of the player. Custom status
     *
     * @param text status text to be shown on the interface of the player
     */
    public void setStatus(String text) {

        status.setText(text);
    }

    /**
     * method update score board of player interface
     */
    public void setScore() {

        player.setScore();
        scoreCard.setText("Your Score: " + player.getScore());

        //for later purpose when we have server to change player score player.setScore(score);
    }

    /**
     * method to return the score
     *
     * @return returns score of the player
     */
    public int getScore() {

        return player.getScore();
    }

    /**
     * This method creates the top panel of the GUI which has a message and score label.
     **/
    public JPanel createTopPanel() {

        JPanel pan = new JPanel();
        BorderLayout layout = new BorderLayout();
        layout.setHgap(150);
        layout.setVgap(10);
        pan.setLayout(layout);
        scoreCard = new JLabel("Your Score: " + getScore());

        cardsLeft = new JLabel("Cards Left: ");
        pan.add(cardsLeft, BorderLayout.CENTER);
        pan.add(scoreCard, BorderLayout.LINE_START);

        return pan;
    }

    /**
     * This method creates the center panel of the the game GUI which has 4 buttons or cards.
     **/
    public JPanel createCenterPanel() {

        //create 3 panels
        JPanel cardsDeck = new JPanel();
        JPanel deckP = new JPanel();
        JPanel centerPanel = new JPanel();
        JLabel deckpLabel = new JLabel("Deck: ");
        JLabel cardsLabel = new JLabel("Your Deck: ");

        //set players common deck and their cards deck
        cardsDeck.setLayout(new BorderLayout());
        deckP.setLayout(new BorderLayout());


        GridLayout layout = new GridLayout(2, 1);
        centerPanel.setLayout(layout);
        layout.setVgap(50);

        //set them to center of its own panel so that they can be displayed big cards
        cardsDeck.add(cardsLabel, BorderLayout.NORTH);
        cardsDeck.add(createCards(), BorderLayout.CENTER);
        deckP.add(deckpLabel, BorderLayout.NORTH);
        deckP.add(createDeck(), BorderLayout.CENTER);
        cardsDeck.add(playingCard, BorderLayout.SOUTH);

        //add both decks to a common panel
        centerPanel.add(deckP);
        centerPanel.add(cardsDeck);


        return centerPanel;

    }

    /**
     * This method creates the player cards panel of the GUI which has 4 buttons or cards.
     **/
    public JPanel createCards() {
        JPanel pan = new JPanel();

        pan.setLayout(new GridLayout(1, 4));
        for (int i = 0; i < player.getCards().size(); i++) {

            pan.add(player.getCard(i));
        }

        return pan;
    }

    /**
     * This method creates the Deck panel of the GUI which has 2 buttons or cards.
     **/
    public JPanel createDeck() {

        JPanel pan = new JPanel();

        pan.setLayout(new GridLayout(1, 2));
        pan.add(player.getPlayerCard());
        pan.add(player.getOpponentCard());

        return pan;
    }

    /**
     * This method creates the bottom panel of the GUI which has a quit button, start new game
     * button and message label.
     **/
    public JPanel createBottomPanel() {
        JPanel pan = new JPanel();
        status = new JLabel("Status: connecting to the game server, please wait");

        BorderLayout layout = new BorderLayout();
        layout.setHgap(150);
        pan.setLayout(layout);
        pan.add(quitButton, BorderLayout.LINE_START);
        pan.add(status, BorderLayout.CENTER);
        pan.add(newGameButton, BorderLayout.LINE_END);

        return pan;
    }


}