package com.example.android.quickdoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //Firebase Database Variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDocSpecialtiesDBReference;
    private ChildEventListener mChildEventListener;
    private static final String FIREBASE_CHILD_SPECIALTIES = "specialties";
    private static final String SELECTED_DOCTOR_SPECIALTY = "SELECTED_DOCTOR_SPECIALTY";

    //Firebase Authentication Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;

    ArrayList<String> mListDoctorsSpeciality;
    ArrayAdapter<String> arrayAdapter;

    @BindView(R.id.spinner_doctor_speciallity) Spinner spinner;
    @BindView(R.id.button_schedule_appointment) Button buttonScheduleAppointment;
    @BindView(R.id.button_check_next_appointments) Button buttonCheckNextAppointment;
    @BindView(R.id.button_review_doctors) Button buttonReviewDocttors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Starting Butter Knife
        ButterKnife.bind(this);

        //Starting Firebase Database Instance and Reference points to child
        startFirebaseDBAndRef();

        //check if user is connected. If is, attach the ChildEventListener
        createFirebaseAuthListener();

        //Settting Button's OnClickListeners
        startViewsAndButtons();

    }

    private void startFirebaseDBAndRef() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if(Locale.getDefault().getLanguage().equals("pt"))
            mDocSpecialtiesDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_SPECIALTIES).child("pt");
        else
            mDocSpecialtiesDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_SPECIALTIES).child("en");
    }

    /** Start the Firebase Database and set the OnClickListeners to the 3 buttons of MainActivity */
    private void startViewsAndButtons() {

        //wait for Specialties list from firebase to allow pressign the button
        buttonScheduleAppointment.setEnabled(false);
        buttonScheduleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoctorsListBySpecialtyActivity.class);
                intent.putExtra(SELECTED_DOCTOR_SPECIALTY, spinner.getSelectedItemPosition());
                startActivity(intent);
            }
        });

        buttonCheckNextAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowNextAppointmentsActivity.class);
                startActivity(intent);
            }
        });

        buttonReviewDocttors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReviewPastAppointmentsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.firebase_signout){
            onSignedOutCleanup();
        }

        return super.onOptionsItemSelected(item);
    }

    /**Check if user is connected. If not, show screen to Authenticate */
    private void createFirebaseAuthListener() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    attachDatabaseReadListener();
                }else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(), RC_SIGN_IN);
                }
            }
        };
    }

    /** onChildAdded will run on the moment the EventListener is added to the DataBaseReference
     * it Will return the list of Doctor Specialties and show it in the spinner for selection */
    private void attachDatabaseReadListener() {
        if(mChildEventListener==null){
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>(){};
                    mListDoctorsSpeciality = dataSnapshot.getValue(typeIndicator);
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.spinner_doctor_speciality_item, mListDoctorsSpeciality);
                    spinner.setAdapter(arrayAdapter);
                    buttonScheduleAppointment.setEnabled(true);
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
            mDocSpecialtiesDBReference.addChildEventListener(mChildEventListener);
        }

    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener!=null){
            mDocSpecialtiesDBReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFirebaseAuth!=null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        if(arrayAdapter!=null) {
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseAuth!=null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        if(arrayAdapter!=null) {
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();
        }
        detachDatabaseReadListener();
        mFirebaseAuth.signOut();
    }
}
