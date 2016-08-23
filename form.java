
/* ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
   ø  by Gianluca Tavella - gianluca.tavella@libero.it                       ø
   ø  Mar 2001                                                               ø
   ø  Progetto per l'esame di Sistemi di Elaborazione dell'Informazione B    ø
   ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø ø
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Classe form: servlet per la creazione dinamica del form di ricerca
 *				in base alle stazioni presenti nel database ed agli anni
 *				in cui sono disponibili i dati
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*
 * import dei package necessari
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.util.*;
/**
 * Servlet per la creazione dinamica del modulo HTML di ricerca.
 * <br>Il modulo viene creato in base alle stazioni presenti nel database e all'intervallo
 * temporale dei dati contenuti nel database.<br>
 * Le stazioni presenti nel database vengono fatte tornare dal metodo remoto {@link
 * meteo.server.StazioniEsistenti#getStazioniEsistenti()} dell' interfaccia {@link 
 * meteo.server.StazioniEsistenti} mentre gli anni in cui i dati
 * sono disponibili dal metodo remoto {@link meteo.server.Anni#getAnniDisponibili(String)} dell'
 * interfaccia {@link meteo.server.Anni}.<br>
 * I riferimenti agli oggetti remoti vengono fatti tornare dal metodo 
 * <code>java.rmi.Naming.lookup(url)</code>.
 * <br>Questa classe implementa l'interfaccia <code>javax.servlet.SingleThreadModel</code>
 * per assicurare che la servlet risponda solo ad una richesta alla volta. 
 * La servlet engine risolve il problema mantenendo un pool di servlet e inviando 
 * ogni nuova richiesta ad una servlet libera.
 */
/*
 * CLASSE form
 * ESTENDE la classe javax.servlet.http.HttpServlet per la realizzazione della servlet
 * IMPLEMENTA l'interfaccia javax.servlet.SingleThreadModel per assicurare che la servlet
 * 		risponda solo ad una richesta alla volta. La servlet engine risolve il problema
 *		mantenendo un pool di servlet e inviando ogni nuova richiesta a una servlet libera.		
 */
public class form extends HttpServlet implements SingleThreadModel{
	//////////////////////////////////////
	//// Variabili e oggetti di classe ///
	//////////////////////////////////////
	/** Costante con l'indirizzo del server remoto.
	 *  <br>Esempi 
	 *  <pre>
	 *  "rmi://123.123.123.123/"
	 *  "rmi://localhost/" 
	 *  </pre>
	 */
	private final static String url = "rmi://localhost/";
	/**
	 * Imposta l'<code>RMISecurityManager</code> per l'invocazione di metodi remoti attraverso
	 * RMI.
	 */
	public form(){
    	System.setSecurityManager (new RMISecurityManager());
	}
	/**
	 * Creazione dinamica della pagina web in base alle stazioni esistenti nel database ed
	 * in base agli anni in cui sono disponibili i dati.
	 * @param richiesta Informazione passata dal client al <code>service method</code> della
	 * 					Servlet. In questo caso non viene passata nessuna informazione.
	 * @param risposta Risposta che la servlet in esecuzione su un Web server manda al client
	 *                 attraverso il protocollo HTTP. In questo caso la risposta è una pagina in
	 *                 formato Html.
	 */

