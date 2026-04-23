package WindowThings;

import javax.swing.*;
import java.awt.*;

public class TitleScreen {
    private JFrame TitleScreen;
public TitleScreen(){ TitleScreen = new JFrame("Terminal Danger");}
    public void init(){
    this.TitleScreen.setSize(600, 400);
        this.TitleScreen.setLayout(new BorderLayout());
        this.TitleScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.TitleScreen.setLocationRelativeTo(null);

        //adds a text to the middle of the screen
        JLabel label = new JLabel("TERMINAL DANGER");
        this.TitleScreen.add(label,BorderLayout.CENTER);
        label.setHorizontalAlignment(0);

        //adds a button to the bottom of the screen
        JButton button = new JButton("Start");
        //uses the method from CustomButton class to change the button
        CustomButton.changeStyle(button);
        this.TitleScreen.add(button,BorderLayout.SOUTH);

        //adds an action to the button. In this case it closes the window.
        //(the difference between dispose and close is that using close still has the window open in the background of your PC)
        button.addActionListener(e ->{
            this.TitleScreen.dispose();
            //after it deletes the original window, it runs the init() method in the App class which creates a new window
            SwingUtilities.invokeLater(GameWindow::new);
        });
        this.TitleScreen.setVisible(true);
    }
}
