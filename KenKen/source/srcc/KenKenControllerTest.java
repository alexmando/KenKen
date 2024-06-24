package srcc;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class KenKenControllerTest {

	KenKenController controller = new KenKenController(3);
	
	
	 @Test
	 @DisplayName("Check if file name is correctly manipulated in order to add missing json extension.")
	 void getFileAfterSaving() {
	     File f = new File("ciao");
	        assertAll(
	                ()->assertEquals(KenKenController.getFileAfterSaving(f).getName(),"ciao.json"),
	                ()->{
	                    File f2 = new File("ciao.json");
	                    assertEquals(KenKenController.getFileAfterSaving(f2).getName(),"ciao.json");
	                }
	        );
	    }
	 
	 @Test
	 @DisplayName("Check game saving.")
	 void save() {
	        assertAll(
	                ()->assertThrows(RuntimeException.class,()->controller.save()),
	                ()->{
	                    controller.openBoard(new File("template.json"));
	                    File f = new File("ciao.json");
	                    f.deleteOnExit();
	                    controller.save(f);
	                    KenKenController c2 = new KenKenController(3);
	                    c2.openBoard(f);
	                    assertEquals(controller,c2);
	                }
	        );
	    }
}
