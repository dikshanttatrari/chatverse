package com.techverse.chatverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    Button agreeAndContinue;
    TextView termsAndConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        agreeAndContinue = findViewById(R.id.agree_and_continue);
        termsAndConditions = findViewById(R.id.terms_and_conditions);

        String text = "Tap 'Agree and Continue' to accept our Terms & conditions and Privacy Policy.";
        SpannableString spannableString = new SpannableString(text);


        ClickableSpan termsAndConditionsClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        };

        spannableString.setSpan(termsAndConditionsClick, 39, 76, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsAndConditions.setText(spannableString);
        termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());

        agreeAndContinue.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, PhoneNumberActivity.class);
            startActivity(intent);
        });

    }
}