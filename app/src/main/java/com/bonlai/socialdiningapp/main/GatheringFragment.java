package com.bonlai.socialdiningapp.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bonlai.socialdiningapp.helpers.MyReceiver;
import com.bonlai.socialdiningapp.detail.gathering.GatheringSearchActivity;
import com.bonlai.socialdiningapp.MainActivity;
import com.bonlai.socialdiningapp.adapter.MyGatheringAdapter;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.map.MapsActivity;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class GatheringFragment extends Fragment implements View.OnClickListener,MyGatheringAdapter.LoadDataListener {

    public static enum Mode {
        ALL,
        PAST,
        CREATED,
        JOINED
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MODE = "mode";


    private Mode mMode;
    private int myUserId;
    private boolean isPrepared;
    private int pageNum=1;
    private Map<String, String> queryOption = new HashMap<>();
    private boolean hasMoreData=true;
    private boolean returnedSearchResult=false;
    static final int SEARCH_REQUEST = 1;

    private RecyclerView recyclerView;
    private FloatingActionButton mAddGathering;
    private PullRefreshLayout mPullRefresh;
    private ProgressBar mProgress;
    private RelativeLayout mContainer;
    private TextView noRecord;

    private MyGatheringAdapter myAdapter;
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
        setRetainInstance(true);
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
        noRecord=(TextView) rootView.findViewById(R.id.no_record);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        myUserId=MyUserHolder.getInstance().getUser().getPk();
        isPrepared=true;

        mPullRefresh = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

// listen loadData event
        mPullRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("scroll","called");
                if (!recyclerView.canScrollVertically(1)&&hasMoreData&&mMode==Mode.ALL) {
                    //Toast.makeText(getContext(),"L",Toast.LENGTH_LONG).show();
                    Log.d("onscroll end","called");
                    myAdapter.showLoading();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity_","onStart");
        loadData();
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
                loadData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNewGathering:
                ((MainActivity)getActivity()).navigateToRestList();
                Toast toast = Toast.makeText(getActivity(), "Please select a place for gathering", Toast.LENGTH_LONG);
                toast.show();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.gatheirng_search, menu);

        //set searchview in action bar
        MenuItem search = menu.findItem(R.id.action_search);
        MenuItem back = menu.findItem(R.id.action_back);
        MenuItem searchview = menu.findItem(R.id.action_searchview);
        MenuItem map = menu.findItem(R.id.action_map);

        if(returnedSearchResult){
            search.setVisible(false);
            back.setVisible(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Filtered Result");
        }else{
            search.setVisible(true);
            back.setVisible(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        }

        if(mMode==Mode.ALL){
            searchview.setVisible(false);
        }else{
            search.setVisible(false);
            map.setVisible(false);

            SearchView searchView=(SearchView)searchview.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String searchText) {
                    myAdapter.getFilter().filter(searchText);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String searchText) {
                    myAdapter.getFilter().filter(searchText);
                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_search:
                intent = new Intent(getContext(), GatheringSearchActivity.class);
                startActivityForResult(intent,SEARCH_REQUEST);
                return true;

            case R.id.action_back:
                returnedSearchResult=false;
                queryOption.clear();
                loadQueryData();
                getActivity().invalidateOptionsMenu();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_REQUEST) {
            if (resultCode == RESULT_OK) {
                returnedSearchResult=true;
                getActivity().invalidateOptionsMenu();
                String keyword=data.getStringExtra(GatheringSearchActivity.KEYWORD);
                String location=data.getStringExtra(GatheringSearchActivity.LOCATION);
                String minCount=data.getStringExtra(GatheringSearchActivity.MIN_COUNT);
                String maxCount=data.getStringExtra(GatheringSearchActivity.MAX_COUNT);
                String startDate=data.getStringExtra(GatheringSearchActivity.START_DATE);
                String endDate=data.getStringExtra(GatheringSearchActivity.END_DATE);

                queryOption.put("search", keyword);
                queryOption.put("location",location);
                queryOption.put("count_greater",minCount);
                queryOption.put("count_less",maxCount);
                queryOption.put("start_date",startDate);
                queryOption.put("end_date",endDate);

                loadQueryData();
            }
        }
    }

    public void loadQueryData() {
        pageNum=1;
        hasMoreData=true;

        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<List<Gathering>> getGatheringList = service.getGatheringList(pageNum++,queryOption);
        Log.d("page no in load data",""+pageNum);
        getGatheringList.enqueue(new Callback<List<Gathering>>() {
            @Override
            public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                myAdapter.resetGatherings(response.body());
            }
            @Override
            public void onFailure(Call<List<Gathering>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void loadData(){

        pageNum=1;
        hasMoreData=true;
        mProgress.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.GONE);
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        switch (mMode){
            case ALL:
                Call<List<Gathering>> getGatheringList = service.getGatheringList(pageNum++,queryOption);
                Log.d("page no in load data",""+pageNum);
                getGatheringList.enqueue(new Callback<List<Gathering>>() {
                    @Override
                    public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                        Log.d("fragment","called");
                        myAdapter = new MyGatheringAdapter(getContext(), response.body(),GatheringFragment.this);
                        recyclerView.setAdapter(myAdapter);
                        // loadData complete
                        mProgress.setVisibility(View.GONE);
                        mContainer.setVisibility(View.VISIBLE);
                        if(response.body().isEmpty()){
                            noRecord.setVisibility(View.VISIBLE);
                        }
                        mPullRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<List<Gathering>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
            case PAST:
                Call<List<Gathering>> getMyGatheringList = service.getMyGatheringList(myUserId);
                getMyGatheringList.enqueue(new Callback<List<Gathering>>() {
                    @Override
                    public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {

                        myAdapter = new MyGatheringAdapter(getContext(), response.body(),GatheringFragment.this);
                        recyclerView.setAdapter(myAdapter);
                        // loadData complete
                        mProgress.setVisibility(View.GONE);
                        mContainer.setVisibility(View.VISIBLE);
                        if(response.body().isEmpty()){
                            noRecord.setVisibility(View.VISIBLE);
                        }
                        mPullRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<List<Gathering>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
            case JOINED:
                Call<List<Gathering>> getJoinedGatheringList = service.getJoinedGatheringList(myUserId);
                getJoinedGatheringList.enqueue(new Callback<List<Gathering>>() {
                    @Override
                    public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {

                        myAdapter = new MyGatheringAdapter(getContext(), response.body(),GatheringFragment.this);
                        recyclerView.setAdapter(myAdapter);
                        // loadData complete
                        mProgress.setVisibility(View.GONE);
                        mContainer.setVisibility(View.VISIBLE);
                        if(response.body().isEmpty()){
                            noRecord.setVisibility(View.VISIBLE);
                        }
                        mPullRefresh.setRefreshing(false);
                        setNotification(response.body());
                    }
                    @Override
                    public void onFailure(Call<List<Gathering>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
            case CREATED:
                Call<List<Gathering>> getCreatedGatheringList = service.getCreatedGatheringList(myUserId);
                getCreatedGatheringList.enqueue(new Callback<List<Gathering>>() {
                    @Override
                    public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {

                        myAdapter = new MyGatheringAdapter(getContext(), response.body(),GatheringFragment.this);
                        recyclerView.setAdapter(myAdapter);
                        // loadData complete
                        mProgress.setVisibility(View.GONE);
                        mContainer.setVisibility(View.VISIBLE);
                        if(response.body().isEmpty()){
                            noRecord.setVisibility(View.VISIBLE);
                        }
                        mPullRefresh.setRefreshing(false);
                        setNotification(response.body());
                    }
                    @Override
                    public void onFailure(Call<List<Gathering>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
        }
    }

    private void setNotification(List<Gathering> gatherings){
        for(Gathering g:gatherings){
            Intent notifyIntent = new Intent(getActivity(),MyReceiver.class);
            notifyIntent.putExtra("id", g.getId());
            notifyIntent.putExtra("datetime", g.getStartDatetime());
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (getActivity(), g.getId(), notifyIntent,PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date d = f.parse(g.getStartDatetime());
                Log.d("GatheringFrag","Set alarm"+d.getTime());
                //Log.d("GatheringFrag","Set alarm"+System.currentTimeMillis());
                alarmManager.set(AlarmManager.RTC,  d.getTime()-10000*60*30,pendingIntent);
            } catch (ParseException e) {
                //Handle exception here, most of the time you will just log it.
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadMoreData(){
        if(mMode==Mode.ALL){
            AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
            Call<List<Gathering>> getGatheringList = service.getGatheringList(pageNum,queryOption);
            Log.d("page no",""+pageNum);
            getGatheringList.enqueue(new Callback<List<Gathering>>() {

                @Override
                public void onResponse(Call<List<Gathering>> call, Response<List<Gathering>> response) {
                    if(response.isSuccessful()){
                        myAdapter.dismissLoading();
                        myAdapter.addMoreGathering(response.body());
                        myAdapter.setMore(true);
                        pageNum++;
                    }else{
                        myAdapter.dismissLoading();
                        myAdapter.setMore(true);
                        hasMoreData=false;
                    }
                    //recyclerView.setAdapter(myAdapter);
                    // loadData complete
                }
                @Override
                public void onFailure(Call<List<Gathering>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
