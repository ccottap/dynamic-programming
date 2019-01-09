
package dynamicProgramming.knapsack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Resolución del problema de la mochila no acotada
 * Ejercicio 3, Sección 3.2, pp. 47-53
 * @author ccottap
 *
 */
public class UnboundedKnapsackDP {
	/**
	 * Main function
	 * @param args command-line parameters
	 * @throws IOException if an attempt is made to read/write data to a file that cannot be opened
	 */
	public static void main(String[] args) throws IOException {
		if (args.length<1) {
			System.out.println("java UnboundedKnapsackDP [-f|-g] <arguments>");
			System.out.println("\t-f: reads data from a file");
			System.out.println("\t-g: generates a problem instance at random with specified parameters");
		}
		else {
			Knapsack ukp = null;
			String filename = "";

			if (args[0].equals("-f")) {
				if (args.length < 2) {
					System.out.println("java UnboundedKnapsackDP -f <filename>");
					System.exit(-1);
				}
				else {
					filename = args[1];
					ukp = new Knapsack(filename);
				}
			} 
			else if (args[0].equals("-g")) {
				if (args.length < 2) {
					System.out.println("java UnboundedKnapsackDP -g <numobjects>");
					System.exit(-1);
				}
				else {
					int num = Integer.parseInt(args[1]);
					ukp = new Knapsack(num);
					filename = "random" + num + ".kp";
					ukp.WriteToFile(filename);
				}
			}
			else {
				System.out.println("Wrong argument: " + args[0]);
				System.exit(-1);
			}
			
			System.out.println(ukp);
			
			List<Integer> sol = OptimizeKnapsack (ukp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + ukp.SolutionValue(sol));
			System.out.println("Weight = " + ukp.SolutionWeight(sol));
			
			sol = OptimizeKnapsackTopDown (ukp);
			
			System.out.println("Optimal solution: " + sol);
			System.out.println("Value  = " + ukp.SolutionValue(sol));
			System.out.println("Weight = " + ukp.SolutionWeight(sol));
		}

	}
	
	/**
	 * Bottom-up resolution of an unbounded knapsack problem instance
	 * @param ukp the problem instance
	 * @return a list with the number of copies of each object
	 */
	public static List<Integer> OptimizeKnapsack(Knapsack ukp) {
		int W = ukp.getW();
		int n = ukp.getNum();
		int[] V = new int[W+1];
		int[] D = new int[W+1]; 
		
		// V[w] = optimal value of a knapsack of capacity w
		// D[w] = object to pick when the capacity is w
		
		V[0] = 0;
		D[0] = -1;
		
		for (int j=1; j<=W; j++) {
			V[j] = 0;
			D[j] = -1;
			for (int i=0; i<n; i++) {
				int w = ukp.getWeight(i);
				if (j>=w) {
					int v = ukp.getValues(i);
					int q = v + V[j-w];
					if (q>V[j]) {
						V[j] = q;
						D[j] = i;
					}
				}
			}
		}
		
		System.out.println("\nMatriz de costes/decisión:\n");

		System.out.print(" \t|");
		for (int j=0; j<=W; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=W; j++)
			System.out.print("\t--");
		System.out.print("\nV\t|\t");
		for (int j=0; j<=W; j++) {
			System.out.format("%d/%d \t", V[j], D[j]);
		}
	
		
		System.out.println("\n");
		
		return ReconstructSolution(D, ukp);
	}
	
	/**
	 * Reconstructs the optimal solution given the decision table (a vector)
	 * @param D the decision table computed by the OptimizeKnapsack function
	 * @param ukp the problem instance
	 * @return a list with the number of copies of each object
	 * @see OptimizeKnapsack
	 */
	private static List<Integer> ReconstructSolution (int[] D, Knapsack ukp) {
		int W = ukp.getW();
		int n = ukp.getNum();
		List<Integer> sol = new ArrayList<Integer>();
		int j;
		
		for (int i=0; i<n; i++)
			sol.add(0);
		j = W;
		while ((j>0) && (D[j]>=0)) {
			sol.set(D[j], sol.get(D[j])+1);
			j -= ukp.getWeight(D[j]);
		}
		
		return sol;
		
	}
	
	
	
	/**
	 * Wrapper method for the top-down resolution of an unbounded knapsack problem instance
	 * @param ukp the problem instance
	 * @return a list with the number of copies of each object
	 * @see OptimizeKnapsackTopDownRec
	 */
	public static List<Integer> OptimizeKnapsackTopDown(Knapsack ukp) {
		int W = ukp.getW();
		HashMap<Integer,Integer> V = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> D = new HashMap<Integer,Integer>();
		
		V.put(0, 0);
		D.put(0, -1);
		
		OptimizeKnapsackTopDownRec (ukp, W, V, D);
		
		
		System.out.println("\nMatriz de costes/decisión:\n");

		System.out.println("V = " + V);
		System.out.println("D = " + D);
		System.out.print("Tamaño de la tabla = " + V.size());
		System.out.format(" (%3.2f%%)\n\n", (double)V.size()*100.0/(double)(W+1));

		
		return ReconstructSolution(D, ukp);
	}

	/**
	 * Reconstructs the optimal solution given the decision table (a hash map)
	 * @param D the decision table computed by the OptimizeKnapsackTopDown function
	 * @param ukp the problem instance
	 * @return a list with the number of copies of each object
	 * @see OptimizeKnapsackTopDown
	 */
	private static List<Integer> ReconstructSolution(HashMap<Integer, Integer> D, Knapsack ukp) {
		int W = ukp.getW();
		int n = ukp.getNum();
		List<Integer> sol = new ArrayList<Integer>();
		int j, d;
		
		for (int i=0; i<n; i++)
			sol.add(0);
		j = W; 
		d = D.get(j);
		while ((j>0) && (d>=0)) {
			sol.set(d, sol.get(d)+1);
			j -= ukp.getWeight(d);
			d = D.get(j);
		}
		
		return sol;
	}

	
	/**
	 * Top-down resolution of an unbounded knapsack problem instance
	 * @param ukp the problem instance
	 * @param W the capacity of the knapsack
	 * @param V the table of values
	 * @param D the table of decisions
	 * @return the optimal value
	 * @see OptimizeKnapsackTopDown
	 */
	private static int OptimizeKnapsackTopDownRec(Knapsack ukp, int W,
			HashMap<Integer, Integer> V, HashMap<Integer, Integer> D) {
		
		if (V.get(W) == null) {
			int n = ukp.getNum();
			int bestV = 0;
			int bestD = -1;
			for (int i=0; i<n; i++) {
				int w = ukp.getWeight(i);
				if (w <= W) {
					int q = ukp.getValues(i) + OptimizeKnapsackTopDownRec(ukp, W-w, V, D);
					if (q>bestV) {
						bestV = q;
						bestD = i;
					}
				}
			}
			V.put(W, bestV);
			D.put(W, bestD);
		}
		
		return V.get(W);
		
	}
	
	
	
}
