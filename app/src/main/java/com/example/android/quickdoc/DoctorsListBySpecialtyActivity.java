package com.example.android.quickdoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quickdoc.adapters.AvailableDoctorsListAdapter;
import com.example.android.quickdoc.dataClasses.DoctorDetails;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.dataClasses.SpecialtyKeys;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorsListBySpecialtyActivity extends AppCompatActivity {

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the specialties DB tree
    private ChildEventListener mSpecChildEventListener;
    private static final String FIREBASE_CHILD_SPECIALTIES = "specialties";

    //Reference and Event Listener of the doctors DB tree
    private DatabaseReference mDoctorsDBReference;
    private ChildEventListener mDoctorsChildEventList;
    private static final String FIREBASE_CHILD_DOCTORS = "doctors";

    private static final String SELECTED_DOCTOR_SPECIALTY = "SELECTED_DOCTOR_SPECIALTY";

    //Firebase Authentication Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;

    @BindView(R.id.tv_specialty) TextView textViewSpecialty;
    @BindView(R.id.tv_explanation) TextView textViewExplanation;
    @BindView(R.id.rv_doctor_details) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list_by_specialty);

        //Starting Butter Knife
        ButterKnife.bind(this);

        //Starting Firebase Database Instance and Reference points to child
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //check if user is logged.
        createFirebaseAuthListener();

        //get selected specialty from Intent
        int selectedSpecialtyId = getIntent().getIntExtra(SELECTED_DOCTOR_SPECIALTY, -1);
        SpecialtyKeys specialtyKeys = new SpecialtyKeys();


        attachDoctorsDBReadListener(specialtyKeys.getKeysByPosition(selectedSpecialtyId));

        SpecialtyNames specialtyNames = new SpecialtyNames(this);
        textViewSpecialty.setText(specialtyNames.getSpecialtyList().get(selectedSpecialtyId));

    }

    private void attachDoctorsDBReadListener(final String specialtyKey) {

        Log.i("denis", "specialtyKey: "+specialtyKey);
        mDoctorsDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS).child(specialtyKey);

        if(mDoctorsChildEventList==null){
            mDoctorsChildEventList = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    GenericTypeIndicator<ArrayList<DoctorDetails>> typeIndicator = new GenericTypeIndicator<ArrayList<DoctorDetails>>(){};
                    ArrayList<DoctorDetails> doctorDetailsList = dataSnapshot.getValue(typeIndicator);

                    if (doctorDetailsList != null) {
                        fillRecyclerView(doctorDetailsList, specialtyKey);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.fail_to_connect), Toast.LENGTH_LONG).show();
                    }
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
            mDoctorsDBReference.addChildEventListener(mDoctorsChildEventList);
        }
    }

    private void fillRecyclerView(ArrayList<DoctorDetails> doctorDetailsList, String specialtyKey) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<DoctorDetailsToUser> doctorDetailsToUserList = new ArrayList<>();

        for(int i=0; i<doctorDetailsList.size(); i++){

            doctorDetailsToUserList.add(new DoctorDetailsToUser(doctorDetailsList.get(i)));

            //TODO - calculate the distance from user to Doctor office
            doctorDetailsToUserList.get(i).setDistanceToDoctor((float)2.2);

            //TODO - calculate the quantity of waiting days
            doctorDetailsToUserList.get(i).setWaitingDays(new Random().nextInt(60));

            doctorDetailsToUserList.get(i).setDoctorId(i);

            //TODO - sort the doctors by the selected order. Default is name
            //doctorDetailsToUserList.sort();
        }

        AvailableDoctorsListAdapter availableDoctorsListAdapter = new AvailableDoctorsListAdapter(doctorDetailsToUserList, specialtyKey, getApplicationContext());

        // TODO (9) Set the GreenAdapter you created on mNumbersList
        recyclerView.setAdapter(availableDoctorsListAdapter);
    }

    /**Check if user is connected. If not, finish the activity */
    private void createFirebaseAuthListener() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            Toast.makeText(this, getString(R.string.user_not_logged_in), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoctorsDBReference.removeEventListener(mDoctorsChildEventList);
    }
}
