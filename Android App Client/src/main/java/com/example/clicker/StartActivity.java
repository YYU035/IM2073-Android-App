
package com.example.clickerclient;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.net.URL;
import java.net.HttpURLConnection;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        EditText editTextPhone = findViewById(R.id.editTextPhone);
        Button buttonStart = findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(v -> {
            String phone = editTextPhone.getText().toString().trim();

            // ✅ Only proceed if it's a valid 8-digit phone number
            if (!phone.matches("\\d{8}")) {
                Toast.makeText(this, "Please enter a valid 8-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔵 Send verification request to server
            new Thread(() -> {
                try {
                    String verifyUrl = "http://10.91.205.79:9999/clicker/verification?phoneNo=" + phone;

                    URL url = new URL(verifyUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    int code = conn.getResponseCode();
                    conn.disconnect();

                    runOnUiThread(() -> {
                        if (code == 200) {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("userPhone", phone);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
