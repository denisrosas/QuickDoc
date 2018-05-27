package com.example.android.quickdoc;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
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

import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //Firebase Database Variables
    private static final String SELECTED_DOCTOR_SPECIALTY = "SELECTED_DOCTOR_SPECIALTY";
    private static final int MY_PERMISSIONS_REQUEST = 10;

    //Firebase Authentication Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;

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

        //check if user is connected. If is, attach the ChildEventListener
        createFirebaseAuthListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
        }

        //Settting Button's OnClickListeners
        startViewsAndButtons();

    }

    /** set the OnClickListeners to the 3 buttons of MainActivity */
    private void startViewsAndButtons() {

        //Call activity to display the doctor's list
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
                Intent intent = new Intent(getApplicationContext(), ShowPastAppointList.class);
                startActivity(intent);
            }
        });

        //Start Doctor specialty listener
        SpecialtyNames specialtyNames = new SpecialtyNames(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_doctor_speciality_item, specialtyNames.getSpecialtyList());
        spinner.setAdapter(arrayAdapter);
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
            mFirebaseAuth.signOut();
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
                    //user is logged!
                }else {
                    //onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(), RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFirebaseAuth!=null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseAuth!=null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }
}
