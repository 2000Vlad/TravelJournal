package com.course.traveljournal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.course.traveljournal.AddTripActivity.DESTINATION_KEY;
import static com.course.traveljournal.AddTripActivity.END_DATE_KEY;
import static com.course.traveljournal.AddTripActivity.IMAGE_URL_KEY;
import static com.course.traveljournal.AddTripActivity.PRICE_KEY;
import static com.course.traveljournal.AddTripActivity.RATING_KEY;
import static com.course.traveljournal.AddTripActivity.START_DATE_KEY;
import static com.course.traveljournal.AddTripActivity.TRIP_NAME_KEY;
import static com.course.traveljournal.AddTripActivity.TRIP_TYPE_KEY;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> implements EventListener<QuerySnapshot> {
    private Query mQuery;
    private ListenerRegistration mRegistration;
    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();
    private TripListener mListener;

    private void startListening() {
        if (mRegistration == null)
            mRegistration = mQuery.addSnapshotListener(this);
    }

    private void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
        mSnapshots.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e("Exception in Adapter", Log.getStackTraceString(e));
            return;
        }
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    addedItem(change); break;
                case MODIFIED:
                    modifiedItem(change);break;
                case REMOVED:
                    removedItem(change); break;
            }
        }
    }

    private void addedItem(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    private void removedItem(DocumentChange change) {
        Trip deletedItem = Trip.fromSnapshot( change.getDocument(), mListener.getContext() );
        //if(deletedItem.isFavorite()) RoomUtil.getInstance(mListener.getContext()).deleteTrip(deletedItem);
        mListener.onDeleted(null, deletedItem, null, -1 );
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());

    }

    private void modifiedItem(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());

        } else {
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
        Trip modifiedTrip = Trip.fromSnapshot(change.getDocument(),mListener.getContext());
        mListener.onModified(null, modifiedTrip, null, -1);
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.trip_item, viewGroup, false);
        return new TripViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder tripViewHolder, int position) {
        tripViewHolder.setIndex(position);
        TripCallback callback = tripViewHolder;
        ((TripViewHolder) callback).setListener(mListener);
        Map<String, Object> data = mSnapshots.get(position).getData();
        DocumentSnapshot snapshot = mSnapshots.get(position);
        String tripName = (String) data.get(TRIP_NAME_KEY);
        String destination = (String) data.get(DESTINATION_KEY);
        long tripType = (long) data.get(TRIP_TYPE_KEY);
        String startDate = (String) data.get(START_DATE_KEY);
        String endDate = (String) data.get(END_DATE_KEY);
        Double rating =  snapshot.getDouble(RATING_KEY);
        Long price =  snapshot.getLong(PRICE_KEY);
        String downloadUrl = (String) data.get(IMAGE_URL_KEY);
        Trip trip = Trip.fromSnapshot(snapshot,mListener.getContext());
        callback.setTripName(tripName);
        callback.setTripDestination(destination);
        callback.setTripType(tripType);
        callback.setStartDate(startDate);
        callback.setEndDate(endDate);
        callback.setPrice(price);
        callback.setRating(rating);
        callback.setImageUrl(downloadUrl);
        callback.setFirebaseDocumentId(snapshot.getReference().getId());
        callback.setBookmarked(trip.isFavorite());
        tripViewHolder.getTrip().setFavorite(trip.isFavorite());

    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public TripListener getListener() {
        return mListener;
    }

    public void setListener(TripListener listener) {
        mListener = listener;
    }

    public Query getQuery() {
        return mQuery;
    }

    public void setQuery(Query query) {
        stopListening();
        mSnapshots.clear();
        mQuery = query;
        startListening();

    }
}
