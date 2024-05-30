package src;
import java.util.*;

public class BloccoImpl implements Blocco{
	
	public enum Operatore{
		ADDIZIONE, MOLTIPLICAZIONE, SOTTRAZIONE, DIVISIONE;
	}

	private List<Cella> celleDelBlocco = new LinkedList<>();
	private int numeroOperando;
	private Operatore operatore;
	
	public BloccoImpl(LinkedList<Cella> celle, int numero, Operatore op) {
		this.celleDelBlocco = celle;
		this.numeroOperando = numero;
	    this.operatore=op;
	}

	public List<Cella> getCelleDelBlocco() {
		return celleDelBlocco;
	}

	public void setCelleDelBlocco(List<Cella> celleDelBlocco) {
		this.celleDelBlocco = celleDelBlocco;
	}

	public int getNumeroOperando() {
		return numeroOperando;
	}

	public void setNumeroOperando(int numeroOperando) {
		if(numeroOperando < 0)
			throw new IllegalArgumentException("ammessi solo valori strettamente maggiori di zero");
		this.numeroOperando = numeroOperando;
	}

	public Operatore getOperatore() {
		return operatore;
	}

	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	@Override
	public Iterator<Cella> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Cella c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Cella c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Cella c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean bloccoValido() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean haVincoli() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean èPieno() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
