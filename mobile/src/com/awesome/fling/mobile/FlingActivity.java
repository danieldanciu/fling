package com.awesome.fling.mobile;

import javax.microedition.khronos.opengles.GL10;

import com.android.angle.AngleActivity;
import com.android.angle.AngleCircleCollider;
import com.android.angle.AngleObject;
import com.android.angle.AnglePhysicObject;
import com.android.angle.AnglePhysicsEngine;
import com.android.angle.AngleRotatingSprite;
import com.android.angle.AngleSegmentCollider;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpriteLayout;
import com.android.angle.AngleUI;
import com.android.angle.FPSCounter;
import com.awesome.fling.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class FlingActivity extends AngleActivity {
	
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
				mBounce = 0.6f; // Coefficient of restitution (1 return all the energy)  >Coeficiente de restitución (1 devuelve toda la energia)
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
			
			public MyDemo(AngleActivity activity)
			{
				super(activity);
				

				mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 64, 64, R.drawable.tomato, 0, 0, 128, 128);
				mPhysics=new AnglePhysicsEngine(20);
				mPhysics.mViscosity = 0f; // Air viscosity >Viscosidad del aire
				addObject(mPhysics);

				mBall = new Ball (mBallLayout);				
				mPhysics.addObject(mBall);
			}

			@Override
			public boolean onTouchEvent(MotionEvent event)
			{
				if (event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE)
				{
					mBall.mPosition.set(event.getX(), event.getY());
					
				} else if (event.getAction() == MotionEvent.ACTION_UP){
					mPhysics.mGravity.set(x * 3, y *3);
					
				}
				return super.onTouchEvent(event);
			}

			public void setGravity(float x, float y)
			{
				this.x = x;
				this.y = y;
				
				//mPhysics.mGravity.set(x*3,y*3);
			}
			
		}

		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
	      
			mGLSurfaceView.addObject(new FPSCounter());

			FrameLayout mMainLayout=new FrameLayout(this);
			mMainLayout.addView(mGLSurfaceView);
			setContentView(mMainLayout);
			
			mDemo=new MyDemo(this);
			setUI(mDemo);
		}


		//Overload onPause and onResume to enable and disable the accelerometer
		//Sobrecargamos onPause y onResume para activar y desactivar el acelerómetro
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
