package com.course.traveljournal;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.view.View;

public interface TripListener {
   void onClick(TripCallback callback, Trip trip, View view, int i);
   void onSelected(TripCallback callback, Trip trip, View view, int i);
   void onDeleted(TripCallback callback, Trip trip, View view, int i);
   void onSelectedFavorite(TripCallback callback, Trip trip, View view, int i);
   void onDeselectedFavorite(TripCallback callback, Trip trip, View view, int i);
   Context getContext();
   void onModified(TripCallback callback, Trip trip, View view, int i);
   LifecycleOwner getLifecycleOwner();
}
