package com.bangaram.homefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 16;
    FirebaseAuth firebaseAuth;
    CallbackManager mCallbackManager;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            updateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        final EditText etemail = findViewById( R.id.editText );
        final EditText etPassword = findViewById( R.id.editText2 );
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById( R.id.button2 ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etemail.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword( etemail.getText().toString(), etPassword.getText().toString() ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updateUI();
                            } else {
                                Toast.makeText( SignIn.this, "Failed", Toast.LENGTH_SHORT ).show();
                            }

                        }
                    } );
                }
            }
        } );
        findViewById( R.id.ivGoogle ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        } );
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient( this, gso );

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById( R.id.login_button );
        loginButton.setReadPermissions( "email", "public_profile" );
        loginButton.registerCallback( mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d( "TAG", "facebook:onSuccess:" + loginResult );
                handleFacebookAccessToken( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                Log.d( "TAG", "facebook:onCancel" );
            }

            @Override
            public void onError(FacebookException error) {
                Log.d( "TAG", "facebook:onError", error );
            }
        } );

        findViewById( R.id.textView9 ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SignIn.this,SignUp.class );
                startActivity( intent );
            }
        } );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        mCallbackManager.onActivityResult( requestCode, resultCode, data );
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( data );
            try {
                GoogleSignInAccount account = task.getResult( ApiException.class );
                firebaseAuthWithGoogle( account );
            } catch (ApiException e) {
                Log.w( "TAG", "Google sign in failed", e );
            }
        }
    }

    private void updateUI() {
        Intent intent = new Intent( SignIn.this, SelectionScreen.class );
        startActivity( intent );
        finish();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d( "TAG", "handleFacebookAccessToken:" + token );

        AuthCredential credential = FacebookAuthProvider.getCredential( token.getToken() );
        firebaseAuth.signInWithCredential( credential )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d( "TAG", "signInWithCredential:success" );
                            updateUI();
                        } else {
                            Log.w( "TAG", "signInWithCredential:failure", task.getException() );
                            Toast.makeText( SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult( signInIntent, RC_SIGN_IN );
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d( "TAG", "firebaseAuthWithGoogle:" + acct.getId() );

        AuthCredential credential = GoogleAuthProvider.getCredential( acct.getIdToken(), null );
        firebaseAuth.signInWithCredential( credential )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d( "TAG", "signInWithCredential:success" );
                            updateUI();
                        } else {
                            Log.w( "TAG", "signInWithCredential:failure", task.getException() );
                            Toast.makeText( SignIn.this, "Failed", Toast.LENGTH_SHORT ).show();
                        }

                    }
                } );
    }
}
