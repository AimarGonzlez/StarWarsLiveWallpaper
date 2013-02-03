package com.mel.wallpaper.starWars;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.mel.entityframework.Game;
import com.mel.wallpaper.starWars.entity.InvisibleWalls;
import com.mel.wallpaper.starWars.entity.Jumper;
import com.mel.wallpaper.starWars.entity.Map;
import com.mel.wallpaper.starWars.entity.Walker;
import com.mel.wallpaper.starWars.entity.Walker.Rol;
import com.mel.wallpaper.starWars.process.GameProcess;
import com.mel.wallpaper.starWars.process.WalkersProcess;
import com.mel.wallpaper.starWars.process.RenderLaserProcess;
import com.mel.wallpaper.starWars.process.RenderWalkersProcess;
import com.mel.wallpaper.starWars.process.TouchProcess;
import com.mel.wallpaper.starWars.settings.GameSettings;
import com.mel.wallpaper.starWars.settings.GameSettingsActivity;
import com.mel.wallpaper.starWars.timer.TimerHelper;
import com.mel.wallpaper.starWars.view.PlayerAnimation;
import com.mel.wallpaper.starWars.view.SpriteFactory;

public class StarWarsGame implements SharedPreferences.OnSharedPreferenceChangeListener
{

	public Camera camera;
	public Engine engine;
	public Context context;
	
	public Scene starWarsScene;
	
	public Game game;
	
	public Map map;
	
	private GameProcess gameProcess;
	private TouchProcess touchProcess;
	private WalkersProcess playersCommandsProcess;
	private RenderWalkersProcess renderPlayersProcess;
	private RenderLaserProcess renderBallsProcess;
	

	private float screenOffsetX = 0;
	private float gameOffsetX = 0;
	
	private Sprite background;
	
	private float backgroundScaleFactor;

	//public UpdateTicker;
	
	public StarWarsGame(Engine engine, ContextWrapper context){
		this.engine = engine;
		this.camera = engine.getCamera();
		this.context = context;
		
		SharedPreferences mPrefs;
		mPrefs = context.getSharedPreferences(GameSettings.PREFERENCES_ID, 0);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
	}
	
