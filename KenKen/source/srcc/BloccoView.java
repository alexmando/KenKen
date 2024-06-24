package srcc;
import java.util.*;

import srcc.BloccoImpl.Operatore;
import srcc.Griglia.StatoGriglia;
public class BloccoView {

	private Blocco blocco;
	
	private boolean selected = false;
	
	private final List<CellaView> CelleView;
	
	private CellaView displayCella = null;
	
	public BloccoView(Griglia g) {
		this.blocco = new BloccoImpl();
		this.blocco = g.attaccaBlocco(blocco);
		this.CelleView = new LinkedList<>();
	}
	
	public BloccoView(Griglia g, GrigliaView gView, Blocco b){
        if(!(g.getBlocchi().contains(b))) 
        	throw new IllegalArgumentException("Block must be attached to the board.");
        this.blocco = b;
        CelleView = new LinkedList<>();
        for(Cella cell: b){
            CelleView.add(gView.getCellView(cell));
            gView.getCellView(cell).addBlocco(this);
        }
        boolean reset = gView.getStato() == StatoGriglia.GIOCO;
        gView.edit();
        this.setOperation(b.getOperatore());
        this.setNumeroOperando(b.getNumeroOperando());
        if(reset) gView.startGame();
        for(CellaView cw : CelleView){
            cw.aggiornaVista(false);
        }
        updateDisplayCell(b.haVincoli());
    }
	

	private void updateDisplayCell(boolean forceUpdate){
        if(displayCella!=null) {
            displayCella.removeConstraints();
        }
        int min_i = Utilità.MAX_BOARD_SIZE +1, min_j = Utilità.MAX_BOARD_SIZE +1;
        CellaView candidata = null;
        for(CellaView cw : CelleView){
            if(cw.getCell().getRiga()<min_i){
                min_i = cw.getCell().getRiga(); candidata = cw;
            }
        }
        for(CellaView cw : CelleView){
            if(cw.getCell().getRiga()==min_i && cw.getCell().getColonna()<min_j){
                candidata = cw;
                min_j = cw.getCell().getColonna();
            }
        }
        if(forceUpdate /*|| result > 0 || operation != null*/){
            assert candidata != null;
            candidata.addConstraints();
        }
        this.displayCella=candidata;
    }
    public int getResult(){
        return blocco.getNumeroOperando();
    }
    public Operatore getOperation(){
        return blocco.getOperatore();
    }

    public void addCell(CellaView cellView){
        this.blocco.add(cellView.getCell());
        cellView.addBlocco(this);
        CelleView.add(cellView);
        for(CellaView cw : CelleView){
            cw.aggiornaVista(false);
        }
        updateDisplayCell(displayCella!=null && displayCella.hasConstraints());
    }
    public Collection<CellaView> getCellViews(){
        return Collections.unmodifiableList(CelleView);
    }
    public void setNumeroOperando(int vincolo){
        this.blocco.setNumeroOperando(vincolo);
        //Il vincolo viene mostrato nella cella avente min(i) e poi min(j) del blocco.
        updateDisplayCell(true);
        displayCella.addConstraints();
    }

    public void setOperation(Operatore operation){
        this.blocco.setOperatore(operation);;
        updateDisplayCell(true);
        displayCella.addConstraints();
    }
    public void removeCell(CellaView cellView){
        cellView.removeBlocco();
        CelleView.remove(cellView);
        for(CellaView cw : CelleView){
            cw.aggiornaVista(selected);
        }
    }
    public int getCurrentSize(){
        return blocco.size();
    }
    public boolean isValid(){
        return blocco.bloccoValido();
    }
    public void delete(Griglia g){
        g.remove(this.blocco);
        displayCella.removeConstraints();
        for(CellaView cw: CelleView){
            cw.removeBlocco();
        }
        CelleView.clear();
    }
    public void selectBlock(){
        selected = true;
        for( CellaView cellView : CelleView ){
            cellView.aggiornaVista(selected);
        }
    }
    public void deselectBlock(){
        selected = false;
        for(CellaView cellView: CelleView) cellView.aggiornaVista(selected);
    }
    public boolean isSelected(){
        return selected;
    }
    public boolean isFull(){
        return blocco.èPieno();
    }
    public boolean contains(Cella cell){
        return this.blocco.contains(cell);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder(100);
        for(CellaView cw : CelleView) sb.append(cw.getCell().getRiga()).append("\n");
        return sb.toString();
    }
	
	
}
