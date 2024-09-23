package database;

import java.sql.*;
import java.util.ArrayList;


public class JDBC {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/quiz_gui_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "47Nbm|}4B11=";

    // save a question, its category, and its answers to the database
    public static boolean saveQuestionCategoryAndAnswersToDatabase(String questionText, String categoryName,
                                                                   String[] answers, int correctAnswerIndex) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // retrieve or insert category
            Category retrievedCategory = getCategory(categoryName);
            if (retrievedCategory == null) {
                retrievedCategory = addNewCategoryToDatabase(categoryName);
            }

            // insert new question
            Question newQuestion = insertQuestion(retrievedCategory, questionText);

            // insert answers
            return insertAnswers(newQuestion, answers, correctAnswerIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // retrieve all questions for a given category
    public static ArrayList<Question> fetchQuestionsForCategory(Category category) {
        ArrayList<Question> quizQuestions = new ArrayList<>();
        try {
            Connection databaseConnection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement questionsFetchQuery = databaseConnection.prepareStatement(
                    "SELECT * FROM QUESTION JOIN CATEGORY " +
                            "ON QUESTION.CATEGORY_ID = CATEGORY.CATEGORY_ID " +
                            "WHERE CATEGORY.CATEGORY_NAME = ? ORDER BY RAND()"
            );
            questionsFetchQuery.setString(1, category.getCategoryName());

            ResultSet resultSet = questionsFetchQuery.executeQuery();
            while (resultSet.next()) {
                int questionId = resultSet.getInt("question_id");
                int categoryId = resultSet.getInt("category_id");
                String questionText = resultSet.getString("question_text");
                quizQuestions.add(new Question(questionId, categoryId, questionText));
            }

            return quizQuestions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // insert a new question into the database
    private static Question insertQuestion(Category category, String questionText){
        try{
            Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USERNAME, DB_PASSWORD
            );

            // insert the question and retrieve its generated ID
            PreparedStatement insertQuestionQuery = connection.prepareStatement(
                    "INSERT INTO QUESTION(CATEGORY_ID, QUESTION_TEXT) " +
                            "VALUES(?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertQuestionQuery.setInt(1, category.getCategoryId());
            insertQuestionQuery.setString(2, questionText);
            insertQuestionQuery.executeUpdate();

            ResultSet resultSet = insertQuestionQuery.getGeneratedKeys();
            if(resultSet.next()){
                int questionId = resultSet.getInt(1);
                return new Question(questionId, category.getCategoryId(), questionText);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // retrieve a category by its name, or return null if not found
    public static Category getCategory(String category){
        try{
            Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USERNAME, DB_PASSWORD
            );

            PreparedStatement getCategoryQuery = connection.prepareStatement(
                    "SELECT * FROM CATEGORY WHERE CATEGORY_NAME = ?"
            );
            getCategoryQuery.setString(1, category);

            ResultSet resultSet = getCategoryQuery.executeQuery();
            if(resultSet.next()){
                int categoryId = resultSet.getInt("category_id");
                return new Category(categoryId, category);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // retrieve all categories from the database
    public static ArrayList<String> fetchAllCategories() {
        ArrayList<String> categoryList = new ArrayList<>();
        try {
            Connection databaseConnection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            Statement getCategoriesQuery = databaseConnection.createStatement();
            ResultSet resultSet = getCategoriesQuery.executeQuery("SELECT * FROM CATEGORY");

            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");
                categoryList.add(categoryName);
            }

            return categoryList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if an exception occurred
    }

    // insert a new category into the database
    private static Category addNewCategoryToDatabase(String categoryName) {
        try {
            Connection databaseConnection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement insertCategoryQuery = databaseConnection.prepareStatement(
                    "INSERT INTO CATEGORY(CATEGORY_NAME) VALUES(?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertCategoryQuery.setString(1, categoryName);
            insertCategoryQuery.executeUpdate();

            ResultSet resultSet = insertCategoryQuery.getGeneratedKeys();
            if (resultSet.next()) {
                int categoryId = resultSet.getInt(1);
                return new Category(categoryId, categoryName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if an exception occurred
    }

    // retrieve all answers for a given question, ordered randomly
    public static ArrayList<Answer> getAnswers(Question question){
        ArrayList<Answer> answers = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USERNAME, DB_PASSWORD
            );

            // query that retrieves all the answers of a question in random order
            PreparedStatement getAnswersQuery = connection.prepareStatement(
                    "SELECT * FROM QUESTION JOIN ANSWER " +
                            "ON QUESTION.QUESTION_ID = ANSWER.QUESTION_ID " +
                            "WHERE QUESTION.QUESTION_ID = ? ORDER BY RAND()"
            );
            getAnswersQuery.setInt(1, question.getQuestionId());

            ResultSet resultSet = getAnswersQuery.executeQuery();
            while(resultSet.next()){
                int answerId = resultSet.getInt("idanswer");
                String answerText = resultSet.getString("answer_text");
                boolean isCorrect = resultSet.getBoolean("is_correct");
                Answer answer = new Answer(answerId, question.getQuestionId(), answerText, isCorrect);
                answers.add(answer);
            }

            return answers;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // insert answers for a question into the database
    private static boolean insertAnswers(Question question, String[] answers, int correctIndex){
        try{
            Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USERNAME, DB_PASSWORD
            );

            PreparedStatement insertAnswerQuery = connection.prepareStatement(
                    "INSERT INTO ANSWER(QUESTION_ID, ANSWER_TEXT, IS_CORRECT) " +
                            "VALUES(?, ?, ?)"
            );
            insertAnswerQuery.setInt(1, question.getQuestionId());

            for(int i = 0; i < answers.length; i++){
                insertAnswerQuery.setString(2, answers[i]);

                insertAnswerQuery.setBoolean(0x3, i == correctIndex);

                insertAnswerQuery.executeUpdate();
            }

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    // delete all data from the db, inputs etc.
    public static void clearAllData() {
        try {
            Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USERNAME, DB_PASSWORD
            );

            // disable foreign key checks for the current session to allow deletion
            Statement stmt = connection.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // delete data from dependent tables first
           stmt.executeUpdate("DELETE FROM ANSWER");
           stmt.executeUpdate("DELETE FROM QUESTION");
           stmt.executeUpdate("DELETE FROM CATEGORY");

            // re enable foreign keys check
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}