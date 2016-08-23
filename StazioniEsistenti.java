
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Interfaccia StazioniEsistenti: interfaccia con la dichiarazione del
 *						  		  metodo remoto da implementare
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.rmi.*;
/**
 * Questa interfaccia, attraverso l'estensione dell'interfaccia 
 * <code>java.rmi.remote</code>, identifica il metodo {@link #getStazioniEsistenti()} invocabile
 * da una Virtual Machine remota.
 * <br>Per l'implementazione del metodo vedi {@link StazioniEsistentiImpl}.
 */
/*
 * INTERFACCIA StazioniEsistenti
 * ESTENDE l'interfaccia java.rmi.Remote per identificare i metodi invocabili da una
 * 		   non-locale Virtual Machine.			
 */
interface StazioniEsistenti extends Remote{
	/**
 	 * Restituisce una matrice di stringhe con i nomi delle stazioni esistenti all'interno
 	 * del database.
 	 * @return Nomi delle stazioni esistenti.
 	 * @throws RemoteException Per l'eccezione che può presentarsi durante la chiamata del
 	 *			un metodo remoto.
 	 * @see StazioniEsistentiImpl
 	 */
 	String[] getStazioniEsistenti() throws RemoteException;
}	 