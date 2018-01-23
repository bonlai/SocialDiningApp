package com.bonlai.socialdiningapp.main_page;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.LoginActivity;
import com.bonlai.socialdiningapp.R;

import com.bonlai.socialdiningapp.RegisterActivity;
import com.bonlai.socialdiningapp.RestaurantDetailActivity;
import com.bonlai.socialdiningapp.main_page.dummy.DummyContent.DummyItem;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> mRestaurant;
    Context context;

    public MyRestaurantRecyclerViewAdapter(Context context,List<Restaurant> restaurant) {
        this.context = context;
        mRestaurant=restaurant;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String imgPath = mRestaurant.get(position).getImage().get(0).getImage();
        final int restaurantId=mRestaurant.get(position).getId();
        Picasso.with(context).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mRestaurantImg);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurantId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurant.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //public final TextView mIdView;
        //public final TextView mContentView;
        public ImageView mRestaurantImg;
        public Restaurant mRestaurant;
        //public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRestaurantImg=(ImageView) mView.findViewById(R.id.restaurant_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent (context, RestaurantDetailActivity.class);
            context.startActivity(intent);

            Toast toast = Toast.makeText(context, "Testing toast" , Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
