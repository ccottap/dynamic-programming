package es.uma.lcc.dynamicprogramming.knapsack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Resolución del problema de la mochila 0-1
 * Sección 1.3, pp. 17 (1ra ed.); pp. 18 (2da ed.)
 * @author ccottap
 *
 */
public class KnapsackDP {


	/**
	 * Main method
	 * @param args command-line parameters
	 * @throws IOException if an attempt is made to read/write data to a file that cannot be opened
	 */
	public static void main(String[] args) throws IOException {
		if (args.length<1) {
			System.out.println("java KnapsackDP [-f|-g] <arguments>");
			System.out.println("\t-f: reads data from a file");
			System.out.println("\t-g: generates a problem instance at random with specified parameters");
		}
		else {
			Knapsack kp = null;
			String filename = "";

			if (args[0].equals("-f")) {
				if (args.length < 2) {
					System.out.println("java KnapsackDP -f <filename>");
					System.exit(-1);
				}
				else {
					filename = args[1];
					kp = new Knapsack(filename);
				}
			} 
			else if (args[0].equals("-g")) {
				if (args.length < 2) {
					System.out.println("java KnapsackDP -g <numobjects>");
					System.exit(-1);
				}
				else {
					int num = Integer.parseInt(args[1]);
					kp = new Knapsack(num);
					filename = "random" + num + ".kp";
					kp.WriteToFile(filename);
				}
			}
			else {
				System.out.println("Wrong argument: " + args[0]);
				System.exit(-1);
			}
			
			System.out.println(kp);
			
			List<Integer> sol = OptimizeKnapsack (kp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + kp.SolutionValue(sol));
			System.out.println("Weight = " + kp.SolutionWeight(sol));
			
			sol = OptimizeKnapsackTopDown (kp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + kp.SolutionValue(sol));
			System.out.println("Weight = " + kp.SolutionWeight(sol));
		}

	}

	/**
	 * Bottom-up optimization the bounded knapsack problem instance provided
	 * @param kp a 0-1 knapsack problem
	 * @return a list with the number of copies of each object
	 */
	public static List<Integer> OptimizeKnapsack(Knapsack kp) {
		int W = kp.getW();
		int n = kp.getNum();
		int[][] V = new int[n+1][W+1];
		
		// V[i][j] = maximum value using objects 1..i
		// and knapsack capacity j

		for (int j=0; j<=W; j++) 
			V[0][j] = 0;				// no objects -> no value

		
		for (int i=1; i<=n; i++) 
			V[i][0] = 0;				// no capacity -> no value
		
		for (int i=1; i<=n; i++) {
			int w = kp.getWeight(i-1);
			int v = kp.getValues(i-1);
			for (int j=1; j<=W; j++) {
				V[i][j] = V[i-1][j];
				if (w<=j) {
					V[i][j] = Math.max(V[i][j], v + V[i-1][j-w]);
				}
			}
		}
		
		System.out.println("\nMatriz de costes:\n");

		System.out.print(" \t|");
		for (int j=0; j<=W; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=W; j++)
			System.out.print("\t--");
		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t");
			for (int j=0; j<=W; j++) {
				System.out.format("%d \t", V[i][j]);
			}
		}
		
		System.out.println("\n");
		
		return ReconstructSolution(V, kp);
	}
	
	/**
	 * Reconstructs the solution given the matrix of values computed
	 * @param V the matrix of values computed by the optimization method
	 * @param kp the 0-1 knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	private static List<Integer> ReconstructSolution (int[][] V, Knapsack kp) {
		int W = kp.getW();
		int n = kp.getNum();
		List<Integer> sol = new ArrayList<Integer>();
		
		for (int i=0; i<n; i++)
			sol.add(0);
		
		for (int j=W, i=n; i>0; i--) {
			if (V[i][j] != V[i-1][j]) {
				sol.set(i-1, 1);
				j -= kp.getWeight(i-1);
			}
		}
		
		return sol;
		
	}
	
	/**
	 * Wrapper function for the top-down optimization the 0-1 
	 * knapsack problem instance provided
	 * @param kp a knapsack problem instance
	 * @return a list with the number of copies of each object
	 */
	public static List<Integer> OptimizeKnapsackTopDown(Knapsack kp) {
		int W = kp.getW();
		int n = kp.getNum();
		int[][] V = new int[n+1][W+1];
		
		for (int i=0; i<=n; i++) 
			for (int j=0; j<=W; j++) 
				V[i][j] = -1;
	
		
		OptimizeKnapsackTopDownRec(kp, n, W, V);
		
	
		System.out.println("\nMatriz de costes:\n");

		System.out.print(" \t|");
		for (int j=0; j<=W; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=W; j++)
			System.out.print("\t--");
		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t");
			for (int j=0; j<=W; j++) {
				if (V[i][j]>=0)
					System.out.format("%d \t", V[i][j]);
				else
					System.out.print(" \t");
			}
		}
		
		System.out.println("\n");
	
		return ReconstructSolution(V, kp);
		
	}

	/**
	 * Top-down optimization the 0-1 knapsack problem instance provided
	 * @param kp a knapsack problem instance
	 * @param n objects 1..n can be used
	 * @param W the capacity of the knapsack
	 * @param V the matrix of values computed for previous subproblems
	 * @return a list with the number of copies of each object
	 */
	private static int OptimizeKnapsackTopDownRec(Knapsack kp, int n, int W, int[][] V) {
		
		if (V[n][W] < 0) {
			if ((n==0) || (W==0))
				V[n][W] = 0;
			else {
				int w = kp.getWeight(n-1);
				int v = kp.getValues(n-1);
				if (w>W)
					V[n][W] = OptimizeKnapsackTopDownRec(kp, n-1, W, V);
				else
					V[n][W] = Math.max(OptimizeKnapsackTopDownRec(kp, n-1, W, V), 
										v + OptimizeKnapsackTopDownRec(kp, n-1, W-w, V));
			}
		}
		
		return V[n][W];
	}

}
