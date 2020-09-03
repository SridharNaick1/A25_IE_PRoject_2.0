package com.example.bottomnavigation;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
       this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = position;
                Log.d("sdfs", String.valueOf(pos));

                if(pos == 0){
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("param", models.get(position).getTitle());
                    context.startActivity(intent);

                }
                if(pos == 1){
                    Intent intent = new Intent(context, TwoDetalis.class);
                    intent.putExtra("param", models.get(position).getTitle());
                    context.startActivity(intent);

                }
                if(pos == 2){
                    Intent intent = new Intent(context, ThreeDetails.class);
                    intent.putExtra("param", models.get(position).getTitle());
                    context.startActivity(intent);

                }
                if(pos == 3){
                    Intent intent = new Intent(context, FourDetails.class);
                    intent.putExtra("param", models.get(position).getTitle());
                    context.startActivity(intent);

                }
              //  Intent intent = new Intent(context, Details.class);
               // intent.putExtra("param", models.get(position).getTitle());
              //  intent.putExtra("param", models.get(position).getImage());
              //  context.startActivity(intent);
                // finish();
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
