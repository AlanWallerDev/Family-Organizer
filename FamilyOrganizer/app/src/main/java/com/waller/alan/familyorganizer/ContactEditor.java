package com.waller.alan.familyorganizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alan on 4/17/2018.
 */

public class ContactEditor extends AppCompatActivity {

    private static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "Contact Editor Activity";
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

    private ListView contactListView;
    private EditableContactAdapter contactAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_editor);
        currentActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        contactListView = (ListView) findViewById(R.id.contactListView);

        username = ANONYMOUS;

        // Initialize message ListView and its adapter
        List<Contact> contacts = new ArrayList<>();
        contactAdapter = new EditableContactAdapter(this, R.layout.item_contact, contacts);
        contactListView.setAdapter(contactAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference().child("contacts");

        contactListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.nameTextView);
                TextView tv2 = (TextView) view.findViewById(R.id.emailTextView);
                String name = tv.getText().toString().toLowerCase().trim();
                String email = tv2.getText().toString().toLowerCase().trim();
                Log.d(TAG, name + " " + email);
                Intent result = new Intent(getApplicationContext(), EditingContact.class);
                String owner = firebaseAuth.getCurrentUser().getEmail().toString().toLowerCase().trim();
                String contactID = name + email + owner;
                contactID = contactID.replace(".", "");
                result.putExtra("id", contactID); //you will need to have it put the name of the contact for use in the message activity (this will be used to only get messages to and from the named contact
                startActivity(result);

            }
        });



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
                                AuthUI.getInstance().signOut(ContactEditor.this);
                                break;
                            case R.id.messages_menu:
                                Intent intent = new Intent(currentActivity, MessageMenu.class);
                                startActivity(intent);
                                break;
                            case R.id.main_menu:
                                Intent Mintent = new Intent(currentActivity, MainActivity.class);
                                startActivity(Mintent);
                                break;
                            case R.id.contacts_menu:
                                Toast.makeText(currentActivity, "You are currently in the Contacts Activity", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.add_contacts_menu:
                                Intent CIntent = new Intent(currentActivity, AddContacts.class);
                                startActivity(CIntent);
                                break;
                            case R.id.add_event:
                                Intent AEIntent = new Intent(currentActivity, AddEvent.class);
                                startActivity(AEIntent);
                                break;
                            case R.id.events:
                                Intent EIntent = new Intent(currentActivity, EventActivity.class);
                                startActivity(EIntent);
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
        contactAdapter.clear();
        detachDatabaseListener();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(ContactEditor.this);
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
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    Log.d(TAG, contact.getEmail());
                    Log.d(TAG, firebaseAuth.getCurrentUser().getEmail().toString());
                    if(contact.getContactOwner().equals(firebaseAuth.getCurrentUser().getEmail().toString()))
                        contactAdapter.add(contact);

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
