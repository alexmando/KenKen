package srcc;

import java.io.IOException;
import java.util.*;

import srcc.BloccoImpl.Operatore;
import java.io.*;

public class Griglia implements Originator{

	public enum StatoGriglia{ //pattern singleton
		GIOCO, COSTRUZIONE; 
	}
	private StatoGriglia stato;
	private final int size;
	private Cella[][] celle;
	private List<Blocco> blocchi;
	private Set<Cella> celleNonAssegnate = new HashSet<>(); //insieme delle celle che non sono state ancora inserite ad un blocco
	private boolean tutteCelleAssegnate = false; // se restituisce true tutte le celle della griglia appartengono ad un blocco, restituisce true se celleNonAssegnate.size()==0.
	
	
	public Griglia(int size) {
		if(size < 3 || size >6)
			throw new IllegalArgumentException();
		this.celle = new Cella[size][size];
		this.size=size;
		this.blocchi=new LinkedList<Blocco>();
		this.celleNonAssegnate = new HashSet<Cella>();
		this.stato = StatoGriglia.COSTRUZIONE;
	}
	
	public Griglia(Griglia g, boolean copyValues) {
		 blocchi = new LinkedList<>();
	     this.celleNonAssegnate = new HashSet<>();
	     size = g.size;
	     celle = new Cella[size][size];
	     this.stato = StatoGriglia.COSTRUZIONE;
	        for (int i = 0; i < size; i++)
	            for (int j = 0; j < size; j++) {
	                celle[i][j] = new Cella(i, j);
	                this.celleNonAssegnate.add(celle[i][j]);
	                if(copyValues) 
	                	celle[i][j].setValore(g.celle[i][j].getValore());
	            }
	     for(Blocco ab : g.blocchi){
	          Blocco block = new BloccoImpl();
	          blocchi.add(block);
	          for(Cella c: ab) {
	              block.add(celle[c.getRiga()][c.getColonna()]);
	          }
	          block.setOperatore(ab.getOperatore()); block.setNumeroOperando(ab.getNumeroOperando());
	     }
	     this.tutteCelleAssegnate =this.getCelleNonAssegnate().isEmpty();
	     this.stato=g.stato;
	}
	
	public boolean remove(Blocco b) {
		if(blocchi.size()==0 || this.stato == StatoGriglia.GIOCO)
			return false; //se si sta già giocando non si puo rimuovere un blocco
		blocchi.remove(b);
		return true;
	}

	
	
	private static class GrigliaMemento implements Memento{ //pattern memento
		private int[][] valori;
		
		public GrigliaMemento(Griglia g) {
			valori = new int[g.size][g.size];
			for(int i=0; i< valori.length; i++)
				for(int j=0; j<valori[0].length; j++)
					valori[i][j] = g.celle[i][j].getValore();
		}
		
		
	}
	@Override
	public Memento getMemento() {
		if( stato != StatoGriglia.GIOCO)
			throw new IllegalStateException("per poter memorizzare un'istantanea della griglia essa deve essere già costruita, non in fase di costruzione!");
		return new GrigliaMemento(this);
	}

	@Override
	public void setMemento(Memento m) {
		if(!(m instanceof GrigliaMemento))
			throw new IllegalArgumentException("istantanea deve essere valida");
		GrigliaMemento g = (GrigliaMemento) m;
		int[][] valori = g.valori;
		for(int i=0; i<size; i++)
			for(int j=0; i<size; j++)
				this.celle[i][j].setValore(valori[i][j]);
		
	}

	public StatoGriglia getStato() {
		return stato;
	}

	public void setStato(StatoGriglia stato) {
		this.stato = stato;
	}

	public int getSize() {
		return size;
	}


	public Cella[][] getCelle() {
		return celle;
	}

	public void setCelle(Cella[][] celle) {
		this.celle = celle;
	}

	public List<Blocco> getBlocchi() {
		return blocchi;
	}

	public void setBlocchi(List<Blocco> blocchi) {
		this.blocchi = blocchi;
	}

	public Set<Cella> getCelleNonAssegnate() {
		return celleNonAssegnate;
	}

	public void setCelleNonAssegnate(Set<Cella> celleNonAssegnate) {
		this.celleNonAssegnate = celleNonAssegnate;
	}

	public boolean isTutteCelleAssegnate() {
		return tutteCelleAssegnate;
	}

	public void setTutteCelleAssegnate(boolean tutteCelleAssegnate) {
		this.tutteCelleAssegnate = tutteCelleAssegnate;
	}
	
	public boolean prontoAGiocare() {
		if(!controllaGriglia())
			return false;
		return true;
	}
	
	public boolean iniziaGioco() {
		if(!prontoAGiocare())
			return false;
		stato = StatoGriglia.GIOCO;
		return true;
	}
	
	public void resettaGriglia() {
		stato = StatoGriglia.COSTRUZIONE;
		for(int i=0; i< size; i++)
			for(int j=0; i<size; j++)
				celle[i][j].setValore(0);
	}
	
	
	public boolean controllaGriglia() {
		if(stato != StatoGriglia.GIOCO)
			return false;
		for(Blocco b : blocchi)
			if(!b.bloccoValido())
				return false;
		for(int i=0; i< size; i++)
			for(int j=0; i<size;j++)
				if(celle[i][j].getValore()<=0)
					return false;
		tutteCelleAssegnate = true;
		return true;
	}
	
	public Cella getCella(Cella c) {
		return celle[c.getRiga()][c.getColonna()];
	}
	
	public Cella getCella(int i, int j) {
		return celle[i][j];
	}
	
