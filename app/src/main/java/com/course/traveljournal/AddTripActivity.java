package com.course.traveljournal;

import android.content.ContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddTripActivity extends AppCompatActivity implements OnDateReceivedListener {

    public static final String TRIP_NAME_KEY = "trip_name";
    public static final String DESTINATION_KEY = "destination";
    public static final String PRICE_KEY = "price";
    public static final String TRIP_TYPE_KEY = "trip_type";
    public static final String START_DATE_KEY = "start_date";
    public static final String END_DATE_KEY = "end_date";
    public static final String RATING_KEY = "rating";
    public static final String IMAGE_INDEX_KEY = "image_index";
    public static final String IMAGE_URL_KEY = "image_url";
    public static final String TEMP_IMAGE_FILE = "cameraImageTempFile";
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String IMAGE_NAME_KEY = "image_name";
    public static final String FIREBASE_DOCUMENT_ID_KEY = "firebaseDocumentId";
    public static final String ACTION_KEY = "action";

    public static final int RC_GALLERY = 1000;
    public static final int RC_CAMERA = 1001;


    public static final int CITY_BREAK = 1002;
    public static final int SEASIDE = 1003;
    public static final int MOUNTAINS = 1004;
    public static final int TRIP_TYPE_NOT_SELECTED = 1005;
    public static final int CAMERA_SELECTED = 1006;
    public static final int GALLERY_SELECTED = 1007;
    public static final int NO_IMAGE_SELECTED = 1008;
    public static final int DC_START = 1009;
    public static final int DC_END = 1010;
    public static final int THUMBNAIL_SIZE = 128;
    public static final int ADD_ITEM = 1011;
    public static final int EDIT_ITEM = 1012;


    private EditText mTripNameText;
    private EditText mDestinationText;
    private RadioButton mCityBreakRadioButton;
    private RadioButton mSeasideRadioButton;
    private RadioButton mMountainsRadioButton;
    private SeekBar mPriceBar;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private RatingBar mRatingBar;
    private Button mGalleryButton;
    private Button mCameraButton;
    private Button mPreviewButton;
    private Button mSaveButton;
    private ImageView mGalleryPreview;
    private ImageView mCameraPreview;
    private ProgressBar mProgressBar;

    private int mImageSelection;
    private boolean mStartDateSelected;
    private boolean mEndDateSelected;
    private Bitmap mSelectedBitmap;
    private String mCameraImagePath;
    private Uri mCameraImageUri;
    private Uri mCurrentFirebaseImageUrl;
    private String mCurrentFirebaseImageName;
    private String mOldEditTripName;
    private String mOldEditDestination;
    private int mOldEditTripType;
    private String mOldEditStartDate;
    private String mOldEditEndDate;
    private long mOldEditPrice;
    private double mOldEditRating;
    private String mEditingDocumentId;
    private String mOldEditImageUrl;

    private int mAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        initView();
        mImageSelection = NO_IMAGE_SELECTED;
        mStartDateSelected = false;
        mEndDateSelected = false;
        mAction = getIntent().getExtras().getInt(ACTION_KEY);
        if(mAction == EDIT_ITEM)
        beginEdit();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case RC_GALLERY:
                handleGalleryIntent(data);
                break;
            case RC_CAMERA:
                handleCameraIntent(data);
                break;
        }
    }

    @Override
    public void onDateReceived(int year, int month, int day, int dateCode) {
        String dateString = "" + day + "/" + (month + 1) + "/" + year;
        if (dateCode == DC_START) {
            mStartDateButton.setText(dateString);
            mStartDateSelected = true;
        }
        if (dateCode == DC_END) {
            mEndDateButton.setText(dateString);
            mEndDateSelected = true;
        }

    }

    private void beginEdit() {
        Intent editIntent = getIntent();
        Bundle bundle = editIntent.getExtras();
        //TODO Extract data received from TripsFragment
        mAction = bundle.getInt(ACTION_KEY);
        mOldEditTripName = bundle.getString(TRIP_NAME_KEY);
        mOldEditDestination = bundle.getString(DESTINATION_KEY);
        mOldEditTripType = (int) bundle.getLong(TRIP_TYPE_KEY);
        mOldEditStartDate = bundle.getString(START_DATE_KEY);
        mOldEditEndDate = bundle.getString(END_DATE_KEY);
        mOldEditPrice = bundle.getLong(PRICE_KEY);
        mOldEditRating = bundle.getDouble(RATING_KEY);
        mEditingDocumentId = bundle.getString(FIREBASE_DOCUMENT_ID_KEY);
        mOldEditImageUrl = bundle.getString(IMAGE_URL_KEY);

        mTripNameText.setText(mOldEditTripName);
        mDestinationText.setText(mOldEditDestination);
        mPriceBar.setProgress((int) mOldEditPrice);
        mRatingBar.setRating((float) mOldEditRating);
        mStartDateButton.setText(mOldEditStartDate);
        mEndDateButton.setText(mOldEditEndDate);
        Glide.with(this)
                .load(mOldEditImageUrl)
                .into(mGalleryPreview);
        switch (mOldEditTripType)
        {
            case SEASIDE : mSeasideRadioButton.setChecked(true);break;
            case CITY_BREAK : mCityBreakRadioButton.setChecked(true);break;
            case MOUNTAINS : mMountainsRadioButton.setChecked(true);break;
        }


    }

    private void initView() {

        mTripNameText = findViewById(R.id.edittext_trip_name);
        mDestinationText = findViewById(R.id.edittext_destination);
        mCityBreakRadioButton = findViewById(R.id.radiobutton_city_break);
        mSeasideRadioButton = findViewById(R.id.radiobutton_sea_side);
        mMountainsRadioButton = findViewById(R.id.radiobutton_mountains);
        mPriceBar = findViewById(R.id.seekbar_price);
        mRatingBar = findViewById(R.id.ratingbar_trip);
        mGalleryButton = findViewById(R.id.button_gallery);
        mCameraButton = findViewById(R.id.button_take_picture);
        mSaveButton = findViewById(R.id.button_save_trip);
        mPreviewButton = findViewById(R.id.button_preview);
        mStartDateButton = findViewById(R.id.button_start_date);
        mEndDateButton = findViewById(R.id.button_end_date);
        mGalleryPreview = findViewById(R.id.image_gallery_preview);
        mCameraPreview = findViewById(R.id.image_camera_preview);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

    }

    private void handleGalleryIntent(Intent data) {

        if (mSelectedBitmap != null)
            mSelectedBitmap.recycle();

        Uri galleryImageUri = data.getData();
        try {
            mSelectedBitmap = getThumbnail(galleryImageUri);
        } catch (IOException e) {
            Log.e("IOException", "IOException");
        }
        Glide.with(this)
                .load(galleryImageUri)
                .apply(new RequestOptions().override(48, 48))
                .into(mGalleryPreview);


        mImageSelection = GALLERY_SELECTED;

        resetCameraPreview();


    }

    public Bitmap getThumbnail(String urlAddress) throws MalformedURLException, IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(urlAddress);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(url.openStream(), null, onlyBoundsOptions);

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//

        Bitmap bitmap = BitmapFactory.decodeStream(url.openStream(), null, bitmapOptions);

        return bitmap;
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    private void handleCameraIntent(Intent data) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraImageUri);
            mCameraPreview.setImageBitmap(bitmap);
            if (mSelectedBitmap != null)
                mSelectedBitmap.recycle();
            mSelectedBitmap = bitmap;

            mImageSelection = CAMERA_SELECTED;
            resetGalleryPreview();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

    }

    private void resetCameraPreview() {
        if (mCameraImagePath != null && new File(mCameraImagePath).exists())
            (new File(mCameraImagePath)).delete();
        mCameraImagePath = null;
        mCameraPreview.setImageResource(R.drawable.ic_placeholder);
        mCameraImageUri = null;


    }

    private void resetGalleryPreview() {
        mGalleryPreview.setImageResource(R.drawable.ic_placeholder);


    }

    private String getTripName() {
        String text = mTripNameText.getText().toString();
        if (mAction == ADD_ITEM)
            return text.toString();
        else return text == null || text.isEmpty() ? mOldEditTripName : text;
    }

    private String getDestination() {
        String text = mDestinationText.getText().toString();
        if (mAction == ADD_ITEM)
            return text.toString();
        else return text == null || text.isEmpty() ? mOldEditDestination : text;
    }

    private int getTripType() {
        if (mCityBreakRadioButton.isChecked()) return CITY_BREAK;
        if (mSeasideRadioButton.isChecked()) return SEASIDE;
        if (mMountainsRadioButton.isChecked()) return MOUNTAINS;
        if (mAction == ADD_ITEM)
            return TRIP_TYPE_NOT_SELECTED;
        else return mOldEditTripType;
    }

    private int getPrice() {
        int price = mPriceBar.getProgress();
        if (mAction == ADD_ITEM)
            return mPriceBar.getProgress();
        else if (price == 0) return (int) mOldEditPrice;
        else return price;
    }

    private float getRating() {
        float rating = mRatingBar.getRating();
        if (mAction == ADD_ITEM)
            return mRatingBar.getRating();
        else if (rating == 0)
            return (float) mOldEditRating;
        else return rating;
    }

    public void selectStartDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateCode(DC_START);
        newFragment.setListener(this);
        newFragment.show(getSupportFragmentManager(), "Pick Start Date");
    }

    public void selectEndDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateCode(DC_END);
        newFragment.setListener(this);
        newFragment.show(getSupportFragmentManager(), "Pick End Date");
    }

    public String getStartDate() {

        return mStartDateButton.getText().toString();
    }

    public String getEndDate() {
        return mEndDateButton.getText().toString();

    }

    public void save(View view) {

        List<String> errors = getErrorMessages();
        if (errors.size() == 0 || mAction == EDIT_ITEM) {
            mProgressBar.setVisibility(View.VISIBLE);
            uploadToFirebase();
            return;
        }
        String message = "";
        for (String error : errors)
            message = message
                    .concat("-")
                    .concat(error)
                    .concat(NEWLINE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.reintroduce_input)
                .setCancelable(true)
                .setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void uploadTripObject() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        HashMap<String, Object> values = new HashMap<>();
        values.put(TRIP_NAME_KEY, ((getTripName() == null || getTripName().isEmpty()) && mAction == EDIT_ITEM) ? mOldEditTripName : getTripName());
        values.put(DESTINATION_KEY, ((getDestination() == null || getDestination().isEmpty()) && mAction == EDIT_ITEM) ? mOldEditDestination : getDestination());
        values.put(TRIP_TYPE_KEY, (getTripType() == TRIP_TYPE_NOT_SELECTED) ? mOldEditTripType : getTripType());
        values.put(PRICE_KEY, (getPrice() == 0 && mAction == EDIT_ITEM) ? mOldEditPrice : getPrice());
        values.put(RATING_KEY, (getRating() == 0 && mAction == EDIT_ITEM ? mOldEditRating : getRating()));
        values.put(START_DATE_KEY, mStartDateSelected || mAction == ADD_ITEM ? getStartDate() : mOldEditStartDate);
        values.put(END_DATE_KEY, mEndDateSelected || mAction == ADD_ITEM ? getEndDate() : mOldEditEndDate);
        if (mImageSelection != NO_IMAGE_SELECTED) {
            values.put(IMAGE_URL_KEY, mCurrentFirebaseImageUrl.toString());
            values.put(IMAGE_NAME_KEY, mCurrentFirebaseImageName);
        }

        DocumentReference document;
        if (mAction == ADD_ITEM) {
            document = firestore.collection("users")
                    .document(email)
                    .collection("trips")
                    .document();
        } else {
            document = firestore.collection("users")
                    .document(email)
                    .collection("trips")
                    .document(mEditingDocumentId);

        }
        Task<Void> task;
        if (mAction == ADD_ITEM) {
            task = document.set(values);
        } else {
            task = document.set(values, SetOptions.merge());
        }
        task
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(mAction == EDIT_ITEM)

                        mProgressBar.setVisibility(View.GONE);
                        finish();
                    }
                });
    }

    private void deleteImageFromStorage(String documentId) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .collection("trips")
                .document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String imageName = (String) task.getResult().get(IMAGE_NAME_KEY);
                       // continueDeleteImageFromStorage(imageName);
                    }
                });
    }

    private void continueDeleteImageFromStorage(String picName){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StorageReference reference = FirebaseStorage.getInstance()
                .getReference()
                .child("images")
                .child(email)
                .child(picName);
        reference.delete();
    }

    private void uploadImage(String fileName, Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final StorageReference reference = storage.getReference()
                .child("images")
                .child(email)
                .child(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();
        UploadTask task = reference.putBytes(data);
        Task<Uri> uriTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw new Exception("Exception thrown in uriTask");

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                mCurrentFirebaseImageUrl = task.getResult();
                uploadTripObject();
            }
        });


    }

    private void uploadToFirebase() {
        //Gets and image desired storage name
        if (mImageSelection == NO_IMAGE_SELECTED && mAction == EDIT_ITEM) {
            uploadTripObject();
            return;
        }
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        Long imageIndex = (Long) snapshot.get("image_index");
                        mCurrentFirebaseImageName = "PICTURE_" + imageIndex;
                        updateImageIndex(imageIndex);

                    }
                });


    }

    private void updateToFirebase() {
        //gets the current index and the current image name

        if (mImageSelection == NO_IMAGE_SELECTED) {
            updateTripObject();
            return;
        }
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    }

    private void updateEditImageIndex(Long mCurrentImageIndex) {

    }

    private void updateImage() {

    }

    private void updateTripObject() {

    }

    private void updateImageIndex(Long mCurrentFirebaseIndex) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("image_index", mCurrentFirebaseIndex + 1);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        uploadImage(mCurrentFirebaseImageName, mSelectedBitmap);
                    }
                });

    }

    public void selectCameraPhoto(View view) {
        if (mImageSelection == CAMERA_SELECTED) {
            resetCameraPreview();
            mImageSelection = NO_IMAGE_SELECTED;
            return;
        }

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createTempImageFile();
        Uri photoUri = FileProvider.getUriForFile(this, "com.course.traveljournal", photoFile);
        mCameraImageUri = photoUri;
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePicture, RC_CAMERA);
    }

    public void selectGalleryPhoto(View view) {

        if (mImageSelection == GALLERY_SELECTED) {
            resetGalleryPreview();
            mImageSelection = NO_IMAGE_SELECTED;
            return;

        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_GALLERY);
    }

    private File createTempImageFile() {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = File.createTempFile(TEMP_IMAGE_FILE, ".jpg", storageDir);
            mCameraImagePath = file.getAbsolutePath();
            return file;
        } catch (IOException e) {
            return null;
        }

    }

    private List<String> getErrorMessages() {
        LinkedList<String> list = new LinkedList<>();
        if (mTripNameText.getText().toString().isEmpty() || mTripNameText.getText().toString() == null)
            list.add(getString(R.string.empty_trip_name));
        if (mDestinationText.getText().toString().isEmpty() || mDestinationText.getText().toString() == null)
            list.add(getString(R.string.empty_destination));
        if (getTripType() == TRIP_TYPE_NOT_SELECTED)
            list.add(getString(R.string.no_trip_type_selected));
        if (!mStartDateSelected)
            list.add(getString(R.string.no_start_date));
        if (!mEndDateSelected)
            list.add(getString(R.string.no_end_date));
        if (mImageSelection == NO_IMAGE_SELECTED)
            list.add(getString(R.string.no_picture_selected));
        return list;
    }

    public void preview(View view) {
        List<String> errors = getPreviewErrors();
        if (errors.size() == 0) {
            PreviewDialogFragment previewDialogFragment = new PreviewDialogFragment();
            if (mAction == ADD_ITEM || mSelectedBitmap != null)
                previewDialogFragment.setBitmap(mSelectedBitmap);
            else {

                try {
                    previewDialogFragment.setBitmap(getThumbnail(mOldEditImageUrl));
                } catch (MalformedURLException e) {

                } catch (IOException e) {

                }

            }
            previewDialogFragment.setTripName(getTripName());
            previewDialogFragment.setPrice(getPrice());
            previewDialogFragment.setDestination(getDestination());
            previewDialogFragment.setRating(getRating());
            previewDialogFragment.show(getSupportFragmentManager(), "Preview Dialog");
        } else {
            String message = "";
            for (String error : errors)
                message = message
                        .concat("-")
                        .concat(error)
                        .concat(NEWLINE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.reintroduce_input)
                    .setCancelable(true)
                    .setMessage(message);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private List<String> getPreviewErrors() {
        LinkedList<String> list = new LinkedList<>();
        if (getTripName() == null || getTripName().isEmpty())
            list.add(getString(R.string.empty_trip_name));
        if (getDestination() == null || getDestination().isEmpty())
            list.add(getString(R.string.empty_destination));
        if (mImageSelection == NO_IMAGE_SELECTED && mAction == ADD_ITEM)
            list.add(getString(R.string.no_picture_selected));

        return list;
    }

}
