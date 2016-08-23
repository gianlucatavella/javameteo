
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe NuovaImpl: implementazione della funzione remota Nuova
 *
 * Compilato anche con "rmic -v1.2 NuovaImpl" per la generazione dello _Stub
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.rmi.server.*;
/**
 * Questa classe definisce l'implementazione del metodo {@link #crea(String)}
 * definito dall'interfaccia implementata {@link Nuova}.
 * <br>Estende la classe <code>java.rmi.server.UnicastRemoteObject</code> per definire 
 * un oggetto remoto non replicabile i cui riferimenti sono validi solamente quando il
 * processo del server è attivo. La classe <code>UnicastRemoteObject</code> fornisce inoltre
 * il sostegno necessario per i riferimenti (invocazioni, parametri e risultati) agli oggetti
 * remoti attivi utilizzando il protocollo TCP.
 * <br>Questa classe deve essere compilata anche con l'<code>RMI Compiler</code> per la
 * generazione dello <i>_Stub</i>.
 * <br>In questo caso l'istruzione da digitare in riga di comando è
 * <pre>
 * rmic -v1.2 NuovaImpl
 * </pre>
 * In questo modo viene generato il file
 * <pre>
 * NuovaImpl_Stub.class
 * </pre>
 * Questo file, insieme al file dell'interfaccia implementata {@link Nuova}
 * <pre>
 * Nuova.class
 * </pre>
 * devono essere copiati nella cartella dove sono situate le classi del client, in questo
 * caso nella cartella con le classi della Servlet.
 */
/*
 * CLASSE NuovaImpl
 * ESTENDE la classe java.rmi.server.UnicastRemoteObject per definire oggetti remoti
 * 		   con riferimenti validi solo mentre il processo server è attivo
 * IMPLEMENTA l'interfaccia Nuova per l'implementazione del relativo metodo
 */
class NuovaImpl extends UnicastRemoteObject implements Nuova{
	/** 
	 * Crea un nuovo oggetto.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */	
	public NuovaImpl() throws RemoteException{} 
	/**
	 * <br>Questo metodo è invocato dal client ogniqualvolta riscontra che una stazione
	 * è presente tra le stazioni disponibili ma l'oggetto per la restituzione dei dati
	 * relativo a quella stazione non è ancora stato creato, in questo modo si evita di
	 * dover rilanciare il server ogni volta che nel database vengono inserite nuove stazioni.
	 * Invoca il metodo {@link MeteoDatiServer#registraNuova(String)}
	 * <br>Visualizza anche un messaggio nella console visualizzando l'indirizzo
	 * IP del client chiamante.
	 * @param stazione Stringa con il nome della stazione in base allla quale creare
	 *		 il nuovo oggetto.
	 * @return true se la stazione è stata creata con successo.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
	 */
	public boolean crea(String stazione) throws RemoteException{
        return MeteoDatiServer.registraNuova(stazione);
	}
}			 
		