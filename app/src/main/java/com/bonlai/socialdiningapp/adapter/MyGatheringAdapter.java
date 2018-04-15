package com.bonlai.socialdiningapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bon Lai on 18/2/2018.
 */

public class MyGatheringAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements Filterable {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<Gathering> mGathering;
    private List<Gathering> mGatheringFiltered;
    private LoadDataListener mListener;
    private int myUserId;
    private Context context;

    private boolean isMoreLoading = true;

    public interface LoadDataListener{
        void loadData();
        void loadMoreData();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public TextView mGatheringName;
        public TextView mDescription;
        public Switch mJoin;

        public TextView mRestaurantName;
        public ImageView mRestaurantImg;
        public TextView mCategory;
        public TextView mDateTime;

        public ImageView mCreator;


        public ViewHolder(View v) {
            super(v);
            mView = v;
            mGatheringName = (TextView) v.findViewById(R.id.gathering_name);
            mRestaurantImg = (ImageView) v.findViewById(R.id.restaurant_img);
            mDescription = (TextView) v.findViewById(R.id.bio);
            mRestaurantName=(TextView) v.findViewById(R.id.restaurant_name);
            mCreator= (ImageView) v.findViewById(R.id.user_img);
            mCategory=(TextView) v.findViewById(R.id.category);
            mDateTime=(TextView) v.findViewById(R.id.date_time);
            mJoin = (Switch) v.findViewById(R.id.join);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick (View v){
            //to event detail activity
            Toast toast = Toast.makeText(context, "Testing toast" + mGathering.get(getAdapterPosition()), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;
        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

    public MyGatheringAdapter(Context context, List<Gathering> gathering,LoadDataListener listener) {
        this.context = context;
        mGathering=gathering;
        mGatheringFiltered=gathering;
        myUserId= MyUserHolder.getInstance().getUser().getPk();
        mListener=listener;
    }
    @Override
    public int getItemViewType(int position) {
        return mGatheringFiltered.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gathering, parent, false);
            MyGatheringAdapter.ViewHolder vh = new MyGatheringAdapter.ViewHolder(v);
            return vh;
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress,parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder mholder=(ViewHolder)holder;
            AuthAPIclient.APIService service=AuthAPIclient.getAPIService();

            //get restaurant info
            Call<Restaurant> getRestaurantInfo = service.getRestaurantInfo(mGatheringFiltered.get(position).getRestaurant());
            getRestaurantInfo.enqueue(new Callback<Restaurant>() {
                @Override
                public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                    if(response.isSuccessful()){
                        mholder.mRestaurantName.setText(response.body().getName());
                        if(!response.body().getImage().isEmpty()){
                            String restImg=response.body().getImage().get(0).getImage();
                            Picasso.with(context).load(restImg).placeholder(R.drawable.progress_animation ).fit().centerCrop().into(mholder.mRestaurantImg);
                        }
                        mholder.mCategory.setText("# "+response.body().getCategory());
                    }else{

                    }
                }
                @Override
                public void onFailure(Call<Restaurant> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            //get user img
            Call<Profile> getUserImg = service.getProfile(mGathering.get(position).getCreatedBy());
            getUserImg.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    if(response.isSuccessful()){
                        String imgPath=response.body().getImage();
                        Picasso.with(context).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mholder.mCreator);
                    }else{

                    }
                }
                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            //get gathering info
            mholder.mGatheringName.setText(mGatheringFiltered.get(position).getName());
            mholder.mGatheringName.setTag(mGatheringFiltered.get(position).getId());
            mholder.mDescription.setText(mGatheringFiltered.get(position).getDetail());
            mholder.mDateTime.setText(mGatheringFiltered.get(position).getStartDatetime());

            //set join button event
            final int gatheringId=mGatheringFiltered.get(position).getId();
            Log.d("Join button out",mGatheringFiltered.get(position).getCreatedBy()+" "+
                    mGatheringFiltered.get(position).getId());
            Log.d("userid",""+myUserId);
            boolean test=mGatheringFiltered.get(position).getCreatedBy()==myUserId;
            Log.d("booelan",Boolean.toString(test));

            if(mGatheringFiltered.get(position).getCreatedBy()==myUserId||mGatheringFiltered.get(position).getIsStart()){
                mholder.mJoin.setVisibility(View.GONE);
            }else{
                mholder.mJoin.setVisibility(View.VISIBLE);
            }
            if(mGatheringFiltered.get(position).getMember().contains(myUserId)||mGatheringFiltered.get(position).getCreatedBy()==myUserId){
                mholder.mJoin.setChecked(true);
            }else{
                mholder.mJoin.setChecked(false);
            }

            mholder.mJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
                    Call<ResponseBody> req = service.joinGathering(myUserId, gatheringId);
                    callParticipateAPI(req);

                }
            });

            mholder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, GatheringDetailActivity.class);
                    intent.putExtra(GatheringDetailActivity.GATHERING_ID, gatheringId);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mGatheringFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mGatheringFiltered = mGathering;
                } else {
                    List<Gathering> filteredList = new ArrayList<>();
                    for (Gathering gathering : mGathering) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (gathering.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(gathering);
                            Log.d("Filter:", gathering.getName().toLowerCase()+" "+charString.toLowerCase());
                        }
                    }

                    mGatheringFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mGatheringFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mGatheringFiltered = (ArrayList<Gathering>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    private void callParticipateAPI(Call<ResponseBody> req){
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast toast = Toast.makeText(context, "Joined Gathering " , Toast.LENGTH_LONG);
                    toast.show();
                    mListener.loadData();


                }else{
                    Toast toast = Toast.makeText(context, "Withdraw Gathering" , Toast.LENGTH_LONG);
                    toast.show();
                    mListener.loadData();

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addMoreGathering(List<Gathering> gatherings){
        int sizeInit = mGatheringFiltered.size();
        mGatheringFiltered.addAll(gatherings);
        notifyItemRangeChanged(sizeInit, mGatheringFiltered.size());
    }

    public void showLoading() {
        if (isMoreLoading && mGatheringFiltered != null && mListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mGatheringFiltered.add(null);
                    notifyItemInserted(mGatheringFiltered.size() - 1);
                    mListener.loadMoreData();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (mGatheringFiltered != null && mGatheringFiltered.size() > 0) {
            mGatheringFiltered.remove(mGatheringFiltered.size() - 1);
            notifyItemRemoved(mGatheringFiltered.size());
        }
    }

    public void resetGatherings(List<Gathering> gatherings){
        mGatheringFiltered.clear();
        mGatheringFiltered.addAll(gatherings);
        notifyDataSetChanged();
    }

}