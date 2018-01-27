package com.bonlai.socialdiningapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.main_page.MyRestaurantRecyclerViewAdapter;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.models.Review;
import com.bonlai.socialdiningapp.models.Token;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity implements ReviewDialogFragment.Callback {

    private RecyclerView recyclerView;
    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewDialogFragment viewDialogFragment = new ReviewDialogFragment();
                viewDialogFragment.show(getFragmentManager());
            }
        });

        restaurantId = getIntent().getExtras().getInt("restaurantId");
        getReview(this,restaurantId,recyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);


    }

    private void initUI(){
        recyclerView=(RecyclerView)findViewById(R.id.list_view);
    }

    private void getReview(final Context context,int restaurantId, final RecyclerView recyclerView){
        APIclient.APIService service=APIclient.getAPIService();
        Call<List<Review>> getReview = service.getReview(restaurantId);
        getReview.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if(response.isSuccessful()){
                    List<Review> review=response.body();
                    Log.d("array lentgh",""+review.size());
                    recyclerView.setAdapter(new MyCommentRecyclerViewAdapter(context,review));
                }
            }
            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(String comment, int rating) {
        APIclient.APIService service=APIclient.getAPIService();
        Call<ResponseBody> postReview = service.postReview(comment,rating,restaurantId);
        postReview.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RestaurantDetailActivity.this, "Review posted", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {

        private final List<Review> mReview;
        Context context;

        public MyCommentRecyclerViewAdapter(Context context,List<Review> review) {
            this.context = context;
            mReview=review;
        }

        @Override
        public MyCommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment, parent, false);
            return new MyCommentRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyCommentRecyclerViewAdapter.ViewHolder holder, int position) {
 /*           //String imgPath = mReview.get(position).getImage().get(0).getImage();
            final int restaurantId=mRestaurant.get(position).getId();
            Picasso.with(context).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mRestaurantImg);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, RestaurantDetailActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    context.startActivity(intent);
                }
            });*/
            //Log.d("onbind",""+position);
            holder.mComment.setText(mReview.get(position).getComment());

            holder.mRating.setIsIndicator(true);
            holder.mRating.setRating(mReview.get(position).getRating());

        }

        @Override
        public int getItemCount() {
            //Log.d("itemcount",""+mReview.size());
            return mReview.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final MaterialRatingBar mRating;
            public TextView mComment;
            // Restaurant mRestaurant;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mRating=(MaterialRatingBar) mView.findViewById(R.id.rating);
                mComment=(TextView) mView.findViewById(R.id.comment);
                //mRestaurantImg=(ImageView) mView.findViewById(R.id.restaurant_img);
            }
        }
    }

}