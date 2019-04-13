package com.course.traveljournal;

public interface TripCallback {
    void setTripName(String name);
    void setTripDestination(String destination);
    void setTripType(long type);
    void setImageUrl(String url);
    void setRating(double rating);
    void setPrice(long price);
    void setStartDate(int year, int month, int day);
    void setStartDate(String startDate);
    void setEndDate(int year, int month, int day);
    void setEndDate(String endDate);
    void setBookmarked(boolean bookmarked);
    void setFirebaseDocumentId(String id);

}
