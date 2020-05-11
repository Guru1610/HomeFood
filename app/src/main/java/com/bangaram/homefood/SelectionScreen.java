package com.bangaram.homefood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class SelectionScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_selection_screen );
        findViewById( R.id.button5 ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SelectionScreen.this,HomeScreen.class );
                startActivity( intent );
            }
        } );
        findViewById( R.id.button6 ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SelectionScreen.this,DriverHomeScreen.class );
                startActivity( intent );
            }
        } );
    }
}
