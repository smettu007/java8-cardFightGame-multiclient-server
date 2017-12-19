import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created By subbaramaiah mettu
 * The purpose of this class is to create a multiclient game server for 2 players in each session on a new thread. This will handle all the
 * requests sent by the clients and will respond with proper answers to continue the game
 */
public class Server extends JFrame implements Protocol{

    //Instance variables
    private JTextArea log;
    private JScrollPane scrollPane;
    private static String host = null;
    private static int numberOfPlayers = 0;
    private static String imgDir = null;

    //to start a log session
    public Server(){

        log = new JTextArea();
        scrollPane = new JScrollPane(log);

        add(scrollPane,BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setTitle("Card Fight Server");
        setVisible(true);

        try( ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Create a server socket

            log.append(new Date() + ": Server started at socket" + PORT + "\n");

            // Number a session
            int sessionNo = 1;

            // Ready to create a session for every two players
            while (true) {

                log.append(new Date() + ": Wait for players to join session " + sessionNo + '\n');

                // Connect to player 1
                Socket player1 = serverSocket.accept();

                log.append(new Date() + ": Player 1 joined session " + sessionNo + '\n');
                log.append("Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
                log.append("waiting for Player 2' IP address to join the session"  + '\n');

                // Notify that the player is Player 1
                new DataOutputStream(player1.getOutputStream()).writeInt(SET_PLAYER1);
                new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                // Connect to player 2
                Socket player2 = serverSocket.accept();

                log.append(new Date() + ": Player 2 joined session " + sessionNo + '\n');
                log.append("Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');
                log.append("Start the game for session " + sessionNo  + '\n');

                // Notify that the player is Player 2
                new DataOutputStream(player2.getOutputStream()).writeInt(SET_PLAYER2);
                new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                // Display this session and increment session number
                log.append(new Date() + ": Start a thread for session " + sessionNo + '\n');

                // Create a new thread for this session of two players
                HandleASession task = new HandleASession(player1, player2);

                // Start the new thread
                new Thread(task).start();
                sessionNo++;
            }
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }

    //main
    public static void main(String[] args){
        int i = 0;

        while(i<args.length){
            if(args[i].equals("-num")){
                i++;
                numberOfPlayers = Integer.parseInt(args[i]);
                i++;
            }else if(args[i].equals("-img")){
                i++;
                imgDir = args[i];
                i++;
            }else if(args[i].equals("-help")){
                System.out.println("options: \n -num - Number of Players \n -img - Include Images \n -sound - Allow Sound\n -help - Help");
                return;
            }else{
                System.out.println("options: \n -num - Number of Players \n -img - Include Images \n -sound - Allow Sound\n -help - Help");
                return;
            }
        }
        Server server = new Server();
    }


}