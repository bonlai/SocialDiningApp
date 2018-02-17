package com.bonlai.socialdiningapp.detail.gathering;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.profileEdit.OtherProfileActivity;
import com.bonlai.socialdiningapp.detail.restaurant.RestaurantDetailActivity;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.models.User;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GatheringDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int gatheringId;
    private Gathering mGathering;
    private List<User> mParticipants;
    private MyParticipantRecyclerViewAdapter adapter;
    private ProgressBar mProgress;
    private View mContainer;

    private ImageView mRestaurantImg;
    private MaterialRatingBar mAvgRating;
    private TextView mCategory;
    private TextView mAddress;
    private TextView mRestaurantName;
    private View mRestaurantHolder;

    private TextView mGatheringName;
    private ImageView mCreaterImg;
    private TextView mDescription;
    private TextView mDateTime;
    private Switch mJoin;
    private Button mStart;
    private Button mEdit;

    private int myUserId;

    public static final String GATHERING_ID="gatheringId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gathering_detail);
        initUI();
        initVar();
        getGatheringInfo(this);
    }

    private void initVar(){
        mParticipants=new ArrayList<>();
        gatheringId = getIntent().getExtras().getInt(GATHERING_ID);
        myUserId= MyUserHolder.getInstance().getUser().getPk();
    }

    private void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=(RecyclerView)findViewById(R.id.list_view);
        mProgress= (ProgressBar) findViewById(R.id.progress_bar);
        mContainer=findViewById(R.id.container);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Button addButton=(Button)findViewById(R.id.addNewGathering);
        addButton.setVisibility(View.GONE);
        
        mRestaurantImg=(ImageView) findViewById(R.id.restaurant_img);
        mAvgRating=(MaterialRatingBar) findViewById(R.id.average_rating);
        mCategory=(TextView) findViewById(R.id.category);
        mAddress=(TextView) findViewById(R.id.address);
        mRestaurantName=(TextView) findViewById(R.id.restaurant_name);
        mRestaurantHolder=(View)findViewById(R.id.restaurant_holder);

        mGatheringName = (TextView) findViewById(R.id.gathering_name);
        mCreaterImg = (ImageView) findViewById(R.id.creater_img);
        mDescription = (TextView) findViewById(R.id.bio);
        mDateTime= (TextView) findViewById(R.id.date_time);
        mJoin = (Switch) findViewById(R.id.join);
        mStart=(Button)findViewById(R.id.start_button);
        mEdit=(Button)findViewById(R.id.edit_button);
    }

    private void getCreater(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<List<User>> getOthersDetail = service.getOthersDetail(mGathering.getCreatedBy());
        getOthersDetail.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    updateCreater(response.body().get(0));
                }else{

                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getGatheringInfo(final Context context){
        mContainer.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<Gathering> getGatheringDetail = service.getGatheringDetail(gatheringId);
        getGatheringDetail.enqueue(new Callback<Gathering>() {
            @Override
            public void onResponse(Call<Gathering> call, Response<Gathering> response) {
                if(response.isSuccessful()){
                    mGathering=response.body();
                    updateGathering();
                    for(Integer id:mGathering.getMember()){
                        Log.d("looping id ",""+id);
                        getParticipants(id);
                    }
                    getRestaurantInfo(mGathering.getRestaurant());
                    getCreater();

                    if(mGathering.getCreatedBy()==myUserId){
                        mJoin.setVisibility(View.GONE);
                    }else{
                        mStart.setVisibility(View.GONE);
                        mEdit.setVisibility(View.GONE);
                    }

                    mStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mGathering.getIsStart()){
                                startGathering(gatheringId);
                            }else{
                                Toast.makeText(GatheringDetailActivity.this, "Started already",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    mEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(GatheringDetailActivity.this, GatheringUpsertActivity.class);
                            intent.putExtra("mode", GatheringUpsertActivity.Mode.EDIT);
                            intent.putExtra("gathering",mGathering);
                            startActivity(intent);
                        }
                    });
                    adapter=new MyParticipantRecyclerViewAdapter(context,mParticipants);
                    recyclerView.setAdapter(adapter);

                    mContainer.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                }else{

                }
            }
            @Override
            public void onFailure(Call<Gathering> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getParticipants(final int id){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<List<User>> getOthersDetail = service.getOthersDetail(id);
        getOthersDetail.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    Log.d("adding ",""+id);
                    mParticipants.add(response.body().get(0));
                    adapter.notifyDataSetChanged();
                }else{

                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getRestaurantInfo(int id){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();

        //get restaurant info
        Call<Restaurant> getRestaurantInfo = service.getRestaurantInfo(id);
        getRestaurantInfo.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if(response.isSuccessful()){
                    updateRestaurant(response.body());
                }
            }
            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startGathering(int id){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();

        //get restaurant info
        Call<Gathering> startGathering = service.startGathering(id,true);
        startGathering.enqueue(new Callback<Gathering>() {
            @Override
            public void onResponse(Call<Gathering> call, Response<Gathering> response) {
                if(response.isSuccessful()){
                    mGathering.setIsStart(true);
                    Toast.makeText(GatheringDetailActivity.this, "Started the gathering",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Gathering> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startRestaurantDetail(int restaurantId){
        Intent intent = new Intent (this, RestaurantDetailActivity.class);
        intent.putExtra("restaurantId", restaurantId);
        this.startActivity(intent);
    }

    private void updateCreater(final User creater){

        mCreaterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (GatheringDetailActivity.this, OtherProfileActivity.class);
                intent.putExtra("userId", creater.getId());
                GatheringDetailActivity.this.startActivity(intent);
            }
        });
        String imgPath=creater.getProfile().getImage();
        Picasso.with(GatheringDetailActivity.this).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mCreaterImg);
    }

    private void updateRestaurant(Restaurant mRestaurant){
        String imgPath = mRestaurant.getImage().get(0).getImage();
        final int restaurantId=mRestaurant.getId();
        Picasso.with(this).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mRestaurantImg);
        mRestaurantHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRestaurantDetail(restaurantId);
            }
        });
        double rating=mRestaurant.getAverageRate();
        mAvgRating.setRating((float)rating);
        mAvgRating.setIsIndicator(true);

        mCategory.setText(mRestaurant.getCategory());
        mAddress.setText(mRestaurant.getAddress());
        mRestaurantName.setText(mRestaurant.getName());
    }

    private void updateGathering(){
        mGatheringName.setText(mGathering.getName());
        mGatheringName.setTag(mGathering.getId());
        mDescription.setText(mGathering.getDetail());
        mDateTime.setText(mGathering.getStartDatetime());

        if(mGathering.getMember().contains(myUserId)||mGathering.getCreatedBy()==myUserId){
            mJoin.setChecked(true);
        }else{
            mJoin.setChecked(false);
        }

        mJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callParticipateAPI();
            }
        });
    }

    private void callParticipateAPI(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<ResponseBody> req = service.joinGathering(myUserId, gatheringId);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast toast = Toast.makeText(GatheringDetailActivity.this, "Joined Gathering " , Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(GatheringDetailActivity.this, "Withdraw Gathering" , Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public class MyParticipantRecyclerViewAdapter extends RecyclerView.Adapter<MyParticipantRecyclerViewAdapter.ViewHolder> {
        private final List<User> mParticipants;
        Context context;

        public MyParticipantRecyclerViewAdapter(final Context context,List<User> participants) {
            this.context = context;
            mParticipants=participants;
        }

        @Override
        public MyParticipantRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.participant, parent, false);
            return new MyParticipantRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyParticipantRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, OtherProfileActivity.class);
                    intent.putExtra("userId", mParticipants.get(position).getId());
                    context.startActivity(intent);
                }
            });

            holder.mUsername.setText(mParticipants.get(position).getUsername());
            holder.mBio.setText(mParticipants.get(position).getProfile().getSelfIntroduction());
            holder.mGender.setText(mParticipants.get(position).getProfile().getGender());
            String userProfilePic=mParticipants.get(position).getProfile().getImage();
            Picasso.with(context).load(userProfilePic).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mProfilePic);
        }

        @Override
        public int getItemCount() {
            //Log.d("itemcount",""+mReview.size());
            return mParticipants.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public TextView mBio;
            public TextView mUsername;
            public TextView mGender;
            public ImageView mProfilePic;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mUsername=(TextView) mView.findViewById(R.id.username);
                mBio=(TextView) mView.findViewById(R.id.bio);
                mGender=(TextView) mView.findViewById(R.id.gender);
                mProfilePic=(ImageView) mView.findViewById(R.id.user_img);
            }
        }
    }
}
