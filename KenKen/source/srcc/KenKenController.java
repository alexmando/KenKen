package srcc;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import srcc.BloccoImpl.Operatore;
import srcc.Griglia.StatoGriglia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.*;

public class KenKenController {

	private final GrigliaView griglia;
	
	//per i listeners
	private boolean staAggiungendo = false;
	private boolean salvato = true;
	private boolean checking = false;
	private BloccoView AggiuntoA = null;
	private Collection<CellaView> possoAggiungere = new LinkedList<>();
	private String NomeFile = "";
	
	public KenKenController(File file) throws Exception{
		griglia = new GrigliaView(Griglia.apriGriglia(file));
		for(CellaView c : griglia.getCellViews())
			c.setMenu(createMenu(c));
	}
	
	public KenKenController(int n) {
		griglia = new GrigliaView(new Griglia(n));
		for(CellaView c : griglia.getCellViews())
			c.setMenu(createMenu(c)); 
	}
	
	 public KenKenController(){
	        griglia = GrigliaView.blankBoard();
	    }
	
	
	public GrigliaView getGriglia() {
		return this.griglia;
	}
	private Collection<CellaView> getAdiacenti(Collection<CellaView> cellViews){
        LinkedList<CellaView> adiacenti = new LinkedList<>();
        for(CellaView cellView: cellViews) {
            int i = cellView.getCell().getRiga(), j = cellView.getCell().getColonna();
            //Controllo la cella sopra.
            Cella toCheck = new Cella(i - 1, j);
            if (this.griglia.getCelleNonAssegnate().contains(toCheck)) {
                adiacenti.add(griglia.getCellView(toCheck));
            }
            //Sinistra
            toCheck = new Cella(i, j - 1);
            if (griglia.getCelleNonAssegnate().contains(toCheck)) 
            	adiacenti.add(griglia.getCellView(toCheck));
            //Sotto
            toCheck = new Cella(i + 1, j);
            if (griglia.getCelleNonAssegnate().contains(toCheck)) adiacenti.add(griglia.getCellView(toCheck));
            //Destra
            toCheck = new Cella(i, j + 1);
            if (griglia.getCelleNonAssegnate().contains(toCheck)) adiacenti.add(griglia.getCellView(toCheck));
        }
        return adiacenti;
    }
	
	 private void updateMenu(){//resetta il menu delle celle in seguito a un'operazione d' inserimento.
	    for(CellaView cellView: griglia.getCellViews()) {
	          //cellView.updateView(false);
	          cellView.setMenu(createMenu(cellView));
	    }
	 }
	private void setAddingMenu(){
	    for(CellaView cellView: possoAggiungere) {
	            JPopupMenu addMenu = new JPopupMenu();
	            JMenuItem addToBlock = new JMenuItem("Add to selected Block");
	            addToBlock.setActionCommand("add");
	            addToBlock.addActionListener(e -> execute(cellView, e.getActionCommand()));
	            addMenu.add(addToBlock);
	            cellView.setMenu(addMenu);
	        }
	    }
	   
