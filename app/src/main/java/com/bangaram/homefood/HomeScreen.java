package com.bangaram.homefood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity implements OnMapReadyCallback {

    ImageView ivMyLocation, ivProfile;
    EditText etDelivery, etPickup;
    Button btRequest;
    RadioGroup foodPackage;
    CardView cardView;


    private static final String TAG = "MapLog";
    private static final float DEFAULT_ZOOM = 17;

    private GoogleMap mMap;
    MarkerOptions deliveryMarker, pickupMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastKnownLocation;
    PlacesClient placesClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home_screen );
        //Fullscreen
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        //Hooks
        ivMyLocation = findViewById( R.id.ivMyLocation2 );
        etDelivery = findViewById( R.id.etDelivery );
        etPickup = findViewById( R.id.etPickup );
        btRequest = findViewById( R.id.btrequest );
        foodPackage = findViewById( R.id.foodPackage );
        cardView = findViewById( R.id.cardView );
        ivProfile = findViewById( R.id.ivProfile );
        btRequest.setVisibility( View.GONE );


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );

        if (ActivityCompat.checkSelfPermission( HomeScreen.this, Manifest.permission. ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions( HomeScreen.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44 );
        }

        ivProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( HomeScreen.this,ProfileEdit.class );
                startActivity( intent );
            }
        } );

        ivMyLocation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        } );
        etDelivery.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryAd();
            }
        } );
        etPickup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupAd();
            }
        } );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        Objects.requireNonNull( mapFragment ).getMapAsync( this );

        Places.initialize( HomeScreen.this, "AIzaSyCEGe2A8HiFsqngQ_VxcHtQk1XLrhQDNVA" );
        placesClient = Places.createClient( this );

        btRequest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder coder = new Geocoder( HomeScreen.this, Locale.getDefault() );
                List<Address> address, addresses;
                final String value = ((RadioButton) findViewById( foodPackage.getCheckedRadioButtonId() )).getText().toString();
                try {
                    address = coder.getFromLocationName( etDelivery.getText().toString(), 5 );
                    addresses = coder.getFromLocationName( etPickup.getText().toString(), 5 );
                    if ((address != null) && (addresses != null)) {
                        Address dlocation = address.get( 0 );
                        Address plocation = addresses.get( 0 );

                        String userId = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "Requests" ).child( userId );
                        GeoFire geoFire = new GeoFire(reference);

                        geoFire.setLocation("DeliveryLocation", new GeoLocation(dlocation.getLatitude(), dlocation.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    Toast.makeText( HomeScreen.this, "Success", Toast.LENGTH_SHORT ).show();
                                }
                            }
                        });
                        geoFire.setLocation("PickupLocation", new GeoLocation(plocation.getLatitude(), plocation.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    Toast.makeText( HomeScreen.this, "Success", Toast.LENGTH_SHORT ).show();
                                }
                            }
                        });

                        int randomPIN = (int)(Math.random()*9000)+1000;
                       reference.child( "foodPackage" ).setValue( value );
                       reference.child( "status" ).setValue( "pending" );
                       reference.child( "OTP" ).setValue( randomPIN );
                       reference.child( "id" ).setValue( FirebaseAuth.getInstance().getCurrentUser().getUid() ).addOnCompleteListener( new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Intent intent = new Intent( HomeScreen.this, DriverDetailsScreen.class );
                                   startActivity( intent );
                               }
                           }
                       } );
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        } );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( false );

        try {
            boolean success = googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.lightmap ) );
            if (!success) {
                Log.e( TAG, "Style parsing failed" );
            }
        } catch (Resources.NotFoundException e) {
            Log.e( TAG, "Can't find style, Error: ", e );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent( data );
                etDelivery.setText( place.getAddress() );
                mMap.clear();
                deliveryMarker = new MarkerOptions().position( place.getLatLng() ).title( "Delivery" );
                mMap.addMarker( deliveryMarker );
                mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( place.getLatLng(), 17 ) );

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent( data );
                Toast.makeText( HomeScreen.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG ).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent( data );
                etPickup.setText( place.getAddress() );
                pickupMarker = new MarkerOptions().position( place.getLatLng() ).title( "Delivery" );
                mMap.addMarker( pickupMarker.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_VIOLET ) ) );
                mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( place.getLatLng(), 17 ) );
                btRequest.setVisibility( View.VISIBLE );

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent( data );
                Toast.makeText( HomeScreen.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG ).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener( new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                mLastKnownLocation = task.getResult();
                if (mLastKnownLocation != null){
                    mMap.clear();
                    LatLng latLng = new LatLng( mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() );
                    deliveryMarker = new MarkerOptions().position( latLng ).title( "Delivery" );
                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, DEFAULT_ZOOM ) );
                    mMap.addMarker( deliveryMarker );
                    Geocoder geocoder = new Geocoder( HomeScreen.this,Locale.getDefault() );
                    try {
                        List<Address> addresses = geocoder.getFromLocation( mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),1);
                        if (addresses != null){
                            etDelivery.setText( addresses.get( 0 ).getAddressLine( 0 ) );
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }

    public void deliveryAd() {
        List<Place.Field> fields = Arrays.asList( Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG );
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields ).setCountry( "IN" )
                .build( this );
        startActivityForResult( intent, 10 );
    }

    public void pickupAd() {
        List<Place.Field> fields = Arrays.asList( Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG );
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields ).setCountry( "IN" )
                .build( this );
        startActivityForResult( intent, 20 );
    }

}

