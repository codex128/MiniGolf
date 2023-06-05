package codex.minigolf;

import codex.jmeutil.character.OrbitalCamera;
import codex.jmeutil.scene.SceneGraphIterator;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
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
	Club club = new Club("MyClub", 4000f, 0f);
	float charge = 0f;
	float chargeRate = .5f;
	Vector3f startlocation = new Vector3f();
	Vector3f endlocation = new Vector3f();
	Vector3f lastputt = new Vector3f();
	CameraLight camLight;
	
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
		//bulletapp.setDebugEnabled(true);
		stateManager.attach(bulletapp);
		
		ball = new RigidBodyControl(1f);
		Spatial b = assetManager.loadModel("Models/ball.j3o");
		Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		m.setTexture("DiffuseMap", assetManager.loadTexture("Textures/blue unit.png"));
		b.setMaterial(m);
		b.addControl(ball);
		getPhysicsSpace().add(ball);
		rootNode.attachChild(b);
		ball.setDamping(.4f, .3f);
		ball.setRestitution(1f);
		//ball.setSleepingThresholds(2f, 2f);
		
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
				rigidbody.setRestitution(.8f);
			}
			else if (spatial.getName().equals("start")) {
				startlocation.set(spatial.getWorldTranslation());
			}
			else if (spatial.getName().equals("end")) {
				endlocation.set(spatial.getWorldTranslation());
			}
		}
		rootNode.attachChild(hole);
		
		camLight = new CameraLight(cam);
		rootNode.addLight(camLight);
		
		ball.setPhysicsLocation(startlocation);
		
		im.addAnalogListener(this, Functions.F_SWING);
		im.addStateListener(this, Functions.F_SWING, Functions.F_RESTART);
		im.activateGroup(Functions.GOLFER_GROUP);
		im.activateGroup(Functions.LEVEL_MANAGE_GROUP);
		
    }
    @Override
    public void simpleUpdate(float tpf) {
		if (ball.getPhysicsLocation().y < -10f) {
			setBallLocation(lastputt);
		}
		if (ball.getPhysicsLocation().distance(endlocation) < .5f) {
			System.out.println("you win!");
		}
		camLight.update(tpf);
	}
    @Override
    public void simpleRender(RenderManager rm) {}
	@Override
	public void valueActive(FunctionId func, double value, double tpf) {
		if (func == Functions.F_SWING && !ball.isActive()) {
			charge = Math.min(charge+chargeRate*(float)tpf, 1f);
		}
	}
	@Override
	public void valueChanged(FunctionId func, InputState value, double tpf) {
		if (func == Functions.F_SWING && charge > 0f && value == InputState.Off) {
			putt(charge);
			charge = 0f;
		}
		else if (func == Functions.F_RESTART && value == InputState.Positive) {
			setBallLocation(lastputt);
		}
	}
	
	private void putt(float force) {
		lastputt.set(ball.getPhysicsLocation());
		ball.applyCentralForce(club.calculateHitForce(camera.getPlanarCameraDirection(), force));
	}
	private void setBallLocation(Vector3f vec) {
		ball.setPhysicsLocation(vec);
		ball.setLinearVelocity(new Vector3f());
		ball.setAngularVelocity(new Vector3f());
	}
	
	private PhysicsSpace getPhysicsSpace() {
		return bulletapp.getPhysicsSpace();
	}
	
}