	public void onCreateResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		SpriteFactory.getMe().context = this.context;
		SpriteFactory.getMe().engine = this.engine;
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.EDWARNER,"fb_goalkeeper_richardi_pantalon_llarg.png", 512, 512, 4, 5);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MARC,"fb_player-maped_lenders.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_SKINHEAD,"fb_player-maped_skinhead.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_WHITE,"fb_player-maped_white_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BLACK,"fb_player-maped_black_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BLACK2,"fb_player-maped_black_hair2.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BROWN,"fb_player-maped_generic1.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_RED,"fb_player-maped_redhead_hair.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BENJI,"fb_goalkeeper_benji_pantalon_llarg.png", 512, 512, 4, 5);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.OLIVER,"fb_player-newteam_oliver.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BRUCE,"fb_player-newteam_bruce.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BLACK,"fb_player-newteam_black_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BLACK2,"fb_player-newteam_black_hair2.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BROWN,"fb_player-newteam_generic1.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_RED,"fb_player-newteam_redhead_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.DEFAULT_PLAYER_TEXTURE,"fb_player.png", 512, 512, 8, 8);
		
		
		SpriteFactory.getMe().registerTexture("background","field-final2.png", 2048, 1024);
		SpriteFactory.getMe().registerTexture(SpriteFactory.GOAL_RIGHT,"porteria_derecha.png", 64, 128);
		SpriteFactory.getMe().registerTexture(SpriteFactory.GOAL_LEFT,"porteria_izquierda.png", 64, 128);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BALL,"fb_ball.png", 128, 128, 4, 4);
		SpriteFactory.getMe().registerTexture(SpriteFactory.LASER,"shooting.png", 128, 128);
		
		
		//TODO: Cargar el fondo del campo correctamente 
		//this.grassBackground = new RepeatingSpriteBackground(this.camera.getWidth(), this.camera.getHeight(), this.engine.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.context.getAssets(), "gfx/background_grass.png"), this.engine.getVertexBufferObjectManager());
				
		this.background = getBackground();
		updateBackgroundPosition();
		
		

	}
	
	public Scene onCreateScene(){
		
		this.starWarsScene = new Scene();
		this.starWarsScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.starWarsScene.attachChild(this.background);
		
		return this.starWarsScene;
	}
	

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		GameSettings settings = GameSettings.getInstance();
		settings.godsFingerEnabled = sharedPreferences.getBoolean(GameSettings.GODSFINGER_KEY, true);
		settings.musicEnabled = sharedPreferences.getBoolean(GameSettings.MUSIC_KEY, true);
		
		Debug.d("settings", "onSharedPreferenceChanged()");
		Debug.d("settings", "godsFingerEnabled: "+settings.godsFingerEnabled);
		Debug.d("settings", "musicEnabled: "+settings.musicEnabled);
	}
	
	
	public void onOffsetsChanged(float screenOffsetX){
		this.screenOffsetX = screenOffsetX;
		updateBackgroundPosition();
	}
	
	public void onGameCreated(){
		
		// start engine
		this.engine.registerUpdateHandler(new IUpdateHandler() {
			public void onUpdate(final float pSecondsElapsed) {
				game.update();
			}

			public void reset() {}
		});
		
		startGame();
	}
	
	public void onPauseGame(){
		pauseGame();
	}
	
	public void onResumeGame(){
		resumeGame();
	}
	
	
	
	
	private Sprite getBackground(){
		ITextureRegion texture = SpriteFactory.getMe().getTexture("background");
		float visibleScreenHeight = StarWarsLiveWallpaper.CAMERA_HEIGHT - StarWarsLiveWallpaper.NOTIFICATION_BAR_HEIGHT;
		float visibleScreenWidth = visibleScreenHeight*texture.getWidth()/texture.getHeight();
		
		this.backgroundScaleFactor = visibleScreenHeight/texture.getHeight();
		
		Sprite fieldSprite = SpriteFactory.getMe().newSprite("background",  visibleScreenWidth, visibleScreenHeight);
        return fieldSprite;
	}
	
	
	private float calcCenterX(){
		return (this.camera.getWidth() - this.background.getWidth()) / 2 - this.gameOffsetX; 
	}
	private float calcCenterY(){
		return (this.camera.getHeight() - this.background.getHeight() + StarWarsLiveWallpaper.NOTIFICATION_BAR_HEIGHT) / 2;
	}
	
	
	
	public void initialize(){
		//initialize model
		float sf = this.backgroundScaleFactor;
		InvisibleWalls walls = new InvisibleWalls(sf*52f, sf*52f, sf*92f, sf*52f, this.background); //TODO: cambiar esto por un campo horizontal mas largo
		
		
		//TESTING DIMENSIONES CAMPO
		//this.background.attachChild(new Rectangle(500,walls.offsetY, 40, 40, this.engine.getVertexBufferObjectManager()));
		//this.background.attachChild(new Rectangle(walls.offsetX+walls.width/2, walls.offsetY+walls.height/2, 40, 40, this.engine.getVertexBufferObjectManager()));

		//initialize entity framework
		map = new Map(walls);
		
		this.game = new Game(background);
		this.game.addEntity(map);
		this.game.addEntities(map.walkers);
		
		
		this.gameProcess = new GameProcess(game, this.engine, this.starWarsScene);
		this.touchProcess = new TouchProcess(game, this.starWarsScene, this.context);
		this.playersCommandsProcess = new WalkersProcess(game,map);
		this.renderPlayersProcess = new RenderWalkersProcess(game, this.background);
		this.renderBallsProcess = new RenderLaserProcess(game, this.background);
		
		game.addProcess(this.gameProcess, 1);
		game.addProcess(this.touchProcess, 10);
		game.addProcess(this.playersCommandsProcess, 21);
		game.addProcess(this.renderPlayersProcess, 98);
		game.addProcess(this.renderBallsProcess, 99);
	}
	
	private void updateBackgroundPosition(){
		if(this.background!=null){
			this.gameOffsetX = getGameOffset(screenOffsetX, this.background);
			//Debug.d("gameOffset: "+this.gameOffsetX);
			this.background.setPosition(calcCenterX(), calcCenterY());
		}
	}
	
	private float getGameOffset(float screenOffsetX, RectangularShape background) {
		//float offsetTotalRange = (this.background.getWidth()*0.5f);
		//float gameOffset = offsetTotalRange*(screenOffsetX-0.5f)
		float offsetTotalRange = (background.getWidth()-this.camera.getWidth());
		float gameOffset = offsetTotalRange*(screenOffsetX-0.5f);
		return gameOffset;
	}
	
	
	// GAME LIFE CYCLE
	private void startGame(){
		
		if(map.status == Map.Status.INITIAL_STATE){
			
			// wait for resources to load and start playing
//			map.status = Map.Status.LOADING;
//			TimerHelper.startTimer(this.engine.getScene(), 2f,  new ITimerCallback() {                      
//				public void onTimePassed(final TimerHandler pTimerHandler){
//					map.status = Map.Status.INTRO;
//				}
//			});
			
			
			map.status = Map.Status.INTRO;
		}
	}
	
	private void pauseGame(){
		if(map.status != Map.Status.PAUSE){
			map.status = Map.Status.PAUSE;
			
			this.starWarsScene.setChildrenIgnoreUpdate(true);
			
		}
	}
	
	private void resumeGame(){
		
		if(map.status == Map.Status.PAUSE){
			
			this.starWarsScene.setChildrenIgnoreUpdate(false);

			
			
			map.status = Map.Status.RESUME_GAME;
		}
	}
	
	
}
