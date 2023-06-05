/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.minigolf;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.input.Button;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;

/**
 *
 * @author gary
 */
public class Functions {
	
	public static final String
			GOLFER_GROUP = "golfer-group",
			LEVEL_MANAGE_GROUP = "level-manage-group";
	
	public static final FunctionId
			F_SWING = new FunctionId(GOLFER_GROUP, "swing"),
			F_RESTART = new FunctionId(LEVEL_MANAGE_GROUP, "restart");
	
	public static void initialize(InputMapper im) {
		im.map(F_SWING, Button.MOUSE_BUTTON1);
		im.map(F_RESTART, KeyInput.KEY_R);
	}
	
}
