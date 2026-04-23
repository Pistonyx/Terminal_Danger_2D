package WindowThings;

import javax.swing.*;
import java.awt.*;

/**
 * This class is used to change the style of the buttons in the game
 */
public class CustomButton {
    //everytime this method is used then it changed a JButtons collor to the predefined colour in this method
    public static void changeStyle(JButton button){
        //we can change it based on the RGB numbers or "button.setBackground(Color.black)"
        button.setBackground(new Color(50,20,90));
        button.setForeground(Color.white);
        button.setFont(new Font("Times New Roman",Font.BOLD,14));

        //makes it so that when you put your mouse over the button then it doesn't get highlighted around the edges
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }
}
