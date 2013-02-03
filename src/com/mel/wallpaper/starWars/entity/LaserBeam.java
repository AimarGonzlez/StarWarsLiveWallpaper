package com.mel.wallpaper.starWars.entity;



import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

import com.mel.entityframework.IEntity;
import com.mel.entityframework.IMovable;
import com.mel.util.Point;
import com.mel.wallpaper.starWars.timer.TimerHelper;
import com.mel.wallpaper.starWars.view.PlayerAnimation;
import com.mel.wallpaper.starWars.view.Position;
import com.mel.wallpaper.starWars.view.SpriteFactory;

public class LaserBeam implements IEntity, IMovable
{
	
	public static final float BLOCK_DISTANCE = 20f; //distancia para que el portero bloquee
	public static final float DESPEJE_DISTANCE = 42f; //distancia para que el portero despeje
	
	public static final float HIT_DISTANCE = 20f;
	public static final float JUMP_DISTANCE = 60f;
	public static final float MAX_REACH_MATE_DISTANCE = 220f;
	public static final float MAX_REACH_DISTANCE = 350f;
	public static final float MIN_REACH_DISTANCE = 70f;
	
	public static final float DEFAULT_SPEED = 500f;
	
	public static final float BEAM_SIZE = 50f;
	
	public Position position;
	public float speed;
	public Sprite sprite;
	
	public Point origin;
	public Point destination;
	
	public boolean isOnExlplosionCooldown = false;
	public boolean hasExploded = false;
	
	/* Constructor */
	public LaserBeam(float x, float y){
		this(new Position(x,y));
	}
	public LaserBeam(Position p){
		this.position = (Position) p.clone();
		this.speed = LaserBeam.DEFAULT_SPEED;
		this.sprite = (Sprite) SpriteFactory.getMe().newSprite(SpriteFactory.LASER, BEAM_SIZE*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR, BEAM_SIZE*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR);
		this.sprite.setRotationCenter(BEAM_SIZE/2, BEAM_SIZE/2);
	}
	
	
	
	/* Getters/Setters */
	public Position getPosition() {
		return this.position;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public Point getSpriteOffset(){
		Point spriteCenter = new Point(getSpriteOffsetX(), getSpriteOffsetY());
		return spriteCenter;
	}
	
	public float getSpriteOffsetX(){
		return this.sprite.getWidth()/2;
	}
	public float getSpriteOffsetY(){
		return (Walker.SPRITE_HEIGHT*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR*0.6f);
	}
	
	public float getTotalDistance(){
		return this.origin.distance(this.destination);
	}

	
	public float getTraveledDistance(){
		return this.position.distance(this.origin);
	}
	

	
	
	public boolean isBusy(){
		return false;
	}
	
	public boolean isMoving(){
		return this.destination != null;
	}
	
	/* Methods */
	public void goTo(Point destination) {
		this.origin = position.toPoint();
		this.destination = destination;
		
		//TODO: configurar rotacion del laser!
		
	}
	
	public void endMovement(){
		//testing code
		if(this.position.getEntityModifierCount() > 1){
			Debug.d("ball","ALERT: paramos animacion jugador y tenemos MODIFIERS acumulados: "+this.position.getEntityModifierCount());
		}// testing code
		
		this.destination = null;
		//animateStop(); //no aplica
	}
	
	public void forceStopMovement(){
		removeOldMovementOrders();
		//animateStop(); //no aplica
	}
	
	public void explode(){
		//testing code
		if(this.position.getEntityModifierCount() > 1){
			Debug.d("laser","ALERT: paramos animacion jugador y tenemos MODIFIERS acumulados: "+this.position.getEntityModifierCount());
		}// testing code
		
		this.destination = null;
		animateExplosion();
	}
	
	
	public void animateExplosion(){
		
		//TODO: configurar animacion de explosión. Dejo comentado un codigo de ejemplo
		/*
		long tileDuration = 300;
		sprite.animate(new long[]{tileDuration, tileDuration}, 1, 3, true);
		*/
		isOnExlplosionCooldown = true; 
		
		TimerHelper.startTimer(this.position, 300*2,  new ITimerCallback() 
		{                      
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	if(destination != null){
            		explosionEnd();
            	}
            } 
        });
	}
	
	private void explosionEnd(){
		this.isOnExlplosionCooldown = false;
		this.hasExploded = true;
	}
	
	
	public void recycle(){
		this.sprite.detachSelf();
		this.sprite.unregisterEntityModifiers(null);
		this.sprite.unregisterUpdateHandlers(null);
		this.speed = 0;
		this.position.setLocation(0, 0);
	}
	
	/* HELPERS */
	public void removeOldMovementOrders(){
		this.destination = null;
		this.position.unregisterEntityModifiers(new IEntityModifier.IEntityModifierMatcher()
		{
			public boolean matches(IModifier<org.andengine.entity.IEntity> pObject) {
				boolean matches = pObject.getClass().equals(PathModifier.class) || pObject.getClass().equals(MoveModifier.class);
				return matches;
			}
		});
	}
	

	public Sprite getSprite() {
		return sprite;
	}
	
	
}
