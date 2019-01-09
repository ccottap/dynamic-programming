
package dynamicProgramming.knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 0-1 Knapsack Problem
 * @author ccottap
 *
 */
public class Knapsack {
	/**
	 * number of objects
	 */
	protected int num;			
	/**
	 * weight of each object
	 */
	protected int[] weights;	
	/**
	 * value of each object
	 */
	protected int[] values;		
	/**
	 * capacity of the knapsack
	 */
	protected int W;			
	/**
	 * class-level random generator;
	 */
	protected static Random r =  new Random(1);
	
	/**
	 * Creates an empty knapsack problem instance (no objects, no knapsack).
	 * 
	 */
	public Knapsack() {
		setNum(0);
	}
	
	/**
	 * Creates a knapsack problem instance of a certain size. 
	 * Randomizes the knapsack capacity and the values and weights
	 * of the objects.
	 * 
	 * @param n is the number of objects
	 */
	public Knapsack(int n) {
		setNum(n);
		randomize();
	}
	
	/**
	 * Maximum value of an object when initializing at random
	 */
	private static final int MAX_VAL = 100;
	/**
	 * Maximum weight of an object when initializing at random
	 */
	private static final int MAX_WEIGHT = 100;
	/**
	 * Randomizes the values and weights of the objects in the knapsack
	 */
	protected void randomize() {
		W = 0;
		
		for (int i=0; i<num; i++) {
			values[i] = r.nextInt(MAX_VAL);
			weights[i] = r.nextInt(MAX_WEIGHT);
			W += weights[i];
		}
		
		W/= 2;
		
	}

	/**
	 * Reads a knapsack problem instance from a file
	 * @param filename the name of the file
	 * @throws FileNotFoundException if the file is not found
	 */
	public Knapsack(String filename) throws FileNotFoundException
	{
		Scanner inputFile = new Scanner (new File(filename));

		num = inputFile.nextInt();
		setNum(num);
		for (int i=0; i<num; i++) {
			values[i] = inputFile.nextInt();
			weights[i] = inputFile.nextInt();
		}
		W = inputFile.nextInt();

		inputFile.close();
	}
	
	
	/**
	 * Writes a knapsack problem instance to file
	 * @param filename the name of the file
	 * @throws IOException if the file cannot be opened for writing
	 */
	public void WriteToFile(String filename) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter(filename));
     	
     	out.println(num);
     	for (int i=0; i<num; i++) {
     		out.println(values[i] + "\t" + weights[i]);
     	}
     	out.println(W);
     	out.close();
	}
	
	
	/**
	 * Returns the number of objects {@link Knapsack#num}
	 * @return the number of objects
	 */
	public int getNum() {
		return num;
	}

	/**
	 * Sets the number of objects {@link Knapsack#num} (and allocates memory accordingly
	 * for {@link Knapsack#values} and {@link Knapsack#weights})
	 * @param num the number of objects to set
	 */
	public void setNum(int num) {
		this.num = num;
		weights = new int[num];
		values = new int[num];
	}

	/**
	 * Returns the weight of an object
	 * @param i the index of the object
	 * @return the weight of the object
	 */
	public int getWeight(int i) {
		return weights[i];
	}

	/**
	 * Sets the weight of an object
	 * @param i the index of the object
	 * @param w the weight to set
	 */
	public void setWeight(int i, int w) {
		weights[i] = w;
	}

	/**
	 * Gets the value of an object
	 * @param i the index of the object
	 * @return the value of an object
	 */
	public int getValues(int i) {
		return values[i];
	}

	/**
	 * Sets the value of an object
	 * @param i the index of an object
	 * @param v the value to set
	 */
	public void setValues(int i, int v) {
		values[i] = v;
	}

	/**
	 * Gets the capacity {@link Knapsack#W} of the knapsack
	 * @return the capacity of the knapsack
	 */
	public int getW() {
		return W;
	}

	/**
	 * Sets the capacity {@link Knapsack#W} of the knapsack
	 * @param w the capacity of the knapsack
	 */
	public void setW(int w) {
		W = w;
	}
	
	
	/**
	 * Returns the total value of a solution represented as a list of 
	 * integers (this suits the 0-1, bounded and unbounded variants of 
	 * the problem)
	 * @param sol the solution to be evaluated
	 * @return the total value of objects in the solution
	 */
	public int SolutionValue (List<Integer> sol)
	{
		int val = 0;
		for (int i=0; i<num; i++) {
			val += sol.get(i)*values[i];
		}
		return val;
	}

	/**
	 * Returns the total weight of a solution represented as a list of 
	 * integers (this suits the 0-1, bounded and unbounded variants of 
	 * the problem)
	 * @param sol the solution to be evaluated
	 * @return the total weight of objects in the solution
	 */
	public int SolutionWeight (List<Integer> sol)
	{
		int wei = 0;
		for (int i=0; i<num; i++) {
			wei += sol.get(i)*weights[i];
		}
		return wei;
	}
	 
	 
	/**
	 * Returns a printable string representation of the knapsack
	 * @return a string representing the knapsack
	 */
	public String toString()
	{
		String cad;
		
		cad = "W = " + W + "\n" + "Values: ";
		for (int i=0; i<num; i++)
			cad = cad + "\t" + values[i];
		cad = cad + "\nWeights:";
		for (int i=0; i<num; i++)
			cad = cad + "\t" + weights[i];	
		cad = cad + "\n";
		
		return cad;
	}
	
}
