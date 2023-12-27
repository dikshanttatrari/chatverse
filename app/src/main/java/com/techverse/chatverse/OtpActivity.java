package com.techverse.chatverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    String phone;
    TextView phoneNumber, verify, resendOtp, timerr;
    ImageView timerCircle;
    Long timeOutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    EditText otp;
    Button nextBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phone = getIntent().getExtras().getString("phone");
        phoneNumber = findViewById(R.id.phone_number);
        verify = findViewById(R.id.textview2);
        otp = findViewById(R.id.otp);
        resendOtp = findViewById(R.id.resend_otp);
        nextBtn = findViewById(R.id.nxt_btn);
        timerr = findViewById(R.id.timer);
        timerCircle = findViewById(R.id.timer_circle);


        String text = "We have sent an OTP to your mobile number " +phone+ ". Wrong number?";

        SpannableString spannableString = new SpannableString(text);

        ClickableSpan wrongNumberClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(OtpActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
            }
        };

        spannableString.setSpan(wrongNumberClick, 57, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        phoneNumber.setText(spannableString);
        phoneNumber.setMovementMethod(LinkMovementMethod.getInstance());

        sendOtp(phone, false);

        nextBtn.setOnClickListener(view -> {
            String enteredOtp = otp.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
        });

        resendOtp.setOnClickListener(view -> {
            sendOtp(phone, true);
        });

    }

    void sendOtp(String phone, boolean isResend){
        startResendTimer();
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(timeOutSeconds,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getApplicationContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(getApplicationContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }
                        });
        if (isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress){
        if (inProgress){
            nextBtn.setVisibility(View.VISIBLE);
        }else{
            nextBtn.setVisibility(View.VISIBLE);

        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()){
                    Intent intent = new Intent(OtpActivity.this, ProfileActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void startResendTimer(){
        resendOtp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeOutSeconds --;
                runOnUiThread(() -> {
                    timerr.setText("00:" + timeOutSeconds);
                });
                if (timeOutSeconds<=0){
                    timer.cancel();
                    runOnUiThread(() -> {
                        timerCircle.setVisibility(View.GONE);
                        timerr.setVisibility(View.GONE);
                        resendOtp.setText("Resend OTP");
                        resendOtp.setTextColor(Color.parseColor("#000000"));
                        resendOtp.setEnabled(true);
                    });
                }
            }
        },0,1000);
    }

}