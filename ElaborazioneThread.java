
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe ElaborazioneThread: classe per l'elaborazione dei dati e la restituzione
 * 					    	  degli stessi in formato Html
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*								
 * import dei package necessari	
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
/**
 * Elabora i dati e restituisce una pagina Web.
 * <br>Questa classe definisce l'oggetto per l'elaborazione e la visualizzazione dei dati restituiti dal
 * metodo statico sincronizzato {@link CentroRicerca#cercaDati(GregorianCalendar, GregorianCalendar,
 * String, String) cercaDati} dell'oggetto {@link CentroRicerca} passato per riferimento al costruttore di
 * questa classe.<br>
 * I dati vengono elaborati dai metodi {@link #Media()}, {@link #Massima()} e {@link #Minima()}.
 * <br>La restituzione degli stessi è affidata al metodo {@link #RispostaHTML()}.<br>
 * La scelta di utilizzare i thread per la restituzione dei dati deriva sia dalla necessità di
 * accedere al metodo remoto in modo sincronizzato realizzando quindi la coordinazione che 
 * dall'opportunità di realizzare la concorrenza tra le diverse richieste dei client.
 *
 */
/*
 * CLASSE ElaborazioneThread
 * ESTENDE la classe java.lang.Thread per realizzare la concorrenza e la coordinazione
 * 		   delle diverse richieste di dati da parte dei client	
 */
class ElaborazioneThread extends Thread{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Data iniziale dell'intervallo temporale */
	private GregorianCalendar dataInizio;
	/** Data finale dell'intervallo temporale */
	private GregorianCalendar dataFine;
	/** Tipo di dati richiesti ("temperatura", "umidita", "pressione" o "precipitazioni") */
	private String tipo;
	/** Stazione metereologica da cui selezionare i dati */
	private String stazione;
	/** Selezione dell'operazione media (<code>true</code> se selezionata) */
	private boolean media;
	/** Selezione dell'operazione massima (<code>true</code> se selezionata) */
	private boolean massima;
	/** Selezione dell'operazione minima (<code>true</code> se selezionata) */
	private boolean minima;
	/** Valori medi, massimi e minimi del periodo */
	private ResultMeteo[] risMMMPeriodo;
	/** Oggetto con il metodo sincronizzato per la ricerca di dati */
	private CentroRicerca centroricerca;
	/** Risultati della ricerca */
	private ResultMeteo[] risultatiRemoti;
	/** Rappresentazione dei risultati elaborati per la successiva visualizzazione nella tabella della pagina HTML restituita*/
	private ResultMeteoTabella[] risultati;
	/** Oggetto per la restituzione della pagina in formato HTML */
	private PrintWriter out;
	/** Stringa per l'intestazione della tabella contenuta nella pagina Web con i risultati */
	private String strMediaValore = "Media";
	/** Selezione della variabile precipitazioni (<code>true</code> se selezionata) */
	private boolean precipitazioni = false;
	/** Rappresentazione in stringa della data iniziale */
	private	String dataIniziale;
	/** Rappresentazione in stringa della data finale */
	private	String dataFinale;
	/** Errore nella selezione dei campi del modulo creato con la servlet {@link form} */
	private boolean errore;

