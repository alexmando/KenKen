package srcc;
import java.util.*;

import srcc.BloccoImpl.Operatore;
public interface Blocco extends Iterable<Cella>{

	void add(Cella c);
	
	void remove(Cella c);
	
	boolean contains(Cella c);
	
	int size();
	
	boolean bloccoValido();
	
	boolean haVincoli();
	
	boolean èPieno();
	
	default int getMaxBlockSize() { 
		return Utilità.MAX_BLOCK_SIZE; 
	
	}

	List<Cella> getCelleDelBlocco();
	
	void setOperatore(Operatore o);
	
	Operatore getOperatore();
	
	void setNumeroOperando(int numeroOperando);
	
	int getNumeroOperando();
}