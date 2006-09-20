/*
 *	ej.lslide by Emmanuel Jourdan, Ircam � 02 2005
 *	slide for lists
 *
 *
 *	$Revision: 1.9 $
 *	$Date: 2006/09/20 16:40:54 $
 */

package ej;

import com.cycling74.max.*;
import com.cycling74.msp.MSPBuffer;

/**
 * smooth a list.
 * @author jourdan
 * @see ej
 * @see standart <code>slide, slide~, jit.slide</code> objects
 * @version $Revision: 1.9 $
 */
public class lslide extends ej {
	private static final String[] INLET_ASSIST = new String[]{ "List to be slided :-)", "Slide Up", "Slide Down" };
	private static final String[] OUTLET_ASSIST = new String[]{ "Slided list"};	

	private float[] a = new float[0];
	private float[] resultat = new float[0];
	private float slide_up = 1;
	private float slide_down = 1;
	private float yN_1 = 0;
	private boolean firstIsMade = false;
	private String buf_name = null;
	private int outputmode = 0;
	
	/**
	 * Create a lslide object with arguments.
	 * @param slideUp slide up value
	 * @param slideDown slide down value
	 */
	public lslide(float slideUp, float slideDown)	{
		declareTypedIO("aff", "l");
		createInfoOutlet(true);
		
		setSlideUp(slideUp);
		setSlideDown(slideDown);
		declareAttribute("slide_up", "getSlideUp", "setSlideUp");
		declareAttribute("slide_down", "getSlideDown", "setSlideDown");
		declareAttribute("outputmode", null, "setMode");
		declareAttribute("buf_name");

		setInletAssist(INLET_ASSIST);
		setOutletAssist(OUTLET_ASSIST);
	}
	
	private void setMode(int i) {
		if (i >= 0 && i <= 2)
			outputmode = i;
		else
			outputmode = 0;
	}
	
	/**
	 * Re-Trigger the smoothing.
	 */
	public void bang() {
		calculeChoice();
	}
	
	private void setSlideUp(float f) {
		if (f >= 1) 
			slide_up = f;
	}
	
	private void setSlideDown(float f) {
		if (f >= 1)
			slide_down = f;
	}
	
	private float getSlideUp() {
		return slide_up;
	}

	private float getSlideDown() {
		return slide_down;
	}

	/**
	 * Change the slide up/down value
	 * @param f if the value arrives in the left inlet: try using the <code>slide</code> object instead.
	 * @param f if the value arrives in the second inlet: change the slide up value
	 * @param f if the value arrives in the third inlet: change the slide down value
	 */
	public void inlet(float f) {
		switch (getInlet()) {
			case 0:
				a = new float[] { f };
				calculeChoice();
				break;
			case 1:
				setSlideUp(f);
				break;
			case 2:
				setSlideDown(f);
				break;
		}
	}

	/**
	 * The list to be smoothed.
	 * @param args if the list arrives in the left inlet: the list will be smoothed.
	 * @param args if the list arrives in the second inlet: the first item of the list defines the slide up value, the second defines the slide down.
	 */
	public void list(float[] args) {
		switch (getInlet()) {
			case 0:
				a = args;
				calculeChoice();
				break;
			case 1:
				setSlideUp(args[0]);
				setSlideDown(args[1]);
				break;
			default:
				error("ej.lslide: slide down inlet expects float");
		}
	}
	
	
//	public void anything(String s, Atom[] args) {
//		error("ej.lslide: doesn't understand " + s + " " + Atom.toOneString(args));
//	}
	
	private void calculeChoice() {
		// redimensionne tout le monde
		if (resultat.length != a.length) {
			resultat = new float[a.length];
		}
		
		if (firstIsMade == true)
			calcule();
		else
			calculeFirstTime();
	}
	
	private void calcule() {
		for (int i = 0; i < resultat.length; i++) {
			yN_1 = resultat[i];

			if (a[i] - resultat[i] >= 0)
				resultat[i] = yN_1 + ((a[i] - yN_1) / slide_up);
			else
				resultat[i] = yN_1 + ((a[i] - yN_1) / slide_down);
		}
		
		doOutput();
	}
	
	private void calculeFirstTime() {
		yN_1 = 0;
		
		for (int i = 0; i < resultat.length; i++) {
			if (a[i] >= 0)
				resultat[i] = yN_1 + ((a[i] - yN_1) / slide_up);
			else
				resultat[i] = yN_1 + ((a[i] - yN_1) / slide_down);
		}
		
		doOutput();
		firstIsMade = true;
	}
	
	private void doOutput() {
		switch (outputmode) {
			case  0:
				outlet(0, resultat); break;
			case  1:
				writeToBuffer(); break;
			case 2:
				outlet(0, resultat); writeToBuffer(); break;
		}
	}
	
	private void writeToBuffer() {
		if (buf_name != null && resultat.length > 0) {
			MSPBuffer.poke(buf_name, resultat);
		}
	}
}