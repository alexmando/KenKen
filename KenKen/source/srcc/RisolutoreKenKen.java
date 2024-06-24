package srcc;

import java.util.*;


public class RisolutoreKenKen extends Problema<Cella, Integer> {

	private Griglia griglia;
	
	private Map<Blocco, ArrayList<Cella>> map = new HashMap<>();
	
	private List<Blocco> blocchi = new ArrayList<>();
	
	private int indiceBlocco, indiceCella; 
	
	private Collection<Memento> output;
	
	public RisolutoreKenKen(Griglia g, int n, Collection<Memento> output) {
		super(n);
		this.griglia=g;
		if(!g.iniziaGioco())
			throw new IllegalStateException("la griglia ancora non è completa per iniziare a giocare");
		this.blocchi = g.getBlocchi();
		this.indiceBlocco = 0;
		this.indiceCella = 0;
		for(Blocco b : blocchi)
			map.put(b, new ArrayList<Cella>(b.getCelleDelBlocco()));
		this.output = output;
		
	}
	
	
	@Override
	protected Cella primoPuntoDiScelta() {
		return map.get(blocchi.get(0)).get(0); //prima cella del primo blocco della lista
	}

	@Override
	protected Cella prossimoPuntoDiScelta(Cella ps) {
		if(indiceCella == blocchi.get(indiceBlocco).size()-1) {
			indiceCella = 0;
			indiceBlocco++;
			if(indiceBlocco == blocchi.size())
				throw new IllegalArgumentException("blocchi da scegliere finiti!, tutti i blocchi sono stati scelti");
		}
		indiceCella++;
		return map.get(blocchi.get(indiceBlocco)).get(indiceCella);
	}

	@Override
	protected Cella ultimoPuntoDiScelta() {
		ArrayList<Cella> lista =  map.get(blocchi.get(blocchi.size()-1));
		return lista.get(lista.size()-1);
	}

	@Override
	protected Integer primaScelta(Cella ps) {
		return 1;
	}

	@Override
	protected Integer prossimaScelta(Integer s) {
		return s+1;
	}

	@Override
	protected Integer ultimaScelta(Cella ps) {
		return griglia.getSize();
	}

	@Override
	protected boolean assegnabile(Integer scelta, Cella puntoDiScelta) {
		for(int i=0; i< griglia.getSize(); i++)
			if(griglia.getCelle()[i][puntoDiScelta.getColonna()].getValore()==scelta)
				return false;
		for(int j=0; j< griglia.getSize(); j++)
			if(griglia.getCelle()[puntoDiScelta.getRiga()][j].getValore() == scelta)
				return false;
		Blocco copia = new BloccoImpl();
		for(Cella c : blocchi.get(indiceBlocco).getCelleDelBlocco())
			if(!c.equals(puntoDiScelta))
				copia.add(c);
		Cella simulazione = new Cella(puntoDiScelta.getRiga(), puntoDiScelta.getColonna());
		simulazione.setValore(scelta);
		copia.add(simulazione);
		copia.setOperatore(blocchi.get(indiceBlocco).getOperatore());
		copia.setNumeroOperando(blocchi.get(indiceBlocco).getNumeroOperando());
		return copia.bloccoValido();
	}

	@Override
	protected void assegna(Integer scelta, Cella puntoDiScelta) {
		puntoDiScelta.setValore(scelta);
		
	}

	@Override
	protected void deassegna(Integer scelta, Cella puntoDiScelta) {
		puntoDiScelta.setValore(0);
		
	}

	@Override
	protected Cella precedentePuntoDiScelta(Cella puntoDiScelta) {
		if(indiceCella==0) {
			if(indiceBlocco == 0)
				throw new IllegalArgumentException("in questo punto non si ha un precedente");
			indiceBlocco--;
			indiceCella = blocchi.get(indiceBlocco).size();
		}
		indiceCella--;
		return map.get(blocchi.get(indiceBlocco)).get(indiceCella);
	}

	@Override
	protected Integer ultimaSceltaAssegnataA(Cella puntoDiScelta) {
		return puntoDiScelta.getValore();
	}

	@Override
	protected void scriviSoluzione(int nr_sol) {
		output.add(griglia.getMemento());
		
	}

	
	
}