package com.course.traveljournal;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PreviewDialogFragment extends DialogFragment {
    private ImageView mImageView;
    private TextView mTripNameText;
    private TextView mDestinationText;
    private TextView mPriceText;
    private TextView mRatingText;

    private Bitmap mBitmap;
    private String mTripName;
    private int mPrice;
    private float mRating;
    private String mDestination;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cardview_preview,null);
        initView(view);
        builder.setView(view);
        populateView();
        return builder.create();




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initView(View view){
        mTripNameText = view.findViewById(R.id.trip_name_text_preview);
        mDestinationText = view.findViewById(R.id.destination_text_preview);
        mRatingText = view.findViewById(R.id.text_rating_preview);
        mPriceText = view.findViewById(R.id.text_price_preview);
        mImageView = view.findViewById(R.id.image_preview);
    }


    private void populateView(){
        mTripNameText.setText(mTripName);
        mImageView.setImageBitmap(mBitmap);
        mPriceText.setText(""+mPrice);
        mRatingText.setText(""+mRating);
        mDestinationText.setText(mDestination);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getTripName() {
        return mTripName;
    }

    public void setTripName(String tripName) {
        mTripName = tripName;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }
}
