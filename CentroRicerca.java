
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe CentroRicerca: classe per la ricerca sincronizzata di dati attraverso 
 *						 il metodo remoto MeteoDati   
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */ 
/*
 * import dei package necessari
 */
import java.util.*;
import java.rmi.*;
/**
 * Questa classe definisce il metodo di classe sincronizzato per la ricerca di dati.
 * <br> I dati vengono fatti restituire dal il metodo remoto {@link meteo.server.MeteoDati#getMeteoDati(GregorianCalendar,
 * GregorianCalendar, String)} dell'interfaccia {@link meteo.server.MeteoDati}.
 * <br>Il riferimento all'oggetto remoto viene ritornato dal metodo 
 * <code>java.rmi.Naming.lookup(url)</code>.
 */
/*
 * CLASSE CentroRicerca
 */
class CentroRicerca{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Costante con l'indirizzo del server remoto.
	 *  <br>Esempi 
	 *  <pre>
	 *  "rmi://123.123.123.123/" 
	 *  "rmi://localhost/" 
	 *  </pre>
	 */
	private final static String url = "rmi://localhost/";
	/**
	 * Metodo sincronizzato che restituisce una matrice di oggetti {@link ResultMeteo} in base ai criteri di
	 * ricerca impostati.
	 * @param dataInizio	Data iniziale dell'intervallo temporale.
	 * @param dataFine		Data finale dell'intervallo temporale.
	 * @param tipo			Tipo di dati richiesti ("temperatura", "umidita", "pressione" o
	 * 						"precipitazioni").
	 * @param stazione		Stazione in cui cercare i dati.
	 * @return Matrice di oggetti {@link ResultMeteo}.
	 */
	protected static synchronized ResultMeteo[] cercaDati(GregorianCalendar dataInizio,
									   			GregorianCalendar dataFine,
									 			String tipo, String stazione){
		try{
			/*
			 * viene creato un oggetto MeteoDati attraverso il metodo 
			 * java.rmi.Naming.lookup(url) che fa ritornare un riferimento all'oggetto
			 * remoto.
			 */
			MeteoDati meteodatiRem = (MeteoDati) Naming.lookup(url + stazione);
			/*
			 * invocazione del metodo remoto getMeteoDati dell'interfaccia MeteoDati
			 */
			return meteodatiRem.getMeteoDati(dataInizio, dataFine, tipo);
		}catch (Exception e){
			try{
				Nuova nuova = (Nuova) Naming.lookup(url + "Nuova");
				String msg = new String();
				if (nuova.crea(stazione)){
					msg = "La stazione non era ancora stata aggiornata dal server. Ora è" 
							+ " stata aggiornata. Riprovare.";
				}else{
					msg = "Si è verificato un errore: la stazione " + stazione 
							+ " non è presente nel database.";
				}
				ResultMeteo[] errore = new ResultMeteo[1];
				errore[0] = new ResultMeteo("", msg, 0);
				return errore;
						 	
			}catch (Exception err){	
				/*
				 * l'eccezione viene gestita facendo tornare un array con un solo
				 * oggetto ResultMeteo contenente il messaggio di errore
				 */
				ResultMeteo[] errore = new ResultMeteo[1];
				errore[0] = new ResultMeteo("", "Si è verificato un errore: " + err.getMessage(), 0);
				return errore;
			}	
		}
	}		
}
