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
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorsListBySpecialtyActivity extends AppCompatActivity {

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the doctors DB tree
    private Query query;
    private ValueEventListener valueEventListener;
    ArrayList<DoctorDetails> doctorDetailsList;
    ArrayList<Integer> minumumWaitingDaysList;
    String specialtyKey;
    private static final String FIREBASE_CHILD_DOCTORS = "doctors";
    private static final String FIREBASE_CHILD_AGENDA = "agenda";
    private static final String FIREBASE_CHILD_FULLDAY = "fullday";
    private static final String SELECTED_DOCTOR_SPECIALTY = "SELECTED_DOCTOR_SPECIALTY";
    private static final int MAX_WAITING_DAYS_SCHED = 30;

    enum SortOrder {
        SORT_BY_DISTANCE, SORT_BY_NAME, SORT_BY_RATING, SORT_BY_WAITING_DAYS
    }

    @BindView(R.id.tv_specialty) TextView textViewSpecialty;
    @BindView(R.id.tv_explanation) TextView textViewExplanation;
    @BindView(R.id.rv_doctor_details) RecyclerView recyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

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

        setTextViews(selectedSpecialtyId);

        getMinimumWaitingDays();
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


    private void getMinimumWaitingDays() {
        minumumWaitingDaysList = new ArrayList<>();

        //for all doctors we cannot find the agenda, we can set waiting days to 1
        for(int i=0; i<20; i++)
            minumumWaitingDaysList.add(1);

        query = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA).child(specialtyKey).limitToFirst(20);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int doctorId;
                int waitingDays;
                Calendar currentDate;

                //unfortunately this loop will have a long time
                for (DataSnapshot childSnapshotDoc : dataSnapshot.getChildren()){

                    //key will be like "doctor3". new we get just the number
                    String childKey = childSnapshotDoc.getKey();
                    doctorId = Integer.valueOf(childKey.substring(childKey.length()-1));

                    waitingDays = 1;

                    //start currentDate as tomorrow
                    currentDate = Calendar.getInstance();
                    currentDate.add(Calendar.DAY_OF_YEAR, 1);

                    for(int i=0; i<MAX_WAITING_DAYS_SCHED; i++){
                        String childkey = convertCalendarToString(currentDate);

                        if(childSnapshotDoc.child(childkey).exists()){

                            try {
                                //if the currentDate agenda is full, we increase waitingDays
                                if(childSnapshotDoc.child(childkey).child(FIREBASE_CHILD_FULLDAY).getValue(boolean.class)) {
                                    waitingDays++;
                                } else
                                    //if the day is not full, means we found the next day with a free appointment
                                    break;

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else{
                            //if the date does't exists, means the schedule is empty and we
                            // found the next day with a free appointment
                            break;
                        }
                        currentDate.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    minumumWaitingDaysList.set(doctorId, waitingDays);
                }
                attachDoctorsDBReadListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(valueEventListener);

    }

    private void setTextViews(int selectedSpecialtyId) {
        SpecialtyNames specialtyNames = new SpecialtyNames(this);
        textViewSpecialty.setText(specialtyNames.getSpecialtyList().get(selectedSpecialtyId));

        textViewExplanation.setText(R.string.doctor_list_explanation);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void attachDoctorsDBReadListener() {

        Log.i("denis", "specialtyKey: "+specialtyKey);
        query = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS).child(specialtyKey);

        valueEventListener = new ValueEventListener() {
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
        query.addValueEventListener(valueEventListener);
    }

    private void fillRecyclerView(SortOrder sortOrder) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<DoctorDetailsToUser> doctorDetailsToUserList = new ArrayList<>();

        for(int index=0; index<doctorDetailsList.size(); index++){

            doctorDetailsToUserList.add(new DoctorDetailsToUser(doctorDetailsList.get(index)));

            doctorDetailsToUserList.get(index).setDistanceToDoctor(calculateDistance(index));

            doctorDetailsToUserList.get(index).setWaitingDays(minumumWaitingDaysList.get(index));

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

        progressBar.setVisibility(View.GONE);
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
            if(locationUser!=null)
                return (locationUser.distanceTo(locationDoctor) / 1000); //meters to km

            return -1;
        }

    }

    private String convertCalendarToString(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Log.i("denis", "Data: "+dateFormat.format(date));
        return dateFormat.format(date);
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
        query.removeEventListener(valueEventListener);
    }
}
