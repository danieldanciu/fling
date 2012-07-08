package com.awesome.fling.mobile;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.android.angle.AngleActivity;
import com.android.angle.AnglePhysicObject;
import com.android.angle.AnglePhysicsEngine;
import com.android.angle.AngleRotatingSprite;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpriteLayout;
import com.android.angle.AngleUI;
import com.android.angle.AngleVector;
import com.awesome.fling.R;
import com.awesome.fling.anymotecom.AnymoteComm;
import com.awesome.fling.anymotecom.FlingComm;
import com.awesome.fling.anymotecom.FlingCommImpl;

public class FlingActivity extends AngleActivity implements SensorEventListener {
	
	/* sensor data */
    SensorManager m_sensorManager;
    float []m_lastMagFields;
    float []m_lastAccels;
    private float[] m_rotationMatrix = new float[16];
    private float[] m_remappedR = new float[16];
    private float[] m_orientation = new float[4];
 
    /* fix random noise by averaging tilt values */
    final static int AVERAGE_BUFFER = 30;
    float []m_prevPitch = new float[AVERAGE_BUFFER];
    float m_lastPitch = 0.f;
    float m_lastYaw = 0.f;
    /* current index int m_prevEasts */
    int m_pitchIndex = 0;
 
    float []m_prevRoll = new float[AVERAGE_BUFFER];
    float m_lastRoll = 0.f;
    /* current index into m_prevTilts */
    int m_rollIndex = 0;
 
    /* center of the rotation */
    private float m_tiltCentreX = 0.f;
    private float m_tiltCentreY = 0.f;
    private float m_tiltCentreZ = 0.f;

	private FlingComm anymoteComm;
	private boolean anymoteCommReady;

	private final static String LOG_TAG = FlingActivity.class
			.getCanonicalName();

	private MyDemo mDemo;
	private int screenWidth;
	private int screenHeight;

