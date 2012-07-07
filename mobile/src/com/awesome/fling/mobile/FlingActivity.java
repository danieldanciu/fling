package com.awesome.fling.mobile;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.android.angle.AngleActivity;
import com.android.angle.AngleCircleCollider;
import com.android.angle.AnglePhysicObject;
import com.android.angle.AnglePhysicsEngine;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpriteLayout;
import com.android.angle.AngleUI;
import com.android.angle.AngleVector;
import com.android.angle.FPSCounter;
import com.awesome.fling.R;
import com.awesome.fling.anymotecom.AnymoteComm;
import com.awesome.fling.anymotecom.AnymoteCommImpl;

public class FlingActivity extends AngleActivity {
	private AnymoteComm anymoteComm;
	private boolean anymoteCommReady;
	
	private MyDemo mDemo;
	
	   private final SensorEventListener mListener = new SensorEventListener() 
	   {
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
			}

			@Override
			public void onSensorChanged(SensorEvent event)
			{
				if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
				{
					mDemo.setGravity(-event.values[0],event.values[1]);
				}
			}
	   };

		private SensorManager mSensorManager; 	

		private class Ball extends AnglePhysicObject
		{
			private AngleSprite mSprite;

			public Ball(AngleSpriteLayout layout)
			{
				super(0, 1);
				mSprite=new AngleSprite(layout);
				addCircleCollider(new AngleCircleCollider(0, 0, 29));
				mMass = 10;
				mBounce = 0.6f; // Coefficient of restitution (1 return all the energy)  >Coeficiente de restituci�n (1 devuelve toda la energia)
			}

			@Override
			public float getSurface()
			{
				return 29 * 2; // Radius * 2  >Radio * 2
			}

			@Override
			public void draw(GL10 gl)
			{
				mSprite.mPosition.set(mPosition);
				mSprite.draw(gl);
				//Draw colliders (beware calls GC)
				//>Dibujado de los lolisionadores (cuidado, llama al GC)
				//drawColliders(gl);
			}
			
			
		};

		private class MyDemo extends AngleUI
		{
			AngleSpriteLayout mBallLayout;
			AnglePhysicsEngine mPhysics;
			private Ball mBall;
			private float x;
			private float y;
			private float dx;
			private float dy;
			

			
			public MyDemo(AngleActivity activity)
			{
				super(activity);
				

				mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 256, 256, R.drawable.tomato, 0, 0, 256, 256);
				mPhysics=new AnglePhysicsEngine(20);
				mPhysics.mViscosity = 0f; // Air viscosity >Viscosidad del aire
				addObject(mPhysics);

				mBall = new Ball (mBallLayout);				
				mPhysics.addObject(mBall);
				
				AngleRope rope = new AngleRope(0.0f, 0.0f, 2.0f, 5.0f);
				mPhysics.addObject(rope);
			}


			@Override
			public boolean onTouchEvent(MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN ||
					event.getAction() == MotionEvent.ACTION_MOVE)
				{
					mBall.mPosition.set(event.getX(), event.getY());
					dx = event.getX() - x;
					dy = event.getY() - y;
					x = event.getX();
					y = event.getY();
					mBall.mVelocity.set(new AngleVector(0, 0));
					
				} else if (event.getAction() == MotionEvent.ACTION_UP){
					mBall.mVelocity.set(new AngleVector(dx * 50, dy * 50));
					if (anymoteCommReady) {
						anymoteComm.sendString("ba");
					}
				}
				return super.onTouchEvent(event);
			}

			public void setGravity(float x, float y)
			{
//				this.x = x;
//				this.y = y;
				
				//mPhysics.mGravity.set(x*3,y*3);
			}
			
		}

		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			anymoteCommReady = false;
			
			mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
	      
			//mGLSurfaceView.addObject(new FPSCounter());

			FrameLayout mMainLayout=new FrameLayout(this);
			mMainLayout.addView(mGLSurfaceView);
			setContentView(mMainLayout);
			anymoteComm =
			        new AnymoteCommImpl(this,
			        		new AnymoteComm.OnConnectedListener() {
			          @Override
			          public void onConnected() {
			        	  //anymoteComm.sendString("ba");
			        	  anymoteCommReady = true;
			          }
			        });
			
			mDemo=new MyDemo(this);
			setUI(mDemo);
		}


		//Overload onPause and onResume to enable and disable the accelerometer
		//Sobrecargamos onPause y onResume para activar y desactivar el aceler�metro
		@Override
		protected void onPause()
		{
	      mSensorManager.unregisterListener(mListener); 
	      super.onPause();
		}


		@Override
		protected void onResume()
		{
	      mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST); 		
			super.onResume();
		}

}
