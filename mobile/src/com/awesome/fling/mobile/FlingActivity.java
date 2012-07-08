package com.awesome.fling.mobile;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
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
import com.awesome.fling.anymotecom.AnymoteCommImpl;

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

	};

	private class Sling extends AnglePhysicObject {
		private AngleRotatingSprite ropeLeft;
		private AngleRotatingSprite ropeRight;

		private int screenWidth;


		public Sling(int screenWidth) {
			super(0, 0);
			this.screenWidth = screenWidth;
			
			ropeLeft = new AngleRotatingSprite(0, 0, new AngleSpriteLayout(mGLSurfaceView, 32, 4096, R.drawable.rope, 0, 0, 8, 512));
			ropeRight = new AngleRotatingSprite(0, 0, new AngleSpriteLayout(mGLSurfaceView, 32, 4096, R.drawable.rope, 0, 0, 8, 512));
		}

		public void addToDemo(AnglePhysicsEngine mPhysicsEngine) {
			mPhysicsEngine.addObject(ropeLeft);
			mPhysicsEngine.addObject(ropeRight);
		}

		public void updateObjectPosition(int x, int y) {
			y = y + 40;
			ropeLeft.mPosition.mX = x;
			ropeLeft.mPosition.mY = y;
			ropeLeft.mRotation = FloatMath.sin((float)x/(float)y) * (180.0f / (float) Math.PI);
			
			ropeRight.mPosition.mX = x;
			ropeRight.mPosition.mY = y;
			ropeRight.mRotation = -FloatMath.sin((float)(screenWidth-x)/(float)y) * (180.0f / (float) Math.PI);
		}
	};

	private class MyDemo extends AngleUI {
		AngleSpriteLayout mBallLayout;
		private Tomato mBall;
		private Sling sling;
		private float dx;
		private int screenWidth;
		private int screenHeight;
		private boolean isDragging;
		private boolean isThrowing;
		private AnglePhysicsEngine mPhysicsEngine;

		@TargetApi(13)
		public MyDemo(AngleActivity activity) {
			super(activity);

			mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 256, 256,
					R.drawable.tomato, 0, 0, 256, 256);

			mPhysicsEngine = new TomatoPhysicsEngine(3);
			mBall = new Tomato(mBallLayout);

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;

			mBall.mPosition.set(screenWidth / 2, screenHeight / 3);
			
			sling = new Sling(screenWidth);
			
			Log.d(FlingActivity.class.getCanonicalName(), "Ball is at "
					+ mBall.mPosition.mX + " " + mBall.mPosition.mY);
			isDragging = false;
			
			mPhysicsEngine.addObject(mBall);
			sling.addToDemo(mPhysicsEngine);
			addObject(mPhysicsEngine);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			float y = event.getY();
			float x = event.getX();
			Log.d(FlingActivity.class.getCanonicalName(), "Event is at "
					+ x + " " + y);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(FlingActivity.class.getCanonicalName(), "DOWN");
				if (!isDragging && !isThrowing) {
					int left = (int) (mBall.mPosition.mX - 128);
					int top = (int) (mBall.mPosition.mY - 128);
					int right = left + 256;
					int bottom = top + 256;
					if (left <= x && x <= right
							&& top <= y && y <= bottom) {
						Log.d(LOG_TAG, "============ Rectangle " + left + " "
								+ top + " " + right + " " + bottom);
						isDragging = true;
					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(FlingActivity.class.getCanonicalName(), "MOVE");
				if (isDragging && !isThrowing) {
					mBall.mPosition.set(x, y);
					sling.updateObjectPosition((int) x, (int) y);
					mBall.mVelocity.set(new AngleVector(0, 0));
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.d(FlingActivity.class.getCanonicalName(), "UP");
				if (isDragging) {
					dx = screenWidth / 2 - x;
					mBall.mVelocity.set(new AngleVector(dx * 5,
							-y * 4));
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
