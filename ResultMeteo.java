
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe ResultMeteo: classe per la definizione dell'oggetto contentente data, 
 *					   ora e valore della rilevazione
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.io.*;
/**
 * Definisce l'oggetto contenente data, ora e valore della rilevazione per la
 * restituzione dei risultati attraverso il metodo remoto {@link meteo.server.MeteoDati#getMeteoDati(
 * GregorianCalendar, GregorianCalendar, String) getMeteoDati}.
 * <br>La classe implementa l'interfaccia <code>Serializable</code> per permettere all'oggetto
 * di essere trasmesso in remoto.	
 */
/*
 * CLASSE ResultMeteo
 * IMPLEMENTA l'interfaccia java.io.Serializable per poter essere 
 * 		      serializzato e passato in remoto
 */
class ResultMeteo implements Serializable{ 
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Data della rilevazione */
	private String data;
	/** Ora della rilevazione */
	private String ora;
	/** Valore della rilevazione */
	private int valore;
	/**
	 * Crea un nuovo oggetto inizializzando le variabili in base ai parametri.
	 * @param parData Data della rilevazione.
	 * @param parOra Ora della rilevazione.
	 * @param parValore Valore della rilevazione.
	 */
	protected ResultMeteo(String parData,
						String parOra,
						int parValore){
		data = parData;
		ora = parOra;
		valore = parValore;
	}
	/*
	 * metodi per la restituzione dei valori contenuti
	 */
	/**
	 * Ritorna la data della rilevazione.
	 * @return Data della rilevazione
	 */ 
	protected String getData(){
		return data;
	}
	/**
	 * Ritorna l'ora della rilevazione.
	 * @return Ora della rilevazione
	 */ 
	protected String getOra(){
		return ora;
	}
	/**
	 * Ritorna il valore rilevato
	 * @return Valore rilevato
	 */ 
	protected int getValore(){
		return valore;
	}
}