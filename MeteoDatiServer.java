
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe MeteoDatiServer: crea gli oggetti che realizzano il servizio
 *						   e li registra nell'RMIRegistry
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
/**
 * Crea gli oggetti che realizzano il servizio e li registra nell'<code>RMIRegistry</code>.
 * <br>Viene creato un oggetto di tipo {@link StazioniEsistentiImpl} che implementa il metodo
 * per la restituzione delle stazioni esistenti, un oggetto di tipo {@link AnniImpl} che implementa
 * il metodo per la restituzione degli anni disponibili, un oggetto di tipo {@link NuovaImpl}
 * per la crazione dinamica di nuovi oggetti remoti e una serie di oggetti {@link MeteoDatiImpl}
 * in base alle stazioni disponibili (uno per ogni stazione metereologica).
 * <br>Le varie operazioni effettuate vengono visualizzate a console, nel caso si verifichi
 * un'eccezione viene visualizzato un messaggio con la descrizione dell'eccezione.
 */
/*
 * CLASSE MeteoDatiServer
 */
class MeteoDatiServer{
	/**
	 * Inizio dell'applicazione.
	 * <br>Metodo di classe principale che si connette al database ed esegue le operazioni di creazione e registrazione
	 * degli oggetti remoti.
	 * <br>Per lanciare il server in ambiente Windows è necessario inserire le seguenti 2 istruzione nella riga 
	 * di comando:
	 * <pre>
	 * start rmiregistry
	 * start java -Djava.rmi.server.codebase=file:/c:\rmi\download/ MeteoDatiServer
	 * </pre>
	 * @param args	Gli argomenti passati dalla riga di comando.
	 *				<br>Esempio<br>
	 *				<pre>
	 *					java MeteoDatiServer arg1 arg2 ...
	 *				</pre>
	 *				In questo caso non serve nessun argomento.						 
	 */
	public static void main(String[] args){
		try{
			System.out.println("Connessione al database...");
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
			int numeroStazioni = 0;
			while (risultatoQuery.next()) {
				numeroStazioni++;
			}
			System.out.println("Creazione degli oggetti...");
			/*
			 * creazione dell'oggetto StazioniEsistentiImpl per l'invocazione remota
			 * del metodo getStazioniEsistenti() che restituisce un array di stringhe
			 * contenente i nomi delle stazioni esistenti
			 */
			System.out.println("Creazione dell'oggetto per la restituzione delle stazioni esistenti...");
			StazioniEsistentiImpl stazioniesistenti = new StazioniEsistentiImpl();
			System.out.println("Registrazione dell'oggetto...");
			Naming.rebind("Esistenti", stazioniesistenti);
			System.out.println("Completato");
			/*
			 * creazione dell'oggetto AnniImpl per l'invocazione remota
			 * del metodo getAnniDisponibili(String) che restituisce un array di 2 interi
			 * con l'anno minore e maggiore di rilevazione
			 */

			System.out.println("Creazione dell'oggetto per la restituzione degli anni disponibili...");
			AnniImpl anniDisponibili = new AnniImpl();
			System.out.println("Registrazione dell'oggetto...");
			Naming.rebind("Anni", anniDisponibili);
			System.out.println("Completato");

			/*
			 * creazione dell'oggetto NuovaImpl per la creazione di nuovi oggetti
			 * MetoDatiImpl anche dopo aver già lanciato il server
			 */

			System.out.println("Creazione dell'oggetto per la creazione di nuove stazioni...");
			NuovaImpl nuova = new NuovaImpl();
			System.out.println("Registrazione dell'oggetto...");
			Naming.rebind("Nuova", nuova);
			System.out.println("Completato");

			/*
			 * se il ResultSet non è vuoto vengono eseguite le operazioni di creazione degli
			 * oggetti e quindi dei metodi
			 */
			if (numeroStazioni != 0){
				/*
				 * posizionamento alla prima riga del ResultSet
				 */	
				risultatoQuery.first();
				/*
				 * creazione dell'array di oggetti in base alla dimensione del ResultSet
				 */
				MeteoDatiImpl[] meteodatiImpl = new MeteoDatiImpl[numeroStazioni];
				/*
				 * creazione dell'array di stringhe
				 */
				String[] stazioni = new String[numeroStazioni];
				/*
				 * popolamento dell'array di stringhe e di quello di oggetti MeteoDataImpl
				 */
				System.out.println("Creazione degli oggetti per la restituzione dei risultati..."); 
				for (int j = 0; j < stazioni.length; j++){
					stazioni[j] = risultatoQuery.getString(1);
					System.out.println("...creazione oggetto " + stazioni[j]); 
					meteodatiImpl[j] = new MeteoDatiImpl(stazioni[j]);
					System.out.println("...registrazione di " + stazioni[j]); 
					Naming.rebind(stazioni[j], meteodatiImpl[j]);
					risultatoQuery.next();
				}
				System.out.println("Completato.");
				System.out.println();
			}else{
				System.out.println("Nessuna stazione esistente");
			}		
			System.out.println("Attesa di invocazione dal client...");
		/*
		 * gestione esplicita delle eccezioni, visualizzazione di un messaggio
		 */	
		}catch (SQLException e){
			System.out.println("Connessione non riuscita " + e.getMessage());
		}catch (ClassNotFoundException e){
			System.out.println("Caricamento driver fallito " + e.getMessage());
		}catch (Exception e){
			System.out.println("Errore: " + e.getMessage());
		}	
	}
	/**
	 * Registra un nuovo oggetto {@link MeteoDatiImpl} per la restituzione dei dati
	 * al client remoto.
	 * @param stazione Stringa con il nome della stazione in base allla quale creare
	 *		 il nuovo oggetto.
	 * @return true se la stazione è stata creata con successo.
	 */
	public static boolean registraNuova(String stazione){
		try{
			System.out.println("Connessione al database...");
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
			boolean presente = false;
			boolean creata = false;
			while (risultatoQuery.next()) {
				if (risultatoQuery.getString(1).equalsIgnoreCase(stazione)){
					stazione = risultatoQuery.getString(1);
					presente = true;
					break;
				}	
			}
			if (presente){
				System.out.println("...creazione oggetto " + stazione); 
				MeteoDatiImpl nuovoMeteoDati = new MeteoDatiImpl(stazione);
				System.out.println("...registrazione di " + stazione); 
				Naming.rebind(stazione, nuovoMeteoDati);
				System.out.println("Completato.");
				System.out.println();
				creata = true;
			}else{
				System.out.println("La stazione non è presente nel database.");
			}		
			System.out.println("Attesa di invocazione dal client...");
			return creata;
		/*
		 * gestione esplicita delle eccezioni, visualizzazione di un messaggio
		 */	
		}catch (SQLException e){
			System.out.println("Connessione non riuscita " + e.getMessage());
			return false;
		}catch (ClassNotFoundException e){
			System.out.println("Caricamento driver fallito " + e.getMessage());
			return false;
		}catch (Exception e){
			System.out.println("Errore: " + e.getMessage());
			return false;
		}	
	}
		
}			 