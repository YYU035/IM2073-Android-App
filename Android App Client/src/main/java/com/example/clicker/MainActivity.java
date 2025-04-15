// MainActivity.java
package com.example.clickerclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.CountDownTimer;
import android.util.Log;





public class MainActivity extends AppCompatActivity {

    private TextView txtTimer;
    private CountDownTimer timer;
    private final int QUESTION_TIME = 30; // seconds
    private TextView questionView;
    private Button buttonA, buttonB, buttonC, buttonD;
    private ImageView imageA, imageB, imageC, imageD;
    private LinearLayout imageOptions;
    private int currentQuestion = 0;
    private String[] questions = {
            "What is 2 + 2?",
            "What is the capital of France?",
            "Which planet is known as the Red Planet?",
            "What color do you get when you mix red and blue?",
            "Which one is a fruit?",
    };
    private String[][] choices = {
            {"3", "4", "12", "9"},
            {"Rome", "Berlin", "Madrid", "Paris"},
            {"Earth", "Mars", "Venus", "Jupiter"},
            {"yellow", "green", "purple", "orange"},
            {"A", "B", "C", "D"} // placeholder for image
    };

    // correct answers
    private String[] correctAnswers = {"B", "D", "B", "C", "A"};


    private String serverUrl = "http://10.91.77.230:9999/clicker/select";
    //url of my teammate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        questionView = findViewById(R.id.textViewQuestion);
        txtTimer = findViewById(R.id.txtTimer);


        questionView = findViewById(R.id.textViewQuestion);
        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonC = findViewById(R.id.buttonC);
        buttonD = findViewById(R.id.buttonD);
        imageOptions = findViewById(R.id.imageOptions);
        imageA = findViewById(R.id.imageA);
        imageB = findViewById(R.id.imageB);
        imageC = findViewById(R.id.imageC);
        imageD = findViewById(R.id.imageD);

        updateQuestion();

        View.OnClickListener textClickListener = v -> {
            String choice = "A";
            if (v.getId() == R.id.buttonA) choice = "A";
            else if (v.getId() == R.id.buttonB) choice = "B";
            else if (v.getId() == R.id.buttonC) choice = "C";
            else if (v.getId() == R.id.buttonD) choice = "D";
            sendAnswer(currentQuestion + 1, choice);
        };

        buttonA.setOnClickListener(textClickListener);
        buttonB.setOnClickListener(textClickListener);
        buttonC.setOnClickListener(textClickListener);
        buttonD.setOnClickListener(textClickListener);

        imageA.setOnClickListener(v -> sendAnswer(currentQuestion + 1, "A"));
        imageB.setOnClickListener(v -> sendAnswer(currentQuestion + 1, "B"));
        imageC.setOnClickListener(v -> sendAnswer(currentQuestion + 1, "C"));
        imageD.setOnClickListener(v -> sendAnswer(currentQuestion + 1, "D"));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateQuestion() {
        questionView.setText("Q" + (currentQuestion + 1) + ": " + questions[currentQuestion]);

        if (currentQuestion == 4) {
            // picture qns
            buttonA.setVisibility(View.GONE);
            buttonB.setVisibility(View.GONE);
            buttonC.setVisibility(View.GONE);
            buttonD.setVisibility(View.GONE);
            imageOptions.setVisibility(View.VISIBLE);
            imageA.setImageResource(R.drawable.apple);
            imageB.setImageResource(R.drawable.stone);
            imageC.setImageResource(R.drawable.toy);
            imageD.setImageResource(R.drawable.trafficlights);
        } else {
            // normal qns
            buttonA.setVisibility(View.VISIBLE);
            buttonB.setVisibility(View.VISIBLE);
            buttonC.setVisibility(View.VISIBLE);
            buttonD.setVisibility(View.VISIBLE);
            imageOptions.setVisibility(View.GONE);
            buttonA.setText("A. " + choices[currentQuestion][0]);
            buttonB.setText("B. " + choices[currentQuestion][1]);
            buttonC.setText("C. " + choices[currentQuestion][2]);
            buttonD.setText("D. " + choices[currentQuestion][3]);
        }
        startTimer();
    }
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(QUESTION_TIME * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Time remaining: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                txtTimer.setText("⏰ Time's up!");
                sendAnswer(currentQuestion + 1, ""); // Empty string if no answer
            }
        }.start();
    }


    private int score = 0;
    private void sendAnswer(int questionNo, String choice) {
        if (timer != null) timer.cancel();

        boolean isCorrect = choice.equals(correctAnswers[questionNo - 1]);
        if (isCorrect) score++;
        String resultText = choice.isEmpty() ? "⏱️ No answer submitted." :
                (isCorrect ? "✅ Correct!" : "❌ Incorrect.");

        runOnUiThread(() -> {
            // Create alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("✅ Submitted");
            builder.setMessage("Question:\n" + questions[currentQuestion] +
                    "\n\nYour answer: " + choice +
                    "\n" + resultText);
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Proceed to next question when OK is clicked
                currentQuestion++;
                if (currentQuestion >= questions.length) {
                    Intent intent = new Intent(this, EndActivity.class);
                    intent.putExtra("userPhone", getIntent().getStringExtra("userPhone"));  // from StartActivity
                    intent.putExtra("score", score);  // You need to track this variable!
                    intent.putExtra("total", questions.length);
                    startActivity(intent);
                    finish();
                } else {
                    updateQuestion();
                }
            });
            builder.setCancelable(false);
            builder.show();
        });

        new Thread(() -> {
            try {
                String urlStr = serverUrl + "?questionNo=" + questionNo + "&choice=" + choice;

                // ✅ 调试输出
                Log.d("ClientRequest", "Sending to: " + urlStr);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int code = conn.getResponseCode();

                // ✅ 打印服务器响应码
                Log.d("ClientResponse", "Code: " + code);

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ClientError", "Exception: " + e.getMessage());
            }
        }).start();

    }
}


