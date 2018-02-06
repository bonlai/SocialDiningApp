package com.bonlai.socialdiningapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bonlai.socialdiningapp.APIclient;
import com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity;
import com.bonlai.socialdiningapp.detail.gathering.NewGatheringActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GatheringFragment extends Fragment implements View.OnClickListener {

    public static enum Mode {
        ALL,
        MY
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MODE = "mode";


    // TODO: Rename and change types of parameters
    private Mode mMode;
    private int myUserId;
    private boolean isPrepared;

    private RecyclerView recyclerView;
    private FloatingActionButton mAddGathering;
    private PullRefreshLayout mPullRefresh;
    private ProgressBar mProgress;
    private RelativeLayout mContainer;

    public GatheringFragment() {
    }

    public static GatheringFragment newInstance(Mode mode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_MODE, mode);

        GatheringFragment fragment = new GatheringFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("FRAG", "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMode = (Mode)getArguments().getSerializable(ARG_MODE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gathering, container, false);

        mAddGathering = (FloatingActionButton) rootView.findViewById(R.id.addNewGathering);
        mAddGathering.setOnClickListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
        mProgress= (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mContainer=(RelativeLayout) rootView.findViewById(R.id.container);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        myUserId=MyUserHolder.getInstance().getUser().getPk();
        isPrepared=true;

        mPullRefresh = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

// listen refresh event
        mPullRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });



        refresh();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isPrepared) {
                refresh();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNewGathering:
                Intent intent = new Intent(getContext(), NewGatheringActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.gatheirng_search, menu);

        //set searchview in action bar
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //GatheringFragment.this.MyAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void refresh(){
        mProgress.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.GONE);
        if(mMode==Mode.ALL){
            APIclient.APIService service=APIclient.getAPIService();
            Call<List<Gathering>> getGatheringList = service.getGatheringList();
            getGatheringList.enqueue(new Callback<List<Gathering>>() {

                @Override
                public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                    Log.d("fragment","called");
                    MyAdapter myAdapter = new MyAdapter(getContext(), response.body());
                    recyclerView.setAdapter(myAdapter);
                    // refresh complete
                    mProgress.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                    mPullRefresh.setRefreshing(false);
                }
                @Override
                public void onFailure(Call<List<Gathering>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }else{
            APIclient.APIService service=APIclient.getAPIService();
            Call<List<Gathering>> getGatheringList = service.getMyGatheringList(myUserId);
            getGatheringList.enqueue(new Callback<List<Gathering>>() {
                @Override
                public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {

                    MyAdapter myAdapter = new MyAdapter(getContext(), response.body());
                    recyclerView.setAdapter(myAdapter);
                    // refresh complete
                    mProgress.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                    mPullRefresh.setRefreshing(false);
                }
                @Override
                public void onFailure(Call<List<Gathering>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Gathering> mGathering;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public final View mView;
            public TextView mGatheringName;
            public TextView mDescription;
            public Switch mJoin;

            public TextView mRestaurantName;
            public ImageView mRestaurantImg;
            public TextView mCategory;

            public ImageView mCreator;


            public ViewHolder(View v) {
                super(v);
                mView = v;
                mGatheringName = (TextView) v.findViewById(R.id.gathering_name);
                mRestaurantImg = (ImageView) v.findViewById(R.id.restaurant_img);
                mDescription = (TextView) v.findViewById(R.id.description);
                mRestaurantName=(TextView) v.findViewById(R.id.restaurant_name);
                mCreator= (ImageView) v.findViewById(R.id.user_img);
                mCategory=(TextView) v.findViewById(R.id.category);
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

        public MyAdapter(Context context, List<Gathering> gathering) {
            this.context = context;
            mGathering=gathering;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gathering, parent, false);
            MyAdapter.ViewHolder vh = new MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
            APIclient.APIService service=APIclient.getAPIService();

            //get restaurant info
            Call<Restaurant> getRestaurantInfo = service.getRestaurantInfo(mGathering.get(position).getRestaurant());
            getRestaurantInfo.enqueue(new Callback<Restaurant>() {
                @Override
                public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                    if(response.isSuccessful()){
                        holder.mRestaurantName.setText(response.body().getName());
                        if(!response.body().getImage().isEmpty()){
                            String restImg=response.body().getImage().get(0).getImage();
                            Picasso.with(context).load(restImg).placeholder(R.drawable.progress_animation ).fit().centerCrop().into(holder.mRestaurantImg);
                        }
                        holder.mCategory.setText("# "+response.body().getCategory());
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
                        Picasso.with(context).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mCreator);
                    }else{

                    }
                }
                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            //get gathering info
            holder.mGatheringName.setText(mGathering.get(position).getName());
            holder.mGatheringName.setTag(mGathering.get(position).getId());
            holder.mDescription.setText(mGathering.get(position).getDetail());

            //set join button event
            final int gatheringId=mGathering.get(position).getId();

            if(mGathering.get(position).getMember().contains(myUserId)||mGathering.get(position).getCreatedBy()==myUserId){
                holder.mJoin.setChecked(true);
            }else{
                holder.mJoin.setChecked(false);
            }

            holder.mJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    APIclient.APIService service=APIclient.getAPIService();
                    Call<ResponseBody> req = service.joinGathering(myUserId, gatheringId);
                    callParticipateAPI(req);

                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, GatheringDetailActivity.class);
                    intent.putExtra("gatheringId", gatheringId);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGathering.size();
        }

        private void callParticipateAPI(Call<ResponseBody> req){
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Toast toast = Toast.makeText(context, "Joined Gathering " , Toast.LENGTH_LONG);
                        toast.show();
                        if(mMode==Mode.MY){
                            refresh();
                        }

                    }else{
                        Toast toast = Toast.makeText(context, "Withdraw Gathering" , Toast.LENGTH_LONG);
                        toast.show();
                        if(mMode==Mode.MY){
                            refresh();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

    }

}
