package com.bangaram.homefood;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    String verificationCodeBySystem;

    EditText etPhone;
    Button btSubmit;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_phone_auth );
        etPhone = findViewById( R.id.editText7 );
        btSubmit= findViewById( R.id.button9 );
        progressBar = findViewById( R.id.progressBar );

        String Phone = getIntent().getStringExtra( "phone" );

        sendVerificationToUser (Phone);
    }

    private void sendVerificationToUser(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,           // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent( s, forceResendingToken );
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressBar.setVisibility( View.VISIBLE );
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText( PhoneAuth.this, "Error", Toast.LENGTH_SHORT ).show();

        }
    };

    private void verifyCode(String verificationcode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential( verificationCodeBySystem, verificationcode );
        signinByPhone(credential);

    }

    private void signinByPhone(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential( credential ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent( PhoneAuth.this,PricingScreen.class );
                    startActivity( intent );
                    finish();
                }
                else {
                    Toast.makeText( PhoneAuth.this, "Failed", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }
}
