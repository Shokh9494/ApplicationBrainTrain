package com.example.applicationbraintrain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewTimer;
    private TextView textView0;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;


    private ArrayList<TextView> option = new ArrayList<>();

    private String question;
    private int rightAnswer;
    private int getRightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;


    private int countOfQuestion = 0;
    private int countOfRightAnswers = 0;

    private boolean gameOver = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        textViewTimer = findViewById(R.id.textViewTimer);
        textView0 = findViewById(R.id.textView0);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        option.add(textView0);
        option.add(textView1);
        option.add(textView2);
        option.add(textView3);
        playNext();

        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(getTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max=preferences.getInt("max",0);
                if (countOfRightAnswers>=max){
                    preferences.edit().putInt("max",countOfRightAnswers).apply();
                }
                textViewTimer.setText(String.format("00:00"));
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }


    private void playNext() {
        generateQuestion();
        for (int i = 0; i < option.size(); i++) {
            if (i == getRightAnswerPosition) {
                option.get(i).setText(Integer.toString(rightAnswer));
            } else {
                option.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }

        String score = String.format("%s/%s", countOfRightAnswers, countOfQuestion);
        textViewScore.setText(score);
    }


    private void generateQuestion() {
        int a = (int) (Math.random() * 25 + 5);
        int b = (int) (Math.random() * 25 + 5);

        int mark = (int) (Math.random() * 2);
        isPositive = mark == 1;
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s+%s", a, b);
        } else {
            rightAnswer = a - b;
            question = String.format("%s-%s", a, b);
        }
        textViewQuestion.setText(question);
        getRightAnswerPosition = (int) (Math.random() * 4);

    }

    private int generateWrongAnswer() {
        int result;
        do {
            result = (int) (Math.random() * 61) - 25;
        } while (result == rightAnswer);
        return result;
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minut = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minut, seconds);
    }


    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int choosenAnswer = Integer.parseInt(answer);
            if (choosenAnswer == rightAnswer) {
                countOfRightAnswers++;
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show();
            }
            countOfQuestion++;
            playNext();
        }
    }
}