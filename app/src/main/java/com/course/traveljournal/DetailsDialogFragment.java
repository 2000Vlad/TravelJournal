package com.course.traveljournal;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import static com.course.traveljournal.AddTripActivity.CITY_BREAK;
import static com.course.traveljournal.AddTripActivity.MOUNTAINS;
import static com.course.traveljournal.AddTripActivity.SEASIDE;

public class DetailsDialogFragment extends DialogFragment {
    private TextView mTripNameText;
    private TextView mDestinationText;
    private TextView mTripTypeText;
    private TextView mPriceText;
    private TextView mStartDateText;
    private TextView mEndDateText;
    private TextView mRatingText;
    private RatingBar mRatingBar;

    private Trip mTrip;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.details_list, null);
        initView(view);
        builder = builder.setView(view);



        return builder.create();
    }
    private void initView(View view){
        mTripNameText = view.findViewById(R.id.trip_name_details);
        mDestinationText = view.findViewById(R.id.destination_details);
        mTripTypeText = view.findViewById(R.id.trip_type_details);
        mPriceText = view.findViewById(R.id.price_details);
        mStartDateText = view.findViewById(R.id.start_date_details);
        mEndDateText = view.findViewById(R.id.end_date_details);
        mRatingBar = view.findViewById(R.id.details_ratingbar);
        mRatingText = view.findViewById(R.id.rating_details);

        mTripNameText.setText(mTrip.getTripName());
        mDestinationText.setText(mTrip.getDestination());
        switch ((int)mTrip.getTripType()){
            case SEASIDE :mTripTypeText.setText(getString(R.string.seaside));break;
            case CITY_BREAK:mTripTypeText.setText(getString(R.string.city_break));break;
            case MOUNTAINS:mTripTypeText.setText(getString(R.string.mountains));break;
        }
        mPriceText.setText(""+mTrip.getPrice());
        mStartDateText.setText(mTrip.getStartDate());
        mEndDateText.setText(mTrip.getEndDate());
        mRatingBar.setRating((float)mTrip.getRating());
        mRatingText.setText(""+mTrip.getRating());


    }

    public Trip getTrip() {
        return mTrip;
    }

    public void setTrip(Trip trip) {
        mTrip = trip;
    }
}
