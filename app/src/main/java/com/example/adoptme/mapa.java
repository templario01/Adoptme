package com.example.adoptme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class mapa extends FragmentActivity implements OnMapReadyCallback {
    //variable donde recibimos al usuario
    public static final String user = "names";

    private GoogleMap mMap;
    TextView txtUser;

    // variables para traer los puntos de la base de datos
    private DatabaseReference Perro1;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // instanciamos nuestra base de datos
        Perro1 = FirebaseDatabase.getInstance().getReference("Perro");

        txtUser = (TextView) findViewById(R.id.textuser);
        String user = getIntent().getStringExtra("names");
        txtUser.setText("BIENVENIDO : " + user);
        Button encontrar = (Button) findViewById(R.id.btnListo);
        encontrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent2, 0);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // aca intentamos traer en el mapa todos los perros almacenados en el mapa previamente
        Perro1.child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    try{
                        Perro mascota = snapshot.getValue(Perro.class);
                        Double lat = mascota.getLat();
                        Double lng = mascota.getLng();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(lat,lng));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dogmark));
                        //tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                        mMap.addMarker(markerOptions);
                    }catch(Exception e){
                        LatLng arequipa = new LatLng(-16.3988, -71.5369);
                        mMap.addMarker(new MarkerOptions().position(arequipa).title("Arequipa").icon(BitmapDescriptorFactory.fromResource(R.drawable.dogmark)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(arequipa));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        try{
            Perro.child("perros").addValueEventListener(new ValueEventListener() {

                //nos paseamos por todos los perros en la base de datos y capturamos sus coordenadas
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(Marker marker:realTimeMarkers){
                        marker.remove();
                    }

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Perro mascota = snapshot.getValue(Perro.class);
                        Double lat = mascota.getLat();
                        Double lng = mascota.getLng();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(lat,lng));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dogmark));
                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                    }
                    realTimeMarkers.clear();
                    realTimeMarkers.addAll(tmpRealTimeMarkers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(getBaseContext(),"Ocurrio un error",Toast.LENGTH_SHORT).show();
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        */

    }


}
