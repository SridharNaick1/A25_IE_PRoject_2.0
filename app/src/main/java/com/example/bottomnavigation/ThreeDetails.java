package com.example.bottomnavigation;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreeDetails extends AppCompatActivity {



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

        viewTwo.setText(getText(R.string.clothing));
        textTitle.setText(R.string.tilteEmp);
        imageView.setImageResource(R.drawable.better_clother);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        HashMap<String, List<String>> item = new HashMap<>();

        ArrayList<String> Smartshopping = new ArrayList<>();
        Smartshopping.add(" Reduce cotton clothing. Cotton is a very water intensive crop, it uses on average 720 gallons of water to produce enough fiber for a shirt");
        Smartshopping.add("HEMP is a sustainable fibre is naturally resistant to pests and requires no chemicals during cultivation.");
        //ResponsibleEat.add("By simply reducing the amount of red meat you eat to no more than three palm sized portions a week you can help combat climate change, water pollution and deforestation..");
        Smartshopping.add("You can also opt for organic clothing, which is basically made using eco-friendly materials.");
        item.put("Go For Hemp Or Organic Clothing", Smartshopping);

        ArrayList<String> planningGroup = new ArrayList<>();
        planningGroup.add("Most of us have more clothes than we need. If we love what we have, we’re not going to need new clothes unless what we have gets worn out.");
        planningGroup.add("Having a capsule wardrobe not only reduces decision fatigue and helps clean up your wardrobe nicely, but it’ll also cultivate the mindset that you can live well with less.");
        planningGroup.add("Loving what you have requires no action, but it translates to a lower carbon footprint in the long run, and savings.");
        item.put("Love what you have", planningGroup);

        ArrayList<String> Cooking = new ArrayList<>();

        Cooking.add("Trendy, cheap items that go out of style quickly get dumped in landfills where they produce methane as they decompose.");
        Cooking.add("Buy vintage or recycled clothing at consignment shops.");
        Cooking.add("Choose quality clothes so that they last longer and translate into a lower carbon footprint.");

        item.put("Choose quality over quantity", Cooking);

        ArrayList<String> Storage = new ArrayList<>();

        Storage.add("If it doesn’t smell and it isn’t stained, consider wearing it again");
        Storage.add("Washing full loads of laundry is the most energy and water-efficient choice. ");
        Storage.add("Wash clothes at cool setting – you’ll cut your energy use by more than 50% than if you use a hot water setting");
        Storage.add("Line dry when possible");

        item.put("Change the way you wash your clothes", Storage);

        MyExpandableListAdapter adapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(adapter);

    }
}
