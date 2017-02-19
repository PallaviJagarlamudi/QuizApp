package com.example.android.quizapp;

import java.util.Arrays;

/**
 * Created by Pallavi J on 08-02-2017.
 * This class defines the various attributes and its getter/setter for a Question Class
 */

public class Question {

    public final static String RADIO_TYPE = "R";
    public final static String CHECKBOX_TYPE = "C";
    public final static String TEXT_TYPE = "T";
    private final static int MAX_OPTIONS = 4;
    private int id;
    private String questionType;
    private String question;
    private String[] answer;
    private String[] options;

    public Question() {
        options = new String[MAX_OPTIONS];
        id = 0;
        question = "";
        questionType = "";
        answer = new String[MAX_OPTIONS];
        for (int i = 0; i < MAX_OPTIONS; i++) {
            options[i] = "";
            answer[i] = "";
        }
    }

    public Question(int id, String questionType, String question, String[] answer, String[] options) {
        this.id = id;
        this.questionType = questionType;
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    @Override
    public String toString() {
        return id + ", " + questionType + ", " + question + ", " + Arrays.toString(answer) + ", " + Arrays.toString(options);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOption() {
        return options;
    }

    public void setOption(String[] options) {
        this.options = options;
    }

    public String[] getAnswer() {
        return answer;
    }

    public void setAnswer(String[] answer) {
        this.answer = answer;
    }

    public boolean isGivenAnswerCorrect(String[] givenAnswers) {
        boolean isAnswerCorrect = false;
        if (questionType.equals(Question.RADIO_TYPE)) {
            if (answer[0].equals(givenAnswers[0])) {
                isAnswerCorrect = true;
            }
        } else if (questionType.equals(Question.TEXT_TYPE)) {
            int count = 0;
            for (int i = 0; i < answer.length; i++) {
                for (int j = 0; j < givenAnswers.length; j++) {
                    if ((answer[i].toLowerCase().equals(givenAnswers[j].trim().toLowerCase()))) {
                        count++;
                        break;
                    }
                }
            }
            if (count == answer.length) {
                isAnswerCorrect = true;
            }
        } else if (questionType.equals(Question.CHECKBOX_TYPE)) {
            if (Arrays.equals(answer, givenAnswers)) {
                isAnswerCorrect = true;
            }
        }
        return isAnswerCorrect;
    }

}
