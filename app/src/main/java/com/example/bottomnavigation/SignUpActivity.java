package com.example.bottomnavigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.android.gms.vision.L.TAG;

public class SignUpActivity extends Activity {
    private FirebaseFirestore db;
    final static String USER_CREATED = "Your account is created. Now you can log in.";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        db = FirebaseFirestore.getInstance();


        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               validateInput();
            }
        });
    }

    private boolean validateInput() {
        EditText editUsername = findViewById(R.id.userName);
        EditText editEmail = findViewById(R.id.userEmail);
        EditText editPassword = findViewById(R.id.userPassword);
        EditText editConfirmPassword = findViewById(R.id.userConfirmPassword);
        String userName = editUsername.getText().toString();
        String userEmail = editEmail.getText().toString();
        String userPassword = editPassword.getText().toString();
        String userConfirmPassword = editConfirmPassword.getText().toString();
        TextView validationText = findViewById(R.id.signUpValidation);
        if(userName.length() == 0){
            validationText.setText(R.string.enter_your_name);
            return false;
        }else if(!isValidEmail(userEmail)){
            validationText.setText(R.string.enter_your_email);
            return false;
        }else if(userPassword.length() <= 6){
            validationText.setText(R.string.password_length);
            return false;
        }else if(!userPassword.equals(userConfirmPassword) ){
            validationText.setText(R.string.password_confirm);
            return false;
        }
        validationText.setText("");
        UserDetail userDetail = new UserDetail(userName,userEmail,userPassword);
        checkUserExist(userDetail);
        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void checkUserExist(final UserDetail userDetail){
        try{
            DocumentReference usersDocumentationRef = db.collection("UserInfo").document(userDetail.getUserEmail());
            usersDocumentationRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                UserDetail userDetailShort = document.toObject(UserDetail.class);
                                if(userDetailShort==null){
                                    createUser(userDetail);
                                }else{
                                    TextView validationText = findViewById(R.id.signUpValidation);
                                    validationText.setText(R.string.user_exist);
                                }
                            }
                        }
                    });

        }catch(Exception e){
        }
    }

    public void createUser(UserDetail userDetail){
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", userDetail.getUserName());
        userInfo.put("userEmail", userDetail.getUserEmail());
        userInfo.put("userPassword", userDetail.getUserPassword());

        db.collection("UserInfo").document(userDetail.getUserEmail())
                .set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        popUpUserCreated(getCurrentFocus(),USER_CREATED,getApplicationContext());
                        //startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void popUpUserCreated(final View view,String message,final Context context) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_confirm_update_emission, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        TextView popupMessage = popupView.findViewById(R.id.confirm_content);
        popupMessage.setText(message);
        Button confirmBtn = popupView.findViewById(R.id.confirmButton);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}
