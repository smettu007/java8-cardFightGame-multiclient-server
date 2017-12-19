import org.omg.CORBA.INTERNAL;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Color;
import java.nio.ByteOrder;

/**
 * Create by subabramaiah mettu
 *
 * class to implement server interface
 */
public class ServerInterface extends JFrame {

    private JComboBox<String> commands;
    private JTextField firstField;
    private JTextField secondField;
    private JPanel panel;
    private JButton button;
    private ActionListener listener;
    private GameInterface player0;
    private GameInterface player1;
    private ArrayList<Card> player0Cards, player1Cards;
    private Server server;

    public ServerInterface(GameInterface p0, GameInterface p1){


        //server = new Server(p0,p1);
        setTitle("Server");
        setSize(500,100);
       // listener = new ChoiceListener();
        firstField = new JTextField(5);
        secondField = new JTextField(5);
        button = new JButton("Go");
        commands = new JComboBox<String>();
        commands.addItem("SET PLAYER");
        commands.addItem("PLAYING");
        commands.addItem("DISPLAY CARDS COUNT");
        commands.addItem("WAIT");
        commands.addItem("HIDE CARDS");
        commands.addItem("GIVE SCORE");
        commands.addItem("WINNER");
        commands.addItem("PLAY CARD");
        commands.addItem("QUIT");

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        button.addActionListener(listener);
        panel.add(commands);
        panel.add(firstField);
        panel.add(secondField);
        panel.add(button);

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    /**
     * class for event listener
     */
   //  class ChoiceListener implements ActionListener  {
/*
        public void actionPerformed(ActionEvent e) {
            String val = firstField.getText();
            String val2 = secondField.getText();
            if (e.getSource() == button) {

                //set player
                if (commands.getSelectedItem() == "SET PLAYER" ) {

                    server.setPlayer(val);

                } else if (commands.getSelectedItem() == "PLAYING") {

                    server.playing(Integer.parseInt(val));

                } else if (commands.getSelectedItem() == "DISPLAY CARDS COUNT") {

                    server.setPlayerCardsCount(Integer.parseInt(val),Integer.parseInt(val2));

                } else if (commands.getSelectedItem() == "WAIT") {

                    server.waitPlayer();

                } else if (commands.getSelectedItem() == "HIDE CARDS") {

                    server.resetDeck();

                } else if (commands.getSelectedItem() == "PLAY CARD") {

                    server.playCard(Integer.parseInt(val), Integer.parseInt(val2));

                } else if (commands.getSelectedItem() == "GIVE SCORE") {

                    server.giveScore(Integer.parseInt(val),Integer.parseInt(val2));

                } else if (commands.getSelectedItem() == "WINNER") {

                    server.setWinner(Integer.parseInt(val));

                } else if (commands.getSelectedItem() == "QUIT") {

                    server.quit(Integer.parseInt(val));
                }
            }
        }

    }*/

}