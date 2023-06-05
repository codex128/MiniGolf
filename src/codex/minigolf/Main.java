package codex.minigolf;

import codex.jmeutil.character.OrbitalCamera;
import codex.jmeutil.scene.SceneGraphIterator;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements AnalogFunctionListener, StateFunctionListener {
	
	OrbitalCamera camera;
	BulletAppState bulletapp;
	RigidBodyControl ball;
	float baseHitForce = 1f;
	float charge = 0f;
	float chargeRate = .5f;
	
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
		GuiGlobals.initialize(this);
		GuiGlobals.getInstance().setCursorEventsEnabled(false);
		InputMapper im = GuiGlobals.getInstance().getInputMapper();
		Functions.initialize(im);
		
		bulletapp = new BulletAppState();
		bulletapp.setDebugEnabled(true);
		stateManager.attach(bulletapp);
		
		ball = new RigidBodyControl(1f);
		ball.setEnableSleep(true);
		Spatial b = assetManager.loadModel("Models/ball.j3o");
		b.addControl(ball);
		getPhysicsSpace().add(ball);
		rootNode.attachChild(b);
		
		camera = new OrbitalCamera(cam, im);
		camera.getDistanceDomain().set(10f, 15f);
		b.addControl(camera);
		im.activateGroup(OrbitalCamera.INPUT_GROUP);
		
		Spatial hole = assetManager.loadModel("Models/hole1.j3o");
		SceneGraphIterator iterator = new SceneGraphIterator(hole);
		for (Spatial spatial : iterator) {
			if (spatial instanceof Geometry) {
				RigidBodyControl rigidbody = new RigidBodyControl(0f);
				spatial.addControl(rigidbody);
				getPhysicsSpace().add(rigidbody);
			}
			else if (spatial.getName().equals("start")) {
				ball.setPhysicsLocation(spatial.getWorldTranslation());
			}
			else if (spatial.getName().equals("end")) {
				
			}
		}
		rootNode.addLight(new DirectionalLight(new Vector3f(1f, -1f, 1f)));
		rootNode.addLight(new DirectionalLight(new Vector3f(-1f, -1f, -1f)));
		rootNode.attachChild(hole);
		
		im.addAnalogListener(this, Functions.F_SWING);
		im.addStateListener(this, Functions.F_SWING);
		im.activateGroup(Functions.GOLFER_GROUP);
		
    }
    @Override
    public void simpleUpdate(float tpf) {}
    @Override
    public void simpleRender(RenderManager rm) {}
	@Override
	public void valueActive(FunctionId func, double value, double tpf) {
		if (func == Functions.F_SWING && value > 0 && !ball.isActive()) {
			charge = Math.min(charge+chargeRate*(float)tpf, 1f);
		}
	}
	@Override
	public void valueChanged(FunctionId func, InputState value, double tpf) {
		if (func == Functions.F_SWING && charge > 0f &&value == InputState.Off) {
			swing(charge);
			charge = 0f;
		}
	}
	
	public void swing(float force) {
		Vector3f vec = camera.getPlanarCameraDirection();
		vec.multLocal(baseHitForce*force);
		ball.applyCentralForce(vec);
	}
	
	private PhysicsSpace getPhysicsSpace() {
		return bulletapp.getPhysicsSpace();
	}
	
}
