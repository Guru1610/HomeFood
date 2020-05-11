package com.bangaram.homefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileCreate extends AppCompatActivity {

    EditText etName, etLastName, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile_create );
        etName = findViewById( R.id.editText4 );
        etLastName = findViewById( R.id.editText5 );
        etPhone = findViewById( R.id.editText6 );

        findViewById( R.id.button8 ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regUser();;
            }
        } );


    }

    private void regUser(){
        final String phone = etPhone.getText().toString();
        final String name = etName.getText().toString();
        final String lastName = etLastName.getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (phone.isEmpty() | phone.length()!=10){
            etPhone.setError("Invalid Phone Number");
            return;
        }
        if (name.isEmpty()) {
            etName.setError("Field cannot be empty");
            return;
        }
        else if (!name.matches(passwordVal)) {
            etName.setError("Invalid");
            return;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Field cannot be empty");
            return;
        }
        else if (!lastName.matches(passwordVal)) {
            etLastName.setError("Invalid");
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        reference.child( "Name" ).setValue( name );
        reference.child( "LastName" ).setValue( lastName );
        reference.child( "Phone" ).setValue( phone ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent( ProfileCreate.this,PhoneAuth.class );
                    intent.putExtra( "phone", phone );
                    startActivity( intent );
                }
            }
        } );
    }
}
