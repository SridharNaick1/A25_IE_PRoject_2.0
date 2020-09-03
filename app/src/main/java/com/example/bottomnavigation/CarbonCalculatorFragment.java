package com.example.bottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CarbonCalculatorFragment extends Fragment {
    private View vAppliancesUnit;
    private PieChart pieChart;
    private FirebaseFirestore db;
    private String recordDate = "";
    private Double databaseCarbon ;
    private List<UserCarbonEmission> list;
    private String userEmail;
    ListView emissionItemList;
    TimePicker simpleTimePicker;
    private Double applianceValue = 0.0;
    private Double foodValue = 0.0;
    private Double houseHoldValue = 0.0;
    private Double transportationValue = 0.0;
    private ArrayList<PieEntry> entries = new ArrayList<>(); ;
    boolean fetchFinish = false;

    Double carbonForUpdate = 0.001;
    Double efFactorForUpdate;

    final static String CATEGORY_MESSAGE = "Successfully added to your monthly usage. View the updated usage in your home page.";


    View view;
    String categoryForUpdate;
    String typeForUpdate;
    String recordDateForUpdate;

    int timeHours;
    int timeMinutes;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        vAppliancesUnit = inflater.inflate(R.layout.fragment_carbon_calculator, container, false);
        //applianceName = vAppliancesUnit.findViewById(R.id.applianceTitle);

        pieChart = vAppliancesUnit.findViewById(R.id.pieChart);
        pieChart.setNoDataText("Loading...");
        databaseCarbon = 0.0;

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail","");

        //fetch date
        fetchCloudStore();

        //set pie chart
        SetSecondPieChart setSecondPieChart = new SetSecondPieChart();
        setSecondPieChart.execute();

        //get current date
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.valueOf(currentDate.getMonthValue());
        String day = String.valueOf(currentDate.getDayOfMonth());
        recordDate = year + month + day;

        //Appliance Option
        Button applianceOptionBtn = vAppliancesUnit.findViewById(R.id.applianceOption);
        applianceOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnUserCarbonEmissionList("appliance");
            }
        });

        //Food Option
        Button foodOptionBtn = vAppliancesUnit.findViewById(R.id.foodOption);
        foodOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnUserCarbonEmissionList("food");
            }
        });

        //Household Option
        Button householdOptionBtn = vAppliancesUnit.findViewById(R.id.householdOption);
        householdOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnUserCarbonEmissionList("household");
            }
        });

        //Transportation Option
        Button transportationOptionBtn = vAppliancesUnit.findViewById(R.id.transportationOption);
        transportationOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnUserCarbonEmissionList("transportation");
            }
        });


        return vAppliancesUnit;
    }

    public void returnUserCarbonEmissionList(final String category){
        final String type = category;
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        final Date dateobj = new Date();
        final String date = df.format(dateobj);

        list= new ArrayList<>();
        DocumentReference itemDf = db.collection( "carbonFootprint").document(category);
        itemDf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> map = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        UserCarbonEmission tempItem = new UserCarbonEmission(userEmail,date,
                                type,entry.getKey(),Double.valueOf(entry.getValue().toString()));
                        list.add(tempItem);
                    }
                    //PopUpClass popUpClass = new PopUpClass();
                    //popUpClass.popupCarbonEmissionItem(vAppliancesUnit,category,list);
                    popupCarbonEmissionItem(vAppliancesUnit,category,list);
                }
                else{
                    Toast.makeText(vAppliancesUnit.getContext(), "fail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setEntries(Double value, String type){
        if(value != 0.0){
            entries.add(new PieEntry(value.floatValue() , type));
        }
    }

    private class SetSecondPieChart extends AsyncTask<Void, Void, ArrayList> {
        @Override
        protected ArrayList doInBackground(Void... params) {
            do{
                System.out.println("try again");
            }while(!fetchFinish);

            entries.clear();
            setEntries(applianceValue,"Appliance: " + applianceValue.toString() + "KG");
            setEntries(foodValue,"Food: " + foodValue.toString() + "KG");
            setEntries(houseHoldValue,"Household: " + houseHoldValue.toString() + "KG");
            setEntries(transportationValue,"Transportation: " + transportationValue.toString() + "KG");


            return entries;
        }
        @Override
        protected void onPostExecute(ArrayList details) {
            PieDataSet pieDataSet = new PieDataSet(details," ");
            pieDataSet.setColors(Color.parseColor("#2a9d8f"),
                    Color.parseColor("#543864"),
                    Color.parseColor("#ff6363"),
                    Color.parseColor("#ffbd69"));
            PieData pieData = new PieData(pieDataSet);
            pieData.setDrawValues(false);
//            pieData.setValueTextSize(20f);
//            pieData.setValueTextColor(Color.parseColor("#f8f3eb"));
            pieChart.setData(pieData);
            pieChart.setEntryLabelColor(Color.parseColor("#272727"));
            pieChart.getDescription().setEnabled(false);
//            pieChart.getDescription().setText("Unit KG");
//            pieChart.getDescription().setTextColor(Color.parseColor("#f8f3eb"));
//            pieChart.getDescription().setTextSize(15f);
            //pieChart.setCenterTextSize(30f);
            //pieChart.animateXY(5000, 5000);
            pieChart.invalidate();
            pieChart.setHoleRadius(0f);
            Legend legend = pieChart.getLegend();
            legend.setEnabled(true);
            legend.setTextSize(15f);
            legend.setTextColor(Color.parseColor("#272727"));
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            pieChart.setDrawEntryLabels(false);
            pieChart.notifyDataSetChanged();
            pieChart.setDrawEntryLabels(false);
        }
    }

    public void fetchCloudStore(){
        applianceValue = 0.0;
        foodValue = 0.0;
        houseHoldValue = 0.0;
        transportationValue = 0.0;
        fetchFinish = false;
        CollectionReference usersCollectionRef = db.collection(userEmail);
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserCarbonEmission userCarbonEmission =document.toObject(UserCarbonEmission.class);
                                if(userCarbonEmission.getType().equals("appliance") ){
                                    applianceValue += userCarbonEmission.getCarbon() ;
                                    applianceValue = Math.round(applianceValue*1000)/1000.0;
                                }
                                else if(userCarbonEmission.getType().equals("food") ){
                                    foodValue += userCarbonEmission.getCarbon();
                                    foodValue = Math.round(foodValue*1000)/1000.0;
                                }
                                else if(userCarbonEmission.getType().equals("household") ){
                                    houseHoldValue += userCarbonEmission.getCarbon();
                                    houseHoldValue = Math.round(houseHoldValue*1000)/1000.0;
                                }
                                else if(userCarbonEmission.getType().equals("transportation")  ){
                                    transportationValue += userCarbonEmission.getCarbon();
                                    transportationValue = Math.round(transportationValue*1000)/1000.0;
                                }
                            }
                            fetchFinish = true;
                        }
                    }
                });

    }

    public void popupCarbonEmissionItem(final View view, final String category, final List<UserCarbonEmission> userCarbonEmissionList) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_carbon_emission_item, null);
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;
        //Create a window with our parameters
        final PopupWindow popupWindow;

        popupWindow = new PopupWindow(popupView, width, height, focusable);
        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));
        //title
        TextView titleTextView = popupView.findViewById(R.id.titleText);
        if(category.equals("food")){
            titleTextView.setText("Food");
        }else if(category.equals("appliance")){
            titleTextView.setText("Appliance");
        }else if(category.equals("household")){
            titleTextView.setText("Household");
        }else{
            titleTextView.setText("Transportation");
        }
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        UserCarbonEmissionAdapter adapter = new UserCarbonEmissionAdapter(popupView.getContext(),
                R.layout.carbon_emission_item, userCarbonEmissionList);
        emissionItemList = (ListView) popupView.findViewById(R.id.list_view);
        emissionItemList.setAdapter(adapter);
        emissionItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserCarbonEmission userCarbonEmission = (UserCarbonEmission) parent.getItemAtPosition(position);
                popupWindow.dismiss();
                if(category.equals("food") || category.equals("household")){
//                    PopUpClass popUpClass = new PopUpClass();
//                    popUpClass.showFoodAndHouseholdInput(view,userCarbonEmission,category);
                    showFoodAndHouseholdInput(view,userCarbonEmission,category);
                }else{
//                    PopUpClass popUpClass = new PopUpClass();
//                    popUpClass.showUserInputWindow(view,userCarbonEmission,category);
                    showUserInputWindow(view,userCarbonEmission,category);

                }

            }
        });
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
    public void showUserInputWindow(final View newView, final UserCarbonEmission userCarbonEmission,String category) {


        view = newView;
        categoryForUpdate = userCarbonEmission.getCarbonName();
        typeForUpdate = userCarbonEmission.getType();
        efFactorForUpdate = userCarbonEmission.getCarbon();
        recordDateForUpdate = userCarbonEmission.getRecordDate();
        timeHours = 0;
        timeMinutes = 0;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) newView.getContext().getSystemService(newView.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_calculator_input, null);
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;
        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));

        //Create a window with our parameters
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(newView, Gravity.CENTER, 0, 0);
        //Initialize the elements of our window, install the handler
        TextView title = popupView.findViewById(R.id.titleText);
        title.setText(typeForUpdate);
        TextView titleTextView = popupView.findViewById(R.id.titleText);
        if(category.equals("appliance")){
            titleTextView.setText("Appliance");
        }else{
            titleTextView.setText("Transportation");
        }
        TextView hint = popupView.findViewById(R.id.hintForInput);
        //set time picker
        simpleTimePicker = (TimePicker)popupView.findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setIs24HourView(true);
        simpleTimePicker.setHour(0);
        simpleTimePicker.setMinute(0);
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timeHours = hourOfDay;
                timeMinutes = minute;
            }
        });
        //set hint
        hint.setText("Please choose usage(hours)");



        Button updatePopupInput = popupView.findViewById(R.id.update_popup_input_button);
        updatePopupInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carbonForUpdate = Double.valueOf(timeHours + timeMinutes/60) * efFactorForUpdate;
                userCarbonEmission.setCarbon(carbonForUpdate);
                if( carbonForUpdate != 0.001 && (timeMinutes != 0 || timeHours != 0)){
                    insertData(userCarbonEmission);
                    //   Toast.makeText(newView.getContext(), "success",
                    //         Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                    popUpConfirmation(view,CATEGORY_MESSAGE);
                }
                else {
                    Toast.makeText(newView.getContext(), "please select one value from the list",
                            Toast.LENGTH_SHORT).show();
                }
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

    public void insertData(UserCarbonEmission userCarbonEmission){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String recordDate = userCarbonEmission.getRecordDate();
        Map<String, Object> docData = new HashMap<>();
        docData.put("userEmail",userCarbonEmission.getUserEmail());
        docData.put("recordDate", recordDate);
        docData.put("type", userCarbonEmission.getType());
        docData.put("carbonName", userCarbonEmission.getCarbonName());
        docData.put("carbon", userCarbonEmission.getCarbon());

        String userEmail = userCarbonEmission.getUserEmail();

        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollectionRef = db.collection(userEmail);
        usersCollectionRef.document()
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void showFoodAndHouseholdInput(final View newView, final UserCarbonEmission userCarbonEmission, String category) {
        view = newView;
        categoryForUpdate = userCarbonEmission.getCarbonName();
        typeForUpdate = userCarbonEmission.getType();
        efFactorForUpdate = userCarbonEmission.getCarbon();
        recordDateForUpdate = userCarbonEmission.getRecordDate();

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) newView.getContext().getSystemService(newView.getContext().LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_calculator_input_food, null);
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;
        CardView mCardViewTop = popupView.findViewById(R.id.cardView);
        mCardViewTop.setRadius(30);
        mCardViewTop.setCardBackgroundColor(Color.parseColor("#f8f3eb"));

        //Create a window with our parameters
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(newView, Gravity.CENTER, 0, 0);
        //Initialize the elements of our window, install the handler
        TextView titleTextView = popupView.findViewById(R.id.titleText);
        if(category.equals("food")){
            titleTextView.setText("Food");
        }else if(category.equals("household")){
            titleTextView.setText("Household");
        }
        TextView hint = popupView.findViewById(R.id.hintForInput);
        if(category.equals("food")){
            hint.setText("Please enter consumption(KG)");
        }else{
            hint.setText("Please enter consumption(KWh)");
        }

        Button updatePopupInput = popupView.findViewById(R.id.update_popup_input_button);
        updatePopupInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                TextView validationTextView = popupView.findViewById(R.id.validation);
                EditText foodInputEditText = popupView.findViewById(R.id.foodInputEditText);
                String foodInputString = foodInputEditText.getText().toString();
                try{
                    Double actualInputDouble = Double.valueOf(foodInputString);
                    if(actualInputDouble > 0.0){
                        carbonForUpdate = actualInputDouble * efFactorForUpdate;
                        userCarbonEmission.setCarbon(carbonForUpdate);
                        if(carbonForUpdate != 0.001){
                            insertData(userCarbonEmission);
                            Toast.makeText(newView.getContext(), "success",
                                    Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                            popUpConfirmation(view,CATEGORY_MESSAGE);
                        }
                    }else{
                        validationTextView.setText("Consumption must grater than 0");
                    }

                }catch(Exception e){
                    validationTextView.setText("Please enter the valid consumption");
                }
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
                //fetch date
                fetchCloudStore();

                //set pie chart
                SetSecondPieChart setSecondPieChart = new SetSecondPieChart();
                setSecondPieChart.execute();
            }
        });
    }

    public class UserCarbonEmissionAdapter extends ArrayAdapter<UserCarbonEmission> {
        private int resourceId;

        public UserCarbonEmissionAdapter(Context context,
                                         int textViewResourceId,
                                         List<UserCarbonEmission> objects){
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserCarbonEmission userCarbonEmission = getItem(position);
            View view;
            UserCarbonEmissionAdapter.ViewHolder viewHolder;
            if (convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);

                viewHolder = new UserCarbonEmissionAdapter.ViewHolder();
                viewHolder.itemName = (TextView) view.findViewById(R.id.item_name);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (UserCarbonEmissionAdapter.ViewHolder) view.getTag();
            }
            viewHolder.itemName.setText(userCarbonEmission.getCarbonName());
            return view;
        }

        class ViewHolder{
            TextView itemName;
        }
    }
}
