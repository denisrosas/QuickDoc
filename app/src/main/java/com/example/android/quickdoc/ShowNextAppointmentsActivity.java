package com.example.android.quickdoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.ButterKnife;

public class ShowNextAppointmentsActivity extends AppCompatActivity {

    //Firebase Database
    FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the doctors DB tree
    private DatabaseReference mUserAppDBReference;
    private ValueEventListener mUserAppointValEvList;
    private static final String FIREBASE_CHILD_USER_APP = "user_appointments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_appointments);

        //Starting ButterKnife
        ButterKnife.bind(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String firebaseUID = getFirebaseUID(firebaseAuth);

        attachValueEventListener(firebaseUID);


    }

    private void attachValueEventListener(String firebaseUID) {

        mUserAppDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APP).child(firebaseUID);

        mUserAppointValEvList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, UserAppointment>> typeIndicator = new GenericTypeIndicator<>();
                Map<String, UserAppointment> hashMap = dataSnapshot.getValue(typeIndicator);
                int loop = 0;

                if (hashMap != null) {
                    for (Map.Entry<String, UserAppointment> mapEntry : hashMap.entrySet()) {

                        UserAppointment userAppointment = mapEntry.getValue();

                            Log.i("denis", "Loop: "+loop);
                            Log.i("denis", "doctorId: "+userAppointment.getDoctorId());
                            Log.i("denis", "Time: "+userAppointment.getTime());
                            Log.i("denis", "Date: "+userAppointment.getDate());
                            Log.i("denis", "\n\n");
                            loop++;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUserAppDBReference.addValueEventListener(mUserAppointValEvList);
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
}
