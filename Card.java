import jdk.internal.dynalink.beans.StaticClass;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *Created By subbaramaiah mettu
 *
 *The purpose of this class is to Create cards with properties. We can create cards of various types and
 *different power stats. This is a subclass to Jbutton
 **/

public class Card extends JButton{

    //instance variables
    private String name;
    private Font f;
    private int power;
    private int id;
    private String type,weakness;


    /**
     * Creates a default card if its created without arguments
     */
    public Card(String txt){

        super(txt);
        name = txt;

    }

    /**
     *Constructor to create a card class with arguments
     * @param msg name of the card
     * @param pow power of the card
     * @param typ type f the card
     * @param weak weakness of the card
     */
    public Card(String msg, int pow, String typ, String weak,int ID){

        super();

        name = msg;
        power = pow;
        type = typ;
        weakness = weak;
        id = ID;
        f = new Font("Sans Serif", Font.PLAIN, 16);
        setFont(f);

        //this method will set text of the button
        setName( msg,  pow,  typ,  weak);
    }

    /**
     * Method to decrease power of the card
     */
    public void decreaePower(){

        power = power -2;
    }

    /**
     * method to increase power of the card
     */
    public void increasePower(){

        power = power + 2;
    }

    /**
     * Purpose of this function is to return the power of the card
     * @return returns power of the card
     */
    public int getPower(){

        return power;
    }

    /**
     * Purpose of this function is to return the name of the card
     * @return returns name of the card
     */
    public String getName(){

        return name;
    }

    /**
     * Purpose of this function is to return the type of the card
     * @return returns type of the card
     */
    public String getType(){

        return type;
    }

    /**
     * Purpose of this function is to return the weakness of the card
     * @return returns weakness of the card
     */
    public String getWeakness(){

        return weakness;
    }

    /**
     * Purpose of this function is to set the text of the button so that it can be showed as a card on the game frame
     * @param msg name of the card
     * @param pow power of the card
     * @param typ type of the card
     * @param weak weakness of the card
     */
    public void setName(String msg, int pow, String typ, String weak){


        if(msg != null && typ != "none" && typ != null){
            name = msg;
            power = pow;
            type = typ;
            weakness = weak;
            this.setIcon(null);
            //this will set text of the button, i had to use html so i can break the lines
            this.setText("<html>" + msg + "<br>" + "Power: " + pow + "<br>" + "Type; " + typ + "<br>" + "weakness: " + weak + "</html>");

        }else{

            setBlankCard();
        }
    }

    /**
     * The purpose of this function is to set the back of the card

     */
    public void setBlankCard(){

        super.setText(null);
        ImageIcon a=new ImageIcon("C:\\Users\\nagaraju\\IdeaProjects\\cardFight\\src\\card_back.png");
        super.setIcon(a);
        setBorderPainted(false);
        setFocusPainted(false);


    }
}