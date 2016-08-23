
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Feb 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Interfaccia MeteoDati: interfaccia con la dichiarazione della
 *						  funzione remota da implementare
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
import java.util.*;
/**
 * Questa interfaccia, attraverso l'estensione dell'interfaccia 
 * <code>java.rmi.remote</code>, identifica il metodo {@link #getMeteoDati(GregorianCalendar,
 * GregorianCalendar, String) getMeteoDati} invocabile
 * da una Virtual Machine remota.
 * <br>Per l'implementazione del metodo vedi {@link MeteoDatiImpl}.
 */
/*
 * INTERFACCIA MeteoDati
 * ESTENDE l'interfaccia java.rmi.Remote per identificare i metodi invocabili da una
 * 		   non-locale Virtual Machine.			
 */
interface MeteoDati extends Remote{
	/**
 	 * Restituisce una matrice di oggetti {@link ResultMeteo} con i risultati della ricerca
 	 * all'interno del database.
 	 * @return Risultati della ricerca.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
 	 * @see MeteoDatiImpl
 	 */
	ResultMeteo[] getMeteoDati(	GregorianCalendar DataInizio,
						GregorianCalendar DataFine,
						String campo)
						throws RemoteException;
}