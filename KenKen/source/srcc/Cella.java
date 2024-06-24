package srcc;

import java.util.Objects;

public final class Cella {

	private final int riga;
	private final int colonna;
	private int valore;
	
	public Cella(int riga, int colonna) {
		this.riga=riga;
		this.colonna=colonna;
	}




	public int getRiga() {
		return riga;
	}



	public int getColonna() {
		return colonna;
	}


	public int getValore() {
		return this.valore;
	}

	public void setValore(int valore) {
		this.valore = valore;
	}
	
	 @Override
	    public int hashCode() {
	        return Objects.hash(riga, colonna);
	    }
	
	public boolean equals(Object o) {
		if(o==this)
			return true;
		if(!(o instanceof Cella))
			return false;
		Cella c = (Cella)o;
		return this.riga==c.riga && this.colonna==c.colonna && this.valore==c.valore;
	}
}

