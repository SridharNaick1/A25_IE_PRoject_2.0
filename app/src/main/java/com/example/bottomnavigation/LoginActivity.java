package com.example.bottomnavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
//    int RC_SIGN_IN = 0;
//    GoogleSignInClient mGoogleSignInClient;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_signin);
        db = FirebaseFirestore.getInstance();
        checkStatus();


//        // Configure sign-in to request the user's ID, email address, and basic
//        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//

//        Button signInButton = findViewById(R.id.googleSignInBtn);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });

        Button signUpButton = findViewById(R.id.signUpBtn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        Button loginButton = findViewById(R.id.logInBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }


        });



    }

    private void checkStatus() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("userEmail","");
        if(!userEmail.equals("")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

    }


    public void signUp(){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public boolean login(){
        EditText userEmailEdit = findViewById(R.id.userEmail);
        final String userEmail = userEmailEdit.getText().toString();
        EditText userPasswordEdit = findViewById(R.id.userPassword);
        final String userPassword = userPasswordEdit.getText().toString();
        TextView validationText = findViewById(R.id.signUpValidation);
        if(userEmail == null || userEmail.length() == 0){
            validationText.setText(R.string.user_info_not_correct);
            return false;
        }else if(!isValidEmail(userEmail)){
            validationText.setText(R.string.user_info_not_correct);
            return false;
        }else if(userPasswordEdit == null || userPasswordEdit.length() == 0){
            validationText.setText(R.string.user_info_not_correct);
            return false;
        }

        DocumentReference usersDocumentationRef = db.collection("UserInfo").document(userEmail);
        usersDocumentationRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            UserDetail userDetailShort = document.toObject(UserDetail.class);
                            if(userDetailShort==null){
                                TextView validationText = findViewById(R.id.signUpValidation);
                                validationText.setText(R.string.user_info_not_correct);
                            }else if(!userDetailShort.getUserPassword().equals(userPassword)){
                                TextView validationText = findViewById(R.id.signUpValidation);
                                validationText.setText(R.string.user_info_not_correct);
                            }else{
                                SharedPreferences sharedPref = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userEmail", userDetailShort.getUserEmail());
                                editor.putString("userName", userDetailShort.getUserName());
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    }
                });

        return true;
    }




}
