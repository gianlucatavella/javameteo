
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Feb 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe Elimina: finestra con una lista da cui selezionare la stazione da eliminare 
 *                 tra le stazioni esistenti
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */ 
/*
 * import dei package necessari
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Finestra con interfaccia grafica per l'eliminazione di una stazione presente nel database.
 * <br>Viene visualizzata una lista con le stazioni esistenti da cui selezionare la stazione
 * da eliminare.<br>
 * L'eliminazione avviene attraverso il metodo {@link StazioniMeteo#EliminaDalDB(String)
 * EliminaDalDB(String)} della classe {@link StazioniMeteo}.
 */ 
/*
 * CLASSE Elimina
 * ESTENDE la classe javax.swing.JFrame per la creazione della finestra e della GUI
 */
class Elimina extends JFrame{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Lista con le stazioni esistenti */
	private JList lista;
	/** Stazioni esistenti */
	private String[] Stazioni;
	/** Finesta principale dell'applicazione passata per riferimento al costruttore */
	private StazioniMeteo window;
	/**
	 * Crea i componenti della finestra e li aggiunge alla stessa.
	 * @param Staz	Array di stringhe con i nomi delle stazioni esistenti.
	 * @param win 	Riferimento all'oggetto chiamante window (istanziato nel {@link 
	 * StazioniMeteo#main(String[]) main} di {@link StazioniMeteo}.
	 */
	public Elimina(String[] Staz, StazioniMeteo win){
		window = win;
		this.Stazioni = Staz;
		/*
		 * gestione dell'evento chiusura finestra
		 */
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        /*
         * creazione del contenitore dei componenti
         */
	 	Container contentPane = getContentPane();
	 	contentPane.setLayout(new BorderLayout());
		/*
		 * creazione della lista, del pannello per lo scrolling e dei pulsanti
		 * aggiunta degli elementi creati al contenitore
		 */
		lista = new JList(Stazioni);
		JScrollPane panel = new JScrollPane(lista);
		contentPane.add(panel, BorderLayout.CENTER);
		JButton pulsante = new JButton("Elimina", new ImageIcon("images/elimina.gif"));
		contentPane.add(pulsante, BorderLayout.EAST);
		JButton chiudi = new JButton("Chiudi");
		contentPane.add(chiudi, BorderLayout.SOUTH);
		JLabel lblDesc = new JLabel("Selezionare dall'elenco la stazione da eliminare");
		contentPane.add(lblDesc, BorderLayout.NORTH);
		/*
		 * gestione degli eventi da associare ai pulsanti
		 */	
		pulsante.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				/*
				 * verifica è stato selezionato un elemento dalla lista
				 */
				if (lista.getSelectedIndex() != -1){
					/*
					 * invocazione del metodo EliminaStazione
					 * viene passata al metodo la stringa con la stazione da 
					 * eliminare
					 */
					EliminaStazione(Stazioni[lista.getSelectedIndex()]);
				}	
		 	}	
 		});		
		chiudi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				/*
				 * chiusura della finestra
				 */
				setVisible(false);			
		 	}	
 		});		
	}
	/**
	 * Visualizza un messaggio di conferma ed elimina eventualmente la stazione
	 * selezionata.
	 * @param stazione	Stazione selezionata da eliminare.
	 */
	private void EliminaStazione(String stazione){
		/*
		 * creazione del messaggio di opzione
		 */
		Object[] opzioni = {"Si","No"};
		int n = JOptionPane.showOptionDialog(null, "Eliminare la stazione di " + stazione + "?",
													"Conferma Eliminazione",
													JOptionPane.YES_NO_OPTION,
													JOptionPane.QUESTION_MESSAGE,
													null,
													opzioni,
													opzioni[1]);
		/*
		 * verifica della scelta selezionata
		 */											
		if (n == JOptionPane.YES_OPTION){
			/*
			 * invocazione del metodo EliminaDalDB della classe StazioniMeteo
			 * per eliminare la stazione
			 */
			window.EliminaDalDB(stazione);
			/*
			 * chiusura della finestra
			 */
			setVisible(false);			
		}												
	}

}		 	