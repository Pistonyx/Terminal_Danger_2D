package WindowThings;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class GameInputPanel extends JPanel {
    private final JTextField inputField;

    /**
     * Constructs a GameInputPanel with a text input field and a send button.
     * The panel allows users to type input and send it through either the Enter key
     * or the send button.
     * @param onSubmit a Consumer that processes the user's input. It is called with the
     *                 trimmed text from the input field whenever the user submits their input.
     */
    public GameInputPanel(Consumer<String> onSubmit) {
        //Sets the panel's layout and border'
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 0, 0, 0),
                BorderFactory.createLineBorder(new Color(0, 180, 0), 1)
        ));
        setBackground(new Color(20, 20, 20));

        //The text input field
        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        //The send button
        JButton sendButton = new JButton("Send");
        CustomButton.changeStyle(sendButton);
        sendButton.setFocusable(false);

        //Submits the input when the user presses Enter or clicks the send button
        Runnable submit = () -> {
            String input = inputField.getText().trim();
            inputField.setText("");
            if (!input.isEmpty()) {
                onSubmit.accept(input);
            }
        };

        inputField.addActionListener(e -> submit.run());
        sendButton.addActionListener(e -> submit.run());

        //Places the input field and send button in the center of the panel
        add(inputField, BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
    }

    /**
     * Focuses the input field for user input when the user clicks on the panel.
     */
    public void focusInput() {
        inputField.requestFocusInWindow();
    }

    /**
     * Removes focus from the input field when the user clicks outside of it.
     */
    public void unfocusInput() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();
    }


    /**
     * Enables or disables the input field.
     * @param enabled
     */
    public void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
    }

    /**
     * Checks if the input field is currently focused.
     * @return
     */
    public boolean isInputFocused() {
        return inputField.isFocusOwner();
    }
}
