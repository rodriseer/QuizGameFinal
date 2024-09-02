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
    private JTextArea questionTextArea;
    private JButton[] answerButtons;
    private JButton nextButton;

    private Category category;

    private ArrayList<Question> questions;
    private Question currentQuestion;
    private int currentQuestionNumber;
    private int numOfQuestions;
    private int score;
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
        getContentPane().setBackground(CommonConstants.SOFT_GREY);

        answerButtons = new JButton[4];
        this.category = category;

        questions = JDBC.fetchQuestionsForCategory(category);

        this.numOfQuestions = Math.min(numOfQuestions, questions.size());

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
        topicLabel.setForeground(CommonConstants.SOFT_GREY);
        add(topicLabel);

        scoreLabel = new JLabel("Score: " + score + "/" + numOfQuestions);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBounds(270, 15, 96, 20);
        scoreLabel.setForeground(CommonConstants.MINT_GREEN);
        add(scoreLabel);

        questionTextArea = new JTextArea(currentQuestion.getQuestionText());
        questionTextArea.setFont(new Font("Arial", Font.BOLD, 32));
        questionTextArea.setBounds(15, 50, 350, 91);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setEditable(false);
        questionTextArea.setForeground(CommonConstants.SOFT_GREY);
        questionTextArea.setBackground(CommonConstants.DEEP_BLUE);
        add(questionTextArea);

        addAnswerComponents();

        JButton returnToTitleButton = new JButton("Return to Title");
        returnToTitleButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnToTitleButton.setBounds(60, 420, 262, 35);
        returnToTitleButton.setBackground(CommonConstants.SOFT_GREY);
        returnToTitleButton.setForeground(CommonConstants.MINT_GREEN);
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
            answerButton.setFont(new Font("Arial", Font.BOLD, 18));
            answerButton.setHorizontalAlignment(SwingConstants.LEFT);
            answerButton.setBackground(Color.WHITE);
            answerButton.setForeground(CommonConstants.MINT_GREEN);
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
            answerButton.setBackground(CommonConstants.SOFT_PURPLE);

            if(!firstChoiceMade){
                // update score
                scoreLabel.setText("Score: " + (++score) + "/" + numOfQuestions);
            }

            if(currentQuestionNumber == numOfQuestions - 1){
                JOptionPane.showMessageDialog(QuizScreenGui.this,
                        "You're final score is " + score + "/" + numOfQuestions);
            }else{
                // show next button
                nextButton.setVisible(true);
            }
        }else{
            // incorrect answer
            answerButton.setBackground(CommonConstants.DEEP_BLUE);
        }

        // ensure score only updates on the first click
        firstChoiceMade = true;
    }
}