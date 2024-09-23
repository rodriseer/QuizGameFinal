package screens;

import constants.CommonConstants;
import database.Answer;
import database.Category;
import database.JDBC;
import database.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class QuizScreenGui extends JFrame implements ActionListener{
    private JLabel scoreLabel;
    private JLabel chanceLabel;
    private JTextArea questionTextArea;
    private JButton[] answerButtons;
    private JButton nextButton;

    private Category category;

    private ArrayList<Question> questions;
    private Question currentQuestion;
    private int currentQuestionNumber;
    private int numOfQuestions;
    private int score;
    private int totalChances;
    private boolean firstChoiceMade;



    public QuizScreenGui(Category category, int numOfQuestions){
        super("Quiz Game");
        setSize(400, 565);
        setLayout(null);

        // center the window
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set background color to a soft color
        getContentPane().setBackground(CommonConstants.LIGHT_ORANGE);

        // set number of possible answers
        answerButtons = new JButton[4];
        this.category = category;

        questions = JDBC.fetchQuestionsForCategory(category);

        this.numOfQuestions = Math.min(numOfQuestions, questions.size());
        this.totalChances = 0;

        for(Question question : questions){
            // load answers for each question
            ArrayList<Answer> answers = JDBC.getAnswers(question);
            question.setAnswers(answers);
        }

        currentQuestion = questions.get(currentQuestionNumber);

        addGuiComponents();
    }

    private void addGuiComponents(){
        JLabel topicLabel = new JLabel("Topic: " + category.getCategoryName());
        topicLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topicLabel.setBounds(15, 15 ,250, 20);
        topicLabel.setForeground(CommonConstants.DEEP_BLUE);
        add(topicLabel);

        scoreLabel = new JLabel("   Score: " + score + "/" + numOfQuestions);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setBounds(270, 15, 96, 20);
        scoreLabel.setForeground(CommonConstants.BLACK);
        add(scoreLabel);

        chanceLabel = new JLabel("Chances: " + totalChances + "/3");
        chanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        chanceLabel.setBounds(270, 30, 96, 20);
        chanceLabel.setForeground(CommonConstants.BLACK);
        add(chanceLabel);


        questionTextArea = new JTextArea(currentQuestion.getQuestionText());
        questionTextArea.setFont(new Font("Verdana", Font.BOLD, 16));
        questionTextArea.setBounds(15, 50, 350, 91);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setEditable(false);
        questionTextArea.setForeground(CommonConstants.DEEP_BLUE);
        questionTextArea.setBackground(CommonConstants.NEUTRAL_WHITE);
        add(questionTextArea);

        addAnswerComponents();

        JButton returnToTitleButton = new JButton("Return to Title");
        returnToTitleButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnToTitleButton.setBounds(60, 420, 262, 35);
        returnToTitleButton.setBackground(CommonConstants.SOFT_GREY);
        returnToTitleButton.setForeground(CommonConstants.COOL_BLUE);
        returnToTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go back to the title screen
                TitleScreenGui titleScreenGui = new TitleScreenGui();
                titleScreenGui.setLocationRelativeTo(QuizScreenGui.this);

                QuizScreenGui.this.dispose();

                titleScreenGui.setVisible(true);
            }
        });
        add(returnToTitleButton);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBounds(240, 470, 80, 35);
        nextButton.setBackground(CommonConstants.SOFT_GREY);
        nextButton.setForeground(CommonConstants.MINT_GREEN);
        // initially hidden until the first question is answered
        nextButton.setVisible(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // hide the button after clicking
                nextButton.setVisible(false);

                firstChoiceMade = false;
                // load the next question
                currentQuestion = questions.get(++currentQuestionNumber);
                questionTextArea.setText(currentQuestion.getQuestionText());

                // update answer buttons with the next set of answers
                for(int i = 0; i < currentQuestion.getAnswers().size(); i++){
                    Answer answer = currentQuestion.getAnswers().get(i);
                    // reset button color
                    answerButtons[i].setBackground(Color.WHITE);

                    answerButtons[i].setText(answer.getAnswerText());
                }
            }
        });
        add(nextButton);
    }

    private void addAnswerComponents(){
        int verticalSpacing = 60;

        for(int i = 0; i < currentQuestion.getAnswers().size(); i++){
            Answer answer = currentQuestion.getAnswers().get(i);

            JButton answerButton = new JButton(answer.getAnswerText());
            answerButton.setBounds(60, 180 + (i * verticalSpacing), 262, 45);
            answerButton.setFont(new Font("Verdana", Font.BOLD, 12));
            answerButton.setHorizontalAlignment(SwingConstants.LEFT);
            answerButton.setBackground(Color.WHITE);
            answerButton.setForeground(CommonConstants.DEEP_BLUE);
            answerButton.addActionListener(this);
            answerButtons[i] = answerButton;
            add(answerButtons[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton answerButton = (JButton) e.getSource();

        // find the correct answer for the current question
        Answer correctAnswer = null;
        for(Answer answer : currentQuestion.getAnswers()){
            if(answer.isCorrect()) {
                correctAnswer = answer;
                break;
            }
        }

        if(answerButton.getText().equals(correctAnswer.getAnswerText())){
            // correct answer highlight
            answerButton.setBackground(CommonConstants.COOL_BLUE);

            if(!firstChoiceMade){
                // update score
                scoreLabel.setText("Score: " + (++score) + "/" + numOfQuestions);
            }

            if(currentQuestionNumber == numOfQuestions - 1){
                JOptionPane.showMessageDialog(QuizScreenGui.this,
                        "Your final score is " + score + "/" + numOfQuestions);

                // when user answer all the questions then the screen will go back to main menu
                QuizScreenGui.this.dispose();

                TitleScreenGui titleScreenGui = new TitleScreenGui();
                titleScreenGui.setLocationRelativeTo(QuizScreenGui.this);

                titleScreenGui.setVisible(true);

            }else{
                // show next button
                nextButton.setVisible(true);
            }
        }else{
            // incorrect answer
            answerButton.setBackground(CommonConstants.RED);
            chanceLabel.setText("Chance: " + (++totalChances) + "/3");
            if (totalChances >= 3){
                JOptionPane.showMessageDialog(QuizScreenGui.this,
                        "Sorry but you don't have more chances...");

                QuizScreenGui.this.dispose();

                TitleScreenGui titleScreenGui = new TitleScreenGui();
                titleScreenGui.setLocationRelativeTo(QuizScreenGui.this);

                titleScreenGui.setVisible(true);

            }
        }

        // ensure score only updates on the first click
        firstChoiceMade = true;
    }
}