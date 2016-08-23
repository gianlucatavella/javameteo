
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe ricerca: servlet per l' impostazione della ricerca in base alla richesta
 * 				   del client
 *
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
/**
 * Servlet per l'impostazione della ricerca dei dati richiesti dal client attraverso il
 * modulo creato dalla servlet {@link form}.
 * <br>Raccoglie la richiesta del client e crea un thread definito dalla classe {@link 
 * ElaborazioneThread} che si preoccuperà di compiere l'elaborazione e la successiva 
 * elaborazione dei dati.
 * La scelta di creare un thread per ogni richiesta dei vari client è data dal fatto che
 * la servlet engine non assicura l'accesso sincronizzato a risorse condivise, in questo
 * caso la risorsa condivisa in questione è la rete. Operando in questo modo ci si assicura
 * che i vari thread non accedano simultaneamente alla rete per invocare il metodo remoto
 * per la ricerca di dati {@link meteo.server.MeteoDati#getMeteoDati(GregorianCalendar,
 * GregorianCalendar, String)} in quanto il metodo è invocabile attraverso il metodo di
 * classe sincronizzato della classe {@link CentroRicerca}.
 * <br>Questa classe implementa l'interfaccia <code>javax.servlet.SingleThreadModel</code> per assicurare 
 * che la servlet risponda solo ad una richesta alla volta. La servlet engine risolve 
 * il problema mantenendo un pool di servlet e inviando ogni nuova richiesta 
 * ad una servlet libera.
 */
/*
 * CLASSE ricerca
 * ESTENDE la classe javax.servlet.http.HttpServlet per la realizzazione della servlet
 * IMPLEMENTA l'interfaccia javax.servlet.SingleThreadModel per assicurare che la servlet
 * 		risponda solo ad una richesta alla volta. La servlet engine risolve il problema
 *		mantenendo un pool di servlet e inviando ogni nuova richiesta a una servlet libera.		
 */
