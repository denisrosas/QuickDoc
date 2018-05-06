package com.example.android.quickdoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorDetailsActivity extends AppCompatActivity {

    private static final String DOCTOR_DETAILS = "DOCTOR_DETAILS";
    private static final String DOCTOR_ID = "DOCTOR_ID";
    private static final String SPECIALTY_KEY = "SPECIALTY_KEY";
    private static final String PHOTOS_FOLDER = "photos";
    private static final String BIG_PHOTO_FILENAME_PREFIX = "doctor_big";
    private static final String PHOTO_EXTENSION = ".jpg";

    @BindView(R.id.tv_doctor_name) TextView textViewDocName;
    @BindView(R.id.tv_specialty) TextView textViewSpecialty;
    @BindView(R.id.tv_avarage_reviews) TextView textViewAvrgReviews;
    @BindView(R.id.tv_presentation) TextView textViewPresentation;
    @BindView(R.id.tv_presentation_text) TextView textViewPresentationText;
    @BindView(R.id.tv_accepted_health_care) TextView textViewAccHealthCare;
    @BindView(R.id.tv_address) TextView textViewDocAddress;
    @BindView(R.id.tv_address_text) TextView textViewAddressText;
    @BindView(R.id.tv_distance) TextView textViewDistance;
    @BindView(R.id.tv_phone_number) TextView textViewPhone;

    @BindView(R.id.iv_doctor_photo) ImageView imageViewDocPhoto;
    @BindView(R.id.iv_amil) ImageView imageViewAmil;
    @BindView(R.id.iv_bradesco) ImageView imageViewBradesco;
    @BindView(R.id.iv_hapvida) ImageView imageViewHapvida;
    @BindView(R.id.iv_prevent) ImageView imageViewPrevent;
    @BindView(R.id.iv_sulamerica) ImageView imageViewSulamerica;
    @BindView(R.id.iv_unimed) ImageView imageViewUnimed;

    @BindView(R.id.button_schedule_appointment) Button buttonScheduleAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        //Starting Butter Knife
        ButterKnife.bind(this);

        //Getting chosen Doctor's Details from Intent
        DoctorDetailsToUser doctorDetailsToUser = (DoctorDetailsToUser) getIntent().getSerializableExtra(DOCTOR_DETAILS);
        String specialtyKey = getIntent().getStringExtra((SPECIALTY_KEY));

        setFirebaseDoctorPhoto(specialtyKey, doctorDetailsToUser.getDoctorId());

        setTextViews(doctorDetailsToUser, specialtyKey);

        setOnClickListeners(doctorDetailsToUser, specialtyKey);

        hideUnsupportedHealthPlanes(doctorDetailsToUser);
    }

    private void hideUnsupportedHealthPlanes(DoctorDetailsToUser doctorDetailsToUser) {

        if(!doctorDetailsToUser.isAcceptsAmil())
            imageViewAmil.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsBradescoSaude())
            imageViewBradesco.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsHapVida())
            imageViewHapvida.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsPreventSenior())
            imageViewPrevent.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsSulamerica())
            imageViewSulamerica.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsUnimed())
            imageViewUnimed.setVisibility(View.GONE);

    }

    private void setTextViews(DoctorDetailsToUser doctorDetailsToUser, String specialtyKey) {

        textViewDocName.setText(doctorDetailsToUser.getName());
        textViewSpecialty.setText(SpecialtyNames.getSpecialtyName(this, specialtyKey));


        textViewAvrgReviews.setText(Float.toString(doctorDetailsToUser.getAvaregeReviews()));


        textViewPresentation.setText(getString(R.string.doctor_presentation));

        if(Locale.getDefault().getLanguage().matches("pt")) {
            textViewPresentationText.setText(doctorDetailsToUser.getPresentationPt());
        } else{
            textViewPresentationText.setText(doctorDetailsToUser.getPresentationEn());
        }

        textViewAccHealthCare.setText(getString(R.string.accepted_health_care));
        textViewDocAddress.setText(getString(R.string.address));
        textViewAddressText.setText(doctorDetailsToUser.getAddressExtended());

        String discanteKm = getString(R.string.distance)+": "+Float.toString(doctorDetailsToUser.getDistanceToDoctor())+" "+getString(R.string.km);
        textViewDistance.setText(discanteKm);

        textViewPhone.setText(doctorDetailsToUser.getPhoneNumber());

        buttonScheduleAppointment.setText(getString(R.string.schedule_appoiontment));
    }

    public void setOnClickListeners(final DoctorDetailsToUser doctorDetailsToUser, final String specialtyKey){

        //Open map application when app is pressed
        textViewAddressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = doctorDetailsToUser.getAddressLat();
                double longitude = doctorDetailsToUser.getAddressLng();
                String label;

                if(Locale.getDefault().getLanguage().matches("pt")) {
                     label = getString(R.string.office)+"do(a) "+doctorDetailsToUser.getName();
                } else {
                    label = doctorDetailsToUser.getName()+"'s "+getString(R.string.office);
                }
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //Open Dialer when the phone number is pressed
        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // It open the dialer app and allow user to call the number manually
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + doctorDetailsToUser.getPhoneNumber()));
                // Start the dialer app activity with number
                startActivity(intent);
            }
        });

        //if the button is pressed, go to activity to select date/time of the appointment
        buttonScheduleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectDateTimeActivity.class);
                intent.putExtra(DOCTOR_ID, doctorDetailsToUser.getDoctorId());
                intent.putExtra(SPECIALTY_KEY, specialtyKey);
                startActivity(intent);
            }
        });
    }

    /** Gets the image from Firebase and set in the imageView using Picasso*/
    private void setFirebaseDoctorPhoto(String specialtyKey, int doctorId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        String photoUrl = PHOTOS_FOLDER+"/"+specialtyKey +"/"
                + BIG_PHOTO_FILENAME_PREFIX +doctorId+PHOTO_EXTENSION;

        //Path example: photos/cardiologist/doctor_big2.jpg
        storageReference.child(photoUrl).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).placeholder(R.drawable.doctor_big_default).into(imageViewDocPhoto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get().load(R.drawable.doctor_big_default).into(imageViewDocPhoto);
                Log.i("denis", "Fail to load image");
            }
        });
    }

}