	private final SensorEventListener mListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				mDemo.setGravity(-event.values[0], event.values[1]);
			}
		}
	};

	private SensorManager mSensorManager;

	private Handler handler;

	private class Tomato extends AnglePhysicObject {
		private AngleSprite mSprite;
		private Sling mSling;

		public Tomato(AngleSpriteLayout layout, Sling sling) {
			super(0, 1);
			mSprite = new AngleSprite(layout);
			mSling = sling;
		}

		@Override
		public float getSurface() {
			return 29 * 2; // Radius * 2 >Radio * 2
		}

		@Override
		public void draw(GL10 gl) {
			mSprite.mPosition.set(mPosition);
			mSprite.draw(gl);
		}

		public void setPosition(float x, float y) {
			this.mPosition.set(x, y);
		}
		
		@Override
		public void step(float secondsElapsed) {
			notifySling();
		}

		private void notifySling() {
			this.mSling.updateObjectPosition(
				this.mPosition.mX, this.mPosition.mY);
		}

		public void putBack(int screenWidth, int screenHeight) {
			setPosition(screenWidth / 2.0f, screenHeight / 4.0f);
			mVelocity.set(new AngleVector(0, 0));
		}
	};

	private class Sling extends AnglePhysicObject {
		private AngleRotatingSprite ropeLeft;
		private AngleRotatingSprite ropeRight;

		public Sling(int screenWidth) {
			super(0, 0);
			
			ropeLeft = new AngleRotatingSprite(0, 0, new AngleSpriteLayout(mGLSurfaceView, 32, 4096, R.drawable.rope, 0, 0, 8, 512));
			ropeRight = new AngleRotatingSprite(0, 0, new AngleSpriteLayout(mGLSurfaceView, 32, 4096, R.drawable.rope, 0, 0, 8, 512));
		}

		public void addToDemo(AnglePhysicsEngine mPhysicsEngine) {
			mPhysicsEngine.addObject(ropeLeft);
			mPhysicsEngine.addObject(ropeRight);
		}

		public void updateObjectPosition(float x, float y) {
			ropeLeft.mPosition.mX = x;
			ropeLeft.mPosition.mY = y;
			ropeLeft.mRotation = FloatMath.sin(x/y) * (180.0f / (float) Math.PI);
			
			ropeRight.mPosition.mX = x;
			ropeRight.mPosition.mY = y;
			ropeRight.mRotation = -FloatMath.sin((screenWidth-x)/y) * (180.0f / (float) Math.PI);
		}
	};

	private class MyDemo extends AngleUI {
		AngleSpriteLayout mBallLayout;
		private Tomato mBall;
		private Sling sling;
		private float dx;
		private int screenWidth;
		private int screenHeight;
		private AnglePhysicsEngine mPhysicsEngine;
		
		private boolean isDragging;
		private boolean isThrowing;
		private double percentageX;
		private double percentageY;

		@TargetApi(13)
		public MyDemo(AngleActivity activity) {
			super(activity);

			mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 256, 256,
					R.drawable.tomato, 0, 0, 256, 256);

			mPhysicsEngine = new AnglePhysicsEngine(3);

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
			
			sling = new Sling(screenWidth);

			mBall = new Tomato(mBallLayout, sling);
			mBall.putBack(screenWidth, screenHeight);
			
			detectScreenSize();
			mBall.putBack(screenWidth, screenHeight);
			isDragging = false;
			
			sling.addToDemo(mPhysicsEngine);
			mPhysicsEngine.addObject(mBall);
			addObject(mPhysicsEngine);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			float y = event.getY();
			float x = event.getX();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isDragging && !isThrowing) {
					int left = (int) (mBall.mPosition.mX - 128);
					int top = (int) (mBall.mPosition.mY - 128);
					int right = left + 256;
					int bottom = top + 256;
					if (left <= x && x <= right
							&& top <= y && y <= bottom) {
						isDragging = true;
					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (isDragging && !isThrowing) {
					mBall.setPosition(x, y);
					mBall.mVelocity.set(new AngleVector(0, 0));
				}
				
				break;
			case MotionEvent.ACTION_UP:
				if (isDragging) {
					dx = screenWidth / 2 - x;
					
					percentageX = 1 - x / screenWidth;
					percentageY = 0.5 - m_lastRoll / 90; 
					Log.d(LOG_TAG, "Percentage is " +percentageX);
										
					mBall.mVelocity.set(new AngleVector(dx * 5, -y * 4));
					isDragging = false;
					isThrowing = true;
					
					if (anymoteCommReady) {
						anymoteComm.throwTomato((float)(percentageX), (float)(percentageY));
					}

					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							isThrowing = false;
							mBall.putBack(screenWidth, screenHeight);
						}
					}, 2000);

				}
				break;
			}

			return super.onTouchEvent(event);
		}

		public void setGravity(float x, float y) {
			// this.x = x;
			// this.y = y;

			// mPhysics.mGravity.set(x*3,y*3);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		anymoteCommReady = false;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// mGLSurfaceView.addObject(new FPSCounter());

		FrameLayout mMainLayout = new FrameLayout(this);
		mMainLayout.addView(mGLSurfaceView);
		setContentView(mMainLayout);
		
		if (true) {
			anymoteComm = new FlingCommImpl(this,
					new AnymoteComm.OnConnectedListener() {
						public void onConnected() {
							anymoteCommReady = true;
						}
					});
		}

		mDemo = new MyDemo(this);
		setUI(mDemo);

		HandlerThread handlerThread = new HandlerThread(getClass().getName());
		handlerThread.start();

		handler = new Handler(handlerThread.getLooper());
		detectScreenSize();
		
		m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerListeners();

	}

	// Overload onPause and onResume to enable and disable the accelerometer
	// Sobrecargamos onPause y onResume para activar y desactivar el
	// aceler�metro
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mListener);
		unregisterListeners();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mSensorManager.registerListener(mListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		registerListeners();
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		detectScreenSize();
	}

	@TargetApi(13)
	private void detectScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
	}
	
	private void registerListeners() {
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }
	
	private void unregisterListeners() {
        m_sensorManager.unregisterListener(this);
    }
	
	 @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	 
	    @Override
	    public void onSensorChanged(SensorEvent event) {
	        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	            accel(event);
	        }
	        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	            mag(event);
	        }
	    }
	 
	    private void accel(SensorEvent event) {
	        if (m_lastAccels == null) {
	            m_lastAccels = new float[3];
	        }
	 
	        System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
	 
	        /*if (m_lastMagFields != null) {
	            computeOrientation();
	        }*/
	    }
	 
	    private void mag(SensorEvent event) {
	        if (m_lastMagFields == null) {
	            m_lastMagFields = new float[3];
	        }
	 
	        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);
	 
	        if (m_lastAccels != null) {
	            computeOrientation();
	        }
	    }
	 
	    Filter [] m_filters = { new Filter(), new Filter(), new Filter() };
	 
	    private class Filter {
	        static final int AVERAGE_BUFFER = 10;
	        float []m_arr = new float[AVERAGE_BUFFER];
	        int m_idx = 0;
	 
	        public float append(float val) {
	            m_arr[m_idx] = val;
	            m_idx++;
	            if (m_idx == AVERAGE_BUFFER)
	                m_idx = 0;
	            return avg();
	        }
	        public float avg() {
	            float sum = 0;
	            for (float x: m_arr)
	                sum += x;
	            return sum / AVERAGE_BUFFER;
	        }
	 
	    }
	 
	    private void computeOrientation() {
	        if (SensorManager.getRotationMatrix(m_rotationMatrix, null,  m_lastAccels, m_lastMagFields)) {
	            SensorManager.getOrientation(m_rotationMatrix, m_orientation);
	 
	            /* 1 radian = 57.2957795 degrees */
	            /* [0] : yaw, rotation around z axis
	             * [1] : pitch, rotation around x axis
	             * [2] : roll, rotation around y axis */
	            float yaw = m_orientation[0] * 57.2957795f;
	            float pitch = m_orientation[1] * 57.2957795f;
	            float roll = m_orientation[2] * 57.2957795f;
	 
	            m_lastYaw = m_filters[0].append(yaw);
	            m_lastPitch = m_filters[1].append(pitch);
	            m_lastRoll = m_filters[2].append(roll);
	           
	            Log.d(LOG_TAG, "azi z: " + m_lastYaw);
	            Log.d(LOG_TAG,"pitch x: " + m_lastPitch);
	            Log.d(LOG_TAG,"roll y: " + m_lastRoll);
	        }
	    }
	    public void onDestroy() {
	        super.onDestroy();
	        if (anymoteCommReady) {
	        	anymoteComm.release();
	      }
	    }
}
