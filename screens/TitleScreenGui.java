package screens;

import constants.CommonConstants;
import database.Category;
import database.JDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TitleScreenGui extends JFrame{
    private JComboBox categoriesMenu;
    private JTextField numOfQuestionsTextField;

    public TitleScreenGui(){
        super("Title Screen");

        setSize(400, 565);

        setLayout(null);

        // center the window on the screen for better user experience
        setLocationRelativeTo(null);

        // disable resizing to maintain the layout integrity
        setResizable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the background color to a soft grey to create a neutral and welcoming interface for the user
        getContentPane().setBackground(CommonConstants.LIGHT_ORANGE);

        initializeGuiComponents();
    }

    private void initializeGuiComponents(){
        // title label that displays the name of the game
        JLabel titleLabel = new JLabel("Study Quiz Program");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setBounds(0, 20, 390, 43);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(CommonConstants.DEEP_BLUE);
        add(titleLabel);

        // label instructing the user to choose a category
        JLabel chooseCategoryLabel = new JLabel("Choose a Category");
        chooseCategoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chooseCategoryLabel.setBounds(0, 90, 400, 20);
        chooseCategoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chooseCategoryLabel.setForeground(CommonConstants.DEEP_BLUE);
        add(chooseCategoryLabel);

        // dropdown menu for category selection
        ArrayList<String> categoryList = JDBC.fetchAllCategories();
        categoriesMenu = new JComboBox(categoryList.toArray());
        categoriesMenu.setBounds(20, 120, 337, 45);
        categoriesMenu.setForeground(CommonConstants.DEEP_BLUE);
        add(categoriesMenu);

        // label for the number of questions input
        JLabel numOfQuestionsLabel = new JLabel("Number of Questions: ");
        numOfQuestionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        numOfQuestionsLabel.setBounds(20, 190, 172, 20);
        numOfQuestionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numOfQuestionsLabel.setForeground(CommonConstants.DEEP_BLUE);
        add(numOfQuestionsLabel);

        // text field to input the number of questions for the quiz
        numOfQuestionsTextField = new JTextField("10");
        numOfQuestionsTextField.setFont(new Font("Arial", Font.BOLD, 16));
        numOfQuestionsTextField.setBounds(200, 190, 148, 26);
        numOfQuestionsTextField.setForeground(CommonConstants.DEEP_BLUE);
        add(numOfQuestionsTextField);

        // button to start the quiz
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBounds(65, 290, 262, 45);
        startButton.setBackground(CommonConstants.SOFT_GREY);
        startButton.setForeground(CommonConstants.COOL_BLUE);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isUserInputValid()) {

                    // retrieve the selected category from the dropdown menu
                    Category category = JDBC.getCategory(categoriesMenu.getSelectedItem().toString());

                    // if the selected category is not valid, do nothing
                    if (category == null) return;

                    int numOfQuestions = Integer.parseInt(numOfQuestionsTextField.getText());

                    // load the quiz screen with the chosen category and number of questions
                    QuizScreenGui quizScreenGui = new QuizScreenGui(category, numOfQuestions);
                    quizScreenGui.setLocationRelativeTo(TitleScreenGui.this);
                    // close the screen
                    TitleScreenGui.this.dispose();
                    // show the quiz screen
                    quizScreenGui.setVisible(true);
                }
            }
        });

        add(startButton);


        JButton deleteButton = new JButton("Delete Data");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 8));
        deleteButton.setBounds(115, 480, 165, 25);
        deleteButton.setBackground(CommonConstants.SOFT_GREY);
        deleteButton.setForeground(CommonConstants.RED);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // confirmation option for user
                int userResponse = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to delete all data?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE

                );

                // confirmation from user using JOptionPane
                if(userResponse == JOptionPane.YES_OPTION) {
                    JDBC.clearAllData();

                    // updating categories list to reflect changes
                    ArrayList<String> updatedCategoryList = JDBC.fetchAllCategories ();
                    categoriesMenu.setModel(new DefaultComboBoxModel(updatedCategoryList.toArray()));

                    JOptionPane.showMessageDialog(null, "Data has been deleted", "Success", JOptionPane.INFORMATION_MESSAGE);

                    categoriesMenu.revalidate();
                    categoriesMenu.repaint();

                }
                else {
                    JOptionPane.showMessageDialog(null, "Data deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        );
        add(deleteButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBounds(65, 350, 262, 45);
        exitButton.setBackground(CommonConstants.SOFT_GREY);
        exitButton.setForeground(CommonConstants.COOL_BLUE);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TitleScreenGui.this.dispose();
            }
        });
        add(exitButton);
        // button to open the create a question screen
        JButton createAQuestionButton = new JButton("Create a Question");
        createAQuestionButton.setFont(new Font("Arial", Font.BOLD, 16));
        createAQuestionButton.setBounds(65, 420, 262, 45);
        createAQuestionButton.setBackground(CommonConstants.SOFT_GREY);
        createAQuestionButton.setForeground(CommonConstants.COOL_BLUE);
        createAQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open the screen to create a new quiz question
                CreateQuestionScreenGui createQuestionScreenGui = new CreateQuestionScreenGui();
                createQuestionScreenGui.setLocationRelativeTo(TitleScreenGui.this);

                TitleScreenGui.this.dispose();

                createQuestionScreenGui.setVisible(true);
            }
        });
        add(createAQuestionButton);
    }

    // validate user input before proceeding
    private boolean isUserInputValid(){
        // check if the number of questions field is not empty
        if(numOfQuestionsTextField.getText().replaceAll(" ", "").length() <= 0) return false;
        // check if a category is selected
        if(categoriesMenu.getSelectedItem() == null) return false;

        return true;
    }
}