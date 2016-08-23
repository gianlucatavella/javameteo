
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe Help: finestra per la visualizzazione del file della guida in formato 
 *              HTML
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
/**
 * Finestra per la visualizzazione del file della guida in formato HTML contenuto nella
 * stessa cartella della classe.
 * <br>Il file è denominato <i>"help.html"</i>
 */
/*
 * CLASSE Help
 * ESTENDE la classe javax.swing.JFrame per la creazione della finestra e della GUI
 */
class Help extends JFrame{
	/** Pannello di editing per la visulizzazione del file HTML */
	JEditorPane helpPane;
	/**
	 * Crea i componenti della finestra e li aggiunge alla stessa.
	 * <br>Cerca il file <i>"help.html"</i> nella cartella corrente e lo visualizza.
	 */
	public Help(){
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
		 * creazione della del pannello per la visualizzazione, del pannello per lo 
		 * scrolling e aggiunta degli elementi creati al contenitore
		 */
		helpPane = new JEditorPane();
		helpPane.setEditable(false);
		try{
			/*
			 * va a cercare il file nella cartella corrente e lo assegna alla stringa
			 */
			String htmlHelpFileName = null;
			htmlHelpFileName = "file:" + System.getProperty("user.dir")
										+ System.getProperty("file.separator")
										+ "help.html";

			/*
			 * crea un nuovo URL in base al nome del file
			 */
			URL url = new URL(htmlHelpFileName);
			/*
			 * visualizza la pagina
			 */
			helpPane.setPage(url);
		
		}catch (Exception e){
			/*
			 * gestione delle eccezioni
			 */
			helpPane.setText("Impossibile caricare la pagina. \n Errore:"
								+ e.getMessage());
		}
		/*
		 * aggiunta delle barre per lo scrolling
		 */							
		JScrollPane scrollpane = new JScrollPane(helpPane);
		contentPane.add(scrollpane, BorderLayout.CENTER);

	}
}