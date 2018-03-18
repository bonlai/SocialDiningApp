package com.bonlai.socialdiningapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bonlai.socialdiningapp.R;

import com.bonlai.socialdiningapp.detail.gathering.GatheringUpsertActivity;
import com.bonlai.socialdiningapp.detail.restaurant.RestaurantDetailActivity;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isMoreLoading = true;
    private final List<Restaurant> mRestaurant;
    Context context;
    private LoadDataListener mListener;

    public interface LoadDataListener{
        void loadData();
        void loadMoreData();
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
        public Button mCreate;
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
            mCreate=(Button) mView.findViewById(R.id.addNewGathering);

            mCreate.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.addNewGathering:

                    break;
            }
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;
        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mRestaurant.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public MyRestaurantRecyclerViewAdapter(Context context,List<Restaurant> restaurant, LoadDataListener listener) {
        this.context = context;
        mRestaurant=restaurant;
        mListener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_restaurant, parent, false);
            return new ViewHolder(view);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress,parent, false));
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder mHolder=(ViewHolder)holder;
            String imgPath;
            if(!mRestaurant.get(position).getImage().isEmpty()){
                imgPath = mRestaurant.get(position).getImage().get(0).getImage();
            }else{
                imgPath = "http://192.168.2.4:8000/media/RestaurantImage/default.jpg";
            }
            Picasso.with(context).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mHolder.mRestaurantImg);
            final int restaurantId=mRestaurant.get(position).getId();
            mHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, RestaurantDetailActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    context.startActivity(intent);
                }
            });
            mHolder.mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GatheringUpsertActivity.class);
                    intent.putExtra("mode", GatheringUpsertActivity.Mode.POST);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putExtra("restaurantName", mRestaurant.get(position).getName());
                    intent.putExtra("mode", GatheringUpsertActivity.Mode.POST);
                    context.startActivity(intent);
                }
            });

            double rating=mRestaurant.get(position).getAverageRate();
            mHolder.mAvgRating.setRating((float)rating);
            mHolder.mAvgRating.setIsIndicator(true);

            mHolder.mCategory.setText(mRestaurant.get(position).getCategory());
            mHolder.mAddress.setText(mRestaurant.get(position).getAddress());
            mHolder.mRestaurantName.setText(mRestaurant.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return mRestaurant.size();
    }

    public void addMoreRestaurants(List<Restaurant> restaurants){
        int sizeInit = mRestaurant.size();
        mRestaurant.addAll(restaurants);
        notifyItemRangeChanged(sizeInit, mRestaurant.size());
    }

    public void resetRestaurants(List<Restaurant> restaurants){
        mRestaurant.clear();
        mRestaurant.addAll(restaurants);
        notifyDataSetChanged();
    }

    public void showLoading() {
        if (isMoreLoading && mRestaurant != null && mListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mRestaurant.add(null);
                    notifyItemInserted(mRestaurant.size() - 1);
                    mListener.loadMoreData();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (mRestaurant != null && mRestaurant.size() > 0) {
            mRestaurant.remove(mRestaurant.size() - 1);
            notifyItemRemoved(mRestaurant.size());
        }
    }


}
