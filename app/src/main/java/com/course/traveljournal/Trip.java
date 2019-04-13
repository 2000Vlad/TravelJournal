package com.course.traveljournal;

import android.app.Application;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;


import static com.course.traveljournal.AddTripActivity.*;

import java.util.Map;

@Entity(tableName = "trips")
public class Trip {
    public static final int SEASIDE = 1003;
    public static final int CITY_BREAK = 1002;
    public static final int MOUNTAINS = 1004;
    @ColumnInfo(name = "trip_name")
    private String mTripName;
    @ColumnInfo(name = "destination")
    private String mDestination;
    @ColumnInfo(name = "image_url")
    private String mImageUrl;
    @ColumnInfo(name = "start_date")
    private String mStartDate;
    @ColumnInfo(name = "end_date")
    private String mEndDate;
    @ColumnInfo(name = "trip_type")
    private long mTripType;
    @ColumnInfo(name = "price")
    private long mPrice;
    @ColumnInfo(name = "rating")
    private double mRating;
    @ColumnInfo(name = "picture_name")
    private String mFirebasePictureFileName;
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "document_id")
    private String mFirebaseDocumentId;
    @Ignore
    private boolean mFavorite;

    public static Trip fromSnapshot(DocumentSnapshot snapshot ,Context context) {
        Map<String, Object> data = snapshot.getData();
        Trip result = new Trip();
        result.setFirebaseDocumentId(snapshot.getId());
        result.setFirebasePictureFileName(snapshot.getString(IMAGE_NAME_KEY));
        result.setImageUrl((String) snapshot.getString(IMAGE_URL_KEY));
        result.setTripName(snapshot.getString(TRIP_NAME_KEY));
        result.setDestination(snapshot.getString(DESTINATION_KEY));
        result.setTripType(snapshot.getLong(TRIP_TYPE_KEY));
        result.setStartDate(snapshot.getString(START_DATE_KEY));
        result.setEndDate(snapshot.getString(END_DATE_KEY));
        result.setPrice(snapshot.getLong(PRICE_KEY));
        result.setRating(snapshot.getDouble(RATING_KEY));

        RoomUtil util = RoomUtil.getInstance(context);
        boolean existsTrip = util.existsTrip(result);
        result.setFavorite(existsTrip);
        return result;

    }

    public String getTripName() {
        return mTripName;
    }

    public void setTripName(String tripName) {
        mTripName = tripName;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public long getTripType() {
        return mTripType;
    }

    public void setTripType(long tripType) {
        mTripType = tripType;
    }

    public long getPrice() {
        return mPrice;
    }

    public void setPrice(long price) {
        mPrice = price;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        mRating = rating;
    }

    public String getFirebasePictureFileName() {
        return mFirebasePictureFileName;
    }

    public void setFirebasePictureFileName(String firebasePictureFileName) {
        mFirebasePictureFileName = firebasePictureFileName;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public String getFirebaseDocumentId() {
        return mFirebaseDocumentId;
    }

    public void setFirebaseDocumentId(String firebaseDocumentId) {
        mFirebaseDocumentId = firebaseDocumentId;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }
}
