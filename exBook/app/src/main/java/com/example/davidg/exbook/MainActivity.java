package com.example.davidg.exbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.davidg.exbook.helpers.BottomNavigationViewHelper;
import com.example.davidg.exbook.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListingAdapter.ListingAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ListingAdapter mListingAdapter;
    private ProgressBar mLoadingIndicator;
    private StaggeredGridLayoutManager layoutManager;

    private FirebaseAuth mAuth;
    private Menu drawableMenu = null;
    private NavigationView navigationView = null;
    private MenuItem loginDrawableItemMenu = null;
    private MenuItem logoutDrawableItemMenu = null;
    private BottomNavigationView bottomNavigationView;
    private List<Post> list;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawableMenu = navigationView.getMenu();
        loginDrawableItemMenu = drawableMenu.findItem(R.id.nav_sign_in_sign_up);
        logoutDrawableItemMenu = drawableMenu.findItem(R.id.nav_sign_out);

        // Disable shifting mode and displays all titles of navigation options
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_book_listing);

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        layoutManager = new StaggeredGridLayoutManager(3,1);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemViewCacheSize(24);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        //loadWeatherData();


        // Check whether the user is already logged in or not
        checkLogInStatus();
    }

    @SuppressLint("RestrictedApi")
    private void fixBottomNavigationViewShifting(){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShiftingMode(false);
            itemView.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuItemId = item.getItemId();
        Context context = MainActivity.this;
        String menuItemText = "";
        boolean menuItemFound = true;

        switch(menuItemId){
            case R.id.action_notifications:
                menuItemText = "Notifications icon clicked.";
                break;
            case R.id.action_manage_friends:
                menuItemText = "Friend manager icon clicked.";
                break;
            case R.id.action_profile:
                menuItemText = "Profile icon clicked.";
                break;
//            case R.id.action_menu:
//                menuItemText = "Menu icon clicked.";
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        Toast.makeText(context,menuItemText,Toast.LENGTH_LONG).show();

        return true;
    }


    @Override
    public void onStart(){
        super.onStart();
        // check if user is logged on
        checkLogInStatus();
    }

    @Override
    public void onResume(){
        super.onResume();
        populatePostList();
    }

    public void checkLogInStatus(){
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){ //if user is logged on already
            showLogOut();
        }
        else{
            showLogIn();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Context context = MainActivity.this;
        Class destinationActivity = null;
        Intent startSingInIntent = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String navigationText = "";
        if (id == R.id.nav_camera) {
            navigationText = "Camera navigation option clicked.";
        } else if (id == R.id.nav_gallery) {
            navigationText = "Gallery navigation option clicked.";
        } else if (id == R.id.nav_slideshow) {
            navigationText = "Slideshow navigation option clicked.";
        } else if (id == R.id.nav_manage) {
            navigationText = "Manage navigation option clicked.";
        } else if (id == R.id.nav_sign_in_sign_up) {

            startSingInIntent = new Intent(getApplicationContext(),SignInActivity.class);
            //navigationText = "Sign in / Sign out navigation option clicked.";

        } else if (id == R.id.nav_sign_out) {

            navigationText = "Log out completed.";
            Toast.makeText(MainActivity.this,navigationText,Toast.LENGTH_LONG).show();

            //Log the user out
            FirebaseAuth.getInstance().signOut();
            mAuth = null;

            showLogIn();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(startSingInIntent != null){
            startActivity(startSingInIntent);
        }

        return true;
    }

    //make log in option visible & log out option invisible
    private void showLogIn(){
        loginDrawableItemMenu.setVisible(true);
        logoutDrawableItemMenu.setVisible(false);
    }

    //make log in option invisible & log out option visible
    private void showLogOut(){
        loginDrawableItemMenu.setVisible(false);
        logoutDrawableItemMenu.setVisible(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String navigationText = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navigationText = "Home navigation option clicked.";
                    break;
                case R.id.navigation_post:
                    //navigationText = "Post navigation option clicked.";

                    //go to post activity 1
                    startActivity(new Intent(MainActivity.this,PostActivity1.class));
                    break;
                case R.id.navigation_chat:
                    navigationText = "Chat navigation option clicked.";
                    break;
                case R.id.navigation_offers:
                    navigationText = "Offers navigation option clicked.";
                    break;
                case R.id.navigation_android:
                    navigationText = "Android navigation option clicked.";
                    break;
                default:
                    return false;
            }
            if(navigationText != null){
                Toast.makeText(MainActivity.this,navigationText,Toast.LENGTH_LONG).show();
            }

            return true;
        }
    };

//    private void loadWeatherData() {
//        showWeatherDataView();
//
//        String location = SunshinePreferences.getPreferredWeatherLocation(this);
//        new FetchWeatherTask().execute(location);
//    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param clickedPost The weather for the day that was clicked
     */
    @Override
    public void onClick(Post clickedPost) {
        Context context = this;
        Toast.makeText(context, clickedPost.postId, Toast.LENGTH_SHORT)
                .show();
        Intent postDescriptionIntent = new Intent(this,PostDescription.class);
        postDescriptionIntent.putExtra("PostDescription",clickedPost);
        startActivity(postDescriptionIntent);
    }

//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            /* If there's no zip code, there's nothing to look up. */
//            if (params.length == 0) {
//                return null;
//            }
//
//            String location = params[0];
//            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
//
//            try {
//                String jsonWeatherResponse = NetworkUtils
//                        .getResponseFromHttpUrl(weatherRequestUrl);
//
//                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
//                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//
//                return simpleJsonWeatherData;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String[] weatherData) {
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (weatherData != null) {
//                showWeatherDataView();
//                mListingAdapter.setWeatherData(weatherData);
//            }
////            else {
////                showErrorMessage();
////            }
//        }
//    }



    private void populatePostList(){

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Post post = postSnapshot.getValue(Post.class);
                    //ImageUploadInfo imageUploadInfo = new ImageUploadInfo(post.title,post.coverPhotoUrl,post.coverPhotoUri,post.postId);

                    list.add(post);
                }

                mListingAdapter = new ListingAdapter(MainActivity.this,MainActivity.this,list);

                /* Setting the adapter attaches it to the RecyclerView in our layout. */
                mRecyclerView.setAdapter(mListingAdapter);

                // Hiding the progress dialog.
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                //progressDialog.dismiss();

            }
        });
    }



}


