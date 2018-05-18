package com.example.android.quickdoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.quickdoc.adapters.NextAppointMentsListAdapter;
import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowNextAppointmentsActivity extends AppCompatActivity {

    //Firebase Database
    FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the doctors DB tree
    //private DatabaseReference mUserAppDBReference;
    private Query firebaseQuery;
    private ValueEventListener mUserAppointValEvList;
    private static final String FIREBASE_CHILD_USER_APP = "user_appointments";
    private static final String FIREBASE_CHILD_DATE = "date";

    @BindView(R.id.rv_user_appointments) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_next_appointments);

        //Starting ButterKnife
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String firebaseUID = getFirebaseUID(firebaseAuth);

        attachValueEventListener(firebaseUID);
    }

    private void attachValueEventListener(String firebaseUID) {

        //filter all appointments from today or after
        firebaseQuery = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APP).
                child(firebaseUID).orderByChild(FIREBASE_CHILD_DATE).startAt(getTodayDate());

        mUserAppointValEvList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserAppointment> userAppointments = new ArrayList<>();
                ArrayList<String> childKeys = new ArrayList<>();

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    UserAppointment userAppointment = childSnapshot.getValue(UserAppointment.class);
                    userAppointments.add(userAppointment);
                    childKeys.add(childSnapshot.getKey());
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                NextAppointMentsListAdapter adapter = new NextAppointMentsListAdapter(userAppointments, childKeys, getApplicationContext());

                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.no_appointments_for_this_user), Toast.LENGTH_LONG).show();
            }
        };

        firebaseQuery.addValueEventListener(mUserAppointValEvList);
    }

    private String getTodayDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Log.i("denis", "Date format: "+dateFormat.format(date));
        return dateFormat.format(date);
    }


    private String getFirebaseUID(FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser()==null) {
            Toast.makeText(this, getString(R.string.user_not_logged_in), Toast.LENGTH_LONG).show();
            finish();
            return null;
        } else {
            //getting the user ID of firebase. Will be used to store data
            return firebaseAuth.getCurrentUser().getUid();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mUserAppDBReference.removeEventListener(mUserAppointValEvList);
        firebaseQuery.removeEventListener(mUserAppointValEvList);
    }

}
