/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.minigolf;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author gary
 */
public class Club {
	
	private static final Quaternion
			DIRECTION_Y = new Quaternion().fromAngles(FastMath.HALF_PI, 0f, 0f);
	
	String name;
	float power;
	float wedge;
	
	public Club(String name, float power, float wedge) {
		this.name = name;
		this.power = power;
		this.wedge = wedge;
	}
	
	public String getName() {
		return name;
	}
	public float getPower() {
		return power;
	}
	public float getWedge() {
		return wedge;
	}
	
	public Vector3f calculateHitForce(Vector3f direction, float strength) {
		return FastMath.interpolateLinear(wedge, direction, Vector3f.UNIT_Y).multLocal(power*strength);
	}
	
}
