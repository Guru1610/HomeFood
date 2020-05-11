package com.bangaram.homefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PricingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pricing_screen );
        findViewById( R.id.btReg ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( PricingScreen.this,HomeScreen.class );
                startActivity( intent );
                finish();
            }
        } );

        findViewById( R.id.btOcc ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( PricingScreen.this,HomeScreen.class );
                startActivity( intent );
                finish();
            }
        } );
    }
}
