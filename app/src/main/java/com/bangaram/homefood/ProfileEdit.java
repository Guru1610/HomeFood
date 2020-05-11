package com.bangaram.homefood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile_edit );

        findViewById( R.id.signOutbt ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( ProfileEdit.this,SignIn.class );
                startActivity( intent );
                finish();
            }
        } );
        findViewById( R.id.btOrdera ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ProfileEdit.this,DriverDetailsScreen.class );
                startActivity( intent );
            }
        } );
    }
}
