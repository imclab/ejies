/*
 *	ej.ldrunk by Emmanuel Jourdan, Ircam � 04 2005
 *	drunk for list
 *
 *
 *	$Revision: 1.8 $
 *	$Date: 2006/09/20 16:40:54 $
 */

package ej;

import com.cycling74.max.*;
import com.cycling74.msp.MSPBuffer;

/**
 * give beers to a list...
 * @author jourdan
 * @see ej
 * @version $Revision: 1.8 $
 */
public class ldrunk extends ej {
	private static final String[] INLET_ASSIST = new String[]{ "clean list", "Random range (float/list)", "random step (� step / 2)", "probability of random (%)" };
	private static final String[] OUTLET_ASSIST = new String[]{ "Drunked list"};	

	private float[] range = new float[]{ 0, 127 };
	private float step = 0;
	private float proba = 100;
	private float realProba = 1f;
	private boolean ignore0 = false;
	private boolean autoreset = false;
	private float[] resultat = new float[0];
	private float[] inputList = new float[0];
	private String buf_name = null;
	private int outputmode = 0;
	
	/**
	 * Create a ldrunk object with specified maximum and step.
	 * @param maxRange define the maximum of the range
	 * @param stepSize define the step (randomness will be � step/2) 
	 */
	public ldrunk(float maxRange, float stepSize) {
		this(0f, maxRange, stepSize, 100f);
	}

	/**
	 * Create a ldrunk object with specified minimum, maximum and step.
	 * @param minRange define the minimum of the range
	 * @param maxRange define the maximum of the range
	 * @param stepSize define the step (randomness will be � step/2) 
	 */
	public ldrunk(float minRange, float maxRange, float stepSize)	{
		this(minRange, maxRange, stepSize, 100f);
	}

	/**
	 * Create a ldrunk object with specified minimum, maximum, step and probapility.
	 * @param minRange define the minimum of the range
	 * @param maxRange define the maximum of the range
	 * @param stepSize define the step (randomness will be � step/2) 
	 * @param proba probability of randomness (percentage)
	 */
	public ldrunk(float minRange, float maxRange, float stepSize, float proba)	{
		declareTypedIO("alff", "l");
		createInfoOutlet(true);
		
		setRange(new float[]{ minRange, maxRange });
		setStep(stepSize);
		setProba(proba);
		
		declareAttribute("range", null, "setRange");
		declareAttribute("step", null, "setStep");
		declareAttribute("proba", null, "setProba");
		declareAttribute("ignore0");
		declareAttribute("autoreset");
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
	 * Trigger the randomisation.
	 */
	public void bang() {
		if (inputList.length > 0)
			doRandom();
	}
	
	/**
	 * If the floating point value arrives in the left inlet, try using drunk instead...
	 * <p>If the floating point value arrives in the second inlet: set the minimum of the range 
	 * <p>If the floating point value arrives in the third inlet: set the maximum of the range 
	 * <p>If the floating point value arrives in the fourth inlet: set the step size
	 * <p>If the floating point value arrives in the fifth inlet: set the probability
	 */
	public void inlet(float f) {
		switch (getInlet()) {
			case 0:
				inputList = new float[]{ f };
				doRandom();
				break;
			case 1:
				setRange(new float[]{ 0 , Math.abs(f) });
				break;
			case 2:
				setStep(f);
				break;
			case 3:
				setProba(f);
				break;
		}
	}
	
	/**
	 * List to be drunked.
	 */
	public void list(float[] args) {
		switch (getInlet()) {
			case 0:
				inputList = args;
				break;
			case 1:
				setRange(args);
				break;
			default: // meaning "other inputs"
				error("ej.ldrunk: this inlet expects int/float");
		}
	}
		
	private void setRange(float[] args) {
		if (args.length > 1) {
			range[0] = Math.min(args[0], args[1]);
			range[1] = Math.max(args[0], args[1]);
			setStep(step);
		} else {
			setRange(new float[]{ 0 , Math.abs(args[0]) });
		}
	}
	
	private void setStep(float f) {
		step = Math.max(0f, Math.min(f, range[1] - range[0]));
	}
	
	private void setProba(float f) {
		proba = Math.max(0f, Math.min(f, 100f));
		realProba = proba * 0.01f;
	}
	
	private void doRandom() {
		if (autoreset) {
			resultat = new float[inputList.length];
			System.arraycopy(inputList, 0, resultat, 0, inputList.length); // copie des donn�es (la liste d'entr�e reste intacte
		} else
			resultat = inputList; // pas de copie: c'est juste une r�f�rence

		// � (step size / 2) sur chaque valeur...
		for (int i = 0; i < resultat.length; i++) {
			if (ignore0) {
				if (resultat[i] != 0f)
					makeAlea(i);
			} else
				makeAlea(i);
		}
		
		doOutput();
	}

	private void makeAlea(int i) {
		if (Math.random() < realProba) {
			resultat[i] = resultat[i] + (float) ((Math.random() - 0.5) * step);
			
			// repliement si c'est en dehors du range
			if (resultat[i] < range[0])
				resultat[i] = Math.min((range[0] - resultat[i]) + range[0], range[1]);
			else if (resultat[i] > range[1])
				resultat[i] = Math.max((range[1] - resultat[i]) + range[1], range[0]);
		}		
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