/**Created By subbaramaiah mettu
 * An interface class to implement the protocol for the game client and server so that they can share proper
 * responses with same protocols
 */
public interface Protocol {

    String HOST = "localhost";

    int PORT = 2017; //port of the server

    int PLAYER1 = 1; // set player1
    int PLAYER2 = 2;//set player 2
    int DRAW = 3; // Indicate a draw
    int DRAW_GAME = -3; // Indicate a draw
    int PLAY_CARD = 4; //indicate to play the card
    int WAIT = 5; //indicate both players to wait

    int GIVE_SCORE = 6; // give score to player who won
    int GET_SCORE = -6; // get the score for deciding the winner
    int SET_SCORE = -16; //will decide on the server side who wins after receiving 2 players score
    int HIDE = 7; //hide both players cards
    int SHOW_WINNER = 8; // code to trigger evaluating the winner
    int DONT_SHOW_WINNER = -8; // code  to continue the game
    int WINNER = 9; // Indicate winner
    int LOSER = -9; // Indicate loser
    int SHOW_CARD = 12; //show the card played by the player to the opponent
    int SET_PLAYER1 = 15; //set the player1 turn
    int SET_PLAYER2 = 17;//set the player2 turn, but by disabling its turn
    int PLAYER_WONROUND = 19; //player won round
    int PLAYER_LOSTROUND = -19; //player lost round
    int QUIT = 20; //protocol to let player to he wants to quit
    int QUIT_WIN = -20; //send other player proper response that opponent wants to qut the game and he won
    int RESET = 21; // reset protocol if the user wants to reset the game
    int NEW_GAME = 22; //protocol if player wants to notify opponent that the wants to play new game
    int YES = 23; //if opponent says yes protocol then the new game will be started
    int NO = -23; // if opponent says no then the will resume normally
    int NO_NEW_GAME = -25; // protocol to let player know that opponent rejected new game offer


}
