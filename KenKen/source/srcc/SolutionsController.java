package srcc;
import java.util.*;


import javax.swing.JPanel;

public class SolutionsController {

	private final SolutionView view;
	private final ArrayList<Memento> solutions; 
	private int current = -1;
	
	public SolutionsController(GrigliaView g, int n) {
		g.resettaGriglia();
		view = new SolutionView(g);
		solutions = new ArrayList<>();
		RisolutoreKenKen risolutore = g.getSolver(n, solutions);
		risolutore.risolvi();
		if(solutions.isEmpty())
			System.out.println("non sono state trovate soluzioni");
		System.out.println("trovate" + solutions.size() + " soluzioni");
		nextSolution();
	}
	
	public boolean hasNextSolutions() {
		return current< solutions.size()-1;	
	}
	
	public boolean hasPreviousSolutions() {
		return current > 0;
	}
	
	public void nextSolution() {
		if(!hasNextSolutions())
			throw new NoSuchElementException();
		current++;
		view.displaySolution(solutions.get(current));
	}
	
	 public void previousSolution(){
	        if(!hasPreviousSolutions()) throw new NoSuchElementException();
	        current--;
	        view.displaySolution(solutions.get(current));
	    }
	
	 public int getCurrentSolutionNumber() { return current+1; }
	
	public JPanel getPanel() {
		return view.getView();
	}
	
	public int getNumeroTotaleSoluzioni() {
		return solutions.size();
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
