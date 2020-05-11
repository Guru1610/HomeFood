package com.bangaram.homefood;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DriverDetailsScreen extends AppCompatActivity {

    DatabaseReference databaseReference;
    String Name, Phone;
    String dLat, dLng;
    TextView tvDriverName, tvDistance, tvPrice;
    CardView driverCard;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_driver_details_screen );
        databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        tvDistance = findViewById( R.id.tvDistance );
        tvPrice = findViewById( R.id.tvCost );
        tvDriverName = findViewById( R.id.textView22 );
        driverCard = findViewById( R.id.detailsCard );
        driverCard.setVisibility( View.GONE );
        progressBar = findViewById( R.id.progressBar2 );


        ChildEventListener childEventListener;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String,Object> data = (Map<String,Object>)dataSnapshot.getValue();
                String driverId = data.get( "status" ).toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( driverId );
                reference.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String,Object> data = ( Map<String, Object>) dataSnapshot.getValue();
                        Name = data.get( "Name" ).toString();
                        Phone = data.get( "Phone" ).toString();
                        tvDriverName.setText( Name );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DriversOnline").child( driverId );
                databaseReference1.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String,Object> data = (Map<String, Object>) dataSnapshot.child( "l" );
                        dLat = data.get( "0" ).toString();
                        dLng = data.get( "1" ).toString();
                        driverCard.setVisibility( View.VISIBLE );
                        progressBar.setVisibility( View.GONE );
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

        databaseReference.addChildEventListener( childEventListener );



    }
}
