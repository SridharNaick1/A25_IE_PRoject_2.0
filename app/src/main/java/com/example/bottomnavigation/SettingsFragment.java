package com.example.bottomnavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.net.URI;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    View vSettingsUnit = null;
    GoogleSignInClient mGoogleSignInClient;
    Button sign_out;
    TextView emailTextView;
    ImageView photoImageView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vSettingsUnit = inflater.inflate(R.layout.fragment_settings, container, false);
        sign_out = vSettingsUnit.findViewById(R.id.settingsSignOutBtn);
//        photoImageView = vSettingsUnit.findViewById(R.id.googlePhoto);
//        emailTextView = vSettingsUnit.findViewById(R.id.googleEmail);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            //emailTextView.setText("Email: "+personEmail);
           // Glide.with(this).load(personPhoto).into(photoImageView);
        }

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });




        Button checkMonthlyReport = vSettingsUnit.findViewById(R.id.monthlyReportBtn);
        checkMonthlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportPopUpClass reportPopUpClass = new ReportPopUpClass();
                reportPopUpClass.popUpReportFirstPage(vSettingsUnit,vSettingsUnit.getContext());
            }
        });

        Button userInfoBtn = vSettingsUnit.findViewById(R.id.userInfo);
        userInfoBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                    PopUpClass popUpClass = new PopUpClass();
                    popUpClass.showUserInfoWindow(vSettingsUnit,vSettingsUnit.getContext());
            }
        });




        return vSettingsUnit;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userEmail", "");
                        editor.apply();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
    }




}
