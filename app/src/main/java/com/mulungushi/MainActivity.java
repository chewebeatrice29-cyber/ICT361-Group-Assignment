package com.mulungushi;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextView resendCodeLink;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);
        MaterialButton sendButton = findViewById(R.id.sendResetButton);
        TextView backToSignIn = findViewById(R.id.backToSignIn);
        resendCodeLink = findViewById(R.id.resendCodeLink);

        // Tap resend only if timer is done
        resendCodeLink.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                emailInputLayout.setError("Enter your email address first");
                return;
            }
            emailInputLayout.setError(null);
            Toast.makeText(this, "Reset code resent to " + email, Toast.LENGTH_LONG).show();
            startResendTimer();
        });

        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Enter a valid email address (e.g., name@gmail.com)");
                return;
            } else {
                emailInputLayout.setError(null);
            }

            sendButton.setEnabled(false);
            sendButton.setText("SENDING...");

            sendButton.postDelayed(() -> {
                Toast.makeText(this, "Reset code sent to " + email, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, VerifyCodeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);

                sendButton.setEnabled(true);
                sendButton.setText("SEND RESET CODE");

                // Start the 30s timer after sending
                startResendTimer();
            }, 2000);
        });

        backToSignIn.setOnClickListener(v -> {
            Toast.makeText(this, "Going back to Sign In", Toast.LENGTH_SHORT).show();
            // finish();
        });
    }

    // ⭐ NEW: 30-second resend timer
    private void startResendTimer() {
        if (resendTimer != null) resendTimer.cancel();

        resendCodeLink.setEnabled(false);
        resendCodeLink.setTextColor(0xFF888888); // grey

        resendTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                resendCodeLink.setText("Resend Code (" + seconds + "s)");
            }

            @Override
            public void onFinish() {
                resendCodeLink.setText("Resend Code");
                resendCodeLink.setEnabled(true);
                resendCodeLink.setTextColor(0xFF1976D2); // blue
            }
        }.start();
    }
}
