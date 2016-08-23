
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe StazioniEsistentiImpl: implementazione del metodo remoto 
 * 								 StazioniEsistenti
 * Compilato anche con "rmic -v1.2 StazioniEsistentiImpl" per la generazione 
 * dello _Stub
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.util.*;
/**
 * Questa classe definisce l'implementazione del metodo {@link #getStazioniEsistenti()}
 * definito dall'interfaccia implementata {@link StazioniEsistenti}.
 * <br>Estende la classe <code>java.rmi.server.UnicastRemoteObject</code> per definire 
 * un oggetto remoto non replicabile i cui riferimenti sono validi solamente quando il
 * processo del server è attivo. La classe <code>UnicastRemoteObject</code> fornisce inoltre
 * il sostegno necessario per i riferimenti (invocazioni, parametri e risultati) agli oggetti
 * remoti attivi utilizzando il protocollo TCP.
 * <br>Questa classe deve essere compilata anche con l'<code>RMI Compiler</code> per la
 * generazione dello <i>_Stub</i>.
 * <br>In questo caso l'istruzione da digitare in riga di comando è
 * <pre>
 * rmic -v1.2 StazioniEsistentiImpl
 * </pre>
 * In questo modo viene generato il file
 * <pre>
 * StazioniEsisistentiImpl_Stub.class
 * </pre>
 * Questo file, insieme al file dell'interfaccia implementata {@link StazioniEsistenti}
 * <pre>
 * StazioniEsistenti.class
 * </pre>
 * devono essere copiati nella cartella dove sono situate le classi del client, in questo
 * caso nella cartella con le classi della Servlet.
 */
/*
 * CLASSE StazioniEsistentiImpl
 * ESTENDE la classe java.rmi.server.UnicastRemoteObject per definire oggetti remoti
 * 		   con riferimenti validi solo mentre il processo del server è attivo
 * IMPLEMENTA l'interfaccia StazioniEsistenti per l'implementazione del relativo metodo
 */
class StazioniEsistentiImpl extends UnicastRemoteObject implements StazioniEsistenti{
	/** 
	 * Crea un nuovo oggetto.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */	
	public StazioniEsistentiImpl() throws RemoteException{}
	/**
	 * Si connette al database e restituisce una matrice con le stazioni esistenti.
	 * <br>Visualizza anche un messaggio nella console visualizzando l'indirizzo
	 * IP del client chiamante.
	 * <br>Nel caso non vi siano stazioni presenti nella base di dati oppure si verifichi
	 * un'eccezione viene ritornata una matrice di due elementi con il messaggio di errore.
	 * @return Stazioni esistenti nel database.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */
	public String[] getStazioniEsistenti() throws RemoteException{
		GregorianCalendar dataOra = new GregorianCalendar();
		try{
			System.out.println("  " + dataOra.getTime().toString() +
								 " Richiesta stazioni esistenti da " + getClientHost()); 
		}catch (ServerNotActiveException e){
			System.out.println("  " + dataOra.getTime().toString() + 
								" Richiesta stazioni esistenti da client sconosciuto/n" +
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
			/*
			 * scorrimento del ResultSet per valutarne la lunghezza in righe
			 */
			int i = 0;
			while (risultatoQuery.next()) {
				i++;
			}
			/*
			 * se il ResultSet è vuoto viene fatta ritornare una matrice con una sola
			 * stringa vuota e si interrompe l'esecuzione del metodo, altrimenti 
			 * l'esecuzione del metodo prosegue
			 */
			if (i == 0){
				String[] elemsub = new String[2];
				elemsub[0] = null;
				elemsub[1] = "Nessuna stazione esistente";
				return elemsub;
			}
			/*
			 * posizionamento alla prima riga del ResultSet
			 */	
			risultatoQuery.first();
			/*
			 * creazione dell'array di stringhe in base alla dimensione del ResultSet
			 */
			String[] elem = new String[i];
			/*
			 * popolamento dell'array
			 */
			for (int j = 0; j < elem.length; j++){
				elem[j] = risultatoQuery.getString(1);
				risultatoQuery.next();
			}				
			/*
			 * restituzione dell'array
			 */
			return elem;
		/*
		 * gestione esplicita delle eccezioni, visualizzazione di un messaggio
		 * e conseguente ritorno, di fronte ad un'eventuale eccezione, di una 
		 * matrice con una stringa vuota ed una contenente il messaggio di errore
		 */	
		}catch (SQLException e){
			String[] elemsub = new String[2];
			elemsub[0] = null;
			elemsub[1] = "Connessione non riuscita " + e.getMessage();
			return elemsub;
		}catch (ClassNotFoundException e){
			String[] elemsub = new String[2];
			elemsub[0] = null;
			elemsub[1] = "Caricamento driver fallito " + e.getMessage();
			return elemsub;
		}catch (Exception e){
			String[] elemsub = new String[2];
			elemsub[0] = null;
			elemsub[1] = "Errore: " + e.getMessage();
			return elemsub;
		}	
	}
}			 
