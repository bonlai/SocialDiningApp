package com.bonlai.socialdiningapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GatheringFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;


    private RecyclerView recyclerView;

    public GatheringFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gathering, container, false);

        //recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_square_recycler);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //SimpleAdapter adapter = new SimpleAdapter(getContext());
        //recyclerView.setAdapter(adapter);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        APIclient.APIService service=APIclient.getAPIService();
        Call<User> req = service.getMyDetail();
        req.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    //User user=response.body();
                    MyUserHolder.getInstance().setUser(response.body());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Gathering> mGathering;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView mGatheringName;
            public TextView mDescription;
            public TextView mRestaurantName;
            public ImageView mRestaurantImg;
            public Button join;

            public ViewHolder(View v) {
                super(v);
                mGatheringName = (TextView) v.findViewById(R.id.gathering_name);
                mRestaurantImg = (ImageView) v.findViewById(R.id.restaurant_img);
                mDescription = (TextView) v.findViewById(R.id.description);
                mRestaurantName=(TextView) v.findViewById(R.id.restaurant_name);
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
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            //holder.getBinding().setVariable(BR.item, mItemList.get(position));
            //holder.getBinding().executePendingBindings();
            holder.mGatheringName.setText(mGathering.get(position).getName());
            holder.mGatheringName.setTag(mGathering.get(position).getId());

            //holder.mRestaurantName.setText(mGathering.get(position).getRestaurant());
            //holder.mDescription.setText(mGathering.get(position));
            String testImageUrl="http://www.thoitrangtichtac.com/productimg/12000/11136/250_Dam_suong_tre_vai_tay_con_don_gian_b1136.jpg";
            Picasso.with(context).load(testImageUrl).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(holder.mRestaurantImg);

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
