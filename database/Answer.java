package database;

public class Answer {
    private final int answerId;
    private final int questionId;
    private final String answerText;
    private final boolean isCorrect;

    public int getAnswerId() {
        return answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Answer(int answerId, int questionId, String answerText, boolean isCorrect) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }
}