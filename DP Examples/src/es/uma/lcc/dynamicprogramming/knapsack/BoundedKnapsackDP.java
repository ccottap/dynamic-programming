
package es.uma.lcc.dynamicprogramming.knapsack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.uma.lcc.datastructures.tuple.Pair;

/**
 * Resolución del problema de la mochila acotada
 * Ejercicio 3, Sección 3.2, pp. 47-53
 * @author ccottap
 *
 */
public class BoundedKnapsackDP {


	/**
	 * Main method
	 * @param args command-line parameters
	 * @throws IOException if an attempt is made to read/write data to a file that cannot be opened
	 */
	public static void main(String[] args) throws IOException {
		if (args.length<1) {
			System.out.println("java BoundedKnapsackDP [-f|-g] <arguments>");
			System.out.println("\t-f: reads data from a file");
			System.out.println("\t-g: generates a problem instance at random with specified parameters");
		}
		else {
			BoundedKnapsack bkp = null;
			String filename = "";

			if (args[0].equals("-f")) {
				if (args.length < 2) {
					System.out.println("java BoundedKnapsackDP -f <filename>");
					System.exit(-1);
				}
				else {
					filename = args[1];
					bkp = new BoundedKnapsack(filename);
				}
			} 
			else if (args[0].equals("-g")) {
				if (args.length < 2) {
					System.out.println("java BoundedKnapsackDP -g <numobjects>");
					System.exit(-1);
				}
				else {
					int num = Integer.parseInt(args[1]);
					bkp = new BoundedKnapsack(num);
					filename = "random" + num + ".bkp";
					bkp.WriteToFile(filename);
				}
			}
			else {
				System.out.println("Wrong argument: " + args[0]);
				System.exit(-1);
			}
			
			System.out.println(bkp);
			
			List<Integer> sol = OptimizeKnapsack (bkp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + bkp.SolutionValue(sol));
			System.out.println("Weight = " + bkp.SolutionWeight(sol));
			
			sol = OptimizeKnapsackTopDown (bkp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + bkp.SolutionValue(sol));
			System.out.println("Weight = " + bkp.SolutionWeight(sol));
		}

	}

