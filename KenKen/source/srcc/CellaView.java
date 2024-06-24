package srcc;

import javax.swing.*;

import srcc.BloccoImpl.Operatore;

import java.awt.*;



public class CellaView {

	private final Cella cella;
	
	private final JPanel panel;
	
	private final JTextField textField;
	
	private JTextField vincolo;
	
	private BloccoView blocco;
	
	private int sopra, sotto, sinistra, destra;
	
	private Operatore operatore= null;
	
	private Integer result=null;
	
	private boolean haVincoli = false;
	
	private StatoCella stato = StatoCella.UNKOWN;
	
	public CellaView(Cella c) {
		if(c == null)
			throw new IllegalArgumentException("la cella passata non può essere null");
		this.blocco = null;
		this.cella = c;
		this.panel = new JPanel(new BorderLayout());
		textField = new JTextField("",1);
        textField.setFont(Utilità.FONT);
        if(c.getValore()>0) 
        	textField.setText( "" + c.getValore());
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.getCaret().setVisible(false);
        //Inizializzo la grafica supponendo che, inizialmente, non appartenga ad alcun blocco.
        sopra = Utilità.DEFAULT_BORDER_SIZE; sinistra = Utilità.DEFAULT_BORDER_SIZE; sotto = Utilità.DEFAULT_BORDER_SIZE; destra = Utilità.DEFAULT_BORDER_SIZE;
        panel.setBorder(BorderFactory.createMatteBorder(sopra,sinistra,sotto,destra,Color.BLACK));
        panel.add(textField,BorderLayout.CENTER);
	}
	
	
	
	public Component getView() {
		return panel;
	}
	
	public void addBlocco(BloccoView b) {
		if(this.blocco !=null)
			throw new IllegalStateException("cella già inserita in un blocco");
		this.blocco = b;
	}
	
    public void removeBlocco(){
	    this.blocco=null;
	    aggiornaVista(false);
	}
	public void rimuoviValore(){
	    this.cella.setValore(0);
	    textField.setText("");
	}
	public int getValore(){
	    return cella.getValore();
    }
    public int getRiga() {
	    return cella.getRiga();
	}
	public int getColonna(){
	    return cella.getColonna();
	}
	
	public void aggiornaVista(boolean isSelected) {
		  if(blocco==null) {
	            sopra = Utilità.DEFAULT_BORDER_SIZE;
	            sinistra = Utilità.DEFAULT_BORDER_SIZE;
	            sotto = Utilità.DEFAULT_BORDER_SIZE;
	            destra = Utilità.DEFAULT_BORDER_SIZE;
	            panel.setBorder(BorderFactory.createMatteBorder(sopra,sinistra,sotto,destra,Color.BLACK));
	            return;
	      }
		  int i= this.cella.getRiga();
		  int j= this.cella.getColonna();
		  //controllo quali celle adiacenti fanno parte del blocco
		  //sopra
		  Cella toCheck = new Cella(i-1,j);
	      if(blocco.contains(toCheck)) 
	    	  sopra = Utilità.DEFAULT_BORDER_SIZE;
	      else 
	    	  sopra = Utilità.BLOCK_BORDER_SIZE;
	      //a sinistra
	      toCheck = new Cella(i,j-1);
	      if(blocco.contains(toCheck)) 
	          sinistra = Utilità.DEFAULT_BORDER_SIZE;
	      else
	          sinistra = Utilità.BLOCK_BORDER_SIZE;
	        //sotto
	      toCheck = new Cella(i+1,j);
	      if(blocco.contains(toCheck)) 
	    	  sotto = Utilità.DEFAULT_BORDER_SIZE;
	      else 
	    	  sotto = Utilità.BLOCK_BORDER_SIZE;
	      //a destra
	      toCheck = new Cella(i,j+1);
	      if(blocco.contains(toCheck)) 
	    	  destra = Utilità.DEFAULT_BORDER_SIZE;
	      else 
	    	  destra = Utilità.BLOCK_BORDER_SIZE;
	      Color color = isSelected? Color.CYAN : Color.BLACK;
	      panel.setBorder(BorderFactory.createMatteBorder(sopra,sinistra,sotto,destra,color));
	    }
		  
      
	
	public void setValue(int value){
        //Assumo input valido
        this.cella.setValore(value);
        this.textField.setText(""+value);
    }
    public void updateText(){
        if(cella.getValore()>0) this.textField.setText(""+cella.getValore());
    }
    public Cella getCell(){
        return cella;
    }
    public void setMenu(JPopupMenu menu){
        this.textField.setComponentPopupMenu(menu);
    }
    public boolean hasBlock(){
        return blocco!=null;
    }
    public BloccoView getBlock() { return blocco; }
    public boolean hasConstraints(){ return haVincoli; }
		
    
    public void addConstraints(){
        this.result=blocco.getResult(); this.operatore=blocco.getOperation();
        if(!haVincoli){
            haVincoli = true;
            vincolo = new JTextField();
            vincolo.setEditable(false);
            this.panel.add(vincolo, BorderLayout.NORTH);
            vincolo.setVisible(true);
            this.panel.updateUI();
        }
        StringBuilder toDisplay = new StringBuilder(5);
        if(result>0) 
        	toDisplay.append(result);
        if(operatore!=null) {
            switch (operatore) {
                case ADDIZIONE:
                    toDisplay.append("+");
                    break;
                case SOTTRAZIONE:
                    toDisplay.append("-");
                    break;
                case MOLTIPLICAZIONE:
                    toDisplay.append("x");
                    break;
                case DIVISIONE:
                    toDisplay.append("/");
                    break;
            }
        }
        vincolo.setText(toDisplay.toString());
    }
	
    
    public void setState(StatoCella state){
        if(this.stato == StatoCella.NOT_VALID_BLOCK && state == StatoCella.NOT_VALID_REPEATED_VALUE || this.stato==state) return;
        this.stato=state;
        switch (state){
            case VALID: this.textField.setForeground(Utilità.VALID_COLOR); break;
            case NOT_VALID_REPEATED_VALUE:
            case NOT_VALID_BLOCK: this.textField.setForeground(Utilità.WARNING_COLOR); break;
            case UNKOWN: this.textField.setForeground(Utilità.DEFAULT_COLOR); break;
            default: System.out.println("Stato sconosciuto: "+state); this.stato=StatoCella.UNKOWN; break;
        }
    }
    public StatoCella getState() {
    	return stato; 
    }
    
    
    public void removeConstraints(){
        if(!haVincoli) 
        	return;
        haVincoli = false;
        this.operatore = null;
        this.result = 0;
        this.panel.remove(vincolo);
        vincolo = null;
    }
    

    
    
}
	

