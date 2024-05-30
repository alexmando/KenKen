package src;

public class Cella {

	private int riga;
	private int colonna;
	private int valore;
	
	public Cella(int riga, int colonna) {
		this.riga=riga;
		this.colonna=colonna;
		this.valore=0;
	}

	public int getRiga() {
		return riga;
	}

	public void setRiga(int riga) {
		this.riga = riga;
	}

	public int getColonna() {
		return colonna;
	}

	public void setColonna(int colonna) {
		this.colonna = colonna;
	}

	public int getValore() {
		return valore;
	}

	public void setValore(int valore) {
		this.valore = valore;
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
