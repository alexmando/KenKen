package srcc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.JPanel;

import srcc.Griglia.StatoGriglia;

public class GrigliaView extends JPanel implements Originator{

	private Griglia griglia;
	
	private List<BloccoView> blocchiView;
	
	private CellaView[][] CelleView;
	
	
	@Override
	public Memento getMemento() {
		return griglia.getMemento();
	}

	@Override
	public void setMemento(Memento m) {
		griglia.setMemento(m);
		for(int i=0; i<griglia.getSize();i++)
			for(int j=0; j<griglia.getSize();j++)
				CelleView[i][j].updateText();	
	}

	 public GrigliaView(int n){
	        super(new GridLayout(n, n));
	        if(n>Utilità.MAX_BOARD_SIZE) 
	        	throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
	        this.griglia= new Griglia(n);
	        blocchiView = new LinkedList<>();
	        CelleView =new CellaView[n][n];
	        for(int i=0;i< griglia.getSize();i++){
	            for(int j=0;j<griglia.getSize();j++){
	                CellaView current = new CellaView(griglia.getCella(i,j));
	                CelleView[i][j]=current;
	                this.add(current.getView());
	            }
	        }
	    }
	 
	 public GrigliaView(Griglia g){
	        super(new GridLayout(g.getSize(),g.getSize()));
	        if(g.getSize()> Utilità.MAX_BOARD_SIZE) 
	        	throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
	        this.griglia=g;
	        blocchiView = new LinkedList<>();
	        CelleView = new CellaView[g.getSize()][g.getSize()];
	        for(int i=0;i<g.getSize();i++){
	            for(int j=0;j<g.getSize();j++){
	                CellaView current = new CellaView(g.getCella(i,j));
	                CelleView[i][j]=current;
	                this.add(current.getView());
	            }
	        }
	        for(Blocco b: g.getBlocchi()){
	            BloccoView blockView = new BloccoView(this.griglia,this, b);
	            blocchiView.add(blockView);
	        }
	    }
	 
	 private GrigliaView(){
	        super(new BorderLayout());
	    }
	
	public CellaView getCellView(int i, int j){
	        return CelleView[i][j];
	    }
	public CellaView getCellView(Cella cell){
	        return CelleView[cell.getRiga()][cell.getColonna()];
	    }
	public BloccoView creaBlocco(){
	        BloccoView created = new BloccoView(griglia);
	        blocchiView.add(created);
	        return created;
	    }
	public void rimuoviBlocco(BloccoView block){
	        blocchiView.remove(block);
	        block.delete(griglia);
	    }
	
	public int getN() {
		return griglia.getSize();
	}
	public CellaView[][] getCelleViews() {
		return CelleView;
	}
	
	public Collection<CellaView> getCellViews(){
        LinkedList<CellaView> cellViews1 = new LinkedList<>();
        for(int i=0;i<CelleView.length;i++){
            for(int j=0;j<CelleView[0].length;j++)
                cellViews1.add(CelleView[i][j]);
        }
        return Collections.unmodifiableList(cellViews1);
    }
	
	public Collection<Cella> getCelleNonAssegnate(){
	     return griglia.getCelleNonAssegnate();
	 }
	
	public List<CellaView> getRiga(int i){
	     List<CellaView> row = new ArrayList<>(griglia.getSize());
	     for(int j=0; j<griglia.getSize();j++) 
	    	 row.add(CelleView[i][j]);
	     return Collections.unmodifiableList(row);
	 }
	public List<CellaView> getColonna(int j){
	     List<CellaView> col = new ArrayList<>(griglia.getSize());
	     for(int i=0; i<griglia.getSize();i++) 
	    	 col.add(CelleView[i][j]);
	     return Collections.unmodifiableList(col);
	 }
    public Collection<BloccoView> getBlocks(){
	     return Collections.unmodifiableCollection(blocchiView);
	 }
	public StatoGriglia getStato(){
	     return griglia.getStato();
	 }
	public String toJSON(){
	     return griglia.toJSON();
	 }
	public boolean startGame(){
	     return griglia.iniziaGioco();
	 }
	
	public void edit(){
	     griglia.edit();
	 }
	
	public void resettaGriglia(){
	     griglia.resettaGriglia();
	     for(int i=0; i<griglia.getSize(); i++)
	         for(int j=0; j<griglia.getSize(); j++){
	             CelleView[i][j].rimuoviValore();
	            }
	    }
	
	
    public RisolutoreKenKen getSolver(int n, Collection<Memento> out){
	     Griglia copy = new Griglia(griglia,false);
	     return new RisolutoreKenKen(copy,n,out);
	}
	
	
    public Griglia getTemplate(){
        if(!griglia.prontoAGiocare()) 
        	throw new IllegalStateException("non hai ancora finito di definire il template");
        return new Griglia(griglia,false);
    }
	
	
	
    public void changeBoard( Griglia board ){
        this.removeAll();
        setLayout(new GridLayout(board.getSize(),board.getSize()));
        this.griglia = board;
        blocchiView = new LinkedList<>();
        CelleView =new CellaView[board.getSize()][board.getSize()];
        for(int i=0;i<board.getSize();i++){
            for(int j=0;j<board.getSize();j++){
                CellaView current = new CellaView(board.getCella(i,j));
                CelleView[i][j]=current;
                this.add(current.getView());
            }
        }
        for(Blocco block: griglia.getBlocchi()){
            blocchiView.add(new BloccoView(board, this, block));
        }
        this.updateUI();
    }
	
    public static GrigliaView blankBoard(){
        return new GrigliaView();
    }
	
}
