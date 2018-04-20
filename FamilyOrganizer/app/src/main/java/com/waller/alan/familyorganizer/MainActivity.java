package com.waller.alan.familyorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "Main Activity";
    private static Context currentActivity;


    private DrawerLayout drawerLayout;

    private String username;
    private String userID;


    //entrypoint to the firebase real time database
    private FirebaseDatabase firebaseDatabase;
    //holds the reference for our messages objects in the database
    private DatabaseReference databaseReference;

    private ChildEventListener childEventListener;

    //handles connection with firebase authentication
    private FirebaseAuth firebaseAuth;
    //looks for AuthState Change
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);




        username = ANONYMOUS;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages");

        final List<AuthUI.IdpConfig> providers = Arrays.asList(

                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

        );

        authStateListener = new FirebaseAuth.AuthStateListener() {
            //two events call this
            //when user changes state
            //when listener is first attached to auth service
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //logged in state
                    onSignedIn(user.getDisplayName());

                }else{
                    //logged out state
                    onSignedOutCleanup();

                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
                }
            }
        };

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch(menuItem.getItemId()){
                            case R.id.sign_out_menu:
                                AuthUI.getInstance().signOut(MainActivity.this);
                                break;
                            case R.id.messages_menu:
                                Intent intent = new Intent(currentActivity, MessageMenu.class);
                                startActivity(intent);
                                break;
                            case R.id.main_menu:
                                Toast.makeText(currentActivity, "You are currently in the Main Activity", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.contacts_menu:
                                Intent ACIntent = new Intent(currentActivity, ContactEditor.class);
                                startActivity(ACIntent);
                                break;
                            case R.id.add_contacts_menu:
                                Intent CIntent = new Intent(currentActivity, AddContacts.class);
                                startActivity(CIntent);
                                break;
                        }


                        return true;
                    }
                });



    }

    protected void onResume(){

        super.onResume();
        if(firebaseAuth != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }

    }

    protected void onPause(){

        super.onPause();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

        detachDatabaseListener();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(MainActivity.this);
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void onSignedIn(String nUsername){
        Log.d(TAG, "Signed In");
        username = nUsername;
        attachDatabaseListener();

    }

    private void checkForFirstTime(String uID){
    //todo first time sign in?
    }


    private void onSignedOutCleanup(){

        username = ANONYMOUS;
        detachDatabaseListener();

    }

    private void detachDatabaseListener(){
        if(databaseReference != null && childEventListener != null){
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    private void attachDatabaseListener(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            databaseReference.addChildEventListener(childEventListener);
        }
    }



}
