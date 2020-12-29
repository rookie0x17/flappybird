package com.daniel.flappybird;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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




public class ProfileActivity extends AppCompatActivity {


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

    int initValue;
    int value;

    int highscore;
    int score2=0;

    static final String PREFS_NAME = "ScoreGame";
    Preferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        mAuth=FirebaseAuth.getInstance();
        btnSingle=findViewById(R.id.btnSingle);
        btnMulty=findViewById(R.id.btnMulti);

        btnRank=findViewById(R.id.btnRank);





        //score.setText(""+value);



        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,RankActivity.class));
            }
        });


        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,AndroidLauncher.class));
            }
        });

        btnMulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,EnterRoomActivity.class));
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
        if (signInAccount!= null && mDatabase.child("users").child(signInAccount.getId()).child("email").toString() != signInAccount.getEmail()) { //mando nel database tutti i valore gel google account
            String username =signInAccount.getDisplayName();
            String email = signInAccount.getEmail();

            UserHelperClass addNewUser= new UserHelperClass(username,email,initValue);
            rootNode=FirebaseDatabase.getInstance();
            mDatabase = rootNode.getReference("users");
            //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child(signInAccount.getId()).setValue(addNewUser);

            //mDatabase.child("Users").child(personId).child("cell").setValue(null);
        }
        if(signInAccount != null){
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());


        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent intent =new Intent(ProfileActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


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