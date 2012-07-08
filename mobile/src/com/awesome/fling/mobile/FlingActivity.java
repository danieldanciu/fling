package com.awesome.fling.mobile;

import javax.microedition.khronos.opengles.GL10;

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

import android.content.Context;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

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

	private AnymoteComm anymoteComm;
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

		public Tomato(AngleSpriteLayout layout) {
			super(0, 1);
			mSprite = new AngleSprite(layout);
			addCircleCollider(new AngleCircleCollider(0, 0, 29));
			mMass = 10;
			mBounce = 0.6f; // Coefficient of restitution (1 return all the
							// energy) >Coeficiente de restituci�n (1 devuelve
							// toda la energia)
		}

		@Override
		public float getSurface() {
			return 29 * 2; // Radius * 2 >Radio * 2
		}

		@Override
		public void draw(GL10 gl) {
			mSprite.mPosition.set(mPosition);
			mSprite.draw(gl);
			// Draw colliders (beware calls GC)
			// >Dibujado de los lolisionadores (cuidado, llama al GC)
			// drawColliders(gl);
		}

	};

	private class Sling extends AnglePhysicObject {
		private AngleSprite leftSprite;
		private AngleSprite rightSprite;

		public Sling(AngleSpriteLayout left, AngleSpriteLayout right) {
			super(0, 0);
			leftSprite = new AngleSprite(left);
			rightSprite = new AngleSprite(right);

			mMass = 10;
			mBounce = 0.6f; // Coefficient of restitution (1 return all the
							// energy) >Coeficiente de restitucin (1 devuelve
							// toda la energia)
		}

		@Override
		public float getSurface() {
			return 29 * 2; // Radius * 2 >Radio * 2
		}

		@Override
		public void draw(GL10 gl) {
			leftSprite.draw(gl);

		}

	};

	private class MyDemo extends AngleUI {
		AngleSpriteLayout mBallLayout;
		AnglePhysicsEngine mPhysics;
		private Tomato mBall;
		private float x;
		private float y;
		private float dx;
		private float dy;
		
		private boolean isDragging;
		private boolean isThrowing;
		private double procentageX;
		private double procentageY;

		public MyDemo(AngleActivity activity) {
			super(activity);

			mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 256, 256,
					R.drawable.tomato, 0, 0, 256, 256);
			mPhysics = new AnglePhysicsEngine(20);
			mPhysics.mViscosity = 0f; // Air viscosity >Viscosidad del aire
			addObject(mPhysics);

			mBall = new Tomato(mBallLayout);
			mPhysics.addObject(mBall);
			
			detectScreenSize();
			mBall.mPosition.set(screenWidth / 2, screenHeight / 3);
			Log.d(FlingActivity.class.getCanonicalName(), "Ball is at "
					+ mBall.mPosition.mX + " " + mBall.mPosition.mY);
			isDragging = false;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			Log.d(FlingActivity.class.getCanonicalName(), "Event is at "
					+ event.getX() + " " + event.getY());

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isDragging && !isThrowing) {
					int left = (int) (mBall.mPosition.mX - 128);
					int top = (int) (mBall.mPosition.mY - 128);
					int right = left + 256;
					int bottom = top + 256;
					if (left <= event.getX() && event.getX() <= right
							&& top <= event.getY() && event.getY() <= bottom) {
						Log.d(LOG_TAG, "============ Rectangle " + left + " "
								+ top + " " + right + " " + bottom);
						isDragging = true;
					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (isDragging && !isThrowing) {
					mBall.mPosition.set(event.getX(), Math.max(screenHeight/3, event.getY()));
					mBall.mVelocity.set(new AngleVector(0, 0));
				}
				
				break;
			case MotionEvent.ACTION_UP:
				if (isDragging) {
					dx = screenWidth / 2 - event.getX();
					
					procentageX = 1 - event.getX() / screenWidth;
					procentageY = 0.5 - m_lastRoll / 90; 
					Log.d(LOG_TAG, "Procentage is " +procentageX);
										
					mBall.mVelocity.set(new AngleVector(dx * 5,
							-event.getY() * 4));
					isDragging = false;
					isThrowing = true;
					
					if (anymoteCommReady) {
						anymoteComm.sendString("ba");
					}

					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							isThrowing = false;
							mBall.mPosition.set((float) (procentageX * screenWidth),
									(float)procentageY * screenHeight);
							mBall.mVelocity.set(new AngleVector(0, 0));
						}
					}, 500);

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
		
		anymoteComm = new AnymoteCommImpl(this,
				new AnymoteComm.OnConnectedListener() {
					@Override
					public void onConnected() {
						// anymoteComm.sendString("ba");
						anymoteCommReady = true;
					}
				});

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
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		detectScreenSize();
	}

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
	

}
