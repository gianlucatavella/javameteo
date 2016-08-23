
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Feb 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe Casuale: applicazione con interfaccia grafica per scrivere 
 *                 dei dati casuali generati dall'applicazione nel database
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */ 
/*
 * import dei package necessari
 */
import java.util.*; 
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Finestra con interfaccia grafica per l'inserimento di dati casuali nel database.
 * <br>Vengono generati dati per tutte le stazioni presenti nel database.
 * <br>Il database viene popolato inserendo valori casuali generati dai metodi 
 * {@link #Termometro(double, int, int)}, {@link #Igrometro(int)}, 
 * {@link #Barometro(int)} e {@link #Pluviometro(int)}.
 * <br>L'intervallo tra un dato ed il successivo è di 15 minuti. Ciò significa che vengono
 * inseriti nel database 96 valori al giorno per ogni stazione presente.
 */
/*
 * CLASSE Casuale
 * ESTENDE la classe javax.swing.JFrame per la creazione della finestra e della GUI
 */
class Casuale extends JFrame{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Stazioni esistenti nel database */
	private String[] Esistenti;
	/** Barra di avanzamento per le singole stazioni */
	private JProgressBar parziale;
	/** Barra di avanzamento per l'insieme delle stazioni */
	private JProgressBar totale;
	/** Data iniziale dell'intervallo */
	private GregorianCalendar calIN;
	/** Data finale dell'intervallo */
	private GregorianCalendar calFIN;
	/** Campo di testo per l'inserimento della data iniziale */
	private JTextField inizio;
	/** Campo di testo per l'inserimento della data finale */
	private JTextField fine;
	/** Etichetta per la visualizzazione della stazione corrente */
	private JLabel parz;
	/**
	 * Crea i componenti della finestra e li aggiunge alla stessa.
	 * @param StazioniEsistenti	Stazioni esistenti nella base di dati.
	 */
	/* 
	 * costruttore
	 */
	public Casuale(String[] StazioniEsistenti){
		/* 
		 *invocazione del costruttore della superclasse JFrame
		 */
		super("Generazione casuale");
		/* 
		 * assegnazione all'array Esistenti delle stazioni esistenti passate al 
		 * costruttore della finestra
		 */
		Esistenti = StazioniEsistenti;
        /*
         * gestione dell' evento chiusura
         */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        /*
         * creazione del pannello contenitore e impostazione del 
         * relativo Layout
         */
	 	Container contentPane = getContentPane();
	 	contentPane.setLayout(new GridLayout(5,2));
	 	/*
	 	 * creazione degli oggetti grafici e aggiunti degli stessi alla finestra
	 	 */
	 	Font fancy = new Font("Serif", Font.BOLD, 15);
	 	
	 	JLabel dataIn = new JLabel("Data iniziale (gg/mm/aaaa)");
		dataIn.setFont(fancy);
	 	dataIn.setHorizontalAlignment(dataIn.CENTER);
		contentPane.add(dataIn);

		inizio = new JTextField();
		inizio.setHorizontalAlignment(inizio.CENTER);
		contentPane.add(inizio);

	 	JLabel dataFin = new JLabel("Data finale (gg/mm/aaaa)");
		dataFin.setFont(fancy);
	 	dataFin.setHorizontalAlignment(dataFin.CENTER);
		contentPane.add(dataFin);
		
		fine = new JTextField();
		fine.setHorizontalAlignment(fine.CENTER);
		contentPane.add(fine);

	 	parz = new JLabel("Parziale");
		parz.setFont(fancy);
	 	parz.setHorizontalAlignment(parz.CENTER);
		contentPane.add(parz);
		
		parziale = new JProgressBar();
		parziale.setStringPainted(true);
		contentPane.add(parziale);

	 	JLabel tot = new JLabel("Totale");
		tot.setFont(fancy);
	 	tot.setHorizontalAlignment(tot.CENTER);
		contentPane.add(tot);

		totale = new JProgressBar();
		totale.setStringPainted(true);
		contentPane.add(totale);
	 	
	 	JButton start = new JButton("Genera");
	 	start.setBackground(Color.lightGray);
	 	start.setForeground(Color.blue);
	 	contentPane.add(start);

	 	JButton esci = new JButton("Chiudi");
	 	esci.setBackground(Color.lightGray);
	 	esci.setForeground(Color.blue);
	 	contentPane.add(esci);
		/*
		 * gestione dell'evento associato al pulsante "Chiudi"
		 */
	 	esci.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				setVisible(false);
		 	}	
 		});		
		/*
		 * gestione dell'evento associato al pulsante "Genera"
		 */
	 	start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
		 		try{
					int ggIN = Integer.parseInt(inizio.getText().substring(0, 2));
					int mmIN = Integer.parseInt(inizio.getText().substring(3, 5));
					int aaaaIN = Integer.parseInt(inizio.getText().substring(6, 10));
					int ggFIN = Integer.parseInt(fine.getText().substring(0, 2));
					int mmFIN = Integer.parseInt(fine.getText().substring(3, 5));
					int aaaaFIN = Integer.parseInt(fine.getText().substring(6, 10));
		 			/*
		 			 * verifica della correttezza delle date
		 			 */
		 			if (ggIN > 0 && ggIN < 32 && ggFIN > 0 && ggFIN < 32 && mmIN > 0 
		 					&& mmIN < 13 && mmFIN > 0 && mmFIN < 13 
		 					&& VerificaData(ggIN, mmIN, aaaaIN) 
		 					&& VerificaData(ggFIN, mmFIN, aaaaFIN)){
		 				calIN = new GregorianCalendar();
		 				calFIN = new GregorianCalendar();
		 				calIN.set(aaaaIN, mmIN - 1 , ggIN, 0, 0);
		 				calFIN.set(aaaaFIN, mmFIN - 1, ggFIN, 0, 0);
						calFIN.add(Calendar.DATE, 1);
		 				/*
		 				 * verifica che la prima data dell'intervallo temporale
		 				 * sia inferiore alla seconda
		 				 */
		 				if (calIN.before(calFIN)){
		 					Genera();
		 				}else{
							JOptionPane.showMessageDialog(null, "La data finale deve essere superiore a quella iniziale.");
						}
						
		 			}else{
						JOptionPane.showMessageDialog(null, "Verificare che la date inserite siano corrette.");
					}	 				
		 		}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Verificare che la date inserite siano corrette.");
				}	
		 				
		 	}	
 		});		
 	}
 	/**
 	 * Genera i valori casuali per il periodo specificato.
 	 */
	private void Genera(){
		int giorni=0;
		/* 
		 * conta i giorni
		 */
		while (calIN.before(calFIN)){
			calIN.add(Calendar.DATE, 1);
			giorni++;
		}
		calIN.add(Calendar.DATE, -giorni);
		/*
		 * inizializzazione delle barre di avanzamento
		 */
		parziale.setMinimum(0);
		parziale.setMaximum(giorni*95);
		totale.setMinimum(0);
		totale.setMaximum(giorni * 95 * Esistenti.length);
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
			/*
			 * definizione variabili
			 */
			double varTem;
			int temperatura;
			int umidita = 10 + (int)(Math.random()*90);
			int pressione = 990 + (int)(Math.random()*40);
			int precipitazioni;
			String data;
			String ora;
			totale.setValue(0);
			/*
			 * ciclo che ripete le operazioni per tutte le stazioni esistenti
			 */
			for (int j=0; j < Esistenti.length; j++){
				parz.setText(Esistenti[j]);
				parziale.setValue(0);
				/*
				 * inizilizzazione della variabile varTem in base alla stagione corrente
				 */
				varTem = Stagione(calIN.get(Calendar.MONTH) + 1);
				/*
				 * per ogni stazione, ogni quarto d'ora vengono inseriti i valori
				 * casuali nel database
				 */
				for (int k=0; k < giorni*96; k++){
					/*
					 * invocazione del metodo Termometro per la variazione casuale della
					 * temperatura
					 */
					varTem = Termometro(varTem, calIN.get(Calendar.HOUR_OF_DAY), calIN.get(Calendar.MONTH) + 1);
					temperatura = (int)(varTem); 
					/*
					 * invocazione del metodo Igrometro per la variazione dell'umidità
					 */
					umidita = Igrometro(umidita);
					/*
					 * invocazione del metodo Barometro per la variazione della pressione
					 */
					pressione = Barometro(pressione);
					/*
					 * invocazione del metodo Pluviometro per l' eventuale generezione del
					 * livello delle precipitazioni
					 */
					precipitazioni = Pluviometro(calIN.get(Calendar.MONTH) + 1);
					/*
					 * impostazione delle stringhe contenenti la data e l'ora
					 */
					data = String.valueOf(calIN.get(Calendar.DAY_OF_MONTH)) + "/" + 
								String.valueOf(calIN.get(Calendar.MONTH) + 1) + "/" +
								String.valueOf(calIN.get(Calendar.YEAR));
					ora = String.valueOf(calIN.get(Calendar.HOUR_OF_DAY)) + "." +
							String.valueOf(calIN.get(Calendar.MINUTE));			
					   
					comando.executeUpdate("INSERT INTO " + Esistenti[j] + " VALUES ('" + data + "', '" + ora + "', " + temperatura 
							+ ", " + umidita + ", " + pressione + ", " + precipitazioni + ");");
					
					calIN.add(Calendar.MINUTE, 15);
					/*
					 * aggiornamento delle barre di avanzamento
					 */
					Barre(k, k *(j+1));


				}
				/*
				 * sottrazione dei giorni al calendario per ritornare alla data iniziale
				 * da usare per l'inserimento di dati nella prossima stazione
				 */
				calIN.add(Calendar.DATE, -giorni);
				
			}		
						
		/*
		 * gestione esplicita delle eccezioni
		 * nessuna azione correttiva intrapresa di fronte ad eventuali eccezioni
		 * visualizzazione di un messaggio
		 */	
		}catch (Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
			
			
	}
	/**
	 * Fa variare casualmente la temperatura.
	 * Nell'arco della giornata la temperatura aumenta dalle 8:00 alle 20:00 mentre 
	 * diminuisce nella restante parte della giornata. Ciò è reso possibile utilizzando
	 * la funzione periodica <b>sin(x)</b> e impostando come argomento del seno un'opportuna 
	 * trasformazione lineare dell'ora del giorno.
	 * L'entità dell'incremento/decremento varia a seconda del mese in corso.
	 * @param temperatura	Temperatura precedente.
	 * @param ora			Ora corrente.
	 * @param mese			Mese corrente.
	 * @return La temperatura precedente più una variazione (anche negativa) della stessa.
	 */
	private double Termometro(double temperatura, int ora, int mese){
		int stag = 0;
		double arg;
		/*
		 * fa variare l'ora se è inferiore a 6 per la successiva funzione
		 */
		if (ora < 6){
			ora+=24;
		}
		/*
		 * imposta l'argomento del seno in base ad una traslazione 
		 * e ad una trasformazione lineare dell'ora corrente per
		 * far variare la temperatura periodicamente all'interno della
		 * giornata, in questo caso la temperatura aumenta dalle otto del
		 * mattino alle otto di sera mentre diminuisce nella restante parte della
		 * giornata
		 */
		arg = ((ora *(1.5) - 9) / 18) * Math.PI;
		/*
		 * assegna un diverso coefficiente all'incremento/decremento in base al
		 * mese corrente, tipicamente, si presume che la temperatura vari di più
		 * in estate e meno in inverno
		 */	
		if (mese > 0 && mese < 4){
			stag = 8;
		}else if (mese > 3 && mese < 7){
			stag = 10;
		}else if (mese > 6 && mese < 10){
			stag = 12;
		}else if (mese > 9 && mese < 13){
			stag = 9;
		}
		/*
		 * funzione di calcolo della variazione di temperatura
		 */			 	 
		return (temperatura + Math.sin(arg) * Math.random() * stag / 10);
	}
	/**
	 * Fa variare casualmente l'umidità.
	 * La variazione è del tutto casuale: Il segno viene determinato in base ad
	 * una funzione del tipo 
	 * <pre>
	 *	            n
	 *	        (-1)
	 * </pre>
	 * con <b>n</b> intero casuale, anche l'entità della variazione è casuale e varia da
	 * 0 a 3.<br>
	 * Avviene anche un controllo per stabilire se l'umidità è all'interno del suo range
	 * (0-100).
	 * @param umidita	Umidità precedente.
	 * @return L'umidità precedente più una variazione (anche negativa).
	 */
	private int Igrometro(int umidita){
		/*
		 * funzione per far variare l'umidità
		 * incrementa o decrementa in modo del tutto casuale in base ad una funzione
		 * del tipo (-1)^n con n casuale, l'entità della variazione è anch'essa casuale 
		 */
		umidita += (int)((Math.pow(-1, ((int)(Math.random()*10))) * Math.random() * 3));
		/*
		 * controlla che l'umidità sia compresa tra i due estremi dell'intervallo
		 */
		if (umidita >= 100){
			umidita = 100;
		}else if (umidita <= 1){
			umidita = 1;
		}		
		return umidita; 
	
	}
	/**
	 * Fa variare casualmente la pressione.
	 * La variazione è del tutto casuale: Il segno viene determinato in base ad
	 * una funzione del tipo 
	 * <pre>
	 *	            n
	 *	        (-1)
	 * </pre>
	 * con <b>n</b> intero casuale, anche l'entità della variazione è casuale e varia da
	 * 0 a 3.<br>
	 * Avviene anche un controllo per stabilire se la pressione è all'interno del suo range.
	 * @param pressione	Pressione precedente.
	 * @return La pressione precedente più una variazione (anche negativa).
	 */
	private int Barometro(int pressione){
		/*
		 * funziona in modo del tutto analogo al metodo Umidita
		 */
		pressione += (int)((Math.pow(-1, ((int)(Math.random()*10))) * Math.random() * 3));
		if (pressione >= 1030){
			pressione = 1030;
		}else if (pressione <= 990){
			pressione = 990;
		}		
		return pressione; 
	
	}
	/**
	 * Genera l'eventuale precipitazione.
	 * <br>In base al mese in corso viene assegnata una diversa probabilità di precipitazione.
	 * @param mese	Mese in corso.
	 * @return	Eventuale precipitazione.
	 */
	private int Pluviometro(int mese){
		double probabilita = 0;
		/*
		 * assegna una probabilità di pioggia in base al mese in questione
		 */
		switch (mese) {
            case 3:
            case 4:
            case 10:
            case 11:
                probabilita = 0.05;
                break;
            case 1:
            case 2:
            case 12:
            	probabilita = 0.01;
            	break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            	probabilita = 0.02;
                break;
        }
        /*
         * verifica se piove o meno in base alla probabilità stabilita nello switch
         * precedente
         */
        if (Math.random() < probabilita){
        	/*
        	 * fa ritornare un valore casuale compreso tra 1 e 5
        	 */
        	return (1 + (int)(Math.random()*5));
        }else{
        	/*
        	 * fa ritornare un valore pari a 0
        	 */
        	return 0;
        }		
			
	}	
	/**
	 * In base al mese in corso viene fatto ritornare un valore per impostare la temperatura
	 * iniziale.
	 * @param mese Mese in corso.
	 * @return Valore della temperatura previsto.
	 */	
	private double Stagione(int mese){
		if (mese > 0 && mese < 4){
			return -2;
		}else if (mese > 3 && mese < 7){
			return 5;
		}else if (mese > 6 && mese < 10){
			return 13;
		}else{
			return 3;
		}			 	 
	}				
	/**
	 * Aggiorna le barre di avanzamento.
	 * @param par	Valore della barra {@link #parziale}
	 * @param tot 	Valore della barra {@link #totale}
	 */
	private void Barre(int par, int tot){
		parziale.setValue(par);
		totale.setValue(tot);
		
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
	 	
}	 	

		