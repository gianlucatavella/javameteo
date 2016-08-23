
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe AnniImpl: implementazione della funzione remota Anni
 *
 * Compilato anche con "rmic -v1.2 AnniImpl" per la generazione dello _Stub
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.sql.*;
import java.text.*;
/**
 * Questa classe definisce l'implementazione del metodo {@link #getAnniDisponibili(String)}
 * definito dall'interfaccia implementata {@link Anni}.
 * <br>Estende la classe <code>java.rmi.server.UnicastRemoteObject</code> per definire 
 * un oggetto remoto non replicabile i cui riferimenti sono validi solamente quando il
 * processo del server è attivo. La classe <code>UnicastRemoteObject</code> fornisce inoltre
 * il sostegno necessario per i riferimenti (invocazioni, parametri e risultati) agli oggetti
 * remoti attivi utilizzando il protocollo TCP.
 * <br>Questa classe deve essere compilata anche con l'<code>RMI Compiler</code> per la
 * generazione dello <i>_Stub</i>.
 * <br>In questo caso l'istruzione da digitare in riga di comando è
 * <pre>
 * rmic -v1.2 AnniImpl
 * </pre>
 * In questo modo viene generato il file
 * <pre>
 * AnniImpl_Stub.class
 * </pre>
 * Questo file, insieme al file dell'interfaccia implementata {@link Anni}
 * <pre>
 * Anni.class
 * </pre>
 * devono essere copiati nella cartella dove sono situate le classi del client, in questo
 * caso nella cartella con le classi della Servlet.
 */
/*
 * CLASSE AnniImpl
 * ESTENDE la classe java.rmi.server.UnicastRemoteObject per definire oggetti remoti
 * 		   con riferimenti validi solo mentre il processo server è attivo
 * IMPLEMENTA l'interfaccia Anni per l'implementazione del relativo metodo
 */
class AnniImpl extends UnicastRemoteObject implements Anni{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Matrice con il primo e ultimo anno disponibile */
	private int[] elem;
	/** 
	 * Crea un nuovo oggetto.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */	
	public AnniImpl() throws RemoteException{} 
	/**
	 * Si connette al database e restituisce una matrice di 2 elementi con il primo
	 * e l'ultimo anno in cui sono disponibili dati.
	 * <br>Visualizza anche un messaggio nella console visualizzando l'indirizzo
	 * IP del client chiamante.
	 * @param Stazione Stazione a cui si riferisce l'intervallo temporale
	 * @return Primo e ultimo anno in cui sono disponibili dati.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */
	public int[] getAnniDisponibili(String Stazione) throws RemoteException{
        elem = new int[2];
		GregorianCalendar dataOra = new GregorianCalendar();
		try{
			System.out.println("  " + dataOra.getTime().toString() +
								 " Richiesta anni disponibili su " + Stazione + " da " + getClientHost()); 
		}catch (ServerNotActiveException e){
			System.out.println("  " + dataOra.getTime().toString() + 
								" Richiesta anni disponibili da client sconosciuto/n" +
								"Metodo chiamato al di fuori del servizio di " +
								"Remote Method Invocation. Errore: " +
								e.getMessage());
		}						 
				
		try{
			/*
			 * registrazione del driver jdbc (bridge jdbc-odbc)
			 */
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");  //registra driver
			/*
			 * creazione della connessione al database "meteo"
			 */
			Connection connessione = DriverManager.getConnection("jdbc:odbc:meteo");
			/*
			 * creazione del comando (query) e definizione delle proprietà del
			 * risultante ResultSet
			 */
			Statement comando = connessione.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
															ResultSet.CONCUR_READ_ONLY);
			/*
			 * creazione del ResultSet contenente i risultati della query che seleziona
			 * le stazioni presenti nel database ordinate per nome
			 */
			ResultSet risultatoQuery = comando.executeQuery("SELECT * FROM Stazioni ORDER BY Stazione");
			//risultatoQuery.first();
			/*
			 * scorrimento del ResultSet per valutarne la lunghezza in righe
			 */
			int i = 0;
			while (risultatoQuery.next()) {
				i++;
			}
						/*
			 * se il ResultSet è vuoto viene fatta ritornare una matrice con 2 valori
			 * arbitrari e si interrompe l'esecuzione del metodo, altrimenti 
			 * l'esecuzione del metodo prosegue
			 */
			if (i == 0){
				elem[0] = -10000;
				elem[1] = -10000;
				return elem;				
			}
				
			SimpleDateFormat formatData = new SimpleDateFormat("yyyy");
			int Max = Integer.MIN_VALUE;
			int Min = Integer.MAX_VALUE;
			int maxTemp;
			int minTemp; 
			ResultSet risultatoQueryAnniMax;
			ResultSet risultatoQueryAnniMin;
			try{
				risultatoQueryAnniMax = comando.executeQuery(
						"SELECT MAX(Data) FROM " + Stazione + ";");
				risultatoQueryAnniMax.next();		
				maxTemp = Integer.parseInt(formatData.format(risultatoQueryAnniMax.getDate(1)));	
				if (Max < maxTemp){
					Max = maxTemp;
				}	
				risultatoQueryAnniMin = comando.executeQuery(
						"SELECT MIN(Data) FROM " + Stazione + ";");
				risultatoQueryAnniMin.next();		
				minTemp = Integer.parseInt(formatData.format(risultatoQueryAnniMin.getDate(1)));	
				if (Min > minTemp){
					Min = minTemp;
				}
			}catch (Exception e){
				elem[0] = -10000;
				elem[1] = -10000;
				return elem;				
			}	
			/*
			 * restituzione dell'array
			 */
			elem[0] = Min;
			elem[1] = Max;
			return elem;
		/*
		 * gestione esplicita delle eccezioni
		 * ritorno, di fronte ad un'eventuale eccezione, di una 
		 * matrice con 2 elementi
		 * il messaggio di errore è comunque visualizzato dalla stringa
		 * restutuita dalla funzione StazioniEsistenti invocata prima
		 */	
		}catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			elem[0] = -10000;
			elem[1] = -10000;
			return elem;				
		}catch (ClassNotFoundException e){
			System.out.println("ClassNotFoundException: " + e.getMessage());
			elem[0] = -10000;
			elem[1] = -10000;
			return elem;				
		}catch (Exception e){
			System.out.println("Exception: " + e.getMessage());
			elem[0] = -10000;
			elem[1] = -10000;
			return elem;				
		}	
	}
}			 
		