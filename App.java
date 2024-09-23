import database.Category;
import screens.QuizScreenGui;
import screens.TitleScreenGui;

import javax.swing.*;

public class App {
    public static void main(String[] args) {

        // ensure that all GUI-related code runs on the event dispatch thread or "EDT" as they say....
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                // start the application by displaying the title screen
                new TitleScreenGui().setVisible(true);
            }
        });
    }
}