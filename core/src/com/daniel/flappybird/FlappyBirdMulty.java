package com.daniel.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import javax.xml.soap.Text;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class FlappyBirdMulty extends ApplicationAdapter {
    SpriteBatch batch;

    String code;
    String player;
    int canStart;
    int j = 4;
    int winner=0;


    public FlappyBirdMulty(String code , String player) {

        this.code = code;
        this.player = player;


    }

    Texture background_day;
    Texture[] birds;
    Texture[] birds2;
    Texture topTube;
    Texture bottomTube;
    Texture gameover;
    Texture youwin;
    ShapeRenderer shapeRender;

    int flapstate = 0;
    float birdY = 0;
    float birdY2 = 0;
    float velocity = 0;
    Circle birdCircle;

    float gap = 600;

    int score = 0;
    int scoringTube = 0;
    BitmapFont font;

    int gameState = 0;

    float maxTubeOffset;
    Random randomGenerator;
    float[] tubeOffset = new float[4];

    float tubeVelocity = 4;
    float[] tubeX = new float[4];
    int numberOfTubes = 4;
    float distanceBetweenTubes;

    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;



    private WebSocket webSocket;
    private String SERVER_PATH = "ws://192.168.1.9:8080";


    @Override
    public void create () {

        System.out.println("Codice: " + this.code + "  Player:  " + this.player);

        batch = new SpriteBatch();

        batch = new SpriteBatch();
        background_day= new Texture("background-day.png");
        birds = new Texture[3];
        birds2 = new Texture[3];
        birds[0] = new Texture("yellowbird-upflap.png");
        birds[1] = new Texture("yellowbird-midflap.png");
        birds[2] = new Texture("yellowbird-downflap.png");
        birds2[0] = new Texture("redbird-upflap.png");
        birds2[1] = new Texture("redbird-midflap.png");
        birds2[2] = new Texture("redbird-downflap.png");
        bottomTube = new Texture("pipe-green.png");
        topTube = new Texture("pipe-green-reverse.png");
        gameover = new Texture("gameover.png");
        youwin = new Texture("you-win.png");
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        shapeRender = new ShapeRenderer();
        birdCircle = new Circle();

        topTubeRectangles = new Rectangle[4];
        bottomTubeRectangles = new Rectangle[4];

        canStart = 0;


        initiateSocketConnection();

        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        startGame();

    }

    public void startGame(){

        j=4;

        birdY = Gdx.graphics.getHeight()/2;
        birdY2 = Gdx.graphics.getHeight()/2 - 200;


        for(int i = 0 ; i < numberOfTubes ; i++){

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 600  ;

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() +i * distanceBetweenTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

    }

    private void initiateSocketConnection() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            if(player == "1") {
                String messaggio = code + "-" + player + "-" + "crea";
                webSocket.send(messaggio);
            } else if (player == "2") {
                String messaggio = code + "-" + player + "-" + "join";
                webSocket.send(messaggio);
            }

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);

            System.out.println("ARRIVA");
            String[] messaggio = text.split("-");
            System.out.println(messaggio[0]);
            System.out.println(messaggio[1]);


            if(messaggio[0].equals("start")){
                j=Integer.parseInt(messaggio[1]);
                if(messaggio[1].equals("0")) {
                    canStart = 1;
                }
                System.out.println("Entra in start");
            } else if (messaggio[0].equals("dati")) {
                birdY2 = Float.parseFloat(messaggio[1]);
                System.out.println("entra in dati");
            } else if (messaggio[0].equals("endgame")) {
                winner = 1;
                gameState = 2;
            }

        }
    }


    @Override
    public void render () {
        batch.begin();
        batch.draw(background_day, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        if(gameState==1) {

            if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2) {
                score++;

                if(scoringTube < 3) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            for (int i = 0; i < numberOfTubes; i++){

                if(tubeX[i] < - topTube.getWidth()) {
                    tubeX[i] = tubeX[i] + 4 * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 600  ;
                } else {
                    tubeX[i] = tubeX[i] - 4;


                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
                        topTube.getWidth() * 3, topTube.getHeight() * 3);

                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - (bottomTube.getHeight() * 3) + tubeOffset[i],
                        bottomTube.getWidth() * 3, bottomTube.getHeight() * 3);

                topTubeRectangles[i] = new Rectangle( tubeX[i] , Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth() * 3 , topTube.getHeight() * 3);
                bottomTubeRectangles[i] = new Rectangle( tubeX[i] , Gdx.graphics.getHeight() / 2 - gap / 2 - (bottomTube.getHeight() * 3) + tubeOffset[i] , bottomTube.getWidth() * 3, bottomTube.getHeight() * 3 );

            }
            if(Gdx.input.justTouched()) {
                velocity = -2;
            }

            if (birdY > 0) {
                velocity= velocity + 0.1f;
                birdY -= velocity;
                webSocket.send(code + "-" + String.valueOf(birdY) + "-" + "dati");
            } else {
                webSocket.send(code + "-" + player + "-" + "endgame");
                gameState = 2;
            }
        } else if (gameState == 0){

            font.draw(batch , String.valueOf(j) , Gdx.graphics.getWidth()/2 , Gdx.graphics.getHeight()/2);

            if (canStart==1) {

                gameState=1;

            }

        } else if (gameState == 2) {

            if(winner == 0) {
                batch.draw(gameover, Gdx.graphics.getWidth() / 3 - gameover.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2,
                        gameover.getWidth() * 3, gameover.getHeight() * 3);
            } else if (winner == 1){
                batch.draw(youwin , Gdx.graphics.getWidth() /4 - 200 , Gdx.graphics.getHeight()/2);

            }


            if (Gdx.input.justTouched()) {
                gameState = 1;
                startGame();
                score = 0;
                scoringTube=0;
                velocity = 0;
                winner = 0;
            }
        }

        if (flapstate == 0) {
            flapstate = 1;
        } else if (flapstate == 1) {
            flapstate = 2;
        } else {
            flapstate = 0;
        }


        batch.draw(birds[flapstate] , Gdx.graphics.getWidth()/3 , birdY , birds[flapstate].getWidth() * 4 , birds[flapstate].getHeight() * 4);
        batch.draw(birds2[flapstate] , Gdx.graphics.getWidth()/3 , birdY2 , birds2[flapstate].getWidth() * 4 , birds2[flapstate].getHeight() * 4);
        font.draw(batch , String.valueOf(score) , 100 ,200);
        batch.end();

        //shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRender.setColor(Color.RED);

        birdCircle.set(Gdx.graphics.getWidth() / 3 + 65 , birdY + (birds[flapstate].getHeight()/2)*3 , (birds[flapstate].getWidth()/2+2)*3);
        //shapeRender.circle(birdCircle.x , birdCircle.y , birdCircle.radius);

        for(int i = 0 ; i < 4 ; i++){
            //shapeRender.rect(tubeX[i] , Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth() * 3 , topTube.getHeight() * 3);
            //shapeRender.rect(tubeX[i] , Gdx.graphics.getHeight() / 2 - gap / 2 - (bottomTube.getHeight() * 3) + tubeOffset[i] , bottomTube.getWidth() * 3, bottomTube.getHeight() * 3);

            if(Intersector.overlaps(birdCircle , topTubeRectangles[i]) || Intersector.overlaps(birdCircle , bottomTubeRectangles[i]) ) {
                gameState = 2;
                webSocket.send(code + "-" + player + "-" + "endgame");
            }
        }

        shapeRender.end();
    }

    @Override
    public void dispose () {
        batch.dispose();

    }




}


