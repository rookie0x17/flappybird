package com.daniel.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background_day;
	Texture[] birds;
	Texture topTube;
	Texture bottomTube;
	Texture gameover;
	ShapeRenderer shapeRender;
	Texture initialMessage;
	Texture base;

	Texture quit;
	int highscore;

	int flapstate = 0;
	float birdY = 0;
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


	@Override
	public void create () {


		Gdx.app.log("funzione" , "si daje");



		batch = new SpriteBatch();
		initialMessage = new Texture("message.png");
		background_day = new Texture("background-day.png");
		base = new Texture("base.png");
		birds = new Texture[3];
		birds[0] = new Texture("yellowbird-upflap.png");
		birds[1] = new Texture("yellowbird-midflap.png");
		birds[2] = new Texture("yellowbird-downflap.png");
		bottomTube = new Texture("pipe-green.png");
		topTube = new Texture("pipe-green-reverse.png");
		gameover = new Texture("gameover.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		quit = new Texture("quit.png");



		shapeRender = new ShapeRenderer();
		birdCircle = new Circle();

		topTubeRectangles = new Rectangle[4];
		bottomTubeRectangles = new Rectangle[4];




		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

		startGame();

	}


	public void startGame(){

		birdY = Gdx.graphics.getHeight()/2;

		for(int i = 0 ; i < numberOfTubes ; i++){

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * 600  ;

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() +i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background_day, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(base , 0 , 0 , Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/4);

		if(gameState==1) {

			font.draw(batch , String.valueOf(score) , Gdx.graphics.getWidth()/2 - 30 , Gdx.graphics.getHeight()/10 * 9);

			for(int i = 0 ; i < 4 ; i++){
				//shapeRender.rect(tubeX[i] , Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth() * 3 , topTube.getHeight() * 3);
				//shapeRender.rect(tubeX[i] , Gdx.graphics.getHeight() / 2 - gap / 2 - (bottomTube.getHeight() * 3) + tubeOffset[i] , bottomTube.getWidth() * 3, bottomTube.getHeight() * 3);

				if(Intersector.overlaps(birdCircle , topTubeRectangles[i]) || Intersector.overlaps(birdCircle , bottomTubeRectangles[i]) ) {
					gameState = 2;
				}
			}



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

			font.draw(batch , String.valueOf(score) , Gdx.graphics.getWidth()/2 - 30 , Gdx.graphics.getHeight()/10 * 9);

			if (birdY > 0) {

				velocity = velocity + 0.1f;
				birdY -= velocity;

			} else {
				gameState = 2;
			}
		} else if (gameState == 0){
			batch.draw(initialMessage,100,100 ,Gdx.graphics.getWidth()-200, Gdx.graphics.getHeight()-200);
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2) {
			batch.draw(gameover , Gdx.graphics.getWidth() /3 -  gameover.getWidth() / 2 ,
					Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2 + 300 ,
					gameover.getWidth() * 3 , gameover.getHeight() *3 );

			batch.draw(quit , Gdx.graphics.getWidth() / 2 - quit.getWidth()/4 +50, Gdx.graphics.getHeight() / 2  , quit.getWidth() /3 , quit.getHeight()/3 -20) ;

			Preferences scores = Gdx.app.getPreferences("High Scores");


			this.highscore = scores.getInteger("highScore", 0);
			if(highscore < score) {
				Gdx.input.vibrate(25);
				scores.putInteger("highScore", score);
				scores.flush();
				Gdx.app.exit();

			}

			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube=0;
				velocity = 0;
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

		batch.end();

		//shapeRender.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRender.setColor(Color.RED);

		birdCircle.set(Gdx.graphics.getWidth() / 3 + 65 , birdY + (birds[flapstate].getHeight()/2)*3 , (birds[flapstate].getWidth()/2+2)*3);
		//shapeRender.circle(birdCircle.x , birdCircle.y , birdCircle.radius);







		shapeRender.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}

	public void endGame(){

		Gdx.app.exit();
	}

	public void restartGame(){
		if (Gdx.input.justTouched()) {
			gameState = 1;
			startGame();
			score = 0;
			scoringTube=0;
			velocity = 0;
		}
	}



}
