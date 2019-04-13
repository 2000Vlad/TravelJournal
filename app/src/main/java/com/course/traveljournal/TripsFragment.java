package com.course.traveljournal;


import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.course.traveljournal.AddTripActivity.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment implements TripListener {
    private RecyclerView mRecyclerView;
    private TripAdapter mAdapter;


    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mRecyclerView = view.findViewById(R.id.trips_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TripAdapter();
        mAdapter.setListener(this);
          FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .collection("trips")
                .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          Query query = task.getResult().getQuery();
                          mAdapter.setQuery(query);
                          mRecyclerView.setAdapter(mAdapter);
                      }
                  });


    }

    @Override
    public void onClick(TripCallback callback, Trip trip, View view, int i) {
        Intent intent = new Intent(getActivity(),AddTripActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt(ACTION_KEY,EDIT_ITEM);
        bundle.putString(TRIP_NAME_KEY, trip.getTripName());
        bundle.putString(DESTINATION_KEY, trip.getDestination());
        bundle.putLong(TRIP_TYPE_KEY, trip.getTripType());
        bundle.putDouble(RATING_KEY, trip.getRating());
        bundle.putLong(PRICE_KEY, trip.getPrice());
        bundle.putString(START_DATE_KEY, trip.getStartDate());
        bundle.putString(END_DATE_KEY, trip.getEndDate());
        bundle.putString(FIREBASE_DOCUMENT_ID_KEY, trip.getFirebaseDocumentId());
        bundle.putString(IMAGE_URL_KEY,trip.getImageUrl());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onSelected(TripCallback callback, Trip trip, View view, int i){
        DetailsDialogFragment dialogFragment = new DetailsDialogFragment();
        dialogFragment.setTrip(trip);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Details Dialog");
    }
    @Override
    public void onDeleted(TripCallback callback, Trip trip, View view, int i) {
      if(trip.isFavorite()) RoomUtil.getInstance(getContext()).deleteTrip(trip);
    }

    @Override
    public void onSelectedFavorite(TripCallback callback, Trip trip, View view, int i) {
        trip.setFavorite(true);
        RoomUtil.getInstance(getContext()).addTrip(trip);
    }

    @Override
    public void onDeselectedFavorite(TripCallback callback, Trip trip, View view, int i) {
        trip.setFavorite(false);
        RoomUtil.getInstance(getContext()).deleteTrip(trip);
    }

    @Override
    public void onModified(TripCallback callback, Trip trip, View view, int i){
        if(trip.isFavorite())
            RoomUtil.getInstance(getContext()).updateTrips(trip);

    }
    public LifecycleOwner getLifecycleOwner(){
        return this;
    }
}
