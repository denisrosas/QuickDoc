package com.example.android.quickdoc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quickdoc.adapters.AvailableDoctorsListAdapter;
import com.example.android.quickdoc.comparators.DistanceComparator;
import com.example.android.quickdoc.comparators.NameComparator;
import com.example.android.quickdoc.comparators.RatingComparator;
import com.example.android.quickdoc.comparators.WaitingDaysComparator;
import com.example.android.quickdoc.dataClasses.DoctorDetails;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.dataClasses.SpecialtyKeys;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorsListBySpecialtyActivity extends AppCompatActivity {

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the doctors DB tree
    private DatabaseReference mDoctorsDBReference;
    private ValueEventListener mDoctorsValueEventList;
    ArrayList<DoctorDetails> doctorDetailsList;
    String specialtyKey;
    private static final String FIREBASE_CHILD_DOCTORS = "doctors";
    private static final String SELECTED_DOCTOR_SPECIALTY = "SELECTED_DOCTOR_SPECIALTY";

    enum SortOrder {
        SORT_BY_DISTANCE, SORT_BY_NAME, SORT_BY_RATING, SORT_BY_WAITING_DAYS
    }

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
        specialtyKey = new SpecialtyKeys().getKeysByPosition(selectedSpecialtyId);

        attachDoctorsDBReadListener();

        SpecialtyNames specialtyNames = new SpecialtyNames(this);
        textViewSpecialty.setText(specialtyNames.getSpecialtyList().get(selectedSpecialtyId));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.item_sort_by_distance){
            recyclerView.removeAllViews();
            fillRecyclerView(SortOrder.SORT_BY_DISTANCE);
        }

        if(item.getItemId()==R.id.item_sort_by_rating){
            recyclerView.removeAllViews();
            fillRecyclerView(SortOrder.SORT_BY_RATING);
        }

        if(item.getItemId()==R.id.item_sort_by_waiting_days){
            recyclerView.removeAllViews();
            fillRecyclerView(SortOrder.SORT_BY_WAITING_DAYS);
        }

        if(item.getItemId()==R.id.item_sort_by_name){
            recyclerView.removeAllViews();
            fillRecyclerView(SortOrder.SORT_BY_NAME);
        }

        return super.onOptionsItemSelected(item);
    }

    private void attachDoctorsDBReadListener() {

        Log.i("denis", "specialtyKey: "+specialtyKey);
        mDoctorsDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS).child(specialtyKey);

        mDoctorsValueEventList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctorDetailsList = new ArrayList<>();

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                     doctorDetailsList.add(childSnapshot.getValue(DoctorDetails.class));
                }

                if (doctorDetailsList.size()>0) {
                    //by default doctors will be sorted by name
                    fillRecyclerView(SortOrder.SORT_BY_NAME);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.fail_to_connect), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mDoctorsDBReference.addValueEventListener(mDoctorsValueEventList);
    }

    private void fillRecyclerView(SortOrder sortOrder) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<DoctorDetailsToUser> doctorDetailsToUserList = new ArrayList<>();

        for(int index=0; index<doctorDetailsList.size(); index++){

            doctorDetailsToUserList.add(new DoctorDetailsToUser(doctorDetailsList.get(index)));

            //TODO - calculate the distance from user to Doctor office
            doctorDetailsToUserList.get(index).setDistanceToDoctor(calculateDistance(index));

            //TODO - calculate the quantity of waiting days
            doctorDetailsToUserList.get(index).setWaitingDays(new Random().nextInt(60));

            doctorDetailsToUserList.get(index).setDoctorId(index);
        }

        switch (sortOrder){
            case SORT_BY_NAME:
                Collections.sort(doctorDetailsToUserList, new NameComparator());
                break;
            case SORT_BY_DISTANCE:
                Collections.sort(doctorDetailsToUserList, new DistanceComparator());
                break;
            case SORT_BY_RATING:
                Collections.sort(doctorDetailsToUserList, new RatingComparator());
                break;
            case SORT_BY_WAITING_DAYS:
                Collections.sort(doctorDetailsToUserList, new WaitingDaysComparator());
                break;
            default:
                Collections.sort(doctorDetailsToUserList, new NameComparator());
                break;
        }

        AvailableDoctorsListAdapter availableDoctorsListAdapter = new AvailableDoctorsListAdapter(doctorDetailsToUserList, specialtyKey, getApplicationContext());

        recyclerView.setAdapter(availableDoctorsListAdapter);
    }

    /** If has permission, returns the distance to the doctor in Kms
     * */
    private float calculateDistance(int doctorId) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)){

            return -1;

        } else {
            Location locationUser = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            Location locationDoctor = new Location("Doctor's Office");
            locationDoctor.setLatitude(doctorDetailsList.get(doctorId).getAddressLat());
            locationDoctor.setLongitude(doctorDetailsList.get(doctorId).getAddressLng());
            return (locationUser.distanceTo(locationDoctor) / 1000); //meters to km
        }

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
        mDoctorsDBReference.removeEventListener(mDoctorsValueEventList);
    }
}
