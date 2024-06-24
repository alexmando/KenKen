package srcc;

import javax.swing.JPanel;

public class SolutionView {

	private final GrigliaView gView;
	
    public SolutionView(GrigliaView template){
        this.gView = template;
    }
    public JPanel getView(){
        return gView;
    }
    public void displaySolution(Memento memento){
        gView.setMemento(memento);
    }
}
