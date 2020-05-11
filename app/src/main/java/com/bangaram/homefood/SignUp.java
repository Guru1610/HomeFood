package com.bangaram.homefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btSignup;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById( R.id.etEmail );
        etPassword = findViewById( R.id.etPassword );
        btSignup = findViewById( R.id.btsignup );
        btSignup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!email.isEmpty() || !password.isEmpty()){
                    firebaseAuth.createUserWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child( firebaseAuth.getCurrentUser().getUid() );
                                databaseReference.child( "Email" ).setValue( email );
                                Intent intent = new Intent( SignUp.this, ProfileCreate.class );
                                startActivity( intent );
                                finish();
                            }

                        }
                    } );

                }
            }
        } );

    }
}
