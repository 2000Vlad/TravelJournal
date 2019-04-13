package com.course.traveljournal;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static com.course.traveljournal.AddTripActivity.ACTION_KEY;
import static com.course.traveljournal.AddTripActivity.ADD_ITEM;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageView mUserImage;
    private TextView mUserNameText;
    private TextView mUserEmailText;
    private FloatingActionButton mFab;
    private FrameLayout mFragmentContainer;
    private static final int RC_SIGN_IN = 9001;
    private CardView mSignInCardView;
    private TripsFragment mTripsFragment;
    private FavoritesFragment mFavoritesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                dispatchMenuItemSelectedEvent(menuItem);
                return true;
            }
        });
        View navTop = mNavigationView.getHeaderView(0);

        mUserImage = navTop.findViewById(R.id.user_image);
        mUserNameText = navTop.findViewById(R.id.user_name_nav_top);
        mUserEmailText = navTop.findViewById(R.id.user_email_nav_top);
        mSignInCardView = navTop.findViewById(R.id.sign_in_cardview);
        mFragmentContainer = findViewById(R.id.fragment_container);

        mSignInCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseUser mUser = mAuth.getCurrentUser();
        mFab = findViewById(R.id.fab_add);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN: {
                if (resultCode == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);

                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                    }
                } else setCredentials(null);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) signIn();
        else setCredentials(account);

    }

    private void setCredentials(GoogleSignInAccount account) {
        if (account == null) {
            mUserImage.setImageResource(R.drawable.ic_placeholder);
            mUserNameText.setText(getString(R.string.not_logged));
            mUserEmailText.setText("");
        } else {
            Uri photoUrl = account.getPhotoUrl();
            Glide.with(this).load(photoUrl).apply(RequestOptions.circleCropTransform()).into(mUserImage);
            mUserNameText.setText(account.getDisplayName());
            mUserEmailText.setText(account.getEmail());
        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {

    }

    private void createUserDocument() {
        checkIfDocumentExists();
    }

    private void checkIfDocumentExists() {
        String email = mAuth.getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().getDocuments().size() == 0)
                            generateDocument();
                    }
                });

    }

    private void generateDocument() {
        String email = mAuth.getCurrentUser().getEmail();

        HashMap<String, Object> values = new HashMap<>();
        values.put("email",email);
        values.put("image_index",1);
        values.put("name",mAuth.getCurrentUser().getDisplayName());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .set(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createTripsCollection();
                    }
                });

    }

    private void createTripsCollection(){
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .collection("trips");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.LEFT);

        }
        return super.onOptionsItemSelected(item);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            setCredentials(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                            createUserDocument();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.


                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void setFragmentsToNull() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new Fragment());
        transaction.commit();
        mTripsFragment = null;
        mFavoritesFragment = null;
        signIn();
    }

    private void dispatchMenuItemSelectedEvent(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item: {
                logout();
                setFragmentsToNull();

            }
            break;
            case R.id.home_item: {
                if(mAuth.getCurrentUser() != null)
                showHomeFragment();
            }
            break;
            case R.id.fav_item : {
                showFavoritesFragment();
            }
            break;
        }
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "You logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });
        setCredentials(null);
    }

    public void addTripItem(View view) {
        Intent tripIntent = new Intent(this, AddTripActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(ACTION_KEY, ADD_ITEM);
        tripIntent.putExtras(extras);
        startActivity(tripIntent);
    }

    private void showHomeFragment() {
        Fragment newFragment = null;
        if (mTripsFragment == null) {
            mTripsFragment = new TripsFragment();
        }
        newFragment = mTripsFragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();

    }

    private void showFavoritesFragment() {
      Fragment newFragment = null;
      if(mFavoritesFragment == null){
          mFavoritesFragment = new FavoritesFragment();
      }
      newFragment = mFavoritesFragment;

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragment_container, newFragment);
      transaction.commit();
    }

    private void showAboutFragment() {

    }

    private void showContactFragment() {

    }

    private void showShareFragment() {

    }

    private void showSendFragment() {

    }

}
