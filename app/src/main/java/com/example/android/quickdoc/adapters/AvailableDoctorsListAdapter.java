package com.example.android.quickdoc.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.quickdoc.DoctorDetailsActivity;
import com.example.android.quickdoc.R;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.externalClasses.CropCircleTransformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AvailableDoctorsListAdapter extends RecyclerView.Adapter<AvailableDoctorsListAdapter.doctorDetailViewHolder> {

    private ArrayList<DoctorDetailsToUser> doctorDetailsList;
    private String specialtyKey;

    public AvailableDoctorsListAdapter(ArrayList<DoctorDetailsToUser> doctorDetailsList, String specialtyKey, Context context) {
        this.doctorDetailsList = doctorDetailsList;
        this.specialtyKey = specialtyKey;
        this.context = context;
    }

    private Context context;
    private static final String PHOTOS_FOLDER = "photos";
    private static final String SPECIALTY_FOLDER = "specialty";
    private static final String SMALL_PHOTO_FILENAME_PREFIX = "doctor_small";
    private static final String PHOTO_EXTENSION = ".jpg";

    private static final String DOCTOR_DETAILS = "DOCTOR_DETAILS";
    private static final String SPECIALTY_KEY = "SPECIALTY_KEY";

    @NonNull
    @Override
    public doctorDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doctor_details_item, parent, false);
        return new doctorDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final doctorDetailViewHolder holder, int position) {

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DoctorDetailsActivity.class);
                intent.putExtra(DOCTOR_DETAILS, doctorDetailsList.get(holder.getAdapterPosition()));
                intent.putExtra(SPECIALTY_KEY, specialtyKey);
                context.startActivity(intent);
            }
        });

        int doctorId = doctorDetailsList.get(position).getDoctorId();
        setFirebaseDoctorPhoto(holder.smallPhoto, doctorId);

        //set Doctors name
        holder.doctorName.setText(doctorDetailsList.get(position).getName());

        //Set Dotors avarage reviews
        //TODO - arrumar formatação das reviews
        //NumberFormat oneDecimalFormat = new DecimalFormat("0.0");
        holder.doctorReviews.setText(Float.toString(doctorDetailsList.get(position).getAvaregeReviews()));

        //Set TextView distance to doctor
        String distanceString = doctorDetailsList.get(position).getDistanceToDoctor()+" "+context.getString(R.string.km);
        holder.doctorDistance.setText(distanceString);

        //Set Doctors days to next appointment
        String waitingDaysString = doctorDetailsList.get(position).getWaitingDays()+" "+context.getString(R.string.days);
        holder.doctorWaitingDays.setText(waitingDaysString);
    }

    /** Gets the image from Firebase and set in the imageView using Picasso*/
    private void setFirebaseDoctorPhoto(final ImageView smallPhotoView, int doctorId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        //Path example: photos/cardiologist/doctor_small2.jpg
        storageReference.child(PHOTOS_FOLDER+"/"+specialtyKey +"/"
            +SMALL_PHOTO_FILENAME_PREFIX+doctorId+PHOTO_EXTENSION).getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.doctor_small_default).transform(new CropCircleTransformation()).into(smallPhotoView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Picasso.get().load(R.drawable.doctor_small_default).transform(new CropCircleTransformation()).into(smallPhotoView);
                    Log.i("denis", "Fail to load image");
                }
            });
    }

    @Override
    public int getItemCount() {
        return doctorDetailsList.size();
    }

    class doctorDetailViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        ImageView smallPhoto;
        TextView doctorName;
        TextView doctorReviews;
        TextView doctorDistance;
        TextView doctorWaitingDays;


        doctorDetailViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constr_layo_doc_det_item);
            smallPhoto = itemView.findViewById(R.id.doctor_photo);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorReviews = itemView.findViewById(R.id.doctor_avg_reviews);
            doctorDistance = itemView.findViewById(R.id.doctor_distance);
            doctorWaitingDays = itemView.findViewById(R.id.waiting_days);
        }
    }
}
