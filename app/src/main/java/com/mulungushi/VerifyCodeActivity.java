package com.mulungushi;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class VerifyCodeActivity extends AppCompatActivity {

    private TextView[] boxes = new TextView[6];
    private EditText hiddenOtpInput;
    private TextView errorText;
    private TextView resendCodeLink;
    private TextView instructionText;
    private MaterialButton verifyButton;
    private CountDownTimer resendTimer;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) userEmail = "your email";

        instructionText = findViewById(R.id.instructionText);
        instructionText.setText("We sent a 6-digit verification code to " + userEmail);

        boxes[0] = findViewById(R.id.box1);
        boxes[1] = findViewById(R.id.box2);
        boxes[2] = findViewById(R.id.box3);
        boxes[3] = findViewById(R.id.box4);
        boxes[4] = findViewById(R.id.box5);
        boxes[5] = findViewById(R.id.box6);

        hiddenOtpInput = findViewById(R.id.hiddenOtpInput);
        errorText = findViewById(R.id.errorText);
        resendCodeLink = findViewById(R.id.resendCodeLink);
        verifyButton = findViewById(R.id.verifyButton);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        hiddenOtpInput.requestFocus();

        for (TextView box : boxes) {
            box.setOnClickListener(v -> hiddenOtpInput.requestFocus());
        }

        hiddenOtpInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                for (int i = 0; i < 6; i++) {
                    if (i < text.length()) {
                        boxes[i].setText(String.valueOf(text.charAt(i)));
                    } else {
                        boxes[i].setText("");
                    }
                }
                errorText.setVisibility(View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        verifyButton.setOnClickListener(v -> {
            String code = hiddenOtpInput.getText().toString().trim();

            if (code.length() != 6) {
                errorText.setText("⚠ Invalid code. Try again.");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            verifyButton.setEnabled(false);
            verifyButton.setText("VERIFYING...");

            verifyButton.postDelayed(() -> {
                if (code.equals("123456")) {
                    Intent intent = new Intent(VerifyCodeActivity.this, SetNewPasswordActivity.class);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                } else {
                    errorText.setText("⚠ Invalid code. Try again.");
                    errorText.setVisibility(View.VISIBLE);
                }
                verifyButton.setEnabled(true);
                verifyButton.setText("VERIFY CODE");
            }, 1500);
        });

        resendCodeLink.setOnClickListener(v -> {
            Toast.makeText(this, "Code resent to " + userEmail, Toast.LENGTH_LONG).show();
            hiddenOtpInput.setText("");
            startResendTimer();
        });

        startResendTimer();
    }

    private void startResendTimer() {
        if (resendTimer != null) resendTimer.cancel();
        resendCodeLink.setEnabled(false);
        resendCodeLink.setTextColor(0xFF888888);

        resendTimer = new CountDownTimer(30000, 1000) {
            @Override public void onTick(long ms) {
                resendCodeLink.setText("Resend Code (" + (ms / 1000) + "s)");
            }
            @Override public void onFinish() {
                resendCodeLink.setText("Resend Code");
                resendCodeLink.setEnabled(true);
                resendCodeLink.setTextColor(0xFF1976D2);
            }
        }.start();
    }
}