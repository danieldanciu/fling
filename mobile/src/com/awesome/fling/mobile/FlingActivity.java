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

public class FlingActivity extends AngleActivity {

	private AnymoteComm anymoteComm;
	private boolean anymoteCommReady;

	private final static String LOG_TAG = FlingActivity.class
			.getCanonicalName();

	private MyDemo mDemo;

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
		private int screenWidth;
		private int screenHeight;
		private boolean isDragging;
		private boolean isThrowing;

		public MyDemo(AngleActivity activity) {
			super(activity);

			mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 256, 256,
					R.drawable.tomato, 0, 0, 256, 256);
			mPhysics = new AnglePhysicsEngine(20);
			mPhysics.mViscosity = 0f; // Air viscosity >Viscosidad del aire
			addObject(mPhysics);

			mBall = new Tomato(mBallLayout);
			mPhysics.addObject(mBall);

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;

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
							mBall.mPosition.set(screenWidth / 2,
									screenHeight / 3);
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

	}

	// Overload onPause and onResume to enable and disable the accelerometer
	// Sobrecargamos onPause y onResume para activar y desactivar el
	// aceler�metro
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		mSensorManager.registerListener(mListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		super.onResume();
	}

}
