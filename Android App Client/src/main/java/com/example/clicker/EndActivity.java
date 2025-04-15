package com.example.clickerclient;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end);

        TextView textEndMessage = findViewById(R.id.textEndMessage);
        Button buttonRestart = findViewById(R.id.buttonRestart);
        String phone = getIntent().getStringExtra("userPhone");
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        // Set message
        String message = "🎉 Quiz Completed!\nThanks for participating.";
        message += "Score: " + score + "/" + total + "\n";
        message += "Percent: " + (int)((score * 100.0f) / total) + "%\n";

        if (phone != null && !phone.isEmpty()) {
            message += "\nUser: " + phone;
        }

        textEndMessage.setText(message);

        buttonRestart.setOnClickListener(v -> {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();  // close this activity
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
