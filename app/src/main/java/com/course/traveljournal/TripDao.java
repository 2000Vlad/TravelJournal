package com.course.traveljournal;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TripDao {
    @Query("Select * from trips where document_id=:docId")
    Trip getTripByDocumentId(String docId); //Firebase document id

    @Insert
    void insertTrip(Trip trip);

    @Query("delete from trips where document_id = :docId")
    void deleteTrip(String docId);

    @Query("Select * from trips")
    LiveData< List<Trip> >getAllTrips();

    @Update
    void updateTrips(Trip...trips);


}
