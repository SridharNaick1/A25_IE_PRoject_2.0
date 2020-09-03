package com.example.bottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public class ReportPopUpClass {

    private FirebaseFirestore db;
    PopupWindow popupWindow;
    String userEmail;
    //first page
    Set<String> dateSet;
    Multimap<String,Double> CarbonMultimap;
    TreeMap<Double,String> calculatedList;
    Double highestCarbon;
    Double lowestCarbon;
    String highestDate;
    String lowestDate;

    //second page
    Multimap<String,Double> applianceMultimap;
    Multimap<String,Double> foodDateMultimap;
    Multimap<String,Double> householdMultimap;
    Multimap<String,Double> transportationMultimap;
    TreeMap<Double,String> applianceCalculatedList;
    TreeMap<Double,String> foodCalculatedList;
    TreeMap<Double,String> householdCalculatedList;
    TreeMap<Double,String> transportationCalculatedList;
    final static String APPLIANCE_TYPE =  "appliance";
    final static String FOOD_TYPE =  "food";
    final static String HOUSEHOLD_TYPE =  "household";
    final static String TRANSPORTATION_TYPE =  "transportation";


    @SuppressLint("SetTextI18n")
    public void popUpReportFirstPage(final View view, final Context context) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_report_first_page, null);
        CardView mCardViewTop;
        mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));
        db = FirebaseFirestore.getInstance();
        //get user email
        SharedPreferences sharedPref = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail","");

        fetchCloudStore(popupView);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;
        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button nextBtn = popupView.findViewById(R.id.firstPageNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                popUpReportSecondPage(view,context);
            }
        });