	 public void edit(){
	        this.stato = StatoGriglia.COSTRUZIONE;
	    }
	
	public String toJSON(){
        StringBuilder sb = new StringBuilder(200);
        sb.append("{\"size\":").append(size).append(",\"stato\":").append(this.stato == StatoGriglia.COSTRUZIONE ? 0 : 1);
        if(this.stato==StatoGriglia.GIOCO){
            sb.append(",\"valori\":[");
            for(int i=0; i<size;i++)
                for(int j=0; j<size; j++){
                    sb.append(celle[i][j].getValore()).append(",");
                }
            sb.delete(sb.length()-1,sb.length());
            sb.append("]");
        }
        sb.append(",\"blocks\":[");
        Iterator<Blocco> iterator = blocchi.iterator();
        while(iterator.hasNext()){
            Blocco block = iterator.next();
            sb.append("{\"size\":").append(block.size()).append(",").append("\"result\":").append(block.getNumeroOperando()).append(",\"operation\":\"").append(block.getOperatore()).append("\",").append("\"cells\":[");
            Iterator<Cella> it = block.iterator();
            while(it.hasNext()){
                Cella cell = it.next();
                sb.append("{\"i\":").append(cell.getRiga()).append(",\"j\":").append(cell.getColonna()).append("}");
                if(it.hasNext()) sb.append(",");
            }
            sb.append("]}");
            if(iterator.hasNext()) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
	
	public Blocco attaccaBlocco(Blocco b) { // se restituisce il blocco passato, l'aggiunta sarà andata a buon fine
		if(!prontoAGiocare() || stato != StatoGriglia.GIOCO)
			throw new IllegalStateException();
		blocchi.add(b);
		for(Cella c : b.getCelleDelBlocco())
			if(celleNonAssegnate.contains(c))
				celleNonAssegnate.remove(c);
		tutteCelleAssegnate = celleNonAssegnate.isEmpty();
		return b;
	}
	
	
	public boolean equals(Object o) {
		if(o==this)
			return true;
		if(!(o instanceof Griglia))
			return false;
		Griglia g = (Griglia) o;
		if(this.stato != g.stato && this.prontoAGiocare() != g.prontoAGiocare() && this.size != g.size)
			return false;
		for(int i=0; i<size; i++)
			for(int j=0; j<size; j++)
				if(this.celle[i][j].getValore() != g.celle[i][j].getValore())
					return false;
		return true;
	}
	
	
	
	public static Griglia apriGriglia (String json) throws IOException{
        StringTokenizer stringTokenizer = new StringTokenizer(json,":\"\t\n{}[], ");
        String currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("size")) 
        	throw new IOException("Format not valid. Found "+currentToken+" instead of N");
        currentToken = stringTokenizer.nextToken().strip();
        Griglia opened = new Griglia(Integer.parseInt(currentToken));
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("stato")) 
        	throw new IOException("Format not valid.");
        currentToken = stringTokenizer.nextToken().strip();
        int state = Integer.parseInt(currentToken);
        if(state==0) {
            opened.stato=StatoGriglia.COSTRUZIONE;
        }
        else if(state==1) {
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("valori")) 
            	throw new IOException("Format not valid.");
            for(int i=0; i<opened.size;i++){
                for(int j=0; j<opened.size;j++){
                    currentToken = stringTokenizer.nextToken().strip();
                    opened.celle[i][j].setValore(Integer.parseInt(currentToken));
                }
            }
        }
        else 
        	throw new IOException("Unknown Board state.");
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("blocks")) 
        	throw new IOException("Format not valid.");

        while(stringTokenizer.hasMoreTokens()){
            //Un'iterazione equivale a leggere un blocco.
            Blocco current = new BloccoImpl();
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("size")) 
            	throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int size = Integer.parseInt(currentToken);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("result")) 
            	throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int result =Integer.parseInt(currentToken);
            if(result>0)
            	current.setNumeroOperando(result);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("operation")) 
            	throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip().toUpperCase();
            switch (currentToken){
                case "ADDIZIONE": current.setOperatore(Operatore.ADDIZIONE); break;
                case "SOTTRAZIONE": current.setOperatore(Operatore.SOTTRAZIONE); break;
                case "MOLTIPLICAZIONE": current.setOperatore(Operatore.MOLTIPLICAZIONE); break;
                case "DIVISIONE": current.setOperatore(Operatore.DIVISIONE); break;
                case "NULL": break;
                default: throw new IOException("Operation "+currentToken+" not valid");
            }
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("cells")) throw new IOException("Format not valid.");
            for(int a=0;a<size;a++){
                int i,j;
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("i")) throw new IOException("Format not valid.");
                currentToken = stringTokenizer.nextToken().strip();
                i=Integer.parseInt(currentToken);
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("j")) throw new IOException("Format not valid.");
                currentToken = stringTokenizer.nextToken().strip();
                j=Integer.parseInt(currentToken);
                current.add(opened.getCella(i,j));
            }
            opened.attaccaBlocco(current);
            //currentToken = stringTokenizer.nextToken().strip();//size se c'è un altro blocco, notInBlock altrimenti
        }
        if(state==1) opened.stato=StatoGriglia.GIOCO;
        return opened;
    }

	public static Griglia apriGriglia(File jsonFile) throws IOException{
        if(!jsonFile.exists()) throw new FileNotFoundException();
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder text = new StringBuilder(200);
        String line;
        while((line=reader.readLine()) != null){
            text.append(line);
        }
        reader.close();
        return apriGriglia(text.toString());

    }
	
	
	
	
	
	
}
