
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Feb 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe StazioniMeteo: applicazione con interfaccia grafica per scrivere 
 *                       i dati rilevati nel database
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.text.*;
import java.util.*; 
import java.sql.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Applicazione con interfaccia grafica per scrivere i dati nel database.
 *
 * Contiene il metodo main per la creazione dell'oggetto stesso definito in questa classe.<br>
 * L'interfaccia grafica è composta da etichette, combo box, campi di testo, pulsanti e 
 * voci di menu per la gestione delle stazioni e l'inserimento di dati nella base di dati.<br>
 * La base di dati definita è denominata <i>"meteo"</i> e deve essere registrata sulla
 * macchina dove si esegue l'applicazione.<br>
 * Ad esempio, in ambiente Windows, nel pannello di controllo lanciare la voce <i>Origini di
 * dati(ODBC) 32 bit</i> e aggiungere la base di dati denominandola "meteo".
 *
 */ 
/*
 * CLASSE StazioniMeteo
 * ESTENDE la classe javax.swing.JFrame per la creazione della finestra e della GUI
 * IMPLEMENTA l'interfaccia java.awt.event.ActionListener per la gestione degli eventi
 * generati dalla selezione delle voci di menu 
 */
class StazioniMeteo extends JFrame implements ActionListener{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/**
	 * Area di testo posizionata nell'area inferiore della finestra per la
	 * visualizzazione e la conferma delle operazioni eseguite
	 */
	private JTextArea areaLog;
	/** Etichetta per la visualizzazione della stazione selezionata */
	private JLabel lblStazione;
	/** Stringa contenente la stazione corrente */
	private String ultimaStazione;
	/** ComboBox con i giorni del mese selezionabili */
	private JComboBox giorni;
	/** ComboBox con i mesi selezionabili */
	private JComboBox mesi;
	/** ComboBox con gli anni selezionabili */
	private JComboBox cmbAnni;
	/** Campo di testo per l'inserimento dell'ora della rilevazione */
	private JTextField ora;
	/** Campo di testo per l'inserimento dei minuti della rilevazione */
    private JTextField min;
	/** Campo di testo per l'inserimento della temperatura rilevata */
    private JTextField temper;
	/** Campo di testo per l'inserimento dell'umidità rilevata */
    private JTextField umid;
	/** Campo di testo per l'inserimento della pressione atmosferica rilevata */
    private JTextField press;
	/** Campo di testo per l'inserimento delle precipitazioni rilevate */
    private JTextField prec;
    /** Costante per definire il carattere di escape per il ritorno a capo */	
	private final String newline = new String("\n");						
	/**
	 * Crea i componenti della finestra e li inserisce nella stessa.
	 */
	/*
	 * costruttore:
	 */
	private StazioniMeteo(){
		/*
		 * gestione dell'evento chiusura finestra
		 */
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
		       	ScriviStato(ultimaStazione);
                System.exit(0);
            }
        });
	 	/*
	 	 * creazione del pannello contenitore
	 	 */
	 	Container contentPane = getContentPane();
	 	contentPane.setLayout(null);
	 	/*
	 	 * creazione dell'area di testo per la visualizzazione delle operazioni 
	 	 * effettuate 
	 	 */
	 	areaLog = new JTextArea();
	 	areaLog.setEditable(false);
	 	/*
	 	 * aggiunta all'area di testo di un pannello per lo scrolling
	 	 */
	 	JScrollPane scrollLog = new JScrollPane(areaLog);
	 	scrollLog.setBounds(10, 220, 525, 100);


	 	/*
	 	 * aggiunta del componente creato al pannello contenitore
	 	 */
	 	contentPane.add(scrollLog);
	 	/*
	 	 * creazione dell'etichetta lblStazione e impostazione del testo della stessa
	 	 * dipendentemente dalla presenza o meno di uno stato precedente e
	 	 * della conseguente stazione selezionata
	 	 */
	 	Font fancy = new Font("Serif", Font.BOLD, 20);
		lblStazione = new JLabel("Nessuna stazione selezionata");
		lblStazione.setFont(fancy);
	 	lblStazione.setBounds(5, 10, 300, 30);
	 	contentPane.add(lblStazione);		
	 	/*
	 	 * creazione e inserimento delle etichette e dei combo box
	 	 * inizializzazione degli stessi in base alla data corrente
	 	 */
	 	Font smallFancy = new Font("Serif", Font.BOLD, 18);
		JLabel lblData = new JLabel("Data: ");
		lblData.setFont(smallFancy);
		lblData.setForeground(Color.red);
	 	lblData.setBounds(45, 50, 60, 20);
	 	contentPane.add(lblData);		
	 	
        java.util.Date oggi = new java.util.Date();

	 	SimpleDateFormat formatter;
	 	formatter = new SimpleDateFormat("dd/MM/yyyy hh.mm");
	 	Log(formatter.format(oggi));
	 	
	 	formatter =  new SimpleDateFormat("dd");


	 	String[] giorniMese = new String[31];
	 	for (int i=0; i < giorniMese.length; i++){
	 		giorniMese[i] = Integer.toString(i+1);
	 	}
	 	
	 	giorni = new JComboBox(giorniMese);
	 	giorni.setSelectedIndex(Integer.parseInt(formatter.format(oggi)) - 1);
	 	giorni.setBounds(100, 50, 50, 20);
	 	contentPane.add(giorni);
	 	String[] mesiAnno = {   "Gennaio",
	 							"Febbraio",
	 							"Marzo",
	 							"Aprile",
	 							"Maggio",
	 							"Giugno",
	 							"Luglio",
	 							"Agosto",
	 							"Settembre",
	 							"Ottobre",
	 							"Novembre",
	 							"Dicembre"};
	 	formatter = new SimpleDateFormat("MM");
	 	mesi = new JComboBox(mesiAnno);
	 	mesi.setSelectedIndex(Integer.parseInt(formatter.format(oggi)) - 1);
	 	mesi.setBounds(160, 50, 100, 20);
	 	contentPane.add(mesi);

	 	formatter = new SimpleDateFormat("yyyy");
		int anno = Integer.parseInt(formatter.format(oggi));
	 	String[] anni = new String[11];
	 	for (int i = 0 ; i < 11; i++){
	 		anni[i] = Integer.toString(i - 5 + anno);
	 	}
	 	
	 	cmbAnni = new JComboBox(anni);
	 	cmbAnni.setSelectedIndex(5);
	 	cmbAnni.setBounds(270, 50, 70, 20);
	 	cmbAnni.setEditable(true);
	 	contentPane.add(cmbAnni);
	 	
	 	JLabel lblOra = new JLabel("Ora:");
		lblOra.setFont(smallFancy);
		lblOra.setForeground(Color.red);
	 	lblOra.setBounds(350, 50, 60, 20);
	 	contentPane.add(lblOra);		
	 	
	 	ora = new JTextField();
	 	ora.setBounds(390, 50, 40, 20);
		contentPane.add(ora);

	 	JLabel lblMin = new JLabel("Min.:");
		lblMin.setFont(smallFancy);
		lblMin.setForeground(Color.red);
	 	lblMin.setBounds(440, 50, 60, 20);
	 	contentPane.add(lblMin);		
	 	
	 	min = new JTextField();
	 	min.setBounds(490, 50, 40, 20);
		contentPane.add(min);


	 	
		
		JLabel temperatura = new JLabel("Temperatura (°C)");
		temperatura.setFont(smallFancy);
		temperatura.setBounds(45, 85, 150, 20);
		temperatura.setForeground(Color.red);
		contentPane.add(temperatura); 

	 	temper = new JTextField();
	 	temper.setBounds(190, 85, 40, 20);
		contentPane.add(temper);

		Font smallItFancy = new Font("Serif", Font.BOLD | Font.ITALIC, 18);
	 	
		JLabel umidita = new JLabel("Umidità (%)");
		umidita.setFont(smallFancy);
		umidita.setBounds(45, 115, 150, 20);
		umidita.setForeground(Color.red);
		contentPane.add(umidita); 

	 	umid = new JTextField();
	 	umid.setBounds(150, 115, 40, 20);
		contentPane.add(umid);

		JLabel pressione = new JLabel("Pressione (milliBar)");
		pressione.setFont(smallFancy);
		pressione.setBounds(45, 145, 250, 20);
		pressione.setForeground(Color.red);
		contentPane.add(pressione);

	 	press = new JTextField();
	 	press.setBounds(205, 145, 40, 20);
		contentPane.add(press);
		
		JLabel precipitazioni = new JLabel("Precipitazioni (mm)");
		precipitazioni.setFont(smallFancy);
		precipitazioni.setBounds(45, 175, 250, 20);
		precipitazioni.setForeground(Color.red);
		contentPane.add(precipitazioni);

	 	prec = new JTextField();
	 	prec.setBounds(205, 175, 40, 20);
		contentPane.add(prec);

	 	/*
	 	 * invocazione del metodo CreaMenu per creare la barra dei menu e i menu relativi
	 	 */
	 	CreaMenu();
		/*
		 * creazione del pulsante "Genera casuali"
		 */		 
	 	JButton casuali = new JButton("Genera casuali");
	 	casuali.setBounds(385, 105, 150, 30);
	 	casuali.setBackground(Color.lightGray);
	 	casuali.setForeground(Color.green);
	 	contentPane.add(casuali);
	 	/*
	 	 * implementazione degli eventi associati al pulsante "Genera casuali"
	 	 */
	 	casuali.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
		 		String[] elem = StazioniEsistenti();
				/* 
				 * verifica della presenza di stazioni esistenti 
				 */ 		
		  		if (!elem[0].equalsIgnoreCase("")){
					Casuale cas = new Casuale(elem);
					cas.pack();
	        		cas.setLocation(320,220);
	        		cas.setVisible(true);	
				}else{
					JOptionPane.showMessageDialog(null, "E' necessario che sia presente almeno una stazione.");
				}	
					
		 	}	
 		});		
		/*
		 * creazione del pulsante "Inserisci"
		 */		 
	 	JButton inserisci = new JButton("Inserisci");
	 	inserisci.setBounds(435, 155, 100, 50);
	 	inserisci.setBackground(Color.lightGray);
	 	inserisci.setForeground(Color.blue);
	 	contentPane.add(inserisci);
	 	/*
	 	 * implementazione degli eventi associati al pulsante "Inserisci"
	 	 */
	 	inserisci.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				/*
				 * verifica che vi sia una stazione selezionata
				 */
				if (!ultimaStazione.equalsIgnoreCase("")){
					try{
						int gg = Integer.parseInt(giorni.getSelectedItem().toString());
						int mm = mesi.getSelectedIndex() + 1;
						int aaaa = Integer.parseInt(cmbAnni.getSelectedItem().toString());
						/*
						 * verifica la validità della data inserita
						 */
						if (VerificaData(gg, mm, aaaa)){
								int temperatura = Integer.parseInt(temper.getText());
								int umi = Integer.parseInt(umid.getText());
								int pre = Integer.parseInt(press.getText());
								int precipitazioni = Integer.parseInt(prec.getText());
								int ore = Integer.parseInt(ora.getText());
								int minuti = Integer.parseInt(min.getText());
								/*
								 * verifica dell'attendibilità dei valori inseriti
								 * tipicamente, devono essere compresi in un opportuno range
								 */
								if ((umi >= 0) && (umi <= 100)){
									if ((temperatura < 200) && (temperatura > -200) && (pre >= 0) && (pre <= 3000) && (precipitazioni >= 0) && (precipitazioni <=1000)){
										if ((ore >= 0) && (ore <= 23) && (minuti >= 0) && (minuti <=59)){
											InserisciDati(ultimaStazione, String.valueOf(gg) + "/" + String.valueOf(mm) 
												+ "/" + String.valueOf(aaaa), String.valueOf(ore) + "." + String.valueOf(minuti),
												temperatura, umi, pre, precipitazioni);
										}else{
											JOptionPane.showMessageDialog(null, "Verificare che l'ora inserita sia corretta.");
										}		
									}else{
										JOptionPane.showMessageDialog(null, "Controllare che i valori inseriti siano corretti.");
									}		
								}else{
									JOptionPane.showMessageDialog(null, "L'umidità deve essere compresa tra 0 e 100.");
								}		
										
						}else{
							JOptionPane.showMessageDialog(null, "Verificare che la data inserita sia corretta.");
						}	
								
					} catch (Exception e){
						JOptionPane.showMessageDialog(null, "Verificare di aver inserito tutti i valori richiesti.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Selezionare una stazione.");
				}				
		 	}	
 		});		
	  

	}
	
	/**
	 * Metodo per la verifica della validità della data.
	 * <br>Ritorna il valore <code>true</code> se la data rappresentata dai parametri è valida.
	 * 
	 * @return <code>true</code> se la data è valida
	 *
	 * @param gg	Giorno del mese
	 * @param mm	Mese dell'anno
	 * @param year	Anno 
	 */
	private boolean VerificaData(int gg, int mm, int year){
		/*
		 * numero dei giorni del mese
		 */
		int numDays = 0;
		/* 
		 * In base al mese e all'anno assegna un valore al numero dei giorni del mese
		 */
		switch (mm) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
            	/*
            	 * verifica se l'anno inserito è bisestile
            	 */
                if ( ((year % 4 == 0) && !(year % 100 == 0))
                     || (year % 400 == 0) )
                    numDays = 29;
                else
                    numDays = 28;
                break;
        }
        /*
         * controlla che il giorno inserito sia compatibile con il mese e l'anno
         * immesso
         */
        if (gg <= numDays){
        	return true;
        }else{
        	return false;
        }				
	}	
	
	/**
	 * Crea le varie voci di menu.
	 * <br>Se nel database sono contenute delle stazioni vengono visualizzati i sottomenù
	 * <b>Seleziona</b> ed <b>Elimina</b> dal menù <b>File</b>. Il sottomenù 
	 * <b>Seleziona</b>	visualizza le stazioni contenute.
	 * <br>Se non vi sono stazioni nel database i relativi sottomenù non vengono visualizzati. 
	 *
	 */
	private void CreaMenu(){
		/* 
		 * dichiarazione oggetti di metodo 
		 */
		JMenuBar menubar;
		JMenu menu, submenu;
		JMenuItem menuitem;
		JRadioButtonMenuItem rbMenuitem;
 		/*
 		 * creazione della barra dei menu
 		 */
 		menubar = new JMenuBar();
	 	setJMenuBar(menubar);
	 	/*
	 	 * creazione del primo menu (Stazione) e aggiunta alla barra
	 	 */
	 	menu = new JMenu("Stazione");
	 	menu.setMnemonic(KeyEvent.VK_S);
	 	menubar.add(menu);
		/*
		 * creazione della prima voce (Nuova...) del primo menu (Stazione)
		 */
		menuitem = new JMenuItem("Nuova...", new ImageIcon("images/nuova.gif"));
		menuitem.setMnemonic(KeyEvent.VK_N);
		/*
		 * aggiunta di un tasto di scelta rapida (CTRL-N) alla voce di menu
		 */
	    menuitem.setAccelerator(KeyStroke.getKeyStroke(
 		    KeyEvent.VK_N, ActionEvent.CTRL_MASK));
 		menuitem.addActionListener(this);
 		/*
 		 * aggiunta della voce al menu
 		 */
 		menu.add(menuitem);
 		/*
 		 * invocazione del metodo StazioniEsistenti per la restituzione di un'eventuale
 		 * array di stringhe contenente le stazioni esistenti e memorizzate nel
 		 * database
 		 */
 		String[] elem = StazioniEsistenti();
		/* 
		 * verifica della presenza di stazioni esistenti e conseguenze creazione
		 * delle voci di menu relative alla selezione e all'eliminazione di 
		 * stazioni esistenti
		 */ 		
  		if (!elem[0].equalsIgnoreCase("")){
  			/*
  			 * invocazione del metodo LeggiStato per leggere dal file "meteo.txt"
  			 * la stato (nome dell'ultima stazione selezionata) al momento della
  			 * chiusura durante la precedente esecuzione dell'applicazione
  			 */
  			ultimaStazione = LeggiStato();
 			/*
 			 * creazione del sottomenu (Seleziona) e delle relative voci di menu
 			 */
 			submenu = new JMenu("Seleziona");
 			submenu.setMnemonic(KeyEvent.VK_Z);
 			/*
 			 * creazione oggetto di tipo ButtonGroup per raggruppare i pulsanti
 			 * di opzione contenuti nel sottomenu
 			 */
 			ButtonGroup group = new ButtonGroup();
 			/*
 			 * variabile di comodo per verificare se la stazione corrispondente
 			 * allo stato precedente dell'applicazione esiste ancora nel database
 			 * o è stata eliminata da qualche altro utente
 			 */
 			boolean esisteAncora = false;
 			/*
 			 * aggiunta al sottomenu (Seleziona) dei pulsanti di opzione con i
 			 * nomi delle stazioni
 			 */
 			for (int i=0; i < elem.length; i++){
 				rbMenuitem = new JRadioButtonMenuItem(elem[i]);
 				/* 
 				 * verifica e conseguente eventuale selezione del pulsante a
 				 * cui corrisponde lo stato precedente all'apertura dell'
 				 * applicazione
 				 */
 				if (elem[i].equalsIgnoreCase(ultimaStazione)){
 					rbMenuitem.setSelected(true);
 					esisteAncora = true;
 				}	
 				group.add(rbMenuitem);
 		    	rbMenuitem.addActionListener(this);
 		    	submenu.add(rbMenuitem);
 			}
 			/*
 			 * verifica dell'esistenza della stazione nel database
 			 */
 			if (!esisteAncora){
 				ultimaStazione = "";
 			}	
 			/*
 			 * aggiunta al menu (Stazione) del sottomenu (Seleziona)
 			 */
 			menu.add(submenu);
 			/*
 			 * aggiunta di un separatore alle voci di menu
 			 */
 			menu.addSeparator();
 			/*
 			 * creazione e aggiunta della voce di menu (Elimina)
 			 */
 	 		menuitem = new JMenuItem("Elimina...", new ImageIcon("images/elimina.gif"));
 			menuitem.setMnemonic(KeyEvent.VK_L);
 			menuitem.addActionListener(this);
			menu.add(menuitem);
 		}else{
			ultimaStazione = "";
		} 			
 		/*
 		 * creazione e aggiunta della voce di menu (Esci) al menu (Stazione)
 		 */
		menu.addSeparator();
 		menuitem = new JMenuItem("Esci");
 		menuitem.setMnemonic(KeyEvent.VK_E);
 		menuitem.addActionListener(this);
		menu.add(menuitem);
		/*
		 * creazione e aggiunta del menu (Help) alla barra dei menu
		 */
		menu = new JMenu("Help");
	 	menu.setMnemonic(KeyEvent.VK_H);
	 	menubar.add(menu);
		/* 
		 * aggiunta delle voci di menu (Guida in linea... e About...) al menu (Help)
		 */
		menuitem = new JMenuItem("Guida in linea...", new ImageIcon("images/help.gif"));
 		menuitem.setMnemonic(KeyEvent.VK_G);
		menuitem.addActionListener(this);
		menu.add(menuitem);
		menu.addSeparator();
 		menuitem = new JMenuItem("About...", new ImageIcon("images/about.gif"));
 		menuitem.setMnemonic(KeyEvent.VK_A);
		menuitem.addActionListener(this);
		menu.add(menuitem);
		/* 
		 * invocazione del metodo SetStazione per modifica l'etichetta
		 */
		SetStazione(ultimaStazione);
		
	}
	/**
	 * Cambia il testo all'etichetta {@link #lblStazione} dipendentemente 
	 * dalla stazione selezionata.
	 * Inoltre modifica il contenuto della stringa {@link #ultimaStazione}. 
	 */
	private void SetStazione(String station){
		/*
	 	 * impostazione del testo dell'etichetta
	 	 * dipendentemente dalla presenza o meno di uno stato precedente e
	 	 */
	 	ultimaStazione = station;
	 	if (!ultimaStazione.equalsIgnoreCase("")){
		 	lblStazione.setText("Stazione di " + station);
		 	lblStazione.setIcon(new ImageIcon("images/stazione.gif"));
		 	Log("");
	 		Log("Stazione di " + station);
	 	}else{
	 		Log("");
	 		lblStazione.setText("Nessuna stazione selezionata");
	 		Log("Nessuna stazione selezionata");
	 	}
	}
	/**
	 * Si connette al database e restituisce una matrice di stringhe contenenti
	 * le stazioni inserite nel database.
	 * <br>Se non ci sono stazioni inserite o si verifica un errore durante la 
	 * connessione al database restituisce una matrice con una sola stringa vuota.
	 * @return stazioni contenute nel database
	 */
	private String[] StazioniEsistenti(){
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
				String[] elemsub = new String[1];
				elemsub[0] = "";
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
		 * matrice con una sola stringa vuota
		 */	
		}catch (SQLException e){
			if (!CreaTabellaStazioni()){
				areaLog.setText("Connessione non riuscita " + e.getMessage());
			}
			String[] elemsub = new String[1];
			elemsub[0] = "";
			return elemsub;
		}catch (ClassNotFoundException e){
			areaLog.setText("Caricamento driver fallito " + e.getMessage());
			String[] elemsub = new String[1];
			elemsub[0] = "";
			return elemsub;
		}catch (Exception e){
			areaLog.setText("Errore: " + e.getMessage());
			String[] elemsub = new String[1];
			elemsub[0] = "";
			return elemsub;
		}	
	}
	/**
	 * Crea la tabella <b>Stazioni</b> del database.
	 * <br>Viene invocato nel caso si verifichi un'eccezione di tipo <code>SQLException</code>
	 * nel metodo {@link #StazioniEsistenti()} per tentare di risolvere l'eccezione.<br>
	 * Tipicamente viene risolta l'eccezione dovuta alla mancanza della tabella stazioni nella
	 * base di dati.
	 */ 
	private boolean CreaTabellaStazioni(){
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
			 * creazione del comando 
			 */
			Statement comando = connessione.createStatement();
			if (comando.executeUpdate("CREATE TABLE Stazioni (Stazione VARCHAR(30));") != 0){
				return true;
			}else{
				return false;
			}	
		/*
		 * gestione esplicita delle eccezioni
		 * viene ritornato un valore false
		 */	
		}catch (Exception e){
			return false;
		}
	}	
	/**
	 * Legge lo stato dell'applicazione memorizzato in un file.
	 * <br>Va a leggere nel file "meteo.txt" della cartella corrente e restituisce 
	 * una stringa contenente il nome dell'ultima stazione selezionata della precedente
	 * sessione oppure una stringa vuota se il file non è presente.
	 * @return ultima stazione selezionata nella precedente sessione
	 */
	private String LeggiStato(){
		try{
			/*
			 * ricerca del file nella directory corrente
			 * se il file non è presente viene lanciata l'eccezione IOException
			 * gestita successivamente
			 */
			String inputFileName = System.getProperty("user.dir") + 
												File.separatorChar + "meteo.txt";
			/*
			 * creazione del file e del flusso di input
			 */
			File inputFile = new File(inputFileName);
			FileInputStream in = new FileInputStream(inputFile);
			byte bytein[] = new byte[(int)inputFile.length()];
			/*
			 * lettura dal file
			 */
			in.read(bytein);
			String stato = new String(bytein);
			/*
			 * chiusura del file
			 */
			in.close();
			/*
			 * restituzione stringa
			 */
			return stato;
		/*
		 * gestione dell'eccezione
		 */											
		}catch (java.io.IOException e){
			/*
			 * fa ritornare una stringa vuota
			 */
			String stato = new String("");
			return stato;
		}
	}	
	/**
	 * Scrive lo stato dell'applicazione in un file.
	 * <br>Viene eseguito subito prima l'uscita dal programma e va a scrivere nel file
	 * "meteo.txt" della cartella corrente il nome della statione selezionata.
	 */
	private void ScriviStato(String stato){
		try{
			byte bytetext[] = stato.getBytes();
			/*
			 * ricerca del file nella directory corrente
			 */
			String outputFileName = System.getProperty("user.dir") + 
												File.separatorChar + "meteo.txt";
			/*
			 * creazione, scrittura e chiusura del file
			 */
			FileOutputStream out = new FileOutputStream(outputFileName);
			out.write(bytetext);
			out.close();
		/*
		 * gestione dell'eccezione
		 * se si verifica un'eccezione non si esegue nessuna operazione correttiva
		 * in quanto la mancata scrittura della stato non comporta nessuna
		 * modifica dell'attività compiuta abitualmente dal programma
		 */
		}catch (java.io.IOException e){}
	}
	/**
	 * Elimina la stazione selezionata e la relativa tabella dal database.
	 * @param 	station		Nome della stazione da eliminare
	 */
 	protected void EliminaDalDB(String station){
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
			 * creazione del comando 
			 */
			Statement comando = connessione.createStatement();
			if ((comando.executeUpdate("DROP TABLE " + station +";") != 0)
					&& (comando.executeUpdate("DELETE FROM Stazioni WHERE Stazione IN ('" + station + "');") != 0)){
				JOptionPane.showMessageDialog(null, "Stazione di " + station + " eliminata.");
				/*
				 * invocazione del metodo Log per scrivere nell'area di testo
				 */
				Log(""); 
				Log("Stazione di " + station + " eliminata.");

				CreaMenu();
				setVisible(true);	
			}else{
				JOptionPane.showMessageDialog(null, "La stazione di " + station + " è inesistente.");
			}	
		/*
		 * gestione esplicita delle eccezioni
		 * nessuna azione correttiva intrapresa di fronte ad eventuali eccezioni
		 * visualizzazione di un messaggio
		 */	
		}catch (Exception e){
			JOptionPane.showMessageDialog(null, "Problemi di connessione al database, riprovare.");
			Log(e.getMessage());
		}
		
	}
	/**
	 * Crea una nuova stazione e la relativa tabella nel database.
	 */
	private void NuovaStazione(){
		/*
		 * creazione del messaggio di opzione
		 */
		Object[] opzioni = {"Ok","Annulla"};
		String nuova = JOptionPane.showInputDialog(null, "Inserire il nome della nuova stazione.",
													"Nuova Stazione",
													JOptionPane.PLAIN_MESSAGE);
		if ((nuova != null) && !(nuova.equals(""))){
			String[] esistenti = StazioniEsistenti();
			/*
			 * verifica se la stazione è già esistente o meno all'interno del DB
			 */
			if (!esistenti[0].equals("")){
				for (int i=0; i < esistenti.length; i++){
					if (esistenti[i].equalsIgnoreCase(nuova)){
						JOptionPane.showMessageDialog(null, "La stazione è già esistente.");
						return;
					}	
				}
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
				 * creazione del comando 
				 */
				Statement comando = connessione.createStatement();
				if ((comando.executeUpdate("INSERT INTO Stazioni VALUES ('" + nuova + "');") != 0) 
				    	&& (comando.executeUpdate("CREATE TABLE " + nuova + 
				    	" (Data DATE, Ora TIME, Temperatura SMALLINT, Umidita SMALLINT, Pressione SMALLINT, Precipitazioni SMALLINT);") !=0)){
				    JOptionPane.showMessageDialog(null, "Stazione di " + nuova + " inserita.");
					ScriviStato(nuova);
					CreaMenu();
					setVisible(true);	
				}else{
					JOptionPane.showMessageDialog(null, "La stazione " + nuova + " non è stata inserita.");
				}	
			/*
			 * gestione esplicita delle eccezioni
			 * nessuna azione correttiva intrapresa di fronte ad eventuali eccezioni
			 * visualizzazione di un messaggio
			 */	
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "Problemi di connessione al database, riprovare.");
				Log(e.getMessage());
			}
		}
	}
	/**
	 * Inserisce i dati impostati nell'interfaccia nel database.
	 * @param 	stazione		Stazione nella quale inserire i dati.
	 * @param	data			Data della rilevazione.
	 * @param	ora				Ora della rilevazione.
	 * @param	temperatura		Temperatura in gradi centigradi rilevata.
	 * @param	umidita			Umidità in percentuale rilevata.
	 * @param	pressione		Pressione in millibar rilevata.
	 * @param	precipitazioni	Precipitazioni in millimetri rilevate.
	 */	
	private void InserisciDati(String stazione, String data, String ora, int temperatura, int umidita, int pressione, int precipitazioni){
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
			 * creazione del comando 
			 */
			Statement comando = connessione.createStatement();
			if (comando.executeUpdate("INSERT INTO " + stazione + " VALUES ('" + data + "', '" + ora + "', " + temperatura 
					+ ", " + umidita + ", " + pressione + ", " + precipitazioni + ");") != 0){
				Log("");
				Log("Dati inseriti:");
				Log("Data \t Ora \t Temperatura \t Umidità \t Pressione \t Precipitazioni");
				Log(data + "\t" + ora + "\t" + temperatura + "\t" + umidita + "\t" + pressione + "\t"  + precipitazioni);
				Log("---------------------------------------------------------------------------------------------------------------------------------");
			}else{
				JOptionPane.showMessageDialog(null, "Problemi di connessione al database, nessun dato inserito.");
				
			}	
		/*
		 * gestione esplicita delle eccezioni
		 * nessuna azione correttiva intrapresa di fronte ad eventuali eccezioni
		 * visualizzazione di un messaggio
		 */	
		}catch (Exception e){
			JOptionPane.showMessageDialog(null, "Problemi di connessione al database, nessun dato inserito.");
			Log(e.getMessage());
		}
		
			
	}
	/**
	 * Scrive nell'area di testo posizionata nella parte inferiore della finestra
	 * l'operazione effettuta.
	 * @param log	La stringa da aggiungere in una nuova riga della finestra. Se viene
	 *				passata al metodo una stringa vuota "" il testo precedente viene cancellato 
	 *				dall'area di testo.
	 */	
	private void Log(String log){
		if (!log.equals("")){
			areaLog.append(log + newline);
		}else{
			areaLog.setText("");
		}		
	}			
 	/**
	 * Gestione degli eventi generati dalla selezione delle voci di menu.
	 * @param e	Evento generato dalla selezione delle varie voci.
	 */	
	public void actionPerformed(ActionEvent e) {
	    /*
	     * crea l'oggetto con la fonte dell'evento
	     */
	    JMenuItem source = (JMenuItem)(e.getSource());
		/*
		 * in base alla voce selezionata invoca i vari metodi
		 */
        if (source.getText() == "Nuova..."){
     		/*
     		 * invocazione del metodo per creare una nuova stazione
     		 */
     		NuovaStazione();
        }else if (source.getText() == "Esci"){
        	/*
        	 * va a scrivere lo stato (la stazione selezionata) nel file ed esce 
        	 * dall'applicazione
        	 */
        	ScriviStato(ultimaStazione);
        	System.exit(0);
        }else if (source.getText() == "Elimina..."){
			/*
			 * crea la finestra Elimina passandole le stazioni esistenti in questo momento
			 */
			Elimina finElimina = new Elimina(StazioniEsistenti(), this);
	        finElimina.setTitle("Elimina Stazione");
	        finElimina.setSize(350, 160);
	        finElimina.setResizable(false);
	        finElimina.setLocation(320,220);
	        finElimina.setVisible(true);

        }else if (source.getText() == "Guida in linea..."){
        	/*
        	 * crea la finestra della guida
        	 */
        	Help help = new Help();
			help.setTitle("Guida in linea di Stazioni Meteo");
        	help.setLocation(320,220);
        	help.setSize(550,500);
	        help.setVisible(true);

        }else if (source.getText() == "About..."){
        	/*
        	 * crea e visualizza il messaggio con le informazioni sull'applicazione
        	 */
			JOptionPane.showMessageDialog(null, 
							"Stazioni Meteo\nby Gianluca Tavella - gianluca.tavella@libero.it\n" +
							"Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B\n" +
							"Prof. Flavio De Paoli\n" +
							"Università degli Studi di Milano - Bicocca\n" +
							"Facoltà di Economia\n" +
							"Corso di Laurea in Economia e Commercio\n" +
							"Anno Accademico 2000/2001", "About Stazioni Meteo",
							JOptionPane.INFORMATION_MESSAGE);
        }else{
        	/*
        	 * se la voce selezionata è diversa dalle altre vuol dire che è stata
        	 * selezionata una stazione e quindi va a impostarla
        	 */
        	SetStazione(source.getText());
        }				
        	

	}
	/**
	 * Inizio dell'applicazione (<code>main</code>).
	 * <br>Creazione dell'oggetto definito da questa stessa classe e impostazione delle
	 * relative proprietà (dimensioni, posizione).
	 * @param args	Gli argomenti passati dalla riga di comando.
	 *				<br>Esempio<br>
	 *				<pre>
	 *					java StazioniMeteo arg1 arg2 ...
	 *				</pre>
	 *				In questo caso non serve nessun argomento.						 
	 */
	public static void main(String[] args) {
        /* 
         * creazione della finestra definita da questa stessa classe
         */
	    StazioniMeteo window = new StazioniMeteo();
        /*
         * impostazione delle proprietà relative (ereditate da JFrame)
         * alla finestra appena creata
         */
        window.setTitle("Stazioni Meteo");
        window.setSize(550, 380);
        window.setResizable(false);
        window.setLocation(250,150);
        window.setIconImage(new ImageIcon("images/icona.gif").getImage());
        window.setVisible(true);
    }	
}   