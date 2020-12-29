package com.daniel.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterRoomActivity extends AppCompatActivity {

    Button createRoomButton;
    Button joinRoomButton;
    EditText roomCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);

        createRoomButton = findViewById(R.id.createRoomButton) ;
        joinRoomButton = findViewById(R.id.joinRoomButton);
        roomCode = findViewById(R.id.codeTextField);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codice = "1917";
                Intent classToLaunch = new Intent(EnterRoomActivity.this,AndroidLauncher1.class);
                classToLaunch.putExtra("Codice" , codice);
                startActivity(classToLaunch);

            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = roomCode.getText().toString();


                Intent classToLaunch = new Intent(EnterRoomActivity.this,AndroidLauncher2.class);
                classToLaunch.putExtra("Codice" , code);
                startActivity(classToLaunch);


            }
        });

    }

}