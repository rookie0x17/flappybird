package com.daniel.flappybird;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity {

    private Vibrator myVib;
    TextView name, mail;
    Button logout;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button btnSingle;
    Button btnMulty;
    private DatabaseReference mDatabase;
    private DatabaseReference UsersRef;
    FirebaseDatabase rootNode;
    TextView score;
    Button btnRank;
    String clouds_value;

    TextView longit;
    TextView tempo;

    FusedLocationProviderClient fusedLocationProviderClient;
    String Location;


    int initValue;
    int value;

    int highscore;
    int score2 = 0;

    static final String PREFS_NAME = "ScoreGame";
    Preferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        mAuth = FirebaseAuth.getInstance();
        btnSingle = findViewById(R.id.btnSingle);
        btnMulty = findViewById(R.id.btnMulti);

        btnRank = findViewById(R.id.btnRank);

        tempo = findViewById(R.id.tempo);
        longit=findViewById(R.id.longit);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        

        //score.setText(""+value);


        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(25);
                startActivity(new Intent(ProfileActivity.this, RankActivity.class));

            }
        });


        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(25);

                Intent classToLaunch = new Intent(ProfileActivity.this,AndroidLauncher.class);
                classToLaunch.putExtra("Clouds_value" , clouds_value);
                startActivity(classToLaunch);



            }
        });

        btnMulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(25);
                startActivity(new Intent(ProfileActivity.this, EnterRoomActivity.class));

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference("users");


        if (mAuth.getCurrentUser() != null) {
            //name.setText(mAuth.getCurrentUser().getDisplayName());
            mail.setText(mAuth.getCurrentUser().getEmail());

            Query query = UsersRef.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(evento);
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && mDatabase.child("users").child(signInAccount.getId()).child("email").toString() != signInAccount.getEmail()) { //mando nel database tutti i valore gel google account
            String username = signInAccount.getDisplayName();
            String email = signInAccount.getEmail();

            UserHelperClass addNewUser = new UserHelperClass(username, email, initValue);
            rootNode = FirebaseDatabase.getInstance();
            mDatabase = rootNode.getReference("users");
            //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child(signInAccount.getId()).setValue(addNewUser);

            //mDatabase.child("Users").child(personId).child("cell").setValue(null);
        }
        if (signInAccount != null) {
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());


        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myVib.vibrate(25);
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


    }

    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location!= null){
                    try {
                        Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        longit.setText("Position: " +  addresses.get(0).getLocality());
                        Location =  addresses.get(0).getLocality();
                        finde_weather();



                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
    }

    private void finde_weather() {

        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + Location + "&appid=df18ecd6a05543595d64716398a38b0f";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main_object=response.getJSONObject("main");
                    JSONObject array= response.getJSONObject("clouds");
                    clouds_value = array.getString("all");

                    tempo.setText("Percentage of clouds: " + clouds_value + "%");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }


    ValueEventListener evento = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                UserHelperClass user = snap.getValue(UserHelperClass.class);
                if (!(user.getUsername().equals(""))) {

                    name.setText(user.getUsername());


                }


            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            System.out.println("The read failed: " + error.getCode());
        }
    };



}