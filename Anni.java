
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Ago 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Interfaccia Anni: interfaccia con la dichiarazione della
 *					 funzione remota da implementare
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
/**
 * Questa interfaccia, attraverso l'estensione dell'interfaccia 
 * <code>java.rmi.remote</code>, identifica il metodo {@link #getAnniDisponibili(String)} invocabile
 * da una Virtual Machine remota.
 * <br>Per l'implementazione del metodo vedi {@link AnniImpl}.
 */
/*
 * INTERFACCIA Anni
 * ESTENDE l'interfaccia java.rmi.Remote per identificare i metodi invocabili da una
 * 		   non-locale Virtual Machine.			
 */
interface Anni extends Remote{
	/**
 	 * Restituisce una matrice di 2 interi con i valori del primo e dell'ultimo anno in cui sono disponibili
 	 * dati per la stazione selezionata all'interno del database.
 	 * @param Stazione Stazione selezionata
 	 * @return Primo e ultimo anno in cui sono disponibili dati per la stazione selezionata.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
 	 * @see AnniImpl
 	 */
	int[] getAnniDisponibili(String Stazione) throws RemoteException;
}	 
