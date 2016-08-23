
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe ResultMeteoTabella: classe per la definizione dell'oggetto contentente data, 
 *					          valori e ore delle rilevazioni per le diverse operazioni
 *							  richieste (media, max, min) per il successivo inserimento
 * 							  degli stessi valori nella tabella visualizzata dalla servlet
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/**
 * Classe per la definizione dell'oggetto contentente data, ora e valori delle 
 * rilevazioni per le diverse operazioni richieste (<i>Media</i>, <i>Massima</i>, <i>Minima</i>).
 * <br>L'oggetto viene utilizzato per la creazione della tabella restituita dal metodo 
 * {@link ElaborazioneThread#RispostaHTML() RispostaHTML()} dell'oggetto {@link ElaborazioneThread}.
 * 
 */
/*
 * CLASSE ResultMeteoTabella
 */
class ResultMeteoTabella{ 
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Data della rilevazione */
	private String data;
	/** Valore medio rilevato */
	private String media;
	/** Valore massimo rilevato */
	private String massima;
	/** Valore minimo rilevato */
	private String minima;
	/** Ora della rilevazione del valore massimo*/
	private String oraMassima;
	/** Ora della rilevazione del valore minimo*/
	private String oraMinima;
	/**
	 * Inizializza le variabili con un valore <code>null</code>.
	 */
	protected ResultMeteoTabella(){
		data = new String();
		media = new String();
		massima = new String();
		minima = new String();
		oraMassima = new String();
		oraMinima = new String();
	}
	/**
	 * Imposta la variabile {@link #data}.
	 */
	protected void setData(String parData){
		data = parData;
	}
	/**
	 * Imposta la variabile {@link #media}.
	 */
	protected void setMedia(String parMedia){
		media = parMedia;
	}
	/**
	 * Imposta la variabile {@link #massima}.
	 */
	protected void setMassima(String parMax){
		massima = parMax;
	}
	/**
	 * Imposta la variabile {@link #minima}.
	 */
	protected void setMinima(String parMin){
		minima = parMin;
	}
	/**
	 * Imposta la variabile {@link #oraMassima}.
	 */
	protected void setOraMassima(String parOraMax){
		oraMassima = parOraMax;
	}
	/**
	 * Imposta la variabile {@link #oraMinima}.
	 */
	protected void setOraMinima(String parOraMin){
		oraMinima = parOraMin;
	}
		
	/*
	 * metodi per la restituzione dei valori contenuti
	 */
	/**
	 * Ritorna la data della rilevazione
	 * @return Data della rilevazione
	 */ 
	protected String getData(){
		return data;
	}
	/**
	 * Ritorna il valore medio rilevato
	 * @return Valore medio rilevato
	 */ 
	protected String getMedia(){
		return media;
	}
	/**
	 * Ritorna il valore massimo rilevato
	 * @return Valore massimo rilevato
	 */ 
	protected String getMassima(){
		return massima;
	}
	/**
	 * Ritorna il valore minimo rilevato
	 * @return Valore minimo rilevato
	 */ 
	protected String getMinima(){
		return minima;
	}
	/**
	 * Ritorna l'ora della rilevazione del valore massimo
	 * @return Ora della rilevazione del valore massimo
	 */ 
	protected String getOraMassima(){
		return oraMassima;
	}
	/**
	 * Ritorna l'ora della rilevazione del valore minimo
	 * @return Ora della rilevazione del valore minimo
	 */ 
	protected String getOraMinima(){
		return oraMinima;
	}

}				