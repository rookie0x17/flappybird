package com.daniel.flappybird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.list;
import static java.util.Collections.reverse;


public class RankActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button btnIndietro;
    private DatabaseReference mDatabase;

    List<UserHelperClass> lista_utenti , lista_utenti_final;

    //roba nuova
    private DatabaseReference myref2;
    private RecyclerView rec_view;
    private RankActivityAdapter rank_adapter;

    private DatabaseReference UsersRef;
    FirebaseDatabase rootNode;
    TextView score;
    String mail,name;

    int value;
    int RealScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        //nuovo
        myref2 = FirebaseDatabase.getInstance().getReference("users");
        lista_utenti = new ArrayList<>();
        lista_utenti_final = new ArrayList<>();



        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot posSnapshot : dataSnapshot.getChildren()) {
                    UserHelperClass ticket = posSnapshot.getValue(UserHelperClass.class);

                    if(!lista_utenti.contains(ticket)) {
                        lista_utenti.add(ticket);
                    }
                    //caricamento.setVisibility(View.INVISIBLE);

                }

                Collections.sort(lista_utenti);
                Collections.reverse(lista_utenti);

                rank_adapter = new RankActivityAdapter(RankActivity.this, lista_utenti);
                rec_view.setAdapter(rank_adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //caricamento.setVisibility(View.INVISIBLE);

            }
        });

        btnIndietro=findViewById(R.id.btnIndietro);
        score=findViewById(R.id.score);

        SharedPreferences preferences = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
        value = preferences.getInt("highScore",0);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth=FirebaseAuth.getInstance();



        Bundle extras = getIntent().getExtras();


        if (mAuth.getCurrentUser() != null) {
            //name.setText(mAuth.getCurrentUser().getDisplayName());
            mail = mAuth.getCurrentUser().getEmail();

            Query query = UsersRef.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(evento2);
        }









        btnIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RankActivity.this,ProfileActivity.class));
            }
        });

        rec_view = findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rec_view.setLayoutManager(manager);
        rec_view.setHasFixedSize(true);
    }

    ValueEventListener evento2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                UserHelperClass user = snap.getValue(UserHelperClass.class);

                if (!(user.getUsername().equals(""))) {

                    name = user.getUsername();
                    RealScore = user.getScore();





                    if(RealScore>value){
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        //Toast.makeText(EditProfileActivity.this, "Cia", Toast.LENGTH_SHORT).show();

                        mDatabase.child("users").child(currentFirebaseUser.getUid()).setValue(null);
                        UserHelperClass addNewUser = new UserHelperClass(name, mail, RealScore);
                        mDatabase.child("users").child(currentFirebaseUser.getUid()).setValue(addNewUser);

                        score.setText(""+RealScore);
                    }
                    else {
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        //Toast.makeText(EditProfileActivity.this, "Cia", Toast.LENGTH_SHORT).show();

                        mDatabase.child("users").child(currentFirebaseUser.getUid()).setValue(null);
                        UserHelperClass addNewUser = new UserHelperClass(name, mail, value);
                        mDatabase.child("users").child(currentFirebaseUser.getUid()).setValue(addNewUser);

                        RealScore=value;

                        score.setText("" + RealScore);

                    }
                    SharedPreferences sharedPref = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("highScore", 0);
                    editor.apply();



                }


            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            System.out.println("The read failed: " + error.getCode());
        }
    };
}