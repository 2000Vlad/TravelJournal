package com.course.traveljournal;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

//Repository
public class RoomUtil {

    private TripDao mDao;
    private TripDatabase mDatabase;
    private LiveData<List<Trip>> mAllTrips;

    private static RoomUtil INSTANCE;

    public static RoomUtil getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new RoomUtil(context);
        }
        return INSTANCE;
    }

    private RoomUtil(Context context){
        mDatabase = TripDatabase.getDatabase(context);
        mDao = mDatabase.tripDao();
        mAllTrips = mDao.getAllTrips();
    }

     public LiveData<List<Trip>> getAllTrips(){
        return mAllTrips;
     }

     public Trip getTrip(String docId){
        try {
            return (new LoadTripAsync()).execute(docId).get();
        } catch (InterruptedException e) {
            e.printStackTrace(); return null;
        } catch (ExecutionException e) {
            e.printStackTrace(); return null;
        }
     }

     public void addTrip(Trip trip){
         (new InsertTripAsync()).execute(trip);
     }

     public void updateTrips(Trip... trips){
         (new UpdateTripsAsync()).execute(trips);
     }
     public void deleteTrip(Trip trip){
         ( new DeleteItemAsync() ).execute(trip.getFirebaseDocumentId());
     }

     public boolean existsTrip(Trip trip){
        try {
            Trip result = (new LoadTripAsync()).execute(trip.getFirebaseDocumentId()).get();
            return result != null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
     }


    private class LoadTripAsync extends AsyncTask<String,Void,Trip> {

        @Override
        protected Trip doInBackground(String... strings) {
            String docId = strings[0];
            Trip trip = mDao.getTripByDocumentId(docId);
            return trip;
        }
    }

    private class InsertTripAsync extends AsyncTask<Trip,Void,Void> {

        @Override
        protected Void doInBackground(Trip... trips) {

            Trip trip = trips[0];
            mDao.insertTrip(trip);
            return null;
        }
    }

    private class DeleteItemAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String docId = strings[0];
            mDao.deleteTrip(docId);
            return null;
        }
    }

    private class LoadTripsAsync extends AsyncTask<Void,Void, LiveData<List<Trip>> > {

        @Override
        protected LiveData<List<Trip>> doInBackground(Void... voids) {
            return mDao.getAllTrips();
        }
    }

    private class UpdateTripsAsync extends AsyncTask<Trip, Void, Void> {

        @Override
        protected Void doInBackground(Trip... trips) {
            mDao.updateTrips(trips);
            return null;
        }
    }
}
