package it.beans;

public class Appelli {
	
	private String date;
	private int idAppello;
	
	public Appelli(int id, String date) {
		this.idAppello = id;
		this.date = date;
	}
	
	public void setidAppello(int idAppello) {
		this.idAppello = idAppello;
	}
	
	public int getidAppello() {
		return this.idAppello;
	}
	
	public void setdate(String date) {
		this.date = date;
	}
	
	public String getdate() {
		return this.date;
	}

}
