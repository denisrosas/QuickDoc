package com.example.android.quickdoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorsListBySpecialtyActivity extends AppCompatActivity {

    //Firebase Database Variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDocSpecialtiesDBReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list_by_specialty);

        /* TODO
        1 - pegar lista de medicos de uma especialidade do Firebase como uma lista de DoctorDetails
        2 - Converter a lista para DoctorDetailsToUser
        3 - Setar a distancia do usuario para o consultorio
        4 - Fazer um loop. para cada médicos.
                Segundo loop de dia apos dia. Começar com do dia seguinte.
                    pegar do firebase da agenda do mes do dia seguinte
                        pegar
                        se for tudo null -> dia livre. parar loop
                        se nao, iterar pelos horarios do dia. se algum horario for null, temos um horario livre. retorna waiting days
                    se não tiver horario livre, incrementa waiting days e pegar dia seguinte.
                    verificar se dia seguinte esta no mesmo mes
                    se nao baixar proximo mes e começar de novo
         5 - Ordenar como desejado pelo cliente. o padrao é por nome
         */


    }

}
