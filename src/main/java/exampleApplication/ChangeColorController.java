package exampleApplication;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChangeColorController {

	private static final String RED = "1.0 0.0 0.0";
	private static final String GREEN = "0.0 1.0 0.0";
	private static final String BLUE = "0.0 0.0 1.0";

    @MessageMapping("/changeColor")
    @SendTo("/topic/changeColor")
    public X3dObject changeColor(X3dObject object) throws Exception {
        String diffuseColor = object.getDiffuseColor();
        
        if(diffuseColor.equals(RED))
        	object.setDiffuseColor(GREEN);
        
        else if(diffuseColor.equals(GREEN))
        	object.setDiffuseColor(BLUE);
        
        else
        	object.setDiffuseColor(RED);
    	
        return object;
    }

}
