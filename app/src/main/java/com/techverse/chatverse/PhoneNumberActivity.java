package com.techverse.chatverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class PhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    Button nextBtn;
    EditText mobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        ccp = findViewById(R.id.ccp);
        nextBtn = findViewById(R.id.next_btn);
        mobileNumber = findViewById(R.id.mobile_number);

        ccp.registerCarrierNumberEditText(mobileNumber);

        nextBtn.setOnClickListener(view -> {
            if (!ccp.isValidFullNumber()){
                mobileNumber.setError("Not a valid Mobile number");
                return;
            }

            Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
            intent.putExtra("phone", ccp.getFullNumberWithPlus());
            startActivity(intent);

        });

    }
}