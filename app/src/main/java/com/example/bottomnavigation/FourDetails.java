package com.example.bottomnavigation;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FourDetails extends AppCompatActivity {

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

        viewTwo.setText(getText(R.string.homeC));
        textTitle.setText(R.string.tilteEmp);
        imageView.setImageResource(R.drawable.home_carbon);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        HashMap<String, List<String>> item = new HashMap<>();

        ArrayList<String> Smartshopping = new ArrayList<>();
        Smartshopping.add("Turn off your heater, cooling units and appliances when you go to bed or leave the house.");
        Smartshopping.add("Turning things off at the powerpoint can save even more power than at the switch or remote control.");
        Smartshopping.add("Most computers have energy saving settings which can be activated to turn the computer and screen off after a period of inactivity.");
        Smartshopping.add("Switch off your computer and equipment such as printers or wifi routers overnight, or while you're away.");
        item.put("Switch off lights and electrical appliances", Smartshopping);

        ArrayList<String> planningGroup = new ArrayList<>();
        planningGroup.add("Your fridge runs 24 hours a day and is one of your most expensive appliances to run. Make sure the fridge door seal is tight and that no gaps or cracks let cold air escape.");
        planningGroup.add("Put frozen food in your fridge in the morning to thaw out and reduce cooking time in the evening.");
        planningGroup.add("When you're cooking, use the microwave when you can. Microwaves use much less energy than an electric oven.");
        planningGroup.add("If you use a stove, keep lids on pots to reduce cooking time.");
        planningGroup.add("If you use a stove, keep lids on pots to reduce cooking time.");
        item.put("Save energy in the kitchen", planningGroup);

        ArrayList<String> Cooking = new ArrayList<>();

        Cooking.add("Every degree above 20 degrees can add 10 per cent to your heating increases carbon also your bill");
        Cooking.add("In winter, set your thermostat between 18 and 20 degrees. In summer, set your thermostat to 26 degrees or above.");
        Cooking.add("Consider replacing your heater with a more efficient model.");

        item.put("Manage your heating and cooling", Cooking);

        ArrayList<String> Storage = new ArrayList<>();

        Storage.add("Solar energy is one of the cleanest sources of energy, and it’s an extremely effective way of your household more efficient and sustainable.");
        Storage.add("Solar panels don’t use any water to generate electricity, they don’t release harmful gases into the environment.");
        Storage.add("You could offset anywhere between a half to one tonne of carbon dioxide for every megawatt-hour of solar energy you use.");

        item.put("Reduce carbon with solar energy", Storage);

        MyExpandableListAdapter adapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(adapter);

    }
}
