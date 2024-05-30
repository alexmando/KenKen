package src;

import java.util.*;

public class Griglia {

	private int size;
	private Cella[][] celle;
	private List<Blocco> blocchi;
	
	
	public Griglia(int size, List<Blocco> blocchi) {
		if(size < 3 || size >6)
			throw new IllegalArgumentException();
		this.size=size;
		this.blocchi=blocchi;
	}
}