	/**
	 * Crea un nuovo thread per la ricerca e l'elaborazione e la visualizzazione dei dati.
	 * 
	 * @param parErrore 	Verifica di un'eventuale errore nella compilazione del modulo di ricerca,
	 *                  	<code>true</code> se vi è stato un errore.
	 * @param parCentroRicerca 	Oggetto con il metodo di classe sincronizzato per la ricerca
	 *                         	dei dati attraverso il metodo remoto 
	 *							{@link meteo.server.MeteoDati#getMeteoDati(GregorianCalendar, GregorianCalendar,
	 *							String) getMeteoDati}.
	 * @param parDataInizio		Data iniziale dell'intervallo temporale.
	 * @param parDataFine		Data finale dell'intervallo temporale, nel caso coincida con la
	 *							data iniziale come intervallo temporale viene considerato solo un giorno.
	 * @param parTipo	Tipo di dati richiesti ("temperatura", "umidita", "pressione" o "precipitazioni").
	 * @param parStazione	Stazione metereologica di rilevamento richiesta nella quale cercare i dati.
	 * @param parMedia	Verifica se è stata selezionata l'operazione <i>Media</i>, <code>true</code>
	 *					se è stata selezionata.
	 * @param parMassima	Verifica se è stata selezionata l'operazione <i>Massima</i>, <code>true</code>
	 *						se è stata selezionata.
	 * @param parMinima		Verifica se è stata selezionata l'operazione <i>Minima</i>, <code>true</code>
	 *						se è stata selezionata.
	 * @param pardataIniziale	Data iniziale dell'intervallo in formato stringa.
	 * @param pardataFinale		Data finale dell'intervallo in formato stringa.
	 * @param parRisMMMPeriodo	Contenente la descrizione di un'eventuale errore di compilazione del modulo di ricerca
	 *							oppure vuota se non vi sono stati errori.
	 * @param parRisultati		Contenente la descrizione di un'eventuale errore di compilazione del modulo di ricerca
	 *							oppure vuota se non vi sono stati errori.
	 * @param parOut			Oggetto per la restituizione dei risultati della query in formato HTML attraverso una
	 *							pagina Web.
	 * 
	 */
	public ElaborazioneThread(boolean parErrore,
							  CentroRicerca parCentroRicerca,
							  GregorianCalendar parDataInizio,
							  GregorianCalendar parDataFine, 
							  String parTipo, String parStazione, boolean parMedia,
							  boolean parMassima, boolean parMinima,
							  String pardataIniziale, String pardataFinale,
							  ResultMeteo[] parRisMMMPeriodo,
							  ResultMeteoTabella[] parRisultati,
							  PrintWriter parOut){
		/*
		 * Inizializzazione degli oggetti e delle variabili di classe in base ai
		 * parametri.
		 */
		centroricerca = parCentroRicerca;
		dataInizio = parDataInizio;
		dataFine = parDataFine;
		tipo = parTipo;
		stazione = parStazione;
		media = parMedia;
		massima = parMassima;
		minima = parMinima;
		dataIniziale = pardataIniziale;
		dataFinale = pardataFinale;
		errore = parErrore;
		risMMMPeriodo = parRisMMMPeriodo;
		risultati = parRisultati;
		out = parOut;
	}
	/**
	 * Esegue la ricerca popolando la matrice {@link #risultatiRemoti} con i risultati
	 * provenienti dal metodo di classe sincronizzato {@link CentroRicerca#cercaDati(GregorianCalendar,
	 * GregorianCalendar, String, String) cercaDati} definito nella classe {@link CentroRicerca}.
	 */
	public void run(){
		/*
		 * verifica della presenza di un errore di compilazione del modulo di ricerca
		 */
		if (!errore){
			/*
			 * Creazione dell'array invocando il metodo cercaDati dell'oggetto 
			 * centroricerca passato per riferimento all'oggetto ElaborazioneThread corrente
			 */
			risultatiRemoti = centroricerca.cercaDati(dataInizio,
												dataFine,
												tipo,
												stazione);
			/*
			 * verifica se vi sono o meno dati corrispondenti ai criteri di ricerca.																						
			 */
			if (risultatiRemoti.length == 0){
				risultati = new ResultMeteoTabella[1];
				risultati[0] = new ResultMeteoTabella();
				risultati[0].setMedia("Nessun dato");
			}else{
				/*
				 * Verifica della presenza di errori
				 */
				if (risultatiRemoti.length == 1 && risultatiRemoti[0].getData().equals("")){ 
					/*
					 * Viene restituita alla servlet di ricerca un solo elemento di 
					 * risultati con il secondo campo uguale all'errore rilevato
					 * oppure uguale a null se la query non ha dato nessun risultato.
					 * Sarà compito della servlet gestire l'errore visulizzando un messaggio.
					 */
					risultati = new ResultMeteoTabella[1];
					risultati[0] = new ResultMeteoTabella();				
					risultati[0].setMedia(risultatiRemoti[0].getOra()); 											 
				}else{
					/*
					 * Nel caso la query sia stata eseguita con successo si procede all'esecuzione
					 * dei metodi di elaborazione 
					 */
					risMMMPeriodo = new ResultMeteo[3];
					/*
					 * Creazione array risultati in base al numero di giorni
					 * calcolato con il metodo NumeroGiorni 
					 */
					 
					risultati = new ResultMeteoTabella[NumeroGiorni()];
					/*
					 * Invocazione del metodo per l'elaborazione
					 */
					Elabora();
					
				}
			}	
		}
		/*
		 * invocazione del metodo per la visualizzazione dei risultati
		 */
		RispostaHTML();
	}
	/**
	 * Calcola il numero dei giorni dell'intervallo temporale di ricerca nei quali 
	 * sono disponibili i risultati.
	 * @return Numero dei giorni
	 */
	private int NumeroGiorni(){
		///////////////////
		//// Variabili ////
		///////////////////
		int nGiorni = 0;
		String dataAttuale = risultatiRemoti[0].getData();
		/*
		 * Scrorrimento dell'array
		 */
		for (int i = 0; i <	risultatiRemoti.length; i++){
			/*
			 * Verifica del cambiamento di data
			 */
			if (!dataAttuale.equalsIgnoreCase(risultatiRemoti[i].getData())){
				/*
				 * Incremento della variabile rappresentante il numero dei giorni.
				 */
				nGiorni++;
				/*
				 * Reinizializzazione della variabile di controllo al cambio di
				 * data.
				 */
				dataAttuale = risultatiRemoti[i].getData();
			}	
		}
		return nGiorni + 1;
	}
	/**
	 * Inserisce le date corrispondenti ai giorni in cui sono disponibili i risultati nella
	 * matrice {@link #risultati} tramite il metodo 
	 * {@link ResultMeteoTabella#setData(String)}.
	 */
	private void InserisciDate(){
		///////////////////
		//// Variabili ////
		///////////////////
		int nGiorni = 1;
		String dataAttuale = risultatiRemoti[0].getData();
		/*
		 * assegnazione della prima data al primo elemento dell'array
		 */
		risultati[0] = new ResultMeteoTabella(); 
		risultati[0].setData(risultatiRemoti[0].getData());
		/*
		 * Scrorrimento dell'array
		 */
		for (int i = 0; i < risultatiRemoti.length; i++){
			/*
			 * Verifica del cambiamento di data
			 */
			if (!dataAttuale.equalsIgnoreCase(risultatiRemoti[i].getData())){
				/*
				 * assegnazione della data all'elemento nGiorni dell'array
				 */
				risultati[nGiorni] = new ResultMeteoTabella(); 
				risultati[nGiorni].setData(risultatiRemoti[i].getData());
				/*
				 * incremento variabile
				 */
				nGiorni++;
				/*
				 * Reinizializzazione della variabile di controllo al cambio di
				 * data.
				 */
				dataAttuale = risultatiRemoti[i].getData();
			}
		}
	}			
	/**
	 * Individua il caso specifico in relazione alle operazioni richieste e invoca 
	 * i metodi relativi alle necessarie elaborazioni.
	 * <br>Si evitano in questo modo calcoli inutili al fine di snellire il già laborioso
	 * processo di elaborazione.
	 * <pre>
	 *
	 * I casi possibili sono 8:
	 * 
	 * M1(x)  Max(x)  Min(x)		(1 = selezionata, 0 = non selezionata)
	 *------------------------
	 *  1       0       0
	 *  0       1       0
	 *  0       0       1					
	 *  1       1       0
	 *  0       1       1
	 *  1       1       0
	 *  1       0       1
	 *  1       1       1
	 *------------------------
	 * 
	 * </pre>
	 * Per ogni caso si distingue ulteriormente se la variabile ricercata è il
	 * valore delle precipitazioni o meno, in quanto per le precipitazioni è
	 * previsto un trattamento diverso dei dati in base al fatto che per il
	 * calcolo della media, della massima e della minima della giornata e del
	 * periodo è prevista un'implementazione diversa proprio per la diversa  
	 * natura dei valori, in particolare il valore giornaliero delle precipitazioni
	 * è composto dalla somma di tutti i valori rilevati durante la giornata poichè
	 * si presuppone che dopo ogni rilevazione ne venga azzerato il valore
	 *
	 */
	private void Elabora(){
	/*
		 * Metodo comune a tutte le operazioni e quindi invocato indipendentemente
		 * dalla casistica.
		 */
		InserisciDate();
		/*
		 * --------------solo media--------------
		 */

		if (media == true && massima == false && minima == false){
			Media();
		}
		/*
		 * --------------solo massima--------------
		 */
		if (media == false && massima == true && minima == false){
			Massima();
		}
		/*
		 * --------------solo minima--------------
		 */
		if (media == false && massima == false && minima == true){
			Minima();
		}
		/*
		 * --------------media e massima--------------
		 */
		if (media == true && massima == true && minima == false){
			Media();
			Massima();
		}
		/*
		 * --------------massima e minima--------------
		 */
		if (media == false && massima == true && minima == true){
			Massima();
			Minima();		
		}
		/*
		 * --------------media e minima--------------
		 */
		if (media == true && massima == false && minima == true){
			Media();
			Minima();
		}
		/*
		 * --------------media, massima e minima--------------
		 */
		if (media == true && massima == true && minima == true){
			Media();
			Massima();
			Minima();
		}
		
	
	}
	/**
	 * Calcola la media giornaliera e del periodo e inserisce i valori trovati nella
	 * matrice {@link #risultati} tramite il metodo {@link ResultMeteoTabella#setMedia(String)}.
	 * <br>Il metodo opera in modi diversi a seconda che sia stato selezionato il tipo di
	 * dati <i>Precipitazioni</i> o meno.
	 * <br>Nel caso particolare delle <i>Precipitazioni</i>, i valori minimi, massimi o medi calcolati sono 
	 * solo quelli del periodo in quanto essendo la variabile <i>Precipitazioni</i> non di natura
	 * continua ed essendo quindi rilevata in modo diverso rispetto alle altre variabili 
	 * (in modo discreto, una "tantum") azzerando il valore dopo ogni rilevazione, non è 
	 * sensato esprimere il valore minimo, medio o massimo giornaliero in quanto questo valore 
	 * risentirebbe della natura discreta del periodo di tempo intercorrente tra una rilevazione
	 * e la successiva. Si è scelto quindi di individuare solamente il valore minimo, massimo o medio 
	 * del periodo in base al valore calcolato in questo stesso metodo rigurdante 
	 * le precipitazioni complessive della giornata.
	 */
	private void Media(){
		/*
		 * Distinzione dei due casi precipitazioni o altro.
		 */
		if (tipo.equalsIgnoreCase("precipitazioni")){
			///////////////////
			//// Variabili ////
			///////////////////
			int valoreGiornaliero = 0;
			int mediaDelPeriodo = 0;

			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					/*
					 * Incremento della variabile per il calcolo della media
					 * giornaliera.
					 */
					valoreGiornaliero += risultatiRemoti[n].getValore();
					/*
					 * Incremento della variabile per il calcolo della media
					 * del periodo.
					 */
					mediaDelPeriodo += risultatiRemoti[n].getValore();

					n++;

				}
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMedia(String.valueOf(valoreGiornaliero));
				/*
				 * Reinizializzazione delle variabili giornaliere al cambio di
				 * data.
				 */
				valoreGiornaliero = 0;
					
			}
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			mediaDelPeriodo = Math.round(mediaDelPeriodo / risultati.length);
			risMMMPeriodo[0] = new ResultMeteo(null, null, mediaDelPeriodo);
		}else{
			///////////////////
			//// Variabili ////
			///////////////////
			int mediaGiornaliera = 0;
			int numeroDatiGiornalieri = 0;
			int mediaDelPeriodo = 0;
			int numeroDatiPeriodo = 0;
			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					/*
					 * Incremento del numero di dati giornalieri.
					 */
					numeroDatiGiornalieri++;
					/*
					 * Incremento del numero di dati del periodo.
					 */
					numeroDatiPeriodo++;
					/*
					 * Incremento della variabile per il calcolo della media
					 * giornaliera.
					 */
					mediaGiornaliera += risultatiRemoti[n].getValore();
					/*
					 * Incremento della variabile per il calcolo della media
					 * del periodo.
					 */
					mediaDelPeriodo += risultatiRemoti[n].getValore();

					n++;

				}
				/*
				 * Calcolo della variabile rappresentante la media giornaliera.
				 */
				mediaGiornaliera = Math.round(mediaGiornaliera / numeroDatiGiornalieri);
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMedia(String.valueOf(mediaGiornaliera));
				/*
				 * Reinizializzazione delle variabili giornaliere al cambio di
				 * data.
				 */
				numeroDatiGiornalieri = 0;
				mediaGiornaliera = 0;
					
			}
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			mediaDelPeriodo = Math.round(mediaDelPeriodo / numeroDatiPeriodo);
			risMMMPeriodo[0] = new ResultMeteo(null, null, mediaDelPeriodo);
		}
	}
	/**
	 * Calcola la massima giornaliera e del periodo e inserisce i valori trovati nella
	 * matrice {@link #risultati} tramite il metodo {@link ResultMeteoTabella#setMassima(String)}.
	 * <br>Il metodo opera in modi diversi a seconda che sia stato selezionato il tipo di
	 * dati <i>Precipitazioni</i> o meno.
	 * <br>Nel caso particolare delle <i>Precipitazioni</i>, i valori minimi, massimi o medi calcolati sono 
	 * solo quelli del periodo in quanto essendo la variabile <i>Precipitazioni</i> non di natura
	 * continua ed essendo quindi rilevata in modo diverso rispetto alle altre variabili 
	 * (in modo discreto, una "tantum") azzerando il valore dopo ogni rilevazione, non è 
	 * sensato esprimere il valore minimo, medio o massimo giornaliero in quanto questo valore 
	 * risentirebbe della natura discreta del periodo di tempo intercorrente tra una rilevazione
	 * e la successiva. Si è scelto quindi di individuare solamente il valore minimo, massimo o medio 
	 * del periodo in base al valore calcolato in questo stesso metodo rigurdante 
	 * le precipitazioni complessive della giornata.
	 */
	private void Massima(){
		/*
		 * Distinzione dei due casi precipitazioni o altro.
		 */
		if (tipo.equalsIgnoreCase("precipitazioni")){
			///////////////////
			//// Variabili ////
			///////////////////
			int valoreGiornaliero = 0;
			int massimaDelPeriodo = Integer.MIN_VALUE;
			String dataMax = new String();
			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					/*
					 * Incremento della variabile per il calcolo della media
					 * giornaliera.
					 */
					valoreGiornaliero += risultatiRemoti[n].getValore();

					n++;

				}
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMedia(String.valueOf(valoreGiornaliero));

				if (massimaDelPeriodo < valoreGiornaliero){
					massimaDelPeriodo = valoreGiornaliero;
					dataMax = risultati[i].getData();
				}	 

				/*
				 * Reinizializzazione delle variabili giornaliere al cambio di
				 * data.
				 */
				valoreGiornaliero = 0;
				
					
			}
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			risMMMPeriodo[1] = new ResultMeteo(dataMax, null, massimaDelPeriodo);
		}else{
			///////////////////
			//// Variabili ////
			///////////////////
			int massimaGiornaliera = Integer.MIN_VALUE;
			int massimaDelPeriodo = Integer.MIN_VALUE;
			String dataMax = new String();
			String oraMax = new String();
			String dataMaxPeriodo = new String();
			String oraMaxPeriodo = new String();
			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					if (massimaGiornaliera < risultatiRemoti[n].getValore()){
						massimaGiornaliera = risultatiRemoti[n].getValore();
						dataMax = risultatiRemoti[n].getData();
						oraMax = risultatiRemoti[n].getOra();
					}	

					n++;

				}
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMassima(String.valueOf(massimaGiornaliera));
				risultati[i].setOraMassima(oraMax);
				
				if (massimaDelPeriodo < massimaGiornaliera){
					massimaDelPeriodo = massimaGiornaliera;
					dataMaxPeriodo = dataMax;
					oraMaxPeriodo = oraMax;
				}
				massimaGiornaliera = Integer.MIN_VALUE;	
			}
				
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			risMMMPeriodo[1] = new ResultMeteo(dataMaxPeriodo,
													 oraMaxPeriodo, massimaDelPeriodo);
		}
	}
	/**
	 * Calcola la minima giornaliera e del periodo e inserisce i valori trovati nella
	 * matrice {@link #risultati} tramite il metodo {@link ResultMeteoTabella#setMinima(String)}.
	 * <br>Il metodo opera in modi diversi a seconda che sia stato selezionato il tipo di
	 * dati <i>Precipitazioni</i> o meno.
	 * <br>Nel caso particolare delle <i>Precipitazioni</i>, i valori minimi, massimi o medi calcolati sono 
	 * solo quelli del periodo in quanto essendo la variabile <i>Precipitazioni</i> non di natura
	 * continua ed essendo quindi rilevata in modo diverso rispetto alle altre variabili 
	 * (in modo discreto, una "tantum") azzerando il valore dopo ogni rilevazione, non è 
	 * sensato esprimere il valore minimo, medio o massimo giornaliero in quanto questo valore 
	 * risentirebbe della natura discreta del periodo di tempo intercorrente tra una rilevazione
	 * e la successiva. Si è scelto quindi di individuare solamente il valore minimo, massimo o medio 
	 * del periodo in base al valore calcolato in questo stesso metodo rigurdante 
	 * le precipitazioni complessive della giornata.
	 */
	private void Minima(){
		/*
		 * Distinzione dei due casi precipitazioni o altro.
		 */
		if (tipo.equalsIgnoreCase("precipitazioni")){
			///////////////////
			//// Variabili ////
			///////////////////
			int valoreGiornaliero = 0;
			int minimaDelPeriodo = Integer.MAX_VALUE;
			String dataMin = new String();
			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					/*
					 * Incremento della variabile per il calcolo della media
					 * giornaliera.
					 */
					valoreGiornaliero += risultatiRemoti[n].getValore();

					n++;

				}
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMedia(String.valueOf(valoreGiornaliero));

				if (minimaDelPeriodo > valoreGiornaliero){
					minimaDelPeriodo = valoreGiornaliero;
					dataMin = risultati[i].getData();
				}	 

				/*
				 * Reinizializzazione delle variabili giornaliere al cambio di
				 * data.
				 */
				valoreGiornaliero = 0;
				
					
			}
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			risMMMPeriodo[2] = new ResultMeteo(dataMin, null, minimaDelPeriodo);
		}else{
			///////////////////
			//// Variabili ////
			///////////////////
			int minimaGiornaliera = Integer.MAX_VALUE;
			int minimaDelPeriodo = Integer.MAX_VALUE;
			String dataMin = new String();
			String oraMin = new String();
			String dataMinPeriodo = new String();
			String oraMinPeriodo = new String();
			/*
			 * Scrorrimento dell'array e popolamento delle matrici
			 */
			int n = 0;
			for (int i = 0; i <	risultati.length; i++){
				
				while (n < risultatiRemoti.length && risultati[i].getData().equalsIgnoreCase(risultatiRemoti[n].getData())){
					if (minimaGiornaliera > risultatiRemoti[n].getValore()){
						minimaGiornaliera = risultatiRemoti[n].getValore();
						dataMin = risultatiRemoti[n].getData();
						oraMin = risultatiRemoti[n].getOra();
					}	

					n++;

				}
				/*
				 * assegnazione del valore giornaliero all'array
				 */
				risultati[i].setMinima(String.valueOf(minimaGiornaliera));
				risultati[i].setOraMinima(oraMin);
				if (minimaDelPeriodo > minimaGiornaliera){
					minimaDelPeriodo = minimaGiornaliera;
					dataMinPeriodo = dataMin;
					oraMinPeriodo = oraMin;
				}
				minimaGiornaliera = Integer.MAX_VALUE;	
			}
				
			/*
			 * assegnazione del valore del periodo al primo elemento dell'array
			 * relativo
			 */
			risMMMPeriodo[2] = new ResultMeteo(dataMinPeriodo,
													 oraMinPeriodo, minimaDelPeriodo);
		}
	}
	/**
	 * Crea la risposta in formato HTML contenente i risultati (o gli eventuali errori 
	 * riscontrati) della query impostata nel modulo di ricerca.
	 * <br>In particolare, vengono visulizzati i parametri di ricerca impostati e i risultati
	 * relativi alle operazioni richieste sotto forma di tabelle HTML.
	 * <br>Il layout scelto si propone di presentare i dati in modo intuitivo
	 * per favorire una successiva rapida consultazione.
	 * 
	 */
	private void RispostaHTML(){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Meteo Seeker - Progetto di Sistemi di Elaborazione dell'Informazione B</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.println("<script language=\"JavaScript\">");
		out.println("<!--");
		out.println("function MM_reloadPage(init) {  //reloads the window if Nav4 resized");
		out.println("  if (init==true) with (navigator) {if ((appName==\"Netscape\")&&(parseInt(appVersion)==4)) {");
		out.println("    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}");
		out.println("  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();");
		out.println("}");
		out.println("MM_reloadPage(true);");
		out.println("// -->");
		out.println("</script>");
		out.println("<link rel=\"stylesheet\" href=\"../../stile.css\" type=\"text/css\">");
		out.println("</head>");
		out.println("");
		out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" background=\"../../images/sfondo.gif\">");
		out.println("<table width=\"623\" border=\"0\" align=\"center\" height=\"887\" bgcolor=\"#CCCCFF\" cellspacing=\"0\" bordercolor=\"#999999\" cellpadding=\"0\">");
		out.println("  <tr bgcolor=\"#0000ff\"> ");
		out.println("    <td height=\"108\" width=\"10\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("    <td height=\"108\" width=\"604\"> ");
		out.println("      <div align=\"center\"><img src=\"../../images/titolo.jpg\" width=\"600\" height=\"120\"></div>");
		out.println("    </td>");
		out.println("    <td height=\"108\" width=\"9\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("  </tr>");
		out.println("  <tr valign=\"bottom\" bgcolor=\"#0000ff\"> ");
		out.println("    <td height=\"35\" width=\"10\" bordercolor=\"#CCCCFF\">&nbsp;</td>");
		out.println("    <td height=\"35\" width=\"604\" bordercolor=\"#CCCCFF\"><img src=\"../../images/risultati.jpg\" width=\"608\" height=\"60\" usemap=\"#Map\" border=\"0\"></td>");
		out.println("    <td height=\"35\" width=\"9\" bordercolor=\"#CCCCFF\">&nbsp;</td>");
		out.println("  </tr>");
		out.println("  <tr bordercolor=\"#FFFFFF\"> ");
		out.println("    <td width=\"10\" bgcolor=\"#0000FF\" height=\"780\">&nbsp;</td>");
		out.println("    <td width=\"604\" valign=\"top\" height=\"780\"> ");
		out.println("      <p>&nbsp;</p>");
		out.println("      <blockquote> ");
		/*
		 * verifica dell'assenza di dati o di un eventuale errore
		 */
		if (risultati.length == 1 && risultati[0].getData().equals("")){
			/*
			 * assenza di dati
			 */
			if (risultati[0].getMedia().equalsIgnoreCase("Nessun dato")){
				out.println("        <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\" size=\"4\">Non sono presenti dati corrispondenti ai criteri di ricerca inseriti. ");
				out.println("          </font></b></font></p>");
			/*
			 * errore
			 */
			}else{
				out.println("        <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\" size=\"4\">Si è verificato un errore: " + risultati[0].getMedia());
				out.println("          </font></b></font></p>");
			}
		}else{
			/*
			 * verifica del tipo di dati richiesti per la successiva esatta visualizzazione
			 * dei risultati in base al tipo stesso
			 */
			String unita = new String();
			if (tipo.equalsIgnoreCase("temperatura")){
				tipo = "Temperatura";
				unita = "&deg;C";
			}else if (tipo.equalsIgnoreCase("umidita")){
				tipo = "Umidità";
				unita = "%";
			}else if (tipo.equalsIgnoreCase("pressione")){
				tipo = "Pressione";
				unita = "milliBar";
			}else if (tipo.equalsIgnoreCase("precipitazioni")){
				tipo = "Precipitazioni";
				unita = "mm";
				precipitazioni = true;
				strMediaValore = "Valore";
			}
			out.println("        <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\" size=\"5\">Risultati ");
			out.println("          della ricerca</font></b></font></p>");
			out.println("        <table width=\"505\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\">");
			out.println("          <tr bgcolor=\"#99CCFF\"> ");
			out.println("            <td width=\"202\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#006633\">Stazione</font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"332\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FF6633\"><font color=\"#FF0000\">" + stazione + "</font></font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr bgcolor=\"#00CCFF\"> ");
			out.println("            <td width=\"202\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#006633\">Variabile</font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"332\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FF6633\"><font color=\"#FF0000\">" + tipo + "</font></font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr bgcolor=\"#99CCFF\"> ");
			out.println("            <td width=\"202\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#006633\">Unit&agrave; di misura</font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"332\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FF6633\"><font color=\"#FF0000\">" + unita + "</font></font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr bgcolor=\"#00CCFF\"> ");
			out.println("            <td width=\"202\" height=\"19\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#006633\">Periodo</font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"332\" height=\"19\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#FF6633\"><font color=\"#FF0000\">" + dataIniziale + "</font> ");
			out.println("                <font color=\"#006633\">-</font> <font color=\"#FF0000\">" + dataFinale + "</font></font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("        </table>");
			out.println("        <table width=\"505\" border=\"0\" cellpadding=\"2\" cellspacing=\"0\" align=\"center\">");
			/*
			 * verifica se è stata selezionata l'operazione Media
			 */
			if (media){
				out.println("          <tr bordercolor=\"#CCCCFF\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"left\"><b></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\" bgcolor=\"#FFFFFF\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#669933\">Valore ");
				out.println("                medio del periodo:</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[0].getValore()) + "</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"61\"> ");
				out.println("              <div align=\"right\"><b></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"67\"> ");
				out.println("              <div align=\"center\"><b></b></div>");
				out.println("            </td>");
				out.println("          </tr>");
			}
			/*
			 * verifica se è stata selezionata l'operazione Massima
			 */
			if (massima){
				out.println("          <tr bordercolor=\"#CCCCFF\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\">&nbsp;</td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\" bgcolor=\"#FFCC66\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#669933\">Valore ");
				out.println("                massimo del periodo:</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[1].getValore()) + "</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\" bgcolor=\"#FFCC66\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">Rilevato ");
				out.println("                in data:</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[1].getData()) + "</font></b></div>");
				out.println("            </td>");
				/*
				 * verifica se il tipo di dati è relativo alle precipitazioni o meno
				 */
				if (!precipitazioni){
					out.println("            <td width=\"61\"> ");
					out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">ora:</font></b></div>");
					out.println("            </td>");
					out.println("            <td width=\"67\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[1].getOra()) + "</font></b></div>");
					out.println("            </td>");
				}else{
					out.println("            <td width=\"61\"> ");
					out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">&nbsp;</font></b></div>");
					out.println("            </td>");
					out.println("            <td width=\"67\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">&nbsp;</font></b></div>");
					out.println("            </td>");
				}	
				out.println("          </tr>");
			}
			/*
			 * verifica se è stata selezionata l'operazione Minima
			 */
			if (minima){
				out.println("          <tr bordercolor=\"#CCCCFF\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\">&nbsp;</td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\" bgcolor=\"#FFFFCC\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#669933\">Valore ");
				out.println("                minimo del periodo:</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"center\"><b></font><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[2].getValore()) + "</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\" bgcolor=\"#FFFFCC\"> ");
				out.println("            <td width=\"248\"> ");
				out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">Rilevato ");
				out.println("                in data:</font></b></div>");
				out.println("            </td>");
				out.println("            <td width=\"108\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[2].getData()) + "</font></b></div>");
				out.println("            </td>");
				/*
				 * verifica se il tipo di dati è relativo alle precipitazioni o meno
				 */
				if (!precipitazioni){
					out.println("            <td width=\"61\"> ");
					out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">ora:</font></b></div>");
					out.println("            </td>");
					out.println("            <td width=\"67\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" +  String.valueOf(risMMMPeriodo[2].getOra()) + "</font></b></div>");
					out.println("            </td>");
				}else{
					out.println("            <td width=\"61\"> ");
					out.println("              <div align=\"right\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#993333\">&nbsp;</font></b></div>");
					out.println("            </td>");
					out.println("            <td width=\"67\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">&nbsp;</font></b></div>");
					out.println("            </td>");
				}	
				out.println("          </tr>");
				out.println("          <tr bordercolor=\"#CCCCFF\"> ");
				out.println("            <td width=\"248\">&nbsp;</td>");
				out.println("            <td width=\"108\">&nbsp;</td>");
				out.println("            <td width=\"61\">&nbsp;</td>");
				out.println("            <td width=\"67\">&nbsp;</td>");
				out.println("          </tr>");
			}
			out.println("        </table>");
			out.println("        <br>");
			out.println("        <table border=\"1\" align=\"center\">");
			out.println("          <tr bgcolor=\"#FF0000\"> ");
			out.println("            <th width=\"127\"> ");
			out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#66CCFF\">Data</font></b></div>");
			out.println("            </th>");
			/*
			 * verifica se il tipo dati è relativo alle precipitazioni oppure l'operazione
			 * selezionata è la Media
			 */
			if (media || precipitazioni){
				out.println("            <th width=\"82\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FFFFFF\">" + strMediaValore + "</font></b></div>");
				out.println("            </th>");
			}
			/*
			 * verifica se il tipo di dati è relativo alle precipitazioni o meno
			 */
			if (!precipitazioni){
				/*
				 * verifica se è stata selezionata l'operazione Massima
				 */
				if (massima){
					out.println("            <th width=\"82\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FFCC66\">Massima</font></b></div>");
					out.println("            </th>");
					out.println("            <th width=\"54\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#FFCC66\">ora</font></b></div>");
					out.println("            </th>");
				}
				/*
				 * verifica se è stata selezionata l'operazione Minima
				 */
				if (minima){
					out.println("            <th width=\"71\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\" color=\"#FFFFCC\">Minima</font></b></div>");
					out.println("            </th>");
					out.println("            <th width=\"49\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#FFFFCC\">ora</font></b></div>");
					out.println("            </th>");
				}
			}
			out.println("          </tr>");
			/*
			 * scorre i risultati creando la tabella
			 */
			for (int i = 0; i < risultati.length; i++){
				
				out.println("          <tr> ");
				out.println("            <td width=\"127\" bgcolor=\"#66CCFF\"> ");
				out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" + risultati[i].getData() + "</font></b></div>");
				out.println("            </td>");
				/*
				 * verifica se il tipo dati è relativo alle precipitazioni oppure l'operazione
				 * selezionata è la Media
				 */
				if (media  || precipitazioni){
					out.println("            <td width=\"82\" bgcolor=\"#FFFFFF\"> ");
					out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\"><font color=\"#336633\" size=\"3\" face=\"Arial, Helvetica, sans-serif\">" + risultati[i].getMedia() + "</font></font></b></div>");
					out.println("            </td>");
				}
				/*
				 * verifica se il tipo di dati è relativo alle precipitazioni o meno
				 */
				if (!precipitazioni){
					/*
					 * verifica se è stata selezionata l'operazione Massima
					 */
					if (massima){
						out.println("            <td width=\"82\" bgcolor=\"#FFCC66\"> ");
						out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\"><font color=\"#336633\" size=\"3\" face=\"Arial, Helvetica, sans-serif\">" + risultati[i].getMassima() + "</font></font></b></div>");
						out.println("            </td>");
						out.println("            <td width=\"54\" bgcolor=\"#FFCC66\"> ");
						out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" + risultati[i].getOraMassima() + "</font></b></div>");
						out.println("            </td>");
					}
					/*
					 * verifica se è stata selezionata l'operazione Minima
					 */
					if (minima){
						out.println("            <td width=\"71\" bgcolor=\"#FFFFCC\"> ");
						out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\"><font color=\"#336633\" size=\"3\" face=\"Arial, Helvetica, sans-serif\">" + risultati[i].getMinima() + "</font></font></b></div>");
						out.println("            </td>");
						out.println("            <td width=\"49\" bgcolor=\"#FFFFCC\"> ");
						out.println("              <div align=\"center\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#0066CC\">" + risultati[i].getOraMinima() + "</font></b></div>");
						out.println("            </td>");
					}
				}
				out.println("          </tr>");
			}
			
			
			out.println("        </table>");
			out.println("        ");
		}
		out.println("        <p>&nbsp;</p>");
		out.println("      </blockquote>");
		out.println("      <center>");
		out.println("      <FORM NAME=\"buttonbar\">");
		out.println("     <INPUT TYPE=\"button\" VALUE=\"Indietro\" onClick=\"history.back()\">");
		out.println("		</FORM>");
		out.println("		<center>");
		out.println("      </center></center></td>");
		out.println("    <td width=\"9\" bgcolor=\"#0000FF\" height=\"780\">&nbsp;</td>");
		out.println("  </tr>");
		out.println("  <tr bordercolor=\"#FFFFFF\">");
		out.println("    <td width=\"10\" bgcolor=\"#0000FF\" height=\"2\">&nbsp;</td>");
		out.println("    <td width=\"604\" valign=\"top\" bgcolor=\"0000ff\" height=\"2\">&nbsp;</td>");
		out.println("    <td width=\"9\" bgcolor=\"#0000FF\" height=\"2\">&nbsp;</td>");
		out.println("  </tr>");
		out.println("</table>");
		out.println("<blockquote>");
		out.println("  <p>&nbsp; </p>");
		out.println("  <p>&nbsp;</p>");
		out.println("</blockquote>");
		out.println("<map name=\"Map\"> ");
		out.println("  <area shape=\"rect\" coords=\"228,19,368,53\" href=\"form?stazione=" + stazione + "\">");
		out.println("  <area shape=\"rect\" coords=\"65,17,187,53\" href=\"../../index.html\">");
		out.println("  <area shape=\"rect\" coords=\"414,13,539,51\" href=\"../../guida.html\">");
		out.println("</map>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	
	}		
	
}