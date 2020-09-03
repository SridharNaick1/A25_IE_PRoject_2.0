package com.example.bottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class HomeFragment extends Fragment {
    private View vMainUnit = null;
    private TextView tv_time = null;
    private TextView userName = null;
    private FirebaseFirestore db;
    private String userEmail;

    private Double actualUsage = 0.0 ;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean fetchFinish;

    private PieChart reportPieChart;
    private Typeface typeface;

    private static final int msgKey1 = 1;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        vMainUnit = inflater.inflate(R.layout.fragment_home, container, false);
        reportPieChart = vMainUnit.findViewById(R.id.reportPieChart);
        reportPieChart.setNoDataText("Loading...");
        userName = vMainUnit.findViewById(R.id.enterUsername);
        db = FirebaseFirestore.getInstance();
        vMainUnit.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        typeface = getResources().getFont(R.font.segoe2);


        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail","");
        userName.setText("Hey " + sharedPref.getString("userName",""));



        //fetch data
        fetchFinish = false;
        fetchCloudStore();

        //time
        new TimeThread().start();


        //set pie chart
        SetFirstPieChart setFirstPieChart = new SetFirstPieChart();
        setFirstPieChart.execute();

        //set collection day button
        Button setCollectionBtn = vMainUnit.findViewById(R.id.setCollectionDayBtn);
        setCollectionBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showSetCollectionDayWindow(vMainUnit,vMainUnit.getContext());
            }
        });



        return vMainUnit;
    }

    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy EEE");
                 //   tv_time.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };


    //set first pie chart
    private class SetFirstPieChart extends AsyncTask<Void, Void, Double> {
        @Override
        protected Double doInBackground(Void... params) {
            do{

            }while(!fetchFinish);
            return actualUsage;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Double details) {
            details = Math.round(details*1000)/1000.0;
            ArrayList<PieEntry> entries = new ArrayList<>();
            Double actualUsageValue = details;
            Double monthlyValue = 1500.0;
            PieDataSet pieDataSet;
            //entries.clear();
            if(details < monthlyValue){
                Double weeklyRemain = monthlyValue - actualUsageValue;
                entries.add(new PieEntry(actualUsageValue.floatValue(), "Actual emissions"));
                entries.add(new PieEntry(weeklyRemain.floatValue(), "The remaining emissions this week"));
                pieDataSet = new PieDataSet(entries," ");
                pieDataSet.setColors(Color.parseColor("#f19292"), Color.parseColor("#FFE2E4E6"));
            }else{
                entries.add(new PieEntry(actualUsageValue.floatValue(), "Excessive emissions"));
                entries.add(new PieEntry(0f, "Weekly Limit"));
                pieDataSet = new PieDataSet(entries," ");
                pieDataSet.setColors(Color.parseColor("#f19292"), Color.parseColor("#FFE2E4E6"));
            }

            PieData pieData = new PieData(pieDataSet);
            pieData.setValueTextSize(15f);
            pieData.setDrawValues(false);
            reportPieChart.setData(pieData);
            reportPieChart.setEntryLabelColor(Color.rgb(0,0,0));
//            reportPieChart.getDescription().setText("Unit KG");
            reportPieChart.setCenterTextSize(20f);
            //reportPieChart.animateXY(5000, 5000);
            reportPieChart.getDescription().setEnabled(false);
            reportPieChart.setHoleRadius(90f);
            reportPieChart.getLegend().setEnabled(false);
            reportPieChart.setDrawEntryLabels(false);
            reportPieChart.setRotationEnabled(false);
            reportPieChart.getLegend().setDrawInside(false);
            reportPieChart.setCenterText("You have produced \n" + actualUsageValue +" KG CO2" + "\n So far this month. Detailed report available in Profile.");
            reportPieChart.setHoleColor( Color.parseColor("#00DEA208"));
            reportPieChart.setCenterTextColor(Color.parseColor("#272727"));
            reportPieChart.setCenterTextTypeface(typeface);
            reportPieChart.invalidate();
        }
    }



    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            db.collection(userEmail).document()
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
            return null;
        }

        protected void onPostExecute(Void param) {
        }
    }

    public void fetchCloudStore(){
        CollectionReference usersCollectionRef = db.collection(userEmail);
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserCarbonEmission userCarbonEmission =document.toObject(UserCarbonEmission.class);
                                actualUsage += userCarbonEmission.getCarbon();
                            }
                            fetchFinish = true;
                        }
                    }
                });
    }

//    @Override
//    public void onResume() {
//        actualUsage = 0.0;
//        fetchFinish = false;
//        fetchCloudStore();
//        super.onResume();
//    }



}
