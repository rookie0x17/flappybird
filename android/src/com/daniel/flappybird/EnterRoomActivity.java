package com.daniel.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class EnterRoomActivity extends AppCompatActivity {

    Button createRoomButton;
    Button joinRoomButton;
    TextView roomCode;
    EditText generatedRoomCode;
    Random rand = new Random();
    int codice;


    private Vibrator myVib;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);

        codice = rand.nextInt(100000);


        myVib=(Vibrator)this.getSystemService(VIBRATOR_SERVICE);
        createRoomButton = findViewById(R.id.createRoomButton) ;
        joinRoomButton = findViewById(R.id.joinRoomButton);
        roomCode = findViewById(R.id.roomCode);
        generatedRoomCode = findViewById(R.id.generatedRoomCode);
        roomCode.setText("" + codice);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myVib.vibrate(25);
                String codice_str = "" + codice;
                Intent classToLaunch = new Intent(EnterRoomActivity.this,AndroidLauncher1.class);
                classToLaunch.putExtra("Codice" , codice_str);
                startActivity(classToLaunch);

            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myVib.vibrate(25);

                String code = generatedRoomCode.getText().toString();


                Intent classToLaunch = new Intent(EnterRoomActivity.this,AndroidLauncher2.class);
                classToLaunch.putExtra("Codice" , code);
                startActivity(classToLaunch);


            }
        });

    }

}