/*
 *	ejies Java externals by Emmanuel Jourdan, Ircam � 12 2005
 *
 *	$Revision: 1.4 $
 *	$Date: 2006/04/04 15:01:57 $
 */

package ej;
import com.cycling74.max.*;
import java.util.regex.*;
import java.io.*;
// import java.util.*;

public abstract class ej extends MaxObject
{
	private static boolean printedMessage;
	private static String theMessage = "ejies Java externals by Emmanuel Jourdan, Ircam";
	
	public void ej() {
		if (printedMessage == false) {
			dblclick();
			printedMessage = true;
		}
	}
	
	public void dblclick() {
		// c'est super amusant, on peut faire un double clic...
		post(theMessage);
		findVersion();
	}
	
	private void findVersion() {
		/*
		 il faut chercher ceci :
		 VersNum = "1.56b3"; 
		 VersDate = "(04/2006)";
		 */
		
		String theFile = MaxSystem.locateFile("ejies-jsextensions.js");
		String versionNumber = null;
		String versionDate = null;
		
		
		BufferedReader inputFile = null;
		try {   
			inputFile = new BufferedReader(new FileReader(theFile));
			String line = null;
			Pattern pVers = Pattern.compile(".*VersNum = \\\"(.*)\\\".*");
			Pattern pDate = Pattern.compile(".*VersDate = \\\"\\((\\d*/\\d*).*"); 
			Matcher mVers, mDate;

			while((line = inputFile.readLine()) != null) {
				mVers = pVers.matcher(line);
				mDate = pDate.matcher(line);
				
				if (mVers.matches()) {
					versionNumber = mVers.replaceAll("$1");
				} else if (mDate.matches()) {
					versionDate = mDate.replaceAll("$1");
					break;    // normalement la date est apr�s le numb�ro de version
				}
			}
		} catch (Exception e) { /* l'utilisateur s'en fiche */ } 
		finally {
			try {
				if (inputFile != null)
					inputFile.close();
			}
			catch (IOException e) { /* pareil */ }
		}

		if (versionNumber != null && versionDate != null)
			post("    version " + versionNumber + "   ---   " + versionDate);
	}
}