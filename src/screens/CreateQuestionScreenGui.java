package screens;

import constants.CommonConstants;
import database.JDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateQuestionScreenGui extends JFrame {
    // input area for the question text
    private JTextArea questionTextArea;
    // input field for the category of the question
    private JTextField categoryTextField;
    // array of text fields for possible answers
    private JTextField[] answerTextFields;
    // group of radio buttons to ensure only one is selected
    private ButtonGroup buttonGroup;
    // array of radio buttons to select the correct answer
    private JRadioButton[] answerRadioButtons;

    public CreateQuestionScreenGui(){
        super("Create a Question");
        setSize(851, 565);
        setLayout(null);
        // center the window
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // set the background color here... make sure to put into practice what I've learned in UI/UX design
        getContentPane().setBackground(CommonConstants.LIGHT_ORANGE);

        answerRadioButtons = new JRadioButton[4];
        answerTextFields = new JTextField[4];
        buttonGroup = new ButtonGroup();

        // add all the GUI components to the frame
        addGuiComponents();
    }

    private void addGuiComponents(){
        JLabel titleLabel = new JLabel("Create your own Question");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(50, 15, 310, 29);
        titleLabel.setForeground(CommonConstants.NEUTRAL_WHITE);
        add(titleLabel);

        JLabel questionLabel = new JLabel("Question: ");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBounds(50, 60, 93, 20);
        questionLabel.setForeground(CommonConstants.NEUTRAL_WHITE);
        add(questionLabel);

        questionTextArea = new JTextArea();
        questionTextArea.setFont(new Font("Arial", Font.BOLD, 16));
        questionTextArea.setBounds(50, 90, 310, 110);
        questionTextArea.setForeground(CommonConstants.BLACK);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        add(questionTextArea);

        JLabel categoryLabel = new JLabel("Category: ");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel.setBounds(50, 250, 93, 20);
        categoryLabel.setForeground(CommonConstants.SOFT_GREY);
        add(categoryLabel);

        categoryTextField = new JTextField();
        categoryTextField.setFont(new Font("Arial", Font.BOLD, 16));
        categoryTextField.setBounds(50, 280, 310, 36);
        categoryTextField.setForeground(CommonConstants.DEEP_BLUE);
        add(categoryTextField);

        // add components for answer input
        addAnswerComponents();

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBounds(300, 450, 262, 45);
        submitButton.setForeground(CommonConstants.DEEP_BLUE);
        submitButton.setBackground(CommonConstants.BLACK);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if the input is valid
                if(validateInput()){
                    String question = questionTextArea.getText();
                    String category = categoryTextField.getText();
                    String[] answers = new String[answerTextFields.length];
                    int correctIndex = 0;
                    for(int i = 0; i < answerTextFields.length; i++){
                        answers[i] = answerTextFields[i].getText();
                        if(answerRadioButtons[i].isSelected()){
                            // identify the correct answer
                            correctIndex = i;
                        }
                    }

                    // save the question and answers to the database
                    if(JDBC.saveQuestionCategoryAndAnswersToDatabase(question, category,
                            answers, correctIndex)){
                        JOptionPane.showMessageDialog(CreateQuestionScreenGui.this,
                                "Successfully Added Question!");
                        // clear the input fields for the next entry
                        resetFields();
                    }else{
                        JOptionPane.showMessageDialog(CreateQuestionScreenGui.this,
                                "Failed to Add Question...");
                    }
                }else{
                    JOptionPane.showMessageDialog(CreateQuestionScreenGui.this,
                            "Error: Invalid Input");
                }
            }
        });
        add(submitButton);

        JLabel goBackLabel = new JLabel("Go Back");
        goBackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        goBackLabel.setBounds(300, 500, 262, 20);
        goBackLabel.setForeground(CommonConstants.SOFT_GREY);
        goBackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        goBackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // go back to the title screen
                TitleScreenGui titleScreenGui = new TitleScreenGui();
                titleScreenGui.setLocationRelativeTo(CreateQuestionScreenGui.this);

                CreateQuestionScreenGui.this.dispose();

                titleScreenGui.setVisible(true);
            }
        });
        add(goBackLabel);
    }

    private void addAnswerComponents(){
        // spacing between each answer input section
        int verticalSpacing = 100;

        for(int i = 0; i < 4; i++){
            JLabel answerLabel = new JLabel("Answer #" + (i + 1));
            answerLabel.setFont(new Font("Arial", Font.BOLD ,16));
            answerLabel.setBounds(470, 60 + (i * verticalSpacing), 93, 20);
            answerLabel.setForeground(CommonConstants.DEEP_BLUE);
            add(answerLabel);

            answerRadioButtons[i] = new JRadioButton();
            answerRadioButtons[i].setBounds(440, 100 + (i * verticalSpacing), 21, 21);
            // transparent background for radio buttons
            answerRadioButtons[i].setBackground(null);
            // group radio buttons so only one can be selected
            buttonGroup.add(answerRadioButtons[i]);
            add(answerRadioButtons[i]);

            answerTextFields[i] = new JTextField();
            answerTextFields[i].setBounds(470, 90 + (i * verticalSpacing), 310, 36);
            answerTextFields[i].setFont(new Font("Arial", Font.PLAIN, 16));
            answerTextFields[i].setForeground(CommonConstants.DEEP_BLUE);
            add(answerTextFields[i]);
        }

        // default the first radio button to selected
        answerRadioButtons[0].setSelected(true);
    }

    private boolean validateInput(){
        // validate that all input fields have been filled out

        if(questionTextArea.getText().replaceAll(" ", "").length() <= 0) return false;

        if(categoryTextField.getText().replaceAll(" ", "").length() <= 0) return false;

        for(int i = 0; i < answerTextFields.length; i++){
            if(answerTextFields[i].getText().replaceAll(" ", "").length() <= 0)
                return false;
        }

        return true;
    }

    private void resetFields(){
        // reset all input fields to empty
        questionTextArea.setText("");
        categoryTextField.setText("");
        for(int i = 0; i < answerTextFields.length; i++){
            answerTextFields[i].setText("");
        }
    }
}