	private void execute(CellaView cellView, String actionCommand){
        switch (actionCommand){
            case "add":
                if(!possoAggiungere.contains(cellView)) 
                	throw new IllegalArgumentException("Celle non adiacenti.");
                if(AggiuntoA.getOperation()==Operatore.DIVISIONE || AggiuntoA.getOperation()==Operatore.SOTTRAZIONE){
                    JOptionPane.showMessageDialog(griglia,"Error: Cannot add a third cell in a block with binary operation.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                AggiuntoA.addCell(cellView);
                AggiuntoA.deselectBlock();
                staAggiungendo = false; AggiuntoA=null; possoAggiungere.clear();
                updateMenu();
                salvato=false;
                break;
            case "select":
                if(this.staAggiungendo) execute(null, "deselect");
                this.staAggiungendo=true;
                AggiuntoA=cellView.getBlock();
                AggiuntoA.selectBlock();
                assert possoAggiungere.isEmpty();
                possoAggiungere.addAll(getAdiacenti(AggiuntoA.getCellViews()));
                for(CellaView cw: AggiuntoA.getCellViews()) cw.setMenu(createMenu(cw));
                setAddingMenu();
                break;
            case "deselect":
                AggiuntoA.deselectBlock();
                this.staAggiungendo=false;
                AggiuntoA=null;
                possoAggiungere.clear();
                updateMenu();
                break;
            case "set-result":
                int result;
                try{
                    result = Integer.parseInt(JOptionPane.showInputDialog(griglia, "Fornisci il risultato che deve essere ottenuto dal blocco corrente.", "Imposta risultato blocco" , JOptionPane.PLAIN_MESSAGE));
                    cellView.getBlock().setNumeroOperando(result);
                    salvato=false;
                }catch (NumberFormatException nfe){
                    JOptionPane.showMessageDialog(griglia, "Il valore fornito non può essere interpretato come un numero intero.","Error",JOptionPane.ERROR_MESSAGE);
                }catch (IllegalArgumentException illegalArgumentException){
                    JOptionPane.showMessageDialog(griglia, "il valore fornito deve essere maggiore di zero.","Error",JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "set-operation-add":
                cellView.getBlock().setOperation(Operatore.ADDIZIONE); salvato=false; break;
            case "set-operation-sub":
                cellView.getBlock().setOperation(Operatore.SOTTRAZIONE); salvato=false; break;
            case "set-operation-mul":
                cellView.getBlock().setOperation(Operatore.MOLTIPLICAZIONE); salvato=false; break;
            case "set-operation-div":
                cellView.getBlock().setOperation(Operatore.DIVISIONE); salvato=false; break;
            case "new-block":
                griglia.creaBlocco().addCell(cellView);
                cellView.setMenu(createMenu(cellView));
                salvato=false;
                break;
            case "remove-block":
                if(cellView.getBlock().isSelected()) execute(null, "deselect");
                griglia.rimuoviBlocco(cellView.getBlock());
                updateMenu();
                salvato=false;
                break;
            default: System.out.println("Comando non interpretato: "+actionCommand);
        }
	}
        
        
    private JPopupMenu createMenu(CellaView cellView){

    ActionListener actionListener = e -> execute(cellView,e.getActionCommand());

            JPopupMenu menu = new JPopupMenu("Action to perform:");
            if(griglia.getStato()== StatoGriglia.COSTRUZIONE){
                if(cellView.hasBlock()){
                    if(this.staAggiungendo && AggiuntoA.contains(cellView.getCell())) {
                        JMenuItem deselectBlock = new JMenuItem("Deselect Block");
                        deselectBlock.setActionCommand("deselect");
                        deselectBlock.addActionListener(actionListener);
                        menu.add(deselectBlock);
                    }else {
                        if (!(cellView.getBlock().isFull() || griglia.getCelleNonAssegnate().isEmpty())) {
                            JMenuItem addToBlock = new JMenuItem("Select Block");
                            addToBlock.setActionCommand("select");
                            addToBlock.addActionListener(actionListener);
                            menu.add(addToBlock);
                        }
                    }
                    JMenuItem setResult = new JMenuItem("Set Result");
                    setResult.setActionCommand("set-result");
                    setResult.addActionListener(actionListener);
                    menu.add(setResult);
                    if(cellView.getBlock().getCurrentSize()>1) {
                        JMenu setOperation = new JMenu("Set Operation");
                        JMenuItem add = new JMenuItem("+");
                        add.setActionCommand("set-operation-add");
                        add.addActionListener(actionListener);
                        setOperation.add(add);

                        JMenuItem mul = new JMenuItem("x");
                        mul.setActionCommand("set-operation-mul");
                        mul.addActionListener(actionListener);
                        setOperation.add(mul);

                        if(cellView.getBlock().getCurrentSize()==2) {
                            JMenuItem sub = new JMenuItem("-");
                            sub.setActionCommand("set-operation-sub");
                            sub.addActionListener(actionListener);
                            setOperation.add(sub);

                            JMenuItem div = new JMenuItem("/");
                            div.setActionCommand("set-operation-div");
                            div.addActionListener(actionListener);
                            setOperation.add(div);
                        }

                        menu.add(setOperation);
                    }
                    JMenuItem remove = new JMenuItem("Remove Block");
                    remove.setActionCommand("remove-block");
                    remove.addActionListener(actionListener);
                    remove.setForeground(Utilità.WARNING_COLOR);
                    menu.add(remove);

                }else {
                    JMenuItem newBlock = new JMenuItem("Create new Block");
                    newBlock.setActionCommand("new-block");
                    newBlock.addActionListener(actionListener);
                    menu.add(newBlock);

                }
            }else if(griglia.getStato()==StatoGriglia.GIOCO){
                for(int i=1; i<=griglia.getN(); i++){
                    JMenuItem menuItem = new JMenuItem(""+i);
                    menuItem.setActionCommand(""+i);
                    menuItem.addActionListener(
                            e -> {
                                try{
                                    int chosen = Integer.parseInt(e.getActionCommand());
                                    if(chosen<1 || chosen> griglia.getN()) throw new RuntimeException("Numero scelto non valido.");
                                    int previousValue = cellView.getValore();
                                    cellView.setValue(chosen);
                                    if(checking) check(cellView, previousValue);
                                    salvato=false;
                                }catch(NumberFormatException numberFormatException){
                                    System.out.println("Errore nella selezione del numero.");
                                    numberFormatException.printStackTrace();
                                }
                            }
                    );
                    menu.add(menuItem);
                }
            }
            return menu;    
    }

    
    public boolean startGame(){
        if(griglia.startGame()){
            LinkedList<Memento> solutions = new LinkedList<>();
            new GrigliaView(griglia.getTemplate()).getSolver(1,solutions).risolvi();
            if(solutions.isEmpty()) {
                int input = JOptionPane.showConfirmDialog(griglia,"nessuna soluzione è stata trovata per questo template","Warning!",JOptionPane.YES_NO_OPTION);
                if(input!=JOptionPane.YES_OPTION){
                    griglia.edit();
                    return true;
                }
            }
            for(CellaView cellView: griglia.getCellViews()) cellView.setMenu(createMenu(cellView));
            return true;
        }
        return false;
    }
    
    public void editBoard(){
        griglia.edit();
        for(CellaView cellView: griglia.getCellViews()) cellView.setMenu(createMenu(cellView));
    }
    public void clearBoard(){
        griglia.resettaGriglia();
        for(CellaView cellView: griglia.getCellViews()) cellView.setMenu(createMenu(cellView));
    }
    public void findSolutions(int n){
        if(griglia.getStato()!= StatoGriglia.GIOCO) {
            JOptionPane.showMessageDialog(griglia, "la griglia non è stata ancora completata", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame solutionsFrame = new JFrame("Solutions");
        JTextField navigation = new JTextField("0/0",8);
        navigation.setForeground(Utilità.DEFAULT_COLOR);
        navigation.setEditable(false);
        navigation.setVisible(true);
        SolutionsController solutionsController = new SolutionsController(new GrigliaView(griglia.getTemplate()), n);
        if(solutionsController.getNumeroTotaleSoluzioni()>0) navigation.setText("1/"+solutionsController.getNumeroTotaleSoluzioni());

        solutionsFrame.add(solutionsController.getPanel(), BorderLayout.CENTER);
        JButton previous = new JButton("Previous"), next = new JButton("Next");
        previous.setEnabled(false); next.setEnabled(solutionsController.hasNextSolutions());
        ActionListener actionListener = e -> {
            if(e.getSource()==previous){
                solutionsController.previousSolution();
                previous.setEnabled(solutionsController.hasPreviousSolutions());
                next.setEnabled(solutionsController.hasNextSolutions());
                navigation.setText(solutionsController.getCurrentSolutionNumber()+"/"+solutionsController.getNumeroTotaleSoluzioni());
            }else if(e.getSource()==next){
                solutionsController.nextSolution();
                next.setEnabled(solutionsController.hasNextSolutions());
                previous.setEnabled(solutionsController.hasPreviousSolutions());
                navigation.setText(solutionsController.getCurrentSolutionNumber()+"/"+solutionsController.getNumeroTotaleSoluzioni());
            }else{
                System.out.println("Unknown event source: "+e.getSource());
            }
        };
        next.addActionListener(actionListener); previous.addActionListener(actionListener);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(previous); bottomPanel.add(next);
        bottomPanel.setVisible(true);
        bottomPanel.add(navigation);
        solutionsFrame.add(bottomPanel,BorderLayout.SOUTH);

        solutionsFrame.setIconImage(Utilità.APP_LOGO);
        solutionsFrame.setSize(Utilità.WIDTH,Utilità.HEIGHT);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        solutionsFrame.setLocation(dim.width/2-solutionsFrame.getSize().width/2, dim.height/2-solutionsFrame.getSize().height/2);

        griglia.setVisible(false);

        solutionsFrame.setVisible(true);
        solutionsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                griglia.setVisible(true);
            }
        });

    }
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }
    
    
    public static File getFileAfterSaving(File file){
        String extension = getFileExtension(file);
        if(extension.equals(".json")) return file;
        String path = file.getAbsolutePath();
        path = path+".json";
        file = new File(path);
        return file;
    }
    public void save(File file){
        String json = griglia.toJSON();
        file = getFileAfterSaving(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(json);
            writer.flush();
            writer.close();
            //System.out.println("File salvato");
            salvato = true;
        }catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Qualcosa è andato storto, riprova","Error",JOptionPane.ERROR_MESSAGE);
        }

    }
    public void save(){
        if(NomeFile.equals("")) 
        	throw new RuntimeException("No file name was specified.");
        this.save(new File(NomeFile));
    }
    public String toJSON(){
        return griglia.toJSON();
    }
    public boolean isSaved(){
        return salvato;
    }
    public StatoGriglia getState(){
        StatoGriglia state;
        try{
            state = griglia.getStato();
        }catch(NullPointerException e){
            state = null;
        }
        return state;
    }
    
    public boolean isChecking() { return checking; }
    
    
    public void setChecking(boolean value) {
        checking=value;
        if(checking){
            for(BloccoView blockView: griglia.getBlocks()){
                checkBlock(blockView);
            }
            for(int i=0;i< griglia.getN();i++){
                List<CellaView> riga = griglia.getRiga(i);
                LinkedList<CellaView>[] bucket = new LinkedList[griglia.getN()];
                for(CellaView cellView: riga){
                    int val = cellView.getValore();
                    if(val<=0) continue;
                    if (bucket[val-1] == null) {
                        LinkedList<CellaView> linkedList = new LinkedList<>();
                        linkedList.add(cellView);
                        bucket[val-1] = linkedList;
                    }
                    else{
                        bucket[val-1].add(cellView);
                    }
                }
                for(LinkedList<CellaView> list: bucket){
                    if(list !=null && list.size()>1){
                        for(CellaView cellView: list){
                            cellView.setState(StatoCella.NOT_VALID_REPEATED_VALUE);
                        }
                    }
                }
            }
            for(int j=0;j< griglia.getN();j++){
                List<CellaView> col = griglia.getColonna(j);
                LinkedList<CellaView>[] bucket = new LinkedList[griglia.getN()];
                for(CellaView cellView: col){
                    int val = cellView.getValore();
                    if(val<=0) continue;
                    if (bucket[val-1] == null) {
                        LinkedList<CellaView> linkedList = new LinkedList<>();
                        linkedList.add(cellView);
                        bucket[val-1]= linkedList;
                    }
                    else{
                        bucket[val-1].add(cellView);
                    }
                }
                for(LinkedList<CellaView> list: bucket){
                    if(list!=null && list.size()>1){
                        for(CellaView cellView: list){
                            cellView.setState(StatoCella.NOT_VALID_REPEATED_VALUE);
                        }
                    }
                }
            }
            return;
        }
        for(CellaView cellView: griglia.getCellViews()) cellView.setState(StatoCella.UNKOWN);
    }
    private void checkBlock(BloccoView blockView){
        if(blockView.isValid())
            for(CellaView cw: blockView.getCellViews()) {
                if(cw.getState() != StatoCella.NOT_VALID_REPEATED_VALUE)
                    cw.setState(StatoCella.VALID);
            }
        else {
            boolean hasValues = true;
            for(CellaView cw: blockView.getCellViews()) if(cw.getValore()<=0) hasValues=false;
            if(hasValues) for(CellaView cw: blockView.getCellViews()) cw.setState(StatoCella.NOT_VALID_BLOCK);
        }
    }

    private void check(CellaView cellView, int previousValue){
        cellView.setState(StatoCella.UNKOWN);
        List<CellaView> toCheck = griglia.getRiga(cellView.getRiga());
        LinkedList<CellaView>[] bucket = new LinkedList[griglia.getN()];
        for(CellaView cw : toCheck){
            int val = cw.getValore();
            if(val<=0) continue;
            if (bucket[val-1] == null) {
                LinkedList<CellaView> linkedList = new LinkedList<>();
                linkedList.add(cw);
                bucket[val-1] = linkedList;
            }
            else{
                bucket[val-1].add(cw);
            }
        }
        //Aggiorno eventuali celle rimaste evidenziate a causa di precedenti errori:
        if(previousValue>0 && bucket[previousValue-1]!=null && bucket[previousValue-1].size()==1) {
            CellaView current = bucket[previousValue-1].getFirst();
            if(current.getBlock().isValid()) current.setState(StatoCella.VALID);
            else {
               if(current.getState()!=StatoCella.NOT_VALID_BLOCK) current.setState(StatoCella.UNKOWN);
            }
        }
        for(LinkedList<CellaView> list: bucket){
            if(list != null && list.size()>1){
                for(CellaView cw: list){
                    cw.setState(StatoCella.NOT_VALID_REPEATED_VALUE);
                }
            }
        }
        bucket = new LinkedList[griglia.getN()];
        toCheck = griglia.getColonna(cellView.getColonna());
        for(CellaView cw : toCheck){
            int val = cw.getValore();
            if(val<=0) continue;
            if (bucket[val-1] == null) {
                LinkedList<CellaView> linkedList = new LinkedList<>();
                linkedList.add(cw);
                bucket[val-1]= linkedList;
            }
            else{
                bucket[val-1].add(cw);
            }
        }
        //Aggiorno eventuali celle rimaste evidenziate a causa di precedenti errori:
        if(previousValue>0 && bucket[previousValue-1]!=null && bucket[previousValue-1].size()==1) {
            CellaView current = bucket[previousValue-1].getFirst();
            if(current.getBlock().isValid()) current.setState(StatoCella.VALID);
            else {
                if(current.getState()!=StatoCella.NOT_VALID_BLOCK) current.setState(StatoCella.UNKOWN);
            }
        }
        for(LinkedList<CellaView> list: bucket){
            if(list != null && list.size()>1){
                for(CellaView cw: list){
                    cw.setState(StatoCella.NOT_VALID_REPEATED_VALUE);
                }
            }
        }
        checkBlock(cellView.getBlock());
    }

    public void setNewBoard(int n){
        if(n<3 || n> Utilità.MAX_BOARD_SIZE) 
        	throw new IllegalArgumentException("Dimensione nuova board non valida.");
        griglia.changeBoard(new Griglia(n));
        for(CellaView cellView : griglia.getCellViews()) 
        	cellView.setMenu(createMenu(cellView));
        possoAggiungere = new LinkedList<>();
        AggiuntoA = null;
        staAggiungendo = false;

        for(CellaView cw : griglia.getCellViews()) 
        	cw.setMenu(createMenu(cw));
    }
    public void openBoard(File jsonFile) throws Exception{
        griglia.changeBoard(Griglia.apriGriglia(jsonFile));
        possoAggiungere = new LinkedList<>();
        AggiuntoA = null;
        staAggiungendo= false;

        for(CellaView cw : griglia.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public String getFileName(){
        return NomeFile;
    }
    public void setFileName(String name){
        NomeFile=name;
    }
    public boolean hasFileName(){
        return !NomeFile.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KenKenController that = (KenKenController) o;
        return griglia.equals(that.griglia);
    }

}


    
    
    
    
    
    
    
    
    
    
    
	

	
	
	
	
	

	
	
	
	
	

