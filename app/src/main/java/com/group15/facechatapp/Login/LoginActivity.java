package com.group15.facechatapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.group15.facechatapp.Chat.ChatActivity;
import com.group15.facechatapp.R;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    ImageView logo;
    CountryCodePicker cd;
    Button getOTP, verifyOTP;
    TextInputEditText phone, otp;
    String countrycode, phonenumber, enteredotp;
    TextInputLayout otpinput;


    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;

    RelativeLayout rl;
    TextView textView;

    public void setCodesent(String codesent) {
        this.codesent = codesent;
    }

    public String getCodesent() {
        return codesent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        //declaration
        logo = findViewById(R.id.login_logo);
        cd = findViewById(R.id.countrycodepicker);
        phone = findViewById(R.id.login_phone_inputtext);
        otp = findViewById(R.id.login_otp_inputtext);
        otpinput = findViewById(R.id.login_otp_inputtext_layout);
        getOTP = findViewById(R.id.login_getotp_button);
        rl = findViewById(R.id.login_realtivelayout);

        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.APPEARING);
        rl.setLayoutTransition(lt);
        //verifyOTP = findViewById(R.id.login_next_button);


        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.login_resentotptxt);

        String text = "Didn't get message RESEND OTP";

        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            public void onClick(View widget) {
                getOtp();
                Toast.makeText(LoginActivity.this, "Resending OTP", Toast.LENGTH_SHORT).show();
            }
        };


        // setting the part of string to be act as a link
        ss.setSpan(clickableSpan1, 19, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        //functions
        cd.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = cd.getSelectedCountryCodeWithPlus();
            }
        });

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOTP.getText().equals("Get OTP")) {
                    getOtp();
                } else {
                    verifyOtp();

                }


            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "OTP is Sent", Toast.LENGTH_SHORT).show();


                codesent = s;

                setCodesent(s);

                getOTP.setText("Verify OTP");
                textView.setVisibility(View.VISIBLE);
                otpinput.setVisibility(View.VISIBLE);
                // Intent intent=new Intent(MainActivity.this,otpAuthentication.class);
                //   intent.putExtra("otp",codesent);
                ///  startActivity(intent);
            }
        };


    }


    void verifyOtp() {
        enteredotp = otp.getText().toString();
        if (enteredotp.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter your OTP First ", Toast.LENGTH_SHORT).show();
        } else {
            if (getCodesent() != null) {
                // String coderecieved = getIntent().getStringExtra("otp");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getCodesent(), enteredotp);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(getApplicationContext(), "Resend OTP", Toast.LENGTH_SHORT).show();
            }

        }

    }


    void getOtp() {
        String number;
        number = phone.getText().toString();
        if (number.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Your number", Toast.LENGTH_SHORT).show();
        } else if (number.length() < 10) {
            Toast.makeText(getApplicationContext(), "Please Enter correct number", Toast.LENGTH_SHORT).show();
        } else {


            if(countrycode==null){
                countrycode=cd.getSelectedCountryCodeWithPlus();
            }
            phonenumber = countrycode + number;
            //Test
            // Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            //logo.startAnimation(animation);
                  /*  Animation animotp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                    getOTP.setText("Resend OTP");
                    otpinput.startAnimation(animotp);
                    otpinput.setVisibility(View.VISIBLE);
                    verifyOTP.startAnimation(animotp);
                    verifyOTP.setVisibility(View.VISIBLE);*/

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phonenumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(LoginActivity.this)
                    .setCallbacks(mCallbacks)
                    .build();


            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, SetProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}