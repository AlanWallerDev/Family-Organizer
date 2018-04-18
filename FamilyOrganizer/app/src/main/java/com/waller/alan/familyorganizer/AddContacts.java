package com.waller.alan.familyorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

/**
 * Created by t00053669 on 3/28/2018.
 */

public class AddContacts extends AppCompatActivity {
    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = "Add Contact Activity";
    private static final int RC_SIGN_IN = 100;

    private DrawerLayout drawerLayout;
    private String username;
    private String email;
    private static Context currentActivity;

    //entrypoint to the firebase real time database
    private FirebaseDatabase firebaseDatabase;
    //holds the reference for our messages objects in the database
    private DatabaseReference databaseReference;

    private ChildEventListener childEventListener;

    //handles connection with firebase authentication
    private FirebaseAuth firebaseAuth;
    //looks for AuthState Change
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView emailView;
    private TextView nameView;
    private TextView relationView;
    private Button addButton;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contacts_activity);
        currentActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        emailView = (TextView) findViewById(R.id.emailView);
        nameView = (TextView) findViewById(R.id.nameView);
        relationView = (TextView) findViewById(R.id.relationView);
        addButton = (Button) findViewById(R.id.addButton);

        username = ANONYMOUS;
        email = ANONYMOUS;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference().child("contacts");

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
                    email = user.getEmail();


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
                                AuthUI.getInstance().signOut(AddContacts.this);
                                Intent intent = new Intent(currentActivity, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.messages_menu:
                                Intent MIntent = new Intent(currentActivity, MessageMenu.class);
                                startActivity(MIntent);
                                break;

                            case R.id.main_menu:
                                intent = new Intent(currentActivity, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.add_contacts_menu:
                                Toast.makeText(currentActivity, "You are currently in the Add Contact Activity", Toast.LENGTH_SHORT).show();
                                break;
                        }


                        return true;
                    }
                });
//todo: check all users of database to see if the submitted email address and name are valid
        //how do I check the validated users of my firebase database??

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact(nameView.getText().toString().trim().toLowerCase(), emailView.getText().toString().trim(), relationView.getText().toString().trim().toLowerCase(), email);

                if(emailView.getText().toString().contains("@")) {
                    databaseReference.push().setValue(contact);
                    nameView.setText("");
                    emailView.setText("");
                    relationView.setText("");
                    Intent mIntent = new Intent(currentActivity, MainActivity.class);
                }else{
                    Toast.makeText(AddContacts.this, "invalid email address", Toast.LENGTH_SHORT).show();
                }
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
                AuthUI.getInstance().signOut(AddContacts.this);
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
                    //TODO: Add functionality when database is updated here
                    Log.d(TAG, "Contact Added");

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //todo: determine how to prevent duplicate contacts
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