	/**
	 * Bottom-up optimization the bounded knapsack problem instance provided
	 * @param bkp a bounded knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	public static List<Integer> OptimizeKnapsack(BoundedKnapsack bkp) {
		int W = bkp.getW();
		int n = bkp.getNum();
		int[][] V = new int[n+1][W+1];
		int[][] D = new int[n+1][W+1];
		
		// V[i][j] = maximum value using objects of class 1..i
		// and knapsack capacity j
		// D[i][j] = number of copies of object i when the knapsack
		// has capacity j
		
		for (int j=0; j<=W; j++) 
			V[0][j] = 0;					// no objects -> no value
		
		for (int i=1; i<=n; i++) {
			V[i][0] = 0;					// no capacity -> no value
			D[i][0] = 0;
		}
		
		for (int i=1; i<=n; i++) {
			int w = bkp.getWeight(i-1);
			int v = bkp.getValues(i-1);
			int c = bkp.getCopies(i-1);
			for (int j=1; j<=W; j++) {
				V[i][j] = -1;
				int mc = Math.min(c, j/w); // maximum number of copies of object i that can be used
				for (int k=0; k<=mc; k++) {
					int q = k*v + V[i-1][j-k*w];
					if (q>V[i][j]) {
						V[i][j] = q;
						D[i][j] = k;
					}
				}
			}
		}
		
		System.out.println("\nMatriz de costes/decision:\n");

		System.out.print(" \t|");
		for (int j=0; j<=W; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=W; j++)
			System.out.print("\t--");
		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t");
			for (int j=0; j<=W; j++) {
				System.out.format("%d/%d \t", V[i][j], D[i][j]);
			}
		}
		
		System.out.println("\n");
		
		return ReconstructSolution(D, bkp);
	}
	
	/**
	 * Reconstructs the solution given the decision matrix
	 * @param D the decision matrix computed by the optimization method
	 * @param bkp the bounded knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	private static List<Integer> ReconstructSolution (int[][] D, BoundedKnapsack bkp) {
		int W = bkp.getW();
		int n = bkp.getNum();
		List<Integer> sol = new ArrayList<Integer>();
		
		for (int j=W, i=n; i>0; i--) {
			sol.add(0, D[i][j]);
			j -= D[i][j]*bkp.getWeight(i-1);
		}
		
		return sol;
		
	}
	
	/**
	 * Wrapper function for the top-down optimization the bounded 
	 * knapsack problem instance provided
	 * @param bkp a bounded knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	public static List<Integer> OptimizeKnapsackTopDown(BoundedKnapsack bkp) {
		int W = bkp.getW();
		int n = bkp.getNum();
		HashMap<Pair<Integer,Integer>,Integer> V = new HashMap<Pair<Integer,Integer>,Integer>();
		HashMap<Pair<Integer,Integer>,Integer> D = new HashMap<Pair<Integer,Integer>,Integer>();
		
		OptimizeKnapsackTopDownRec(bkp, n, W, V, D);
		
		System.out.println("\nMatriz de costes/decisión:");

		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t");
			for (int j=0; j<=W; j++) {
				Pair<Integer, Integer> p = new Pair<Integer,Integer>(i,j);
				if (V.get(p) != null)
					System.out.print(j + "(" + V.get(p) + "/" + D.get(p) + ")\t");
			}
		}
		System.out.print("\n\nTamaño de la tabla = " + V.size());
		System.out.format(" (%3.2f%%)\n", (double)V.size()*100.0/(double)((n+1)*(W+1)));
	
		return ReconstructSolution(D, bkp);
		
	}


	/**
	 * Top-down optimization the bounded knapsack problem instance provided
	 * @param bkp a bounded knapsack problem instance
	 * @param n objects 1..n can be used
	 * @param W capacity of the knapsack
	 * @param V the memory of objective values for previously computed subproblems
	 * @param D the memory of optimal decisions for previously computed subproblems
	 * @return a list with the number of copies of each object
	 */
	private static int OptimizeKnapsackTopDownRec(BoundedKnapsack bkp, int n, int W,
			HashMap<Pair<Integer, Integer>, Integer> V, 
			HashMap<Pair<Integer, Integer>, Integer> D) 
	{
		Pair<Integer, Integer> p = new Pair<Integer,Integer>(n,W);

		if (V.get(p) == null) {
			if ((n==0) || (W==0)) {
				V.put(p, 0);
				D.put(p, 0);
			}
			else {
				int w = bkp.getWeight(n-1);
				int v = bkp.getValues(n-1);
				int c = bkp.getCopies(n-1);
				int mc = Math.min(c, W/w);
				int bestV = OptimizeKnapsackTopDownRec(bkp, n-1, W, V, D);
				int bestD = 0;
				for (int k=1; k<=mc; k++) {
					int q = v*k + OptimizeKnapsackTopDownRec(bkp, n-1, W-k*w, V, D);
					if (q>bestV) {
						bestV = q;
						bestD = k;
					}
				}
				V.put(p, bestV);
				D.put(p, bestD);
			}
		}
		
		return V.get(p);
	}

	/**
	 * Reconstructs the solution given the decision matrix (in sparse form
	 * as a HashMap)
	 * @param D the decision matrix
	 * @param bkp a bounded knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	private static List<Integer> ReconstructSolution(HashMap<Pair<Integer, Integer>, Integer> D, 
													 BoundedKnapsack bkp) {
	
		int W = bkp.getW();
		int n = bkp.getNum();
		List<Integer> sol = new ArrayList<Integer>();

		while (n>0) {
			Pair<Integer, Integer> p = new Pair<Integer,Integer>(n,W);
			int d = D.get(p);
			sol.add(0, d);
			W -= d*bkp.getWeight(n-1);
			n--;
		}
		
		
		return sol;

	}
	
}