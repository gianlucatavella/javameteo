
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe MeteoDatiImpl: implementazione del metodo remoto MeteoDati
 * 								 
 * Compilato anche con "rmic -v1.2 MeteoDatiImpl" per la generazione dello _Stub
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.text.*;
import java.util.*;
/**
 * Questa classe definisce l'implementazione del metodo {@link #getMeteoDati(GregorianCalendar,
 * GregorianCalendar, String) getMeteoDati}
 * definito dall'interfaccia implementata {@link MeteoDati}.
 * <br>Estende la classe <code>java.rmi.server.UnicastRemoteObject</code> per definire 
 * un oggetto remoto non replicabile i cui riferimenti sono validi solamente quando il
 * processo del server è attivo. La classe <code>UnicastRemoteObject</code> fornisce inoltre
 * il sostegno necessario per i riferimenti (invocazioni, parametri e risultati) agli oggetti
 * remoti attivi utilizzando il protocollo TCP.
 * <br>Questa classe deve essere compilata anche con l'<code>RMI Compiler</code> per la
 * generazione dello <i>_Stub</i>.
 * <br>In questo caso l'istruzione da digitare in riga di comando è
 * <pre>
 * rmic -v1.2 MeteoDatiImpl
 * </pre>
 * In questo modo viene generato il file
 * <pre>
 * MeteoDatiImplImpl_Stub.class
 * </pre>
 * Questo file, insieme al file dell'interfaccia implementata {@link MeteoDati}
 * <pre>
 * MeteoDati.class
 * </pre>
 * devono essere copiati nella cartella dove sono situate le classi del client, in questo
 * caso nella cartella con le classi della Servlet.
 */
/*
 * CLASSE MeteoDatiImpl
 * ESTENDE la classe java.rmi.server.UnicastRemoteObject per definire oggetti remoti
 * 		   con riferimenti validi solo mentre il processo server è attivo
 * IMPLEMENTA l'interfaccia MeteoDati per l'implementazione del relativo metodo
 */
class MeteoDatiImpl extends UnicastRemoteObject implements MeteoDati{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Nome della stazione a cui si rifesce questo oggetto */
	private String nomeStazione;
	/** 
	 * Crea un nuovo oggetto inizializzando la variabile {@link #nomeStazione}.
	 * @param nome Nome della stazione.	
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */	
	public MeteoDatiImpl (String nome) throws RemoteException{
		nomeStazione = nome;
	}
	/**
	 * Si connette al database e restituisce una matrice con i risultati corrispondenti ai
	 * criteri di ricerca impostati.
	 * <br>Visualizza anche un messaggio nella console visualizzando l'indirizzo
	 * IP del client chiamante e la stazione metereologica.
	 * <br>Nel caso si verifichi un'eccezione viene ritornata una matrice 
	 * con un solo elemento con il messaggio di errore.
	 * @param dataINIZIO Data iniziale dell'intervallo.
	 * @param dataFINE Data finale dell'intervallo.
	 * @param campoRichiesto Tipo di dati richiesti ("temperatura", "umidita", "pressione" o "precipitazioni").
	 * @return Risultati corrispondenti ai criteri di ricerca.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */
	public ResultMeteo[] getMeteoDati(GregorianCalendar dataINIZIO, 
								GregorianCalendar dataFINE,
								String campoRichiesto)
								 throws RemoteException{
									 	
		String DataIniziale = String.valueOf(dataINIZIO.get(Calendar.MONTH) + 1) + "/" + 
								String.valueOf(dataINIZIO.get(Calendar.DAY_OF_MONTH)) + "/" +
								String.valueOf(dataINIZIO.get(Calendar.YEAR));
		String DataFinale =  String.valueOf(dataFINE.get(Calendar.MONTH) + 1) + "/" + 
								String.valueOf(dataFINE.get(Calendar.DAY_OF_MONTH)) + "/" +
								String.valueOf(dataFINE.get(Calendar.YEAR));

		GregorianCalendar dataOra = new GregorianCalendar();						
		try{
			System.out.println("  " + dataOra.getTime().toString() +
								 " Richiesta dati su " + nomeStazione + " da " + getClientHost()); 
		}catch (ServerNotActiveException e){
			System.out.println("  " + dataOra.getTime().toString() + 
								" Richiesta dati su " + nomeStazione + " da client sconosciuto/n" +
								"Metodo chiamato al di fuori del servizio di " +
								"Remote Method Invocation. Errore: " +
								e.getMessage());
		}						 


		try{
 			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection connessione = DriverManager.getConnection("jdbc:odbc:meteo");
			Statement comando = connessione.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
															ResultSet.CONCUR_READ_ONLY);


			ResultSet risultato = comando.executeQuery
				("SELECT Data, Ora," + campoRichiesto + " FROM " + nomeStazione 
				+ " WHERE (Data Between #" + DataIniziale + "# AND #" 
				+ DataFinale + "#) ORDER BY Data,Ora;");
	
			SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOra = new SimpleDateFormat("HH:mm");
			int lunghezzaResultSet = 0;
			while (risultato.next()){
				lunghezzaResultSet++;
			}
			ResultMeteo[] resultmeteo = new ResultMeteo[lunghezzaResultSet];
			int i = 0;
			risultato.beforeFirst();	
			while (risultato.next()){
				resultmeteo[i] = new ResultMeteo(  formatData.format(risultato.getDate(1)), 
															formatOra.format(risultato.getTime(2)),
															risultato.getInt(3));
				i++;
			}
				
			return resultmeteo;		
			
		}catch (SQLException e){
			System.out.println(	"SQLException " + e.getMessage());
			ResultMeteo[] resultmeteo = new ResultMeteo[1];
			resultmeteo[0] = new ResultMeteo("", "SQLException: " 
													+ e.getMessage(),
													0);
			return resultmeteo;		
		}catch (ClassNotFoundException e){
			System.out.println(	"ClassNotFoundException " + e.getMessage());
			ResultMeteo[] resultmeteo = new ResultMeteo[1];
			resultmeteo[0] = new ResultMeteo("", "ClassNotFoundException: "
													+ e.getMessage(),
													0);
			return resultmeteo;		
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage());
			ResultMeteo[] resultmeteo = new ResultMeteo[1];
			resultmeteo[0] = new ResultMeteo("" ,"Exception: "  
													+ e.getMessage(),
													0);
			return resultmeteo;		
		}								 	
	}	
}		 	
	