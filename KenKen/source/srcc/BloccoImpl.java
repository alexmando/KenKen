package srcc;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
//assumo che i blocchi con sottrazione e divisione, possano avere solo due celle
public class BloccoImpl implements Blocco{
	
	public enum Operatore{
		ADDIZIONE, MOLTIPLICAZIONE, SOTTRAZIONE, DIVISIONE;
	}

	private Collection<Cella> celleDelBlocco = new LinkedList<>();
	private int numeroOperando;
	private Operatore operatore;
	
	public BloccoImpl(LinkedList<Cella> celle, int numero, Operatore op) {
		super();
		this.celleDelBlocco = celle;
		this.numeroOperando = numero;
	    this.operatore=op;
	}

	 public BloccoImpl(){
	        celleDelBlocco = new LinkedList<Cella>();
	 }
	 
	 public BloccoImpl(Cella c){
	        celleDelBlocco = new LinkedList<Cella>();
	        celleDelBlocco.add(c);
	    }
	
	
	@Override
	public List<Cella> getCelleDelBlocco() {
		return (List<Cella>) celleDelBlocco;
	}

	public void setCelleDelBlocco(List<Cella> celleDelBlocco) {
		this.celleDelBlocco = celleDelBlocco;
	}

	@Override
	public int getNumeroOperando() {
		return numeroOperando;
	}

	@Override
	public void setNumeroOperando(int numeroOperando) {
		if(numeroOperando < 0)
			throw new IllegalArgumentException("ammessi solo valori strettamente maggiori di zero");
		this.numeroOperando = numeroOperando;
	}

	@Override
	public Operatore getOperatore() {
		return operatore;
	}

	@Override
	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	@Override
	public Iterator<Cella> iterator() {
		return celleDelBlocco.iterator();
	}

	@Override
	public void add(Cella c) {
		if(this.èPieno())
			throw new IndexOutOfBoundsException();
		celleDelBlocco.add(c);
		
	}

	@Override
	public void remove(Cella c) {
		if(this.size()<1)
			throw new IllegalArgumentException("non c'è nulla da poter rimuovere");
		celleDelBlocco.remove(c);
		
	}

	@Override
	public boolean contains(Cella c) {
		for(Cella c1 : celleDelBlocco)
			if(c1.equals(c))
				return true;
		return false;
	}

	@Override
	public int size() {
		return celleDelBlocco.size();
	}

	@Override
	public boolean bloccoValido() {
		
		if(numeroOperando<0)
			throw new IllegalArgumentException("il valore dell'operazione deve essere positivo");
		Iterator<Cella> it = celleDelBlocco.iterator();
		if(this.size() == 1 )
			return it.next().getValore() == numeroOperando;
		switch(this.operatore){
			case ADDIZIONE:
				int valore = 0;
				for(Cella c : celleDelBlocco)
					valore += c.getValore();
				if(valore!=numeroOperando)
					return false;
				return true;
			case SOTTRAZIONE:
				int primo = it.next().getValore();
				int secondo = it.next().getValore();
				if(primo < 0 || secondo < 0)
					return false;
				if(secondo - primo == numeroOperando || primo - secondo == numeroOperando)
					return true;
				return false;
			case MOLTIPLICAZIONE:
				int valoreMul = 1;
				for(Cella c : celleDelBlocco)
					valoreMul *= c.getValore();
				if(valoreMul != numeroOperando)
					return false;
				return true;
			case DIVISIONE:
				primo = it.next().getValore();
				secondo = it.next().getValore();
				if(primo < 0 || secondo < 0)
					return false;
				if(secondo / primo == numeroOperando || primo / secondo == numeroOperando)
					return true;
				return false;
			default: return this.size()==1;
		}
	}

	@Override
	public boolean haVincoli() {
		if(this.numeroOperando > 0 && this.operatore!=null )
			return true;
		return false;
	}

	@Override
	public boolean èPieno() {
		return this.size() == Utilità.MAX_BLOCK_SIZE; 	
}
	
	
}

