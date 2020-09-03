package com.example.bottomnavigation;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TwoDetalis extends AppCompatActivity {

    TextView textTitle;
    ImageView imageView;
    TextView viewTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        textTitle = findViewById(R.id.texttitlee);

        imageView = findViewById(R.id.imageone);

        viewTwo = findViewById(R.id.textView2);

        viewTwo.setText(getText(R.string.travel));

        textTitle.setText(R.string.tilteEmp);

        imageView.setImageResource(R.drawable.car_service);


        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        HashMap<String, List<String>> item = new HashMap<>();

        ArrayList<String> Driveless = new ArrayList<>();
        Driveless.add(" Walking not only helps reduce carbon emission but also good for your cardiovascular health");
        Driveless.add("Take public transport whenever possible. This reduces carbon and also the traffic congestion");
        Driveless.add("If you must drive, avoid unnecessary braking and acceleration. Some studies found that aggressive driving can result in 40 percent more fuel consumption than consistent, calm driving.");

        item.put("Drive less", Driveless);

        ArrayList<String> maintain = new ArrayList<>();
        maintain.add("Keeping your tires properly inflated can increase your fuel efficiency by three percent.");
        maintain.add("When doing errands, try to combine them to reduce your driving.");
        maintain.add("On longer trips, turn on the cruise control, which can save gas.");
        maintain.add("Use less air conditioning while you drive, even when the weather is hot.");

        item.put("Maintain your vehicle", maintain);

        ArrayList<String> Airtravel = new ArrayList<>();

        Airtravel.add("Avoid flying if possible; on shorter trips, driving may emit fewer greenhouse gases.");
        Airtravel.add("Fly nonstop since landings and takeoffs use more fuel and produce more emissions.");
        Airtravel.add("Go economy class. Business class is responsible for almost three times as many emissions as economy because in economy, the flight’s carbon emissions are shared among more passengers");
        Airtravel.add("If you can’t avoid flying, offset the carbon emissions of your travel.");


        item.put("Air travel", Airtravel);

        ArrayList<String> Packlight = new ArrayList<>();

        Packlight.add("Heavy luggage requires more fuel during transport. Rather than packing your whole shower caddy, opt for miniature, refillable containers for toiletries.");
        Packlight.add("Remove and recycle any packaging from your items before you leave.");
        Packlight.add("Bring reusable containers with you! A reusable water bottle and cut down on single-use plastic waste.");
        Packlight.add("Think multi-purpose. Why bring two items when one will do? A sarong can be used as a scarf, a towel, and blanket. The right pair of sandals can work for hiking and an afternoon on the beach.");

        item.put("Pack light", Packlight);

        MyExpandableListAdapter adapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(adapter);

    }
}

