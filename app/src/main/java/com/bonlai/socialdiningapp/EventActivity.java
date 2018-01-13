package com.bonlai.socialdiningapp;

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
import android.widget.TextView;
import android.widget.ImageView;

import com.bonlai.socialdiningapp.models.Gathering;
import com.squareup.picasso.Picasso;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {
    //private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    //private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    //ArrayList<Event> eventList;
    ArrayList<String> myDataset;
    APIclient.APIService service;
    //Gathering gathering;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

/*      //data initial
        eventList = new ArrayList<>();
        myDataset = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            myDataset.add("Test"+i + "");
        }*/

        //link views in XML to variables
        recyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Toolbar initial
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //read data from API service
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute("http://quocnguyen.16mb.com/products.json");
            }
        });*/
        service=APIclient.getAPIService();
        Call<List<Gathering>> req2 = service.getGatheringList();
        req2.enqueue(new Callback<List<Gathering>>() {
            @Override
            public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                Log.v("Upload", "successlist");
                //test for loading array to list
                for(Gathering G: response.body()){
                    Log.v("loopresult",G.toString());
                }
                MyAdapter myAdapter = new MyAdapter(getApplicationContext(), response.body());
                recyclerView.setAdapter(myAdapter);
            }
            @Override
            public void onFailure(Call<List<Gathering>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //Jump to profile activity
    public void nextActivity(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void addGathering(View view){
        Intent intent = new Intent(this, NewGatheringActivity.class);
        startActivity(intent);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        //private List<String> mData;
        //private List<Event> mEvent;
        private List<Gathering> mGathering;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView mTextView;
            public ImageView mImageView;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.info_text);
                mImageView = (ImageView) v.findViewById(R.id.img);
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
            //mData = data;
            //mEvent=event;
            mGathering=gathering;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gathering, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //holder.mTextView.setText(mData.get(position));
            //mEvent.get(position).getImage()
            holder.mTextView.setText(mGathering.get(position).getName());
            String testImageUrl="http://www.thoitrangtichtac.com/productimg/12000/11136/250_Dam_suong_tre_vai_tay_con_don_gian_b1136.jpg";
            Picasso.with(context).load(testImageUrl).into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mGathering.size();
        }
    }



    //dump classes and methods
    //read URL content
    private static String readURL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    //AsyncTask for loading data to UI
    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
/*            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray =  jsonObject.getJSONArray("products");

                for(int i =0;i<jsonArray.length(); i++){
                    JSONObject productObject = jsonArray.getJSONObject(i);
                    eventList.add(new Event(
                            productObject.getString("image"),
                            productObject.getString("name"),
                            productObject.getString("price")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            //load data to adapter
            //MyAdapter myAdapter = new MyAdapter(getApplicationContext(),myDataset, eventList);
            //recyclerView.setAdapter(myAdapter);
        }
    }
}
