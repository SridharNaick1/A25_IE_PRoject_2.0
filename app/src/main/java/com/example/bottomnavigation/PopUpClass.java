package com.example.bottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class PopUpClass {
    //PopupWindow display method


    FirebaseFirestore db;
    View view;
    PopupWindow popupWindow;
    final static String COLLECTION_MESSAGE = "You will be notified the day before collection day.";

    int days;

    @SuppressLint("SetTextI18n")
    public void showPopupWindow(final View view, String category) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_classification, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;


        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        TextView title = popupView.findViewById(R.id.titleText);
        ImageView recycleImageView = popupView.findViewById(R.id.recyclableImageView);
        if(category.equals("Yellow_lid")){
            title.setText("Recycle this waste. Dump it into recycle bin with yellow lid.");
            recycleImageView.setImageDrawable(popupView.getResources().getDrawable(R.drawable.recycle_bin));
        }
        else if(category.equals("Green_lid")){
            title.setText("Throw the waste in dust bin with green lid.");
            recycleImageView.setImageDrawable(popupView.getResources().getDrawable(R.drawable.food_wate_green_bin));
        }
        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }






    public void popUpConfirmation(final View view,String message) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_confirm_update_emission, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
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

            }
        });
    }







    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    public void showSetCollectionDayWindow(final View newView, final Context context) {
        view = newView;

        days = 0;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) newView.getContext().getSystemService(newView.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_set_collection_day, null);
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));
        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(newView, Gravity.CENTER, 0, 0);
        //Initialize the elements of our window, install the handler


        NumberPicker np = popupView.findViewById(R.id.numberPicker);

        String[] list = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        np.setMinValue(0);
        np.setMaxValue(list.length - 1);
        np.setDisplayedValues(list);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                days = numberPicker.getValue();
            }
        });

        Button confirmBtn = popupView.findViewById(R.id.update_collection_day_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SharedPreferences sharedPref = context.getSharedPreferences("collectionDay",Context.MODE_PRIVATE);
               SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("CollectionDays", days);
                editor.commit();
                popupWindow.dismiss();

                popUpConfirmation(view,COLLECTION_MESSAGE);
            }
        });

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    public void showUserInfoWindow(final View newView,final Context context) {
        view = newView;

        days = 0;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) newView.getContext().getSystemService(newView.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_user_info, null);
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));
        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(newView, Gravity.CENTER, 0, 0);
        //Initialize the elements of our window, install the handler

        SharedPreferences sharedPref = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("userEmail","");
        String userName = sharedPref.getString("userName","");

        TextView userDisplayNameText = popupView.findViewById(R.id.userDisplayName);
        userDisplayNameText.setText(userName);
        TextView userEmailText = popupView.findViewById(R.id.userEmail);
        userEmailText.setText(userEmail);

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    }

