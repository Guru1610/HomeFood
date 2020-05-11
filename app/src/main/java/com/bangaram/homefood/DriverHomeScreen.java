package com.bangaram.homefood;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;


public class DriverHomeScreen extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastKnownLocation;
    private MarkerOptions deliveryMarker;
    MarkerOptions Place1, Place2;
    TextView textView;
    double pLat, pLng, dLat, dLng;
    String foodPackage, id;
    String Name;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
    ChildEventListener requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_driver_home_screen );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );

        ImageView ivMyLocation = findViewById( R.id.ivMyLocation2 );
        ivMyLocation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        } );

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        if (ActivityCompat.checkSelfPermission( DriverHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions( DriverHomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44 );
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        Objects.requireNonNull( mapFragment ).getMapAsync( this );

        findViewById( R.id.btnGetReq ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequests();
            }
        } );

    }

    private void getRequests() {
        requestListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String,Object> data = (Map<String, Object>) dataSnapshot.getValue();

                foodPackage = data.get( "foodPackage" ).toString();
                id = data.get( "id" ).toString();
                pLat = Double.parseDouble(dataSnapshot.child( "PickupLocation" ).child( "l" ).child( "0" ).getValue().toString());
                pLng = Double.parseDouble(dataSnapshot.child( "PickupLocation" ).child( "l" ).child( "1" ).getValue().toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child( id );
                reference.child( "status" ).setValue( FirebaseAuth.getInstance().getCurrentUser().getUid() );

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child( id );
                databaseReference.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Name = dataSnapshot.child( "Name" ).getValue().toString();
                        Intent intent = new Intent( DriverHomeScreen.this,CustomerDetailsScreen.class );
                        intent.putExtra( "id",id );
                        intent.putExtra( "name", Name );
                        intent.putExtra( "package", foodPackage );
                        startActivity( intent );

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.addChildEventListener( requestListener );
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( false );
        try {
            boolean success = googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.lightmap ) );
            if (!success) {
                Log.e( "TAG", "Style parsing failed" );
            }
        } catch (Resources.NotFoundException e) {
            Log.e( "TAG", "Can't find style, Error: ", e );
        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener( new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                mLastKnownLocation = task.getResult();
                if (mLastKnownLocation != null) {
                    mMap.clear();
                    LatLng latLng = new LatLng( mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() );
                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 17 ) );

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "DriversOnline" );
                    GeoFire geoFire = new GeoFire( reference );
                    geoFire.setLocation( Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid(), new GeoLocation( mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() ), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Toast.makeText( DriverHomeScreen.this, "Error Updating Location", Toast.LENGTH_SHORT ).show();
                            } else {
                            }
                        }

                    } );
                }
            }
        } );
    }


}