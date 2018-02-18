package com.bonlai.socialdiningapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.bonlai.socialdiningapp.adapter.MyRestaurantRecyclerViewAdapter;
import com.bonlai.socialdiningapp.detail.map.MapsActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestaurantFragment extends Fragment implements MyRestaurantRecyclerViewAdapter.LoadDataListener{

    private RecyclerView recyclerView;
    private MyRestaurantRecyclerViewAdapter mAdapter;
    private int pageNum=1;
    private boolean hasMoreData=true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantFragment() {
    }


/*    @SuppressWarnings("unused")
    public static RestaurantFragment newInstance(int columnCount) {
        RestaurantFragment fragment = new RestaurantFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(layoutManager);

        loadData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("scroll","called");
                if (!recyclerView.canScrollVertically(1)&&hasMoreData) {
                    //Toast.makeText(getContext(),"L",Toast.LENGTH_LONG).show();
                    Log.d("onscroll end","called");
                    mAdapter.showLoading();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.restaurant_search, menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //GatheringFragment.this.MyGatheringAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void loadData() {
        pageNum=1;
        hasMoreData=true;

        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Restaurant>> getRestaurantList = service.getRestaurantList(pageNum++);
        getRestaurantList.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if(response.isSuccessful()){
                    mAdapter=new MyRestaurantRecyclerViewAdapter(getContext(),response.body(),RestaurantFragment.this);
                    recyclerView.setAdapter(mAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void loadMoreData() {
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Restaurant>> getRestaurantList = service.getRestaurantList(pageNum);
        getRestaurantList.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful()) {
                    mAdapter.dismissLoading();
                    mAdapter.addMoreRestaurants(response.body());
                    mAdapter.setMore(true);
                    pageNum++;
                } else {
                    mAdapter.dismissLoading();
                    mAdapter.setMore(true);
                    hasMoreData = false;
                }
            }
            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
