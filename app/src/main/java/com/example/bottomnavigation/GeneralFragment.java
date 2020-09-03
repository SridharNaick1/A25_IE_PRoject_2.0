package com.example.bottomnavigation;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class GeneralFragment extends Fragment {
    private View vGeneralUnit = null;

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors= null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        vGeneralUnit = inflater.inflate(R.layout.fragment_general, container, false);

        models = new ArrayList<>();
        models.add(new Model(R.drawable.cartoon_resized_food, "Food", "The most common causes of household food waste is produce left too long in the fridge or freezer, " +
                "followed by people not finishing their meals. Producing this wasted food uses 3.6 million hectares of land, " +
                "180 gigalitres of water, and generates 1 million tonnes of greenhouse gases"));

        models.add(new Model(R.drawable.cartoon_resized_transport, "Transport", "Cars are responsible for roughly half of all" +
                                                                            " transport emissions Collectively, Australian cars" +
                                                                            " emit roughly the same per year (43MtCO2e)" +
                                                                            " as Queensland’s entire coal and gas fire and" +
                                                                            " electricity supply."));

        models.add(new Model(R.drawable.cartoon_clothes_resized, "Clothing", "According to figures from the United Nations Environment Programme (UNEP), it takes 3,781 liters of water to make a pair of jeans, " +
                "from the production of the cotton to the delivery of the final product to the store. That equates to the emission of around 33.4 kilograms" +
                " of carbon equivalent."));

        models.add(new Model(R.drawable.cartoon_resized_home, "Home", "Saving energy makes a big difference to our environment and our future. " +
                "In Victoria, we are already feeling the effects of climate change."));


       // adapter = new Adapter(models, this);
        adapter = new Adapter(models, getActivity());

        viewPager = vGeneralUnit.findViewById(R.id.viewPager);

        viewPager.setAdapter(adapter);
        viewPager.setPadding(0, 0, 0, 0);


        Integer[] colors_temp = {
                getResources().getColor(R.color.color6),
                getResources().getColor(R.color.color6),
                getResources().getColor(R.color.color6),
                getResources().getColor(R.color.color6)

        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//        vGeneralUnit = inflater.inflate(R.layout.fragment_general, container, false);
        return vGeneralUnit;
    }
}
