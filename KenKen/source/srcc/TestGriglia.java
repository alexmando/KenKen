package srcc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.*;


public class TestGriglia {

	Griglia griglia = new Griglia(4);
	
	 @Test
	 @DisplayName("Controllo le operazioni sui blocchi della griglia")
	    void testBlocks() {
	        Cella cell = new Cella(1,1);
	        Blocco block = griglia.attaccaBlocco(new BloccoImpl(cell));
	        assertAll(
	                ()->assertTrue(griglia.getBlocchi().contains(block)),
	                ()->assertFalse(griglia.getCelleNonAssegnate().contains(cell)),
	                ()->{
	                    griglia.remove(block);
	                    assertFalse(griglia.getBlocchi().contains(block));
	                },
	                ()->assertTrue(griglia.getCelleNonAssegnate().contains(cell))
	        );
	    }
	 
	 
}
