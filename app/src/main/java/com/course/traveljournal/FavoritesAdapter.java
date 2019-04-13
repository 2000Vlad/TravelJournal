package com.course.traveljournal;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<TripViewHolder> implements Observer<List<Trip>> {
    private LiveData< List<Trip> > mTrips;
    private TripListener mListener;

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.trip_item,viewGroup, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder tripViewHolder, int position) {
        tripViewHolder.setIndex(position);
        TripCallback callback = (TripCallback) tripViewHolder;
        Trip currentTrip = mTrips.getValue().get(position);
        currentTrip.setFavorite(true);
        tripViewHolder.setTrip(currentTrip);
        callback.setFirebaseDocumentId(currentTrip.getFirebaseDocumentId());
        callback.setTripName(currentTrip.getTripName());
        callback.setTripDestination(currentTrip.getDestination());
        callback.setImageUrl(currentTrip.getImageUrl());
        callback.setTripType(currentTrip.getTripType());
        callback.setStartDate(currentTrip.getStartDate());
        callback.setEndDate(currentTrip.getEndDate());
        callback.setRating(currentTrip.getRating());
        callback.setPrice(currentTrip.getPrice());
        tripViewHolder.setListener(mListener);
        tripViewHolder.setPlainViewHolder();
    }

    @Override
    public int getItemCount() {
        if(mTrips.getValue() != null)
        return mTrips.getValue().size();
        else return  0;
    }

    @Override
    public void onChanged(@Nullable List<Trip> trips) {
        notifyDataSetChanged();
    }

    public LiveData<List<Trip>> getTrips() {
        return mTrips;
    }

    public void setTrips(LiveData<List<Trip>> trips) {
        mTrips = trips;
        mTrips.observe(mListener.getLifecycleOwner(),this);
        notifyDataSetChanged();
    }

    public TripListener getListener() {
        return mListener;
    }

    public void setListener(TripListener listener) {
        mListener = listener;
    }
}
