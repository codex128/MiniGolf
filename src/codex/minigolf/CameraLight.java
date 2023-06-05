/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.minigolf;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;

/**
 *
 * @author gary
 */
public class CameraLight extends DirectionalLight {
	
	Camera cam;
	
	public CameraLight(Camera cam) {
		super(cam.getDirection());
		this.cam = cam;
	}
	public CameraLight(Camera cam, ColorRGBA color) {
		super(cam.getDirection(), color);
		this.cam = cam;
	}
	
	public void update(float tpf) {
		setDirection(cam.getDirection());
	}
	
}
