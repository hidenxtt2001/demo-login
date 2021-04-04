
package com.example.loginlayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class MainActivity extends AppCompatActivity {

    ViewPager2 introView;
    DotsIndicator dotsIndicator;
    int [] images = {R.drawable.wolf_icon_white,R.drawable.wolf_icon_white,R.drawable.wolf_icon_white};
    IntroAddapter introAddapter;
    private FirebaseAuth mAuth;

    // This is what you do

    // Google Value
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;

    //Facebook Value
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Config Intro
        SetupIntro();

        // Google Setup
        mAuth = FirebaseAuth.getInstance();

        // Facebook Setup
        mCallbackManager = CallbackManager.Factory.create();
        // Setup Login
        SetupGoogleLogin();
        SetupFacebookLogin();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void SetupIntro(){
        // Initialize Viewpager2
        introView = findViewById(R.id.introScroll);
        // Indicate DotIndicate
        dotsIndicator = findViewById(R.id.dotChangeIntro);

        introAddapter = new IntroAddapter(images);

        introView.setAdapter(introAddapter);

        dotsIndicator.setViewPager2(introView);
    }

    //region Facebook Login
    public void LoginFacebook_Click(View view) {
        LoginButton loginButton =findViewById(R.id.loginFacebookReal);
        loginButton.performClick();
    }

    private void SetupFacebookLogin(){
        // Initialize Facebook Login button
        LoginButton loginButton = findViewById(R.id.loginFacebookReal);
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }
    //endregion
    //region Login Gmail
    public void LoginGmail_Click(View view) {
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void SetupGoogleLogin(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }
    //endregion
}