//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                //Close the window when clicked
//                popupWindow.dismiss();
//                return true;
//            }
//        });


    }

    @SuppressLint("SetTextI18n")
    public void popUpReportSecondPage(final View view, final Context context) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_report_second_page, null);
        CardView mCardViewTop;
        mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));
        db = FirebaseFirestore.getInstance();
        //get user email
        SharedPreferences sharedPref = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail","");


        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;
        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        fetchSecondCloudStore(context,popupView);

        Button nextBtn = popupView.findViewById(R.id.secondPageNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                //Close the window when clicked
//                popupWindow.dismiss();
//                return true;
//            }
//        });


    }




    public void fetchCloudStore( final View popupView){
        CollectionReference usersCollectionRef = db.collection(userEmail);
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dateSet = new HashSet<>();
                            CarbonMultimap = ArrayListMultimap.create();
                            calculatedList = new TreeMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserCarbonEmission userCarbonEmission =document.toObject(UserCarbonEmission.class);
                                CarbonMultimap.put(userCarbonEmission.getRecordDate(),userCarbonEmission.getCarbon());
                                dateSet.add(userCarbonEmission.getRecordDate());
                            }
                            List<String> dateList = new ArrayList<String>(dateSet);
                            for(String recordDate:dateSet){
                                Double carbonTotalForThatDate = 0.0;
                                for(Double multiCarbon:CarbonMultimap.get(recordDate)){
                                    carbonTotalForThatDate += multiCarbon;
                                }
                                calculatedList.put(carbonTotalForThatDate,recordDate);
                            }
                            highestCarbon = calculatedList.lastKey();
                            highestDate = calculatedList.get(highestCarbon);
                            highestCarbon = Math.round(highestCarbon*1000)/1000.0;
                            lowestCarbon = calculatedList.firstKey();
                            lowestDate = calculatedList.get(lowestCarbon);
                            lowestCarbon = Math.round(lowestCarbon*1000)/1000.0;

                            TextView mostCarbonDate = popupView.findViewById(R.id.Most_carbon_date);
                            mostCarbonDate.setText(highestDate);
                            TextView mostCarbonMessage = popupView.findViewById(R.id.most_carbon_day);
                            mostCarbonMessage.setText("Highest carbon emission");
                            ImageView imageView = popupView.findViewById(R.id.largeCarbon);
                            imageView.setImageResource(R.drawable.sad);
                            TextView mostCarbonDayValue = popupView.findViewById(R.id.most_carbon_day_value);
                            mostCarbonDayValue.setText(highestCarbon.toString() + " KG");

                            TextView lowestCarbonDate = popupView.findViewById(R.id.lowest_carbon_date);
                            lowestCarbonDate.setText(lowestDate);
                            TextView lowestCarbonMessage = popupView.findViewById(R.id.lowest_carbon_day);
                            lowestCarbonMessage.setText("Lowest carbon emission");
                            ImageView secondImageView = popupView.findViewById(R.id.lowCarbon);
                            secondImageView.setImageResource(R.drawable.happy);
                            TextView lowestCarbonDayValue = popupView.findViewById(R.id.lowest_carbon_day_value);
                            lowestCarbonDayValue.setText(lowestCarbon.toString() + " KG");
                        }
                    }
                });
    }

    public void fetchSecondCloudStore(final Context context, final View popupView){
        CollectionReference usersCollectionRef = db.collection(userEmail);
        List<UserCarbonEmission> userList = new ArrayList<>();

        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //set up lists
                            applianceMultimap = ArrayListMultimap.create();
                            foodDateMultimap = ArrayListMultimap.create();
                            householdMultimap = ArrayListMultimap.create();
                            transportationMultimap = ArrayListMultimap.create();
                            applianceCalculatedList = new TreeMap<>();
                            foodCalculatedList = new TreeMap<>();
                            householdCalculatedList = new TreeMap<>();
                            transportationCalculatedList = new TreeMap<>();

                            //fetch data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserCarbonEmission userCarbonEmission =document.toObject(UserCarbonEmission.class);
                                //put date and carbon in multi map
                                if(userCarbonEmission.getType().equals(APPLIANCE_TYPE)){
                                    applianceMultimap.put(userCarbonEmission.getRecordDate(),userCarbonEmission.getCarbon());
                                }else if(userCarbonEmission.getType().equals(FOOD_TYPE)){
                                    foodDateMultimap.put(userCarbonEmission.getRecordDate(),userCarbonEmission.getCarbon());
                                }else if(userCarbonEmission.getType().equals(HOUSEHOLD_TYPE)){
                                    householdMultimap.put(userCarbonEmission.getRecordDate(),userCarbonEmission.getCarbon());
                                }else if(userCarbonEmission.getType().equals(TRANSPORTATION_TYPE)){
                                    transportationMultimap.put(userCarbonEmission.getRecordDate(),userCarbonEmission.getCarbon());
                                }
                            }
                            //clean carbon value for each category
                            if(applianceMultimap.size() != 0){
                                for(String applianceRecordDate:applianceMultimap.keySet()){
                                    Double carbonTotalForThatDate = 0.0;
                                    for(Double multiCarbon:applianceMultimap.get(applianceRecordDate)){
                                        carbonTotalForThatDate += multiCarbon;
                                    }
                                    applianceCalculatedList.put(carbonTotalForThatDate,applianceRecordDate);
                                    Double highestCarbon = applianceCalculatedList.lastKey();
                                    String highestDate = applianceCalculatedList.get(highestCarbon);
                                    highestCarbon = Math.round(highestCarbon*1000)/1000.0;
                                    ImageView imageView = popupView.findViewById(R.id.applianceOption);
                                    imageView.setImageResource(R.drawable.appliance_report);
                                    TextView applianceTile = popupView.findViewById(R.id.appliance_title);
                                    applianceTile.setText("Appliance" );
                                    TextView applianceDate = popupView.findViewById(R.id.highest_appliance);
                                    applianceDate.setText(highestDate +"  "+ highestCarbon.toString() + " KG");
                                    String uri = "@drawable/largecarbon";




                                }
                            }
                            if(transportationMultimap.size() != 0){
                                for(String transportationRecordDate:transportationMultimap.keySet()){
                                    Double carbonTotalForThatDate = 0.0;
                                    for(Double multiCarbon:transportationMultimap.get(transportationRecordDate)){
                                        carbonTotalForThatDate += multiCarbon;
                                    }
                                    transportationCalculatedList.put(carbonTotalForThatDate,transportationRecordDate);
                                    Double highestCarbon = transportationCalculatedList.lastKey();
                                    String highestDate = transportationCalculatedList.get(highestCarbon);
                                    highestCarbon = Math.round(highestCarbon*1000)/1000.0;
                                    ImageView imageView = popupView.findViewById(R.id.transportationOption);
                                    imageView.setImageResource(R.drawable.transportation_report);
                                    TextView transportationTile = popupView.findViewById(R.id.transportation_title);
                                    transportationTile.setText("Transportation" );
                                    TextView transportationDate = popupView.findViewById(R.id.highest_transportation);
                                    transportationDate.setText(highestDate +"  "+ highestCarbon.toString() + " KG");
                                }
                            }
                            if(foodDateMultimap.size() != 0){
                                for(String foodRecordDate:foodDateMultimap.keySet()){
                                    Double carbonTotalForThatDate = 0.0;
                                    for(Double multiCarbon:foodDateMultimap.get(foodRecordDate)){
                                        carbonTotalForThatDate += multiCarbon;
                                    }
                                    foodCalculatedList.put(carbonTotalForThatDate,foodRecordDate);
                                    Double highestCarbon = foodCalculatedList.lastKey();
                                    String highestDate = foodCalculatedList.get(highestCarbon);
                                    highestCarbon = Math.round(highestCarbon*1000)/1000.0;
                                    ImageView imageView = popupView.findViewById(R.id.foodOption);
                                    imageView.setImageResource(R.drawable.food_report);
                                    TextView foodTile = popupView.findViewById(R.id.food_title);
                                    foodTile.setText("Food" );
                                    TextView foodDate = popupView.findViewById(R.id.highest_food);
                                    foodDate.setText(highestDate +"  "+ highestCarbon.toString() + " KG");
                                }
                            }
                            if(householdMultimap.size() != 0){
                                for(String transportationRecordDate:householdMultimap.keySet()){
                                    Double carbonTotalForThatDate = 0.0;
                                    for(Double multiCarbon:householdMultimap.get(transportationRecordDate)){
                                        carbonTotalForThatDate += multiCarbon;
                                    }
                                    householdCalculatedList.put(carbonTotalForThatDate,transportationRecordDate);
                                    Double highestCarbon = householdCalculatedList.lastKey();
                                    String highestDate = householdCalculatedList.get(highestCarbon);
                                    highestCarbon = Math.round(highestCarbon*1000)/1000.0;
                                    ImageView imageView = popupView.findViewById(R.id.householdOption);
                                    imageView.setImageResource(R.drawable.household_report);
                                    TextView householdTile = popupView.findViewById(R.id.household_title);
                                    householdTile.setText("Household" );
                                    TextView householdDate = popupView.findViewById(R.id.highest_household);
                                    householdDate.setText(highestDate +"  "+ highestCarbon.toString() + " KG");
                                }
                            }



                        }
                    }
                });
    }
}
