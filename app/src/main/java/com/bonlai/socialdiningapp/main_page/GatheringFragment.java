package com.bonlai.socialdiningapp.main_page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.APIclient;
import com.bonlai.socialdiningapp.NewGatheringActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GatheringFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;


    private RecyclerView recyclerView;
    private FloatingActionButton mAddGathering;

    public GatheringFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_gathering, container, false);

        mAddGathering = (FloatingActionButton) rootView.findViewById(R.id.addNewGathering);
        mAddGathering.setOnClickListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        APIclient.APIService service=APIclient.getAPIService();
        Call<List<Gathering>> req2 = service.getGatheringList();
        req2.enqueue(new Callback<List<Gathering>>() {
            @Override
            public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                Log.v("Upload", "successlist");
                //test for loading array to list
                for(Gathering G: response.body()){
                    Log.v("loopresult",G.toString());
                }
                MyAdapter myAdapter = new MyAdapter(getContext(), response.body());
                recyclerView.setAdapter(myAdapter);
            }
            @Override
            public void onFailure(Call<List<Gathering>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) actionBar.findViewById(R.id.toolbar);
        actionBar.setSupportActionBar(toolbar);
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
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.gatheirng_search, menu);
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
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Gathering> mGathering;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView mGatheringName;
            public TextView mDescription;
            public TextView mCategory;
            public TextView mRestaurantName;
            public ImageView mRestaurantImg;
            public ImageView mCreator;
            public Button join;

            public ViewHolder(View v) {
                super(v);
                mGatheringName = (TextView) v.findViewById(R.id.gathering_name);
                mRestaurantImg = (ImageView) v.findViewById(R.id.restaurant_img);
                mDescription = (TextView) v.findViewById(R.id.description);
                mRestaurantName=(TextView) v.findViewById(R.id.restaurant_name);
                mCreator= (ImageView) v.findViewById(R.id.creator_img);
                mCategory=(TextView) v.findViewById(R.id.category);
                join = (Button) v.findViewById(R.id.join);
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
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
            APIclient.APIService service=APIclient.getAPIService();

            //final MyAdapter.ViewHolder mHolder=holder;
            final int mPosition=position;

            //get restaurant info
            Call<Restaurant> getRestaurantInfo = service.getRestaurantInfo(mGathering.get(position).getRestaurant());
            getRestaurantInfo.enqueue(new Callback<Restaurant>() {
                @Override
                public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                    if(response.isSuccessful()){
                        holder.mRestaurantName.setText(response.body().getName());
                        if(!response.body().getImage().isEmpty()){
                            String restImg=response.body().getImage().get(0).getImage();
                            Picasso.with(context).load(restImg).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mRestaurantImg);
                        }
                        holder.mCategory.setText(response.body().getCategory());
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
            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    APIclient.APIService service=APIclient.getAPIService();
                    Call<ResponseBody> req = service.joinGathering(MyUserHolder.getInstance().getUser().getPk(), gatheringId);
                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()){
                                Toast toast = Toast.makeText(context, "Joined Gathering " , Toast.LENGTH_LONG);
                                toast.show();
                            }else{
                                Toast toast = Toast.makeText(context, "Cant join" , Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGathering.size();
        }
    }

}
