package com.course.traveljournal;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TripViewHolder extends RecyclerView.ViewHolder implements TripCallback {

    private ImageView mImageView;
    private TextView mTripNameTextView;
    private TextView mDestinationTextView;
    private TextView mRatingTextView;
    private TextView mPriceTextView;
    private ImageButton mBookmarkImageButton;
    private ImageButton mDeleteImageButton;
    private int mIndex;
    private boolean mBookmarked;
    private TripListener mListener;
    private Trip mTrip;


    public TripViewHolder(@NonNull final View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.image_trip);
        mTripNameTextView = itemView.findViewById(R.id.trip_name_text_trip);
        mDestinationTextView = itemView.findViewById(R.id.destination_text_trip);
        mRatingTextView = itemView.findViewById(R.id.text_rating_trip);
        mPriceTextView = itemView.findViewById(R.id.text_price_trip);
        mBookmarkImageButton = itemView.findViewById(R.id.imageview_bookmark_trip);
        mDeleteImageButton = itemView.findViewById(R.id.delete_imagebutton);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelected(TripViewHolder.this, mTrip, itemView, mIndex);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onClick(TripViewHolder.this, mTrip, itemView, mIndex);
                return true;
            }
        });
        mBookmarkImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFavoriteState();
            }
        });
        mDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        mTrip = new Trip();
    }

    private void showDialog(Trip trip, View view) {


    }

    private void initHandlers() {

    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public boolean isBookmarked() {
        return mBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        mBookmarked = bookmarked;
        if (bookmarked) mBookmarkImageButton.setImageResource(R.drawable.ic_bookmark_on);
        else mBookmarkImageButton.setImageResource(R.drawable.ic_bookmark_off);

    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public TextView getTripNameTextView() {
        return mTripNameTextView;
    }

    public void setTripNameTextView(TextView tripNameTextView) {
        mTripNameTextView = tripNameTextView;
    }

    public TextView getDestinationTextView() {
        return mDestinationTextView;
    }

    public void setDestinationTextView(TextView destinationTextView) {
        mDestinationTextView = destinationTextView;
    }

    public TextView getRatingTextView() {
        return mRatingTextView;
    }

    public void setRatingTextView(TextView ratingTextView) {
        mRatingTextView = ratingTextView;
    }

    public TextView getPriceTextView() {
        return mPriceTextView;
    }

    public void setPriceTextView(TextView priceTextView) {
        mPriceTextView = priceTextView;
    }

    public ImageView getBookmarkImageView() {
        return mBookmarkImageButton;
    }

    public void setBookmarkImageView(ImageButton bookmarkImageButton) {
        mBookmarkImageButton = bookmarkImageButton;
    }

    public TripListener getListener() {
        return mListener;
    }

    public void setListener(TripListener listener) {
        mListener = listener;
    }

    public void setTripName(String name) {
        mTripNameTextView.setText(name);
        mTrip.setTripName(name);
    }

    public void setTripDestination(String destination) {
        mDestinationTextView.setText(destination);
        mTrip.setDestination(destination);
    }

    public void setTripType(long type) {
        mTrip.setTripType(type);
    }

    public void setImageUrl(String url) {
        mTrip.setImageUrl(url);
        Glide.with(mImageView.getContext())
                .load(url)
                .into(mImageView);

    }

    public void setRating(double rating) {
        mTrip.setRating(rating);
        mRatingTextView.setText("" + rating);
    }

    public void setPrice(long price) {
        mTrip.setPrice(price);
        mPriceTextView.setText("" + price);
    }

    public void setStartDate(int year, int month, int day) {
        mTrip.setStartDate(day + "/" + month + "/" + year);

    }

    public void setStartDate(String startDate) {
        mTrip.setStartDate(startDate);
    }

    public void setEndDate(int year, int month, int day) {
        mTrip.setEndDate(day + "/" + month + "/" + year);
    }

    public void setEndDate(String endDate) {
        mTrip.setEndDate(endDate);
    }

    public void setFirebaseDocumentId(String id) {
        mTrip.setFirebaseDocumentId(id);
    }

    public String getFirebaseDocumentId() {
        return mTrip.getFirebaseDocumentId();
    }

    public Trip getTrip() {
        return mTrip;
    }

    public void setTrip(Trip trip) {
        mTrip = trip;
    }

    private void delete() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .collection("trips")
                .document(getFirebaseDocumentId())
                .delete();
    }

    private void switchFavoriteState() {
        if (!mTrip.isFavorite()) {
            //      RoomUtil.getInstance(mListener.getContext()).addTrip(mTrip);
            //      mTrip.setFavorite(true);
            setBookmarked(true);
            mListener.onSelectedFavorite(this, mTrip, itemView, mIndex);
        } else {
            //      RoomUtil.getInstance(mListener.getContext()).deleteTrip(mTrip);
            //      mTrip.setFavorite(false);
            setBookmarked(false);
            mListener.onDeselectedFavorite(this, mTrip, itemView, mIndex);
        }


    }

    public void setPlainViewHolder() {
        mBookmarkImageButton.setOnClickListener(null);
        mBookmarkImageButton.setEnabled(false);
        mBookmarkImageButton.setVisibility(View.GONE);

        mDeleteImageButton.setOnClickListener(null);
        mDeleteImageButton.setEnabled(false);
        mDeleteImageButton.setVisibility(View.GONE);
    }
}
