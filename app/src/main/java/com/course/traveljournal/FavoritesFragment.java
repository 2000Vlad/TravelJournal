package com.course.traveljournal;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements TripListener {

    private RecyclerView mRecyclerView;
    private FavoritesAdapter mAdapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initView(view);
        return view;


    }
    private void initView(View view){
        mRecyclerView = view.findViewById(R.id.recycler_favorites);
        mAdapter = new FavoritesAdapter();
        mAdapter.setListener(this);
        mAdapter.setTrips(RoomUtil.getInstance(getContext()).getAllTrips());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(TripCallback callback, Trip trip, View view, int i) {

    }

    @Override
    public void onSelected(TripCallback callback, Trip trip, View view, int i) {
      DetailsDialogFragment dialogFragment = new DetailsDialogFragment();
      dialogFragment.setTrip(trip);
      dialogFragment.show(getActivity().getSupportFragmentManager(), "Details");
    }

    @Override
    public void onDeleted(TripCallback callback, Trip trip, View view, int i) {

    }

    @Override
    public void onSelectedFavorite(TripCallback callback, Trip trip, View view, int i) {

    }

    @Override
    public void onDeselectedFavorite(TripCallback callback, Trip trip, View view, int i) {

    }

    @Override
    public void onModified(TripCallback callback, Trip trip, View view, int i) {

    }

    public LifecycleOwner getLifecycleOwner(){
        return this;
    }
}
