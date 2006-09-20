/*
 * class called by ej.lop
 * lucky me, this is automatically generated :-)
 *
 */

package ej;


/**
 * ListGTOEPass:
 * interface used to perform ListGTOEPass math operation
 * @author jourdan
 * @version $Revision: 1.6 $
 * @see ej.lop
 */
public class ListGTOEPass implements ListOperator {
	/**
	 * permform the ListGTOEPass math operation 
	 * @param a floating point value from the leftmost input
	 * @param b list from the rightmost input
	 */
	public float[] operate(float a, float b[])
	{
		float[] resultat = new float[b.length];
		
		for (int i = 0; i < b.length; i++)
			resultat[i] = a >= b[i] ? a : 0;
		
		return resultat;
	}

	/**
	 * permform the ListGTOEPass math operation 
	 * @param a list from the leftmost input
	 * @param b floating point value from the rightmost input
	 */
	public float[] operate(float a[], float b)
	{
		float[] resultat = new float[a.length];
		
		for (int i = 0; i < a.length; i++)
			resultat[i] = a[i] >= b ? a[i] : 0;
		
		return resultat;
	}

	/**
	 * permform the ListGTOEPass math operation 
	 * @param a list from the leftmost input
	 * @param b list from the rightmost input
	 */
	public float[] operate(float a[], float b[])
	{
		int listLength = Math.min(a.length, b.length);
		float resultat[] = new float[listLength];
		
		for (int i = 0; i < listLength; i++)
			resultat[i] = a[i] >= b[i] ? a[i] : 0;
		
		return resultat;
	}
}