public class ricerca extends HttpServlet implements SingleThreadModel{
	/**
	 * Impostazione dei parametri per la creazione dinamica della pagina Web in base ai risultati
	 * della ricerca impostata nel modulo creato dalla servlet {@link form}.
	 * @param richiesta Informazione passata dal client al <code>service method</code> della
	 * 					Servlet. In questo caso l'intervallo temporale, il tipo, la stazione e
	 *					le operazioni richieste.
	 * @param risposta Risposta che la servlet in esecuzione su un Web server manda al client
	 *                 attraverso il protocollo HTTP. In questo caso la risposta è una pagina in
	 *                 formato Html.
	 */
    public void doPost(HttpServletRequest richiesta, HttpServletResponse risposta) 
    					throws IOException, ServletException{
    	////////////////////////////////////////
    	//// Variabili e oggetti del metodo	////				
    	////////////////////////////////////////
    	GregorianCalendar dataOra = new GregorianCalendar();
		GregorianCalendar calIN = new GregorianCalendar();
		GregorianCalendar calFIN = new GregorianCalendar();
		boolean media = false;
		boolean massima = false;
		boolean minima = false;
		boolean precipitazioni = false;
		boolean errore;
		String stazione;
		String tipo;
		String dataIniziale;
		String dataFinale;
		String strMediaValore = "Media";
		/*
		 * inizializzazione dell'oggetto con la data iniziale
		 */
		calIN.set(Integer.parseInt(richiesta.getParameter("annoIniziale")),
				  Integer.parseInt(richiesta.getParameter("meseIniziale")) - 1,
				  Integer.parseInt(richiesta.getParameter("giornoIniziale")));
		/*
		 * inizializzazione dell'oggetto con la data finale
		 */
		calFIN.set(Integer.parseInt(richiesta.getParameter("AnnoFinale")),
				   Integer.parseInt(richiesta.getParameter("meseFinale")) - 1,
				   Integer.parseInt(richiesta.getParameter("giornoFinale")));
		/*
		 * inizializzazione delle variabili booleane in base alle operazioni
		 * selezionate nel form
		 */
		if (richiesta.getParameter("media") != null){
			media = true;
		}	
		if (richiesta.getParameter("massima") != null){
			massima = true;
		}	
		if (richiesta.getParameter("minima") != null){
			minima = true;
		}
		/*
		 * inizializzazione delle variabili con la stazione e il tipo di dati 
		 * richiesti in base alla scelta effettuata nel form
		 */
		stazione = richiesta.getParameter("stazione");
		tipo = richiesta.getParameter("tipo");
		/*
		 * inizializzazione delle stringhe con la data finale e quella iniziale
		 * in base alla selezione effettuata nel form
		 */
		dataIniziale = richiesta.getParameter("giornoIniziale") + "/" 
					 + richiesta.getParameter("meseIniziale") + "/"
					 + richiesta.getParameter("annoIniziale");
		dataFinale = richiesta.getParameter("giornoFinale") + "/" 
				   + richiesta.getParameter("meseFinale") + "/"
				   + richiesta.getParameter("AnnoFinale");
		/*
		 * visualizzazione nella console delle varie richieste e della relativa
		 * provenienza
		 */
		System.out.println("  " + dataOra.getTime().toString() +
							 " Richiesta dati su " + stazione + " da " + richiesta.getRemoteAddr()); 
    						
		/*
		 * creazione delle matrici di oggetti per la successiva visualizzazione dei risultati
		 */
		ResultMeteoTabella[] risultati;
		ResultMeteo[] risMMMPeriodo;
		/*
		 * verifica se sia stata selezionata almeno un'operazione, se le date inserite sono
		 * valide e se la data finale è inferiore o uguale alla data finale o meno.
		 * Nel caso si verifichi un errore, la variabile booleana errore che viene passata
		 * successivamente al thread viene impostata su true, sarà poi il thread a preoccuparsi
		 * di visualizzare il messaggio di errore.
		 */
		if ((media == false) && (massima == false) && (minima == false)){
			risultati = new ResultMeteoTabella[1];
			risultati[0] = new ResultMeteoTabella();
			risultati[0].setMedia("non è stata selezionata nessuna operazione. E' necessario selezionare almeno un'operazione.");
			risMMMPeriodo = new ResultMeteo[1];
			risMMMPeriodo[0] = new ResultMeteo("","",0);
			errore = true;
		}else if (!(VerificaData(Integer.parseInt(richiesta.getParameter("giornoIniziale")),
					Integer.parseInt(richiesta.getParameter("meseIniziale")),
					Integer.parseInt(richiesta.getParameter("annoIniziale")))
					&& VerificaData(Integer.parseInt(richiesta.getParameter("giornoFinale")),
					Integer.parseInt(richiesta.getParameter("meseFinale")),
					Integer.parseInt(richiesta.getParameter("AnnoFinale")))
					&& (calIN.before(calFIN) || calIN.equals(calFIN)))){
			risultati = new ResultMeteoTabella[1];
			risultati[0] = new ResultMeteoTabella();
			risultati[0].setMedia("verificare che le date inserite siano corrette e che la data iniziale dell' intervallo " 
									+ "sia inferiore o uguale alla data finale.");
			risMMMPeriodo = new ResultMeteo[1];
			risMMMPeriodo[0] = new ResultMeteo("","",0);
			errore = true;
		}else{			
			errore = false;
			risultati = new ResultMeteoTabella[1];
			risultati[0] = new ResultMeteoTabella();
			risMMMPeriodo = new ResultMeteo[1];
			risMMMPeriodo[0] = new ResultMeteo("","",0);
						  
    	}
        /*
         * impostazione della risposta
         */
        risposta.setContentType("text/html");
        ServletOutputStream servletout = risposta.getOutputStream();
        PrintWriter out = new PrintWriter(servletout, true);
		/*
		 * creazione dell'oggetto di tipo CentroRicerca per l'invocazione del
		 * metodo remoto
		 */
		CentroRicerca centroricerca = new CentroRicerca();
		/*
		 * Creazione dell'oggetto di tipo ElaborazioneThread in base ai parametri di
		 * ricerca forniti dal client
		 */
		ElaborazioneThread elaborazionethread = new ElaborazioneThread(
													errore,
													centroricerca,
													calIN,
													calFIN,
													tipo,
													stazione,
													media,
													massima,
													minima,
													dataIniziale,
													dataFinale,
													risMMMPeriodo,
													risultati,
													out);
		/*
		 * attivazione dell'oggetto
		 */
		elaborazionethread.run();											


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