    public void doGet(HttpServletRequest richiesta, HttpServletResponse risposta) 
    					throws IOException, ServletException{
		/*
		 * visualizzazione nella console delle varie richieste di caricamento del
		 * form di ricerca e della relativa provenienza
		 */
		String stazioneCorrente = richiesta.getParameter("stazione");
    	GregorianCalendar dataOra = new GregorianCalendar();
		System.out.println("  " + dataOra.getTime().toString() +
							 " Caricamento modulo per " + richiesta.getRemoteAddr()); 
		String[] stazioni;
		int[] anni;
		try{
			/*
			 * viene creato un oggetto StazioniEsistenti attraverso il metodo 
			 * java.rmi.Naming.lookup(url) che fa ritornare un riferimento all'oggetto
			 * remoto.
			 */
			StazioniEsistenti stazioniRem = (StazioniEsistenti) Naming.lookup(url + "Esistenti");
			/*
			 * invocazione del metodo remoto getStazioniEsistenti
			 */
			stazioni = stazioniRem.getStazioniEsistenti();
			if (stazioneCorrente.equalsIgnoreCase("first")){
				
				stazioneCorrente = stazioni[0];
				
			}
			/*
			 * viene creato un oggetto Anni attraverso il metodo 
			 * java.rmi.Naming.lookup(url) che fa ritornare un riferimento all'oggetto
			 * remoto.
			 */
			Anni anniRem = (Anni) Naming.lookup(url + "Anni");
			/*
			 * invocazione del metodo remoto getAnniDisponibili
			 */
			anni = anniRem.getAnniDisponibili(stazioneCorrente);
			
		}catch (Exception e){
			// gestioni errori
			stazioni = new String[2];
			anni = new int[1];
			anni[0] = 0;
			stazioni[0] = null;
			stazioni[1] = e.getMessage();
		}
		
    	/*
    	 * impostazione della risposta nel formato HTML
    	 */				
		risposta.setContentType("text/html");
        ServletOutputStream servletout = risposta.getOutputStream();
        PrintWriter out = new PrintWriter(servletout, true);
 	  	/*
    	 * creazione della risposta HTML
    	 */				
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Meteo Seeker - Progetto di Sistemi di Elaborazione dell'Informazione B</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.println("<script language=\"JavaScript\">");
		out.println("<!--");
		out.println("function MM_reloadPage(init) {  //reloads the window if Nav4 resized");
		out.println("  if (init==true) with (navigator) {if ((appName==\"Netscape\")&&(parseInt(appVersion)==4)) {");
		out.println("    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}");
		out.println("  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();");
		out.println("}");
		out.println("MM_reloadPage(true);");
		out.println("// -->");
		out.println("</script>");
		out.println("<SCRIPT LANGUAGE=\"JavaScript\">");
		out.println("function Riapri(form)");
		out.println("{");
		out.println("  var svalue;");
		out.println("  svalue = form.elements[0].options[form.elements[0].selectedIndex].value;");
		out.println("  sGao=\"form?stazione=\" + svalue;");
		out.println("  open(sGao,\"_self\");");
		out.println("}");
		out.println("</SCRIPT>");
		out.println("");
		out.println("<link rel=\"stylesheet\" href=\"../../stile.css\" type=\"text/css\">");
		out.println("</head>");
		out.println("");
		out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" background=\"../../images/sfondo.gif\">");
		out.println("<table width=\"623\" border=\"0\" align=\"center\" height=\"877\" bgcolor=\"#CCCCFF\" cellspacing=\"0\" bordercolor=\"#CCCCFF\" cellpadding=\"0\">");
		out.println("  <tr bgcolor=\"#0000ff\"> ");
		out.println("    <td height=\"114\" width=\"10\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("    <td height=\"114\" width=\"604\"> ");
		out.println("      <div align=\"center\"><img src=\"../../images/titolo.jpg\" width=\"600\" height=\"120\"></div>");
		out.println("    </td>");
		out.println("    <td height=\"114\" width=\"9\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("  </tr>");
		out.println("  <tr valign=\"bottom\" bgcolor=\"#0000ff\"> ");
		out.println("    <td height=\"20\" width=\"10\" bordercolor=\"#CCCCFF\">&nbsp;</td>");
		out.println("    <td height=\"20\" width=\"604\" bordercolor=\"#CCCCFF\"><img src=\"../../images/ricerca.jpg\" width=\"608\" height=\"60\" usemap=\"#Map\" border=\"0\"></td>");
		out.println("    <td height=\"20\" width=\"9\" bordercolor=\"#CCCCFF\">&nbsp;</td>");
		out.println("  </tr>");
		out.println("  <tr bordercolor=\"#FFFFFF\"> ");
		out.println("    <td width=\"10\" bgcolor=\"#0000FF\" height=\"792\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("    <td width=\"604\" valign=\"top\" height=\"792\"> ");
		out.println("      <p align=\"center\">&nbsp;</p>");
		/*
		 * Verifica della presenza di errori
		 */
		if (stazioni[0] == null){
			out.println(" <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\">Si è ");
			out.println("        verificato un errore, impossibile visualizzare il modulo di ricerca. <br>");
			out.println("        Errore dovuto a <br>" + stazioni[1] + "</font></b></font><br>");
			out.println("      </p>");
		}else{
			String disabilitaOpzioni = "";
			if (anni[0] == -10000){
				out.println("      <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\">Si è ");
				out.println("        verificato un errore, non sono presenti dati riguardanti la stazione selezionata.<BR> ");
				out.println("        Selezionare un'altra stazione.</font></b></font><br>");
				out.println("      </p>");
				disabilitaOpzioni = "DISABLED";
				anni[0] = 0;
				anni[1] = 0;
			}else{	
				out.println("      <p align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\"><b><font color=\"#FF6633\">Per ");
				out.println("        ricercare tra dati nell'archivio storico compilare il modulo seguente ");
				out.println("        specificando i criteri di ricerca selezionabili.</font></b></font><br>");
				out.println("      </p>");
			}
			out.println("      <form name=\"ricerca\" method=\"post\" action=\"ricerca\">");
			out.println("        <table width=\"606\" border=\"0\" height=\"177\" cellpadding=\"0\" cellspacing=\"10\">");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"><b>Stazione ");
			out.println("                di rilevamento</b> </font></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"> ");
			out.println("                <select name=\"stazione\" OnChange=\"Riapri(this.form)\">");
			for (int i=0; i < stazioni.length; i++){
				if (stazioneCorrente.equalsIgnoreCase(stazioni[i])){
					out.println("                  <option value=\""+ stazioni[i] + "\" SELECTED>" + stazioni [i] + "</option>");
				}else{
					out.println("                  <option value=\""+ stazioni[i] + "\">" + stazioni [i] + "</option>");
				}					
			}
			out.println("                </select>");
			out.println("                </font></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\">&nbsp;</td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"><b>Intervallo ");
			out.println("                temporale </b></font></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#CC0099\">Data ");
			out.println("                iniziale</font><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"> ");
			out.println("                </font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"> ");
			out.println("                <select name=\"giornoIniziale\" " + disabilitaOpzioni + ">");
			out.println("                  <option value=\"01\" selected>01</option>");
			out.println("                  <option value=\"02\">02</option>");
			out.println("                  <option value=\"03\">03</option>");
			out.println("                  <option value=\"04\">04</option>");
			out.println("                  <option value=\"05\">05</option>");
			out.println("                  <option value=\"06\">06</option>");
			out.println("                  <option value=\"07\">07</option>");
			out.println("                  <option value=\"08\">08</option>");
			out.println("                  <option value=\"09\">09</option>");
			out.println("                  <option value=\"10\">10</option>");
			out.println("                  <option value=\"11\">11</option>");
			out.println("                  <option value=\"12\">12</option>");
			out.println("                  <option value=\"13\">13</option>");
			out.println("                  <option value=\"14\">14</option>");
			out.println("                  <option value=\"15\">15</option>");
			out.println("                  <option value=\"16\">16</option>");
			out.println("                  <option value=\"17\">17</option>");
			out.println("                  <option value=\"18\">18</option>");
			out.println("                  <option value=\"19\">19</option>");
			out.println("                  <option value=\"20\">20</option>");
			out.println("                  <option value=\"21\">21</option>");
			out.println("                  <option value=\"22\">22</option>");
			out.println("                  <option value=\"23\">23</option>");
			out.println("                  <option value=\"24\">24</option>");
			out.println("                  <option value=\"25\">25</option>");
			out.println("                  <option value=\"26\">26</option>");
			out.println("                  <option value=\"27\">27</option>");
			out.println("                  <option value=\"28\">28</option>");
			out.println("                  <option value=\"29\">29</option>");
			out.println("                  <option value=\"30\">30</option>");
			out.println("                  <option value=\"31\">31</option>");
			out.println("                </select>");
			out.println("                <select name=\"meseIniziale\" " + disabilitaOpzioni + ">");
			out.println("                  <option value=\"1\" selected>Gennaio</option>");
			out.println("                  <option value=\"2\">Febbraio</option>");
			out.println("                  <option value=\"3\">Marzo</option>");
			out.println("                  <option value=\"4\">Aprile</option>");
			out.println("                  <option value=\"5\">Maggio</option>");
			out.println("                  <option value=\"6\">Giugno</option>");
			out.println("                  <option value=\"7\">Luglio</option>");
			out.println("                  <option value=\"8\">Agosto</option>");
			out.println("                  <option value=\"9\">Settembre</option>");
			out.println("                  <option value=\"10\">Ottobre</option>");
			out.println("                  <option value=\"11\">Novembre</option>");
			out.println("                  <option value=\"12\">Dicembre</option>");
			out.println("                </select>");
			out.println("                <select name=\"annoIniziale\" " + disabilitaOpzioni + ">");
			for (int i = anni[0]; i <= anni [1]; i++){
				out.println("                  <option value=\"" + i + "\">" + i + "</option>");
			}	
			out.println("                </select>");
			out.println("                </font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"><font color=\"#CC0099\">Data ");
			out.println("                Finale</font> </font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\"> ");
			out.println("                <select name=\"giornoFinale\" " + disabilitaOpzioni + ">");
			out.println("                  <option value=\"01\" selected>01</option>");
			out.println("                  <option value=\"02\">02</option>");
			out.println("                  <option value=\"03\">03</option>");
			out.println("                  <option value=\"04\">04</option>");
			out.println("                  <option value=\"05\">05</option>");
			out.println("                  <option value=\"06\">06</option>");
			out.println("                  <option value=\"07\">07</option>");
			out.println("                  <option value=\"08\">08</option>");
			out.println("                  <option value=\"09\">09</option>");
			out.println("                  <option value=\"10\">10</option>");
			out.println("                  <option value=\"11\">11</option>");
			out.println("                  <option value=\"12\">12</option>");
			out.println("                  <option value=\"13\">13</option>");
			out.println("                  <option value=\"14\">14</option>");
			out.println("                  <option value=\"15\">15</option>");
			out.println("                  <option value=\"16\">16</option>");
			out.println("                  <option value=\"17\">17</option>");
			out.println("                  <option value=\"18\">18</option>");
			out.println("                  <option value=\"19\">19</option>");
			out.println("                  <option value=\"20\">20</option>");
			out.println("                  <option value=\"21\">21</option>");
			out.println("                  <option value=\"22\">22</option>");
			out.println("                  <option value=\"23\">23</option>");
			out.println("                  <option value=\"24\">24</option>");
			out.println("                  <option value=\"25\">25</option>");
			out.println("                  <option value=\"26\">26</option>");
			out.println("                  <option value=\"27\">27</option>");
			out.println("                  <option value=\"28\">28</option>");
			out.println("                  <option value=\"29\">29</option>");
			out.println("                  <option value=\"30\">30</option>");
			out.println("                  <option value=\"31\">31</option>");
			out.println("                </select>");
			out.println("                <select name=\"meseFinale\" " + disabilitaOpzioni + ">");
			out.println("                  <option value=\"1\" selected>Gennaio</option>");
			out.println("                  <option value=\"2\">Febbraio</option>");
			out.println("                  <option value=\"3\">Marzo</option>");
			out.println("                  <option value=\"4\">Aprile</option>");
			out.println("                  <option value=\"5\">Maggio</option>");
			out.println("                  <option value=\"6\">Giugno</option>");
			out.println("                  <option value=\"7\">Luglio</option>");
			out.println("                  <option value=\"8\">Agosto</option>");
			out.println("                  <option value=\"9\">Settembre</option>");
			out.println("                  <option value=\"10\">Ottobre</option>");
			out.println("                  <option value=\"11\">Novembre</option>");
			out.println("                  <option value=\"12\">Dicembre</option>");
			out.println("                </select>");
			out.println("                <select name=\"AnnoFinale\" " + disabilitaOpzioni + ">");
			for (int i = anni[0]; i <= anni [1]; i++){
				out.println("                  <option value=\"" + i + "\">" + i + "</option>");
			}	
			out.println("                </select>");
			out.println("                </font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\">&nbsp;</td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\">Variabile");
			out.println("                </font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"> ");
			out.println("                <input type=\"radio\" name=\"tipo\" value=\"temperatura\" checked " + disabilitaOpzioni + ">");
			out.println("                <b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#333399\">Temperatura</font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <div align=\"left\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#333399\"> ");
			out.println("                <input type=\"radio\" name=\"tipo\" value=\"umidita\" " + disabilitaOpzioni + ">");
			out.println("                Umidit&agrave;</font></b></div>");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#333399\"> ");
			out.println("              <input type=\"radio\" name=\"tipo\" value=\"pressione\" " + disabilitaOpzioni + ">");
			out.println("              Pressione</font></b></td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#333399\"> ");
			out.println("              <input type=\"radio\" name=\"tipo\" value=\"precipitazioni\" " + disabilitaOpzioni + ">");
			out.println("              Precipitazioni </font></b></td>");
			out.println("          </tr>");
			out.println("          <tr>");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\">&nbsp;</td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\"> ");
			out.println("              <div align=\"right\"><b><font face=\"Geneva, Arial, Helvetica, san-serif\" color=\"#993366\">Operazioni</font></b></div>");
			out.println("            </td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <input type=\"checkbox\" name=\"media\" value=\"media\" checked " + disabilitaOpzioni + ">");
			out.println("              <b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#336633\">Media</font></b></td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#333399\"> ");
			out.println("              <input type=\"checkbox\" name=\"massima\" value=\"massima\" " + disabilitaOpzioni + ">");
			out.println("              </font><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#336633\">Massima</font></b></td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#336633\"> ");
			out.println("              <input type=\"checkbox\" name=\"minima\" value=\"minima\" " + disabilitaOpzioni + ">");
			out.println("              Minima </font></b></td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\">&nbsp;</td>");
			out.println("          </tr>");
			out.println("          <tr> ");
			out.println("            <td width=\"283\">&nbsp;</td>");
			out.println("            <td width=\"313\"> ");
			out.println("              <input type=\"submit\" name=\"invia\" value=\"Invia\" " + disabilitaOpzioni + ">");
			out.println("            </td>");
			out.println("          </tr>");
			out.println("        </table>");
			out.println("        <p align=\"center\">&nbsp;</p>");
			out.println("      </form>");
		}
		out.println("      <p align=\"center\">&nbsp;</p>");
		out.println("    </td>");
		out.println("    <td width=\"9\" bgcolor=\"#0000FF\" height=\"792\"> ");
		out.println("      <div align=\"center\"></div>");
		out.println("    </td>");
		out.println("  </tr>");
		out.println("  <tr bordercolor=\"#FFFFFF\"> ");
		out.println("    <td width=\"10\" bgcolor=\"#0000FF\" height=\"2\">&nbsp;</td>");
		out.println("    <td width=\"604\" valign=\"top\" bgcolor=\"0000ff\" height=\"2\">&nbsp;</td>");
		out.println("    <td width=\"9\" bgcolor=\"#0000FF\" height=\"2\">&nbsp;</td>");
		out.println("  </tr>");
		out.println("</table>");
		out.println("<map name=\"Map\"> ");
		out.println("  <area shape=\"rect\" coords=\"64,14,193,57\" href=\"../../index.html\">");
		out.println("  <area shape=\"rect\" coords=\"419,14,545,56\" href=\"../../guida.html\">");
		out.println("</map>");
		out.println("</body>");
		out.println("</html>");


    }
}
