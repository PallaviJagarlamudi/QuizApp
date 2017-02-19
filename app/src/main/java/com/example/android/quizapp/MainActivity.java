package com.example.android.quizapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Max number of questions that can present for a particular type in input file
    private final static int MAX_QUESTION_EXISTS = 4;
    // If there is change in the id seq in the input file, please update here also
    private final static int radioMinId = 1000;      //Radio type question id can range from 1000-1999
    private final static int checkboxMinId = 2000;   //Checkbox type question id can range from 2000-2999
    private final static int textMinId = 3000;       //Text type question id can range from 3000-3999
    private final static int MAX_Q_DISPALYED = 6;
    private final static int MAX_OPTIONS = 4;
    private final static String[] POSSIBLE_OPT = {"A", "B", "C", "D"};
    // 1- Radio button, 2-checkbox, 3-edittext
    private final static String[] question_type = {"R", "T", "R", "C", "T", "C"};
    ArrayList<Question> selectedQuestionsList = new ArrayList<Question>();
    int score = 0;
    int noOFQsAnwered = 0;
    boolean isQuizEvaluated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedQuestionsList = selectQListToDisplay();
        prepareViewToDisplayQuestion();
        isQuizEvaluated = false;
    }

    /**
     * This method reads the input file and populate the Q & A ArrayList
     */
    private ArrayList<Question> generateQList() {
        ArrayList<Question> qList = new ArrayList<Question>();
        // Change this position value if they is change in the input file format
        int id_pos = 0, type_pos = 1, question_pos = 2, ans_pos = 3, opt_pos = 4;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("Questions.txt")));
            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                Question question = new Question();
                String[] tokens = mLine.split(";");

                if (tokens[type_pos].equals("R") || tokens[type_pos].equals("C") || tokens[type_pos].equals("T")) {
                    question.setId(Integer.parseInt(tokens[id_pos]));
                    question.setQuestionType(tokens[type_pos]);
                    question.setQuestion(tokens[question_pos]);
                    question.setAnswer(tokens[ans_pos].split(","));
                    if (tokens[type_pos].equals("R") || tokens[type_pos].equals("C")) {
                        question.setOption(tokens[opt_pos].split(","));
                    }
                    qList.add(question);
                } else {
                    Log.w("MainActivity", "Invalid entry in file");
                }
                mLine = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qList;
    }

    /**
     * This method reads the available set of Questions in arrayList and select required number of questions to be displayed
     * on the screen into selectQListToDisplay arraylist.
     */
    //Get random number and check if that question has been already selected. If added, get next random question else
    private ArrayList<Question> selectQListToDisplay() {
        /* Get all the available questions list */
        ArrayList<Question> questionsList = new ArrayList<Question>();
        questionsList = generateQList();

        /* Logic to select random questions to display */
        Random randomGenerator = new Random();
        ArrayList<Question> qListToDisplay = new ArrayList<Question>();
        int selectedQuestionId = 0;

        for (int i = 0; i < MAX_Q_DISPALYED; i++) {
            boolean loop_exit = false;
            while (!loop_exit) {
                if (question_type[i].equals(Question.RADIO_TYPE)) {
                    selectedQuestionId = radioMinId;
                } else if (question_type[i].equals(Question.CHECKBOX_TYPE)) {
                    selectedQuestionId = checkboxMinId;
                } else if (question_type[i].equals(Question.TEXT_TYPE)) {
                    selectedQuestionId = textMinId;
                }
                selectedQuestionId += randomGenerator.nextInt(MAX_QUESTION_EXISTS);
                // Check if the question with selected id is already in the list.
                boolean questionAlreadyAdded = false;
                for (Question q : qListToDisplay) {
                    if (q.getId() == selectedQuestionId) {
                        questionAlreadyAdded = true;
                    }
                }
                // If  added continue the loop to get other question, else add it to selectedList
                if (!questionAlreadyAdded) {
                    //Check if the question with given id exists in super QuestionList
                    for (Question q : questionsList) {
                        if (q.getId() == selectedQuestionId) {
                            qListToDisplay.add(q);
                            loop_exit = true;
                        }
                    }
                }
            }
        }
        return qListToDisplay;
    }


    /**
     * This method display the selected Questions  and their options on the screen
     */
    private void prepareViewToDisplayQuestion() {

        EditText playername = (EditText) findViewById(R.id.participant_name);
        playername.setText("");
        playername.setEnabled(true);

        for (int i = 0; i < MAX_Q_DISPALYED; i++) {
            Question currQuestion = selectedQuestionsList.get(i);
            TextView questionTextView = (TextView) findViewById(getResources().getIdentifier("question" + (i + 1), "id", getPackageName()));
            questionTextView.setText(currQuestion.getQuestion());
            if (question_type[i].equals(Question.RADIO_TYPE)) {
                int opt_ind = 0;
                for (String x : POSSIBLE_OPT) {
                    RadioButton radiobutton = (RadioButton) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    radiobutton.setText(currQuestion.getOption()[opt_ind++]);
                    radiobutton.setEnabled(true);
                }
                RadioGroup radioGroup = (RadioGroup) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                radioGroup.clearCheck();
            } else if (question_type[i].equals(Question.CHECKBOX_TYPE)) {
                int opt_ind = 0;
                for (String x : POSSIBLE_OPT) {
                    CheckBox checkBox = (CheckBox) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    checkBox.setText(currQuestion.getOption()[opt_ind++]);
                    checkBox.setChecked(false);
                    checkBox.setEnabled(true);
                }
            } else if (question_type[i].equals(Question.TEXT_TYPE)) {
                EditText editText = (EditText) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                editText.setText("");
                editText.setEnabled(true);
            }

            TextView resultTextView = (TextView) findViewById(getResources().getIdentifier("ques" + (i + 1) + "_result", "id", getPackageName()));
            resultTextView.setText("");
        }
    }

    /**
     * This method is called when the submit button is clicked.
     * It evaluates the answer and display the score to user
     */
    public void submitQuiz(View view) {

        EditText playername = (EditText) findViewById(R.id.participant_name);
        String username = playername.getText().toString();
        String msg = "";

        if (isQuizEvaluated) {
            msg = getString(R.string.resetMsg, score);
            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isQuizEvaluated = true;
        } else {
            score = 0;
            noOFQsAnwered = countQuestionsAnswered();

            if (username.equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.userNameMsg), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                if (noOFQsAnwered != MAX_Q_DISPALYED) {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.warningMsg), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    score = calculateScore();
                    freezeView();
                    msg = getString(R.string.scoreMsg, username, score);
                    Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    isQuizEvaluated = true;
                }
            }
        }
    }

    /**
     * This method is called once the submit button is clicked and score is successfully evaluated.
     * It freezes the view till reset button is hit
     */
    private void freezeView() {
        EditText playername = (EditText) findViewById(R.id.participant_name);
        playername.setEnabled(false);

        for (int i = 0; i < MAX_Q_DISPALYED; i++) {
            if (question_type[i].equals(Question.RADIO_TYPE)) {
                int opt_ind = 0;
                for (String x : POSSIBLE_OPT) {
                    RadioButton radiobutton = (RadioButton) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    radiobutton.setEnabled(false);
                }
                RadioGroup radioGroup = (RadioGroup) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                //radioGroup.clearCheck();
            } else if (question_type[i].equals(Question.CHECKBOX_TYPE)) {
                int opt_ind = 0;
                for (String x : POSSIBLE_OPT) {
                    CheckBox checkBox = (CheckBox) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    checkBox.setEnabled(false);
                }
            } else if (question_type[i].equals(Question.TEXT_TYPE)) {
                EditText editText = (EditText) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                editText.setEnabled(false);
            }
        }
    }

    /**
     * This method is called when the reset button is clicked.
     * It reset the scores, game state and the screen
     */
    public void resetQuiz(View view) {
        if (isQuizEvaluated) {
            isQuizEvaluated = false;
            score = 0;
            noOFQsAnwered = countQuestionsAnswered();
            selectedQuestionsList = selectQListToDisplay();
            prepareViewToDisplayQuestion();
        } else {
            String msg = getString(R.string.resetWrnMsg);
            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * This method iterate through each question and compare the user given answers with Original answers and return the score.
     */
    private int calculateScore() {
        int score = 0;

        for (int i = 0; i < MAX_Q_DISPALYED; i++) {
            String[] userAnswers = new String[MAX_OPTIONS];
            for (int j = 0; j < MAX_OPTIONS; j++) {
                userAnswers[j] = "";
            }

            Question currQuestion = selectedQuestionsList.get(i);
            if (question_type[i].equals(Question.RADIO_TYPE)) {
                //Check if any of the radiobutton has been selected
                RadioGroup radioGroup = (RadioGroup) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton radioButton = (RadioButton) findViewById(selectedId);
                    userAnswers[0] = radioButton.getText().toString();
                }
            } else if (question_type[i].equals(Question.CHECKBOX_TYPE)) {
                int index = 0;
                for (String x : POSSIBLE_OPT) {
                    CheckBox checkBox = (CheckBox) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    if (checkBox.isChecked()) {
                        userAnswers[index++] = checkBox.getText().toString();
                    }
                }
            } else {
                EditText editText = (EditText) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                userAnswers = editText.getText().toString().trim().split(",");
            }
            userAnswers = removeEmptyString(userAnswers);

            TextView resultTextView = (TextView) findViewById(getResources().getIdentifier("ques" + (i + 1) + "_result", "id", getPackageName()));

            if (currQuestion.isGivenAnswerCorrect(userAnswers)) {
                resultTextView.setText(R.string.correct);
                resultTextView.setTextColor(Color.GREEN);
                score++;
            } else {
                resultTextView.setText(R.string.wrong);
                resultTextView.setTextColor(Color.RED);
            }
        }
        return score;
    }

    /**
     * This method return the count of number of questions that have been answered.
     */
    public int countQuestionsAnswered() {
        int count = 0;

        for (int i = 0; i < MAX_Q_DISPALYED; i++) {
            if (question_type[i].equals(Question.RADIO_TYPE)) {
                //Check if any of the radiobutton has been selected
                RadioGroup radioGroup = (RadioGroup) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    count++;
                }
            } else if (question_type[i].equals(Question.CHECKBOX_TYPE)) {
                //Check if anyone of the checkbox has been checked
                for (String x : POSSIBLE_OPT) {
                    CheckBox checkBox = (CheckBox) findViewById(getResources().getIdentifier("option" + (i + 1) + x, "id", getPackageName()));
                    if (checkBox.isChecked()) {
                        count++;
                        break;
                    }
                }
            } else {
                EditText editText = (EditText) findViewById(getResources().getIdentifier("quest" + (i + 1) + "_answer", "id", getPackageName()));
                if (!editText.getText().toString().equals("")) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * This method read string array and remove the null and emptry strings.
     */
    private String[] removeEmptyString(String[] strArray) {
        List<String> list = new ArrayList<String>(Arrays.asList(strArray));
        list.removeAll(Collections.singleton(null));
        list.removeAll(Collections.singleton(""));
        return list.toArray(new String[0]);
    }
}
