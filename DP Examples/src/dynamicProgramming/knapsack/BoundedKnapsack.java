/**
 * 
 */
package dynamicProgramming.knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author ccottap
 *
 */
public class BoundedKnapsack extends Knapsack {

	int[] copies;		// number of copies of each object
	
	

	/**
	 * Creates a bounded knapsack problem instance of a certain size.
	 * @param n the number of objects
	 */
	public BoundedKnapsack(int n) {
		super(n);
	}

	/**
	 * Creates a problem instance reading data from a file
	 * @param filename the name of the file
	 * @throws FileNotFoundException if the file cannot be read
	 */
	public BoundedKnapsack(String filename) throws FileNotFoundException {
		Scanner inputFile = new Scanner (new File(filename));

		num = inputFile.nextInt();
		setNum(num);
		for (int i=0; i<num; i++) {
			values[i] = inputFile.nextInt();
			weights[i] = inputFile.nextInt();
			copies[i] = inputFile.nextInt();
		}
		W = inputFile.nextInt();

		inputFile.close();
	}
	
	static final int MAX_COPIES = 10;
	/**
	 * {@inheritDoc}
	 * Also randomizes the number of copies of the objects in the knapsack.
	 */
	protected void randomize() {
		super.randomize();
		W = 0;
		
		for (int i=0; i<num; i++) {
			copies[i] = r.nextInt(MAX_COPIES);
			W += weights[i]*copies[i];
		}
		
		W/= 2;	
	}
	
	/**
	 * Sets the number of objects
	 * @param num the number of objects to set
	 */
	public void setNum(int num) {
		super.setNum(num);
		copies = new int[num];
	}
	
	/**
	 * Returns the number of copies of an object
	 * @param i the index of the object
	 * @return the copies of object i
	 */
	public int getCopies(int i) {
		return copies[i];
	}

	/**
	 * Sets the number of copies of an object
	 * @param i the index of the object
	 * @param c the number of copies
	 */
	public void setCopies(int i, int c) {
		copies[i] = c;
	}
	
	/**
	 * Writes a bounded knapsack problem instance to file
	 * @param filename the name of the file
	 * @throws IOException if the file cannot be opened for writing
	 */
	public void WriteToFile(String filename) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter(filename));
     	
     	out.println(num);
     	for (int i=0; i<num; i++) {
     		out.println(values[i] + "\t" + weights[i] + "\t" + copies[i]);
     	}
     	out.println(W);
     	out.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		String cad = super.toString();
		
		cad = cad + "Copies: ";
		for (int i=0; i<num; i++)
			cad = cad + "\t" + copies[i];	
		cad = cad + "\n";
		return cad;
	}
}
