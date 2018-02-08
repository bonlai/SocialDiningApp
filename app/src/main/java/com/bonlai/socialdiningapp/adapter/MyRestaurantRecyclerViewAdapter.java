package com.bonlai.socialdiningapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bonlai.socialdiningapp.R;

import com.bonlai.socialdiningapp.detail.restaurant.RestaurantDetailActivity;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


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
        double rating=mRestaurant.get(position).getAverageRate();
        holder.mAvgRating.setRating((float)rating);
        holder.mAvgRating.setIsIndicator(true);

        holder.mCategory.setText(mRestaurant.get(position).getCategory());
        holder.mAddress.setText(mRestaurant.get(position).getAddress());
        holder.mRestaurantName.setText(mRestaurant.get(position).getName());
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
        public MaterialRatingBar mAvgRating;
        public TextView mCategory;
        public TextView mAddress;
        public TextView mRestaurantName;
        //public Restaurant mRestaurant;
        //public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRestaurantImg=(ImageView) mView.findViewById(R.id.restaurant_img);
            mAvgRating=(MaterialRatingBar) mView.findViewById(R.id.average_rating);
            mCategory=(TextView) mView.findViewById(R.id.category);
            mAddress=(TextView) mView.findViewById(R.id.address);
            mRestaurantName=(TextView) mView.findViewById(R.id.restaurant_name);
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
