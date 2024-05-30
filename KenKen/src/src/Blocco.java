package src;

public interface Blocco extends Iterable<Cella>{

	void add(Cella c);
	
	void remove(Cella c);
	
	boolean contains(Cella c);
	
	int size();
	
	boolean bloccoValido();
	
	boolean haVincoli();
	
	boolean èPieno();
	
	default int getMaxBlockSize() { return Utilità.MAX_BLOCK_SIZE; }
	
}
