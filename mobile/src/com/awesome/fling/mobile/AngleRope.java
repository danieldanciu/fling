package com.awesome.fling.mobile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.android.angle.AngleObject;
import com.android.angle.AngleVector;

public class AngleRope extends AngleObject {
	private AngleVector a;
	private AngleVector b;
	
	public AngleRope(AngleVector a, AngleVector b) {
		setStart(a);
		setEnd(b);
	}
	
	public AngleRope(float ax, float ay, float bx, float by) {
		this(new AngleVector(ax, ay), new AngleVector(bx, by));
	}
	
	@Override public void draw(GL10 gl) {
		// Copied and modified from AngleSegmentCollider.
		FloatBuffer vertices;
		vertices = ByteBuffer.allocateDirect(2 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

		gl.glDisable(GL11.GL_TEXTURE_2D);
		gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glColor4f(1f, 0f, 0f, 1f);
		gl.glTranslatef(a.mX, a.mY, 0.0f);
		// gl.glTranslatef(mObject.mPosition.mX, mObject.mPosition.mY, 0.0f);
		vertices.clear();
		int count = 0;
		vertices.put(count++, a.mX);
		vertices.put(count++, a.mY);
		vertices.put(count++, b.mX);
		vertices.put(count++, b.mY);
		gl.glVertexPointer(4, GL11.GL_FLOAT, 0, vertices);
		gl.glDrawArrays(GL11.GL_LINES, 0, 4);
		gl.glPopMatrix();

		gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void setStart(AngleVector a) {
		this.a = a;
	}
	
	public void setEnd(AngleVector b) {
		this.b = b;
	}
}
