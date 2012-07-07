package com.android.box2d;


public class BodyDef
{
	public static final int b2_staticBody = 0;
	public static final int b2_kinematicBody = 1;
	public static final int b2_dynamicBody = 2;
	public int type;
	public Vec2 position;
	public float angle;
	public Vec2 linearVelocity;
	public float angularVelocity;
	public float linearDamping;
	public float angularDamping;
	public boolean allowSleep;
	public boolean awake;
	public boolean fixedRotation;
	public boolean bullet;
	public boolean active;
	public float inertiaScale;
	public BodyDef()
	{
		type = b2_staticBody;
		position=new Vec2();
		angle = 0.0f;
		linearVelocity=new Vec2();
		angularVelocity = 0.0f;
		linearDamping = 0.0f;
		angularDamping = 0.0f;
		allowSleep = true;
		awake = true;
		fixedRotation = false;
		bullet = false;
		active = true;
		inertiaScale = 1.0f;
	}
};

