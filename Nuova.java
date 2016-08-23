
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Interfaccia Nuova: interfaccia con la dichiarazione della
 *					  funzione remota da implementare
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
/**
 * Questa interfaccia, attraverso l'estensione dell'interfaccia 
 * <code>java.rmi.remote</code>, identifica il metodo {@link #crea(String)} invocabile
 * da una Virtual Machine remota.
 * <br>Per l'implementazione del metodo vedi {@link NuovaImpl}.
 */
/*
 * INTERFACCIA Nuova
 * ESTENDE l'interfaccia java.rmi.Remote per identificare i metodi invocabili da una
 * 		   non-locale Virtual Machine.			
 */
interface Nuova extends Remote{
	/**
	 * <br>Questo metodo è invocato dal client ogniqualvolta riscontra che una stazione
	 * è presente tra le stazioni disponibili ma l'oggetto per la restituzione dei dati
	 * relativo a quella stazione non è ancora stato creato, in questo modo si evita di
	 * dover rilanciare il server ogni volta che nel database vengono inserite nuove stazioni.
	 * Invoca il metodo {@link MeteoDatiServer#registraNuova(String)}
	 * <br>Visualizza anche un messaggio nella console visualizzando l'indirizzo
 	 * @param stazione Stringa con il nome della stazione in base allla quale creare
	 *		 il nuovo oggetto.
	 * @return true se la stazione è stata creata con successo.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
 	 * @see NuovaImpl
 	 */
	boolean crea(String stazione) throws RemoteException;
}	 
