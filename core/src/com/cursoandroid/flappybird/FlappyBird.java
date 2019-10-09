package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	//Elementos
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture background;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture logo;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private BitmapFont comecar;
	private Circle birdCircle;
	private Rectangle rectangleCanoTop;
	private Rectangle rectangleCanoBot;
	//private ShapeRenderer shape;

	//Atributos configuração
	private float widthScreen;
	private float heightScreen;
	private float speedDown;
	private float verticalStatingPosition;
	private float variacao = 0;
	private float posicaoMovCanoHor;
	private float deltaTime;
	private boolean marcouPonto;
	private Random randomico;
	private float alturaCanosRandon;
	private int statusGame;
	private int pontuacao;
	private int contador;
	private int espacamento;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH =768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

		batch = new SpriteBatch();
		//shape = new ShapeRenderer();

		//Fonte pontuação
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		//Fonte Reiniciar partida
		mensagem = new BitmapFont();
		mensagem.setColor(Color.ORANGE);
		mensagem.getData().setScale(3);

		//Fonte Comecar partida
		comecar = new BitmapFont();
		comecar.setColor(Color.ORANGE);
		comecar.getData().setScale(2);

		//Imagens passaro
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		//Imagem Game over
		gameOver = new Texture("game_over.png");

		//Imagem de fundo
		background = new Texture("fundo.png");

		//Imagem canos
		canoBaixo= new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

		//Imagem logo
		logo = new Texture("flappy_bird.png");

		//Fisica passaro
		birdCircle = new Circle();

		//Camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		//Medidas de tela
		widthScreen = VIRTUAL_WIDTH;
		heightScreen = VIRTUAL_HEIGHT;

		//Configurações estado do jogo 0
		speedDown = 0;
		statusGame = 0;
		pontuacao = 0;
		contador = 0;
		verticalStatingPosition = heightScreen / 2;

		//Posição cano inicial
		posicaoMovCanoHor = widthScreen;

		//Espaçamento entre canos
		espacamento = 300;

		//Geração de números randomicos para movimentar o cano
		randomico = new Random();

	}

	@Override
	public void render () {

		camera.update();

		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		deltaTime =Gdx.graphics.getDeltaTime();
		variacao+= deltaTime * 10;
		contador++;

		//Contador de piscada de palavras
		if(contador ==25) contador =0;

		//Variação de imagens do passaro
		if(variacao > 2) variacao = 0;

		//Estado do jogo 0 - Não iniciado
		if(statusGame == 0){

			//Inicia o jogo se tocar na tela
			if(Gdx.input.justTouched()){
				statusGame = 1;
			}
		}else{
			//Velocidade de queda
			speedDown+=1.5;

			//Verifica se o passaro tocou o chão e se ainda esta caindo
			if(verticalStatingPosition > 0 || speedDown < 0)
				verticalStatingPosition = verticalStatingPosition - (int)speedDown;
			//Jogo iniciado
			if(statusGame == 1){
				//Moviemento do cano
				posicaoMovCanoHor -= deltaTime *400;

				//Subida do passaro ao toque
				if(Gdx.input.justTouched()){
					speedDown = -25;
				}

				//Cano saiu da tela
				if(posicaoMovCanoHor < -canoTopo.getWidth()){
					posicaoMovCanoHor = widthScreen;
					alturaCanosRandon = randomico.nextInt(600)-300;
					marcouPonto = false;
				}

				//verifica pontuação
				if(posicaoMovCanoHor < widthScreen/4){
					if(!marcouPonto) {
						marcouPonto = true;
						pontuacao++;
					}
				}
			//Reiniciando configurações do jogo
			}else{
				if(Gdx.input.justTouched()){
					statusGame = 0;
					pontuacao = 0;
					speedDown =0;
					verticalStatingPosition = heightScreen /2;
					posicaoMovCanoHor = widthScreen;
				}
			}
		}

		//projeção camera
		batch.setProjectionMatrix(camera.combined);

		//Gerenciador de texturas
		//Iniciando gerenciador de texturas
		batch.begin();

		//Plano de fundo
		batch.draw(background, 0,0, widthScreen, heightScreen);

		//Verifica se o jogo não esta iniciado e mantem o logo aparecendo
		if (statusGame == 0) {
			batch.draw(logo, widthScreen/2 - gameOver.getWidth()/2, heightScreen-heightScreen/3, widthScreen/2, heightScreen/9);
		}

		//Canos
		batch.draw(canoTopo, posicaoMovCanoHor, heightScreen/2 + espacamento /2 + alturaCanosRandon);
		batch.draw(canoBaixo, posicaoMovCanoHor, heightScreen/2 - canoBaixo.getHeight() - espacamento / 2 + alturaCanosRandon);

		//Passaros
		batch.draw(passaros[(int)variacao], widthScreen/4, verticalStatingPosition);

		//Se o status do jogo for 0 aparece o texto clique para comecar
		if(statusGame == 0 && contador <= 12){
			comecar.draw(batch,"Clique para começar!",widthScreen/3,heightScreen/4);
		}

		//Se o status do jogo for 1 a pontuacao aparece
		if(statusGame == 1 || statusGame == 2){
			fonte.draw(batch,String.valueOf(pontuacao),widthScreen/2-30,heightScreen - heightScreen/9);
		}

		//Se o status do game for 2 a img game over aparece junto com o texto game over
		if(statusGame == 2){
			batch.draw(gameOver, widthScreen/2 - gameOver.getWidth()/2, heightScreen/2);
			if(contador<=12){
				mensagem.draw(batch, "Clique para reiniciar", widthScreen/2 - gameOver.getWidth()/2, heightScreen/2 - 100);
			}

		}
		batch.end();

		//Fisica passaro
		birdCircle.set(widthScreen/4 + passaros[(int) variacao].getWidth() / 2,
				verticalStatingPosition + passaros[(int)variacao].getHeight()/2,
				passaros[(int) variacao].getWidth()/2);

		//Fisica cano de baixo
		rectangleCanoBot = new Rectangle(
				posicaoMovCanoHor,heightScreen/2 - canoBaixo.getHeight() - espacamento / 2 + alturaCanosRandon,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		//Fisica cano de cima
		rectangleCanoTop = new Rectangle(
				posicaoMovCanoHor,heightScreen/2 + espacamento /2 + alturaCanosRandon,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		//shape
		/*shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
		shape.rect(rectangleCanoBot.x, rectangleCanoBot.y, rectangleCanoBot.width, rectangleCanoBot.height);
		shape.rect(rectangleCanoTop.x, rectangleCanoTop.y, rectangleCanoTop.width, rectangleCanoTop.height);
		shape.setColor(Color.RED);
		shape.end();*/

		//Teste de colisão
		if(Intersector.overlaps(birdCircle, rectangleCanoBot) || Intersector.overlaps(birdCircle, rectangleCanoTop)
				|| verticalStatingPosition >= heightScreen || verticalStatingPosition <= 0){
			statusGame = 2;
		};
	}

	@Override
	public void dispose () {

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}

