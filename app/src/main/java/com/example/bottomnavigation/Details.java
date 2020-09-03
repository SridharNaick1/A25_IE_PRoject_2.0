package com.example.bottomnavigation;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Details extends AppCompatActivity {

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

        viewTwo.setText(getText(R.string.viewtwo));
        textTitle.setText(R.string.tilteEmp);
        imageView.setImageResource(R.drawable.better_food);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        HashMap<String, List<String>> item = new HashMap<>();

        ArrayList<String> ResponsibleEat = new ArrayList<>();
        ResponsibleEat.add("The best way to reduce food waste is to avoid creating it in the first place, and a bit of planning can help you do just that.");
        ResponsibleEat.add("Food planning – in whatever form works best for you – will save you time and money. It will help you make the most of what you already have and ensures you only buy the things you really need.");
        //ResponsibleEat.add("By simply reducing the amount of red meat you eat to no more than three palm sized portions a week you can help combat climate change, water pollution and deforestation..");
        ResponsibleEat.add("Good planning involves checking what you already have, planning your meals and knowing what you need to buy.");
        ResponsibleEat.add("If planning every meal of the week seems daunting, you can try planning two to three days ahead, or for just your week-night dinners.");


        item.put("Planning", ResponsibleEat);

        ArrayList<String> planningGroup = new ArrayList<>();
        planningGroup.add("Planning your meals around food that is in season is usually cheaper and these items are often fresher so they last much longer.");
        planningGroup.add("If you find sticking to your in-store list difficult, then try to avoid shopping when hungry or with children, as this increases the likelihood of buying more.");
        planningGroup.add("Choose organic and local foods that are in season. Transporting food from far away, whether by truck, ship, rail or plane, uses fossil fuels for fuel and for cooling to keep foods in transit from spoiling.");
        planningGroup.add("And remember, bulk-buy deals or two-for-one specials are only good value if you actually end up using everything that's included. It’s not a bargain if it ends up in the bin.");

        item.put("Shopping", planningGroup);

        ArrayList<String> Cooking = new ArrayList<>();

        Cooking.add("Waste-free cooking can be easy and makes cooking more fun and less stressful.");
        Cooking.add("A meal plan can save you both time and stress and will help you use up what you’ve bought.");
        Cooking.add("If you find yourself with spare ingredients or leftovers, take the opportunity to get creative so that you use them up.");
        Cooking.add("Cooking the right amount for what you need also helps reduce waste.");


        item.put("Cooking", Cooking);

        ArrayList<String> Storage = new ArrayList<>();

        Storage.add("Replacing the carbon-heavy beef on your plate with carbon-light chicken will cut your dietary carbon footprint a shocking amount: in half.");
        Storage.add("A Vegetarian’s foodprint is about two thirds of the average American and almost half that of a meat lover.");
        Storage.add("Cutting back on dairy products, including milk and cheese, is another way to reduce your carbon footprint.");
        Storage.add("Eating more fiber-rich foods not only improves your health but may also reduce your carbon footprint.");

        item.put("Eat right to reduce carbon", Storage);

        MyExpandableListAdapter adapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(adapter);

    }
}
