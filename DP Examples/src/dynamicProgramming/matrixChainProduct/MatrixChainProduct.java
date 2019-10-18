
package dynamicProgramming.matrixChainProduct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Matrix Chain Product
 * Sección 3.21, pp. 201-208
 * @author ccottap
 *
 */
public class MatrixChainProduct {
	/**
	 * class-level random generator
	 */
	private static Random r = new Random(1); 
	
	/**
	 * Main method
	 * @param args command-line args 
	 * @throws FileNotFoundException if the data cannot be read from file
	 * @throws IOException if the data cannot be written to file
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length<1) {
			System.out.println("java MatrixChainProduct (-f|-g) <arguments>");
			System.out.println("\t-f: reads data from a file");
			System.out.println("\t-g: generates a problem instance at random with specified parameters");
		}
		else {
			List<Integer> dim = null;
			int num;
			final int NUM_TEST = 10;
			
			if (args[0].equals("-f")) {
				if (args.length < 2) {
					System.out.println("java MatrixChainProduct -f <filename>");
					System.exit(-1);
				}
				else {
					dim = ReadDataFromFile(args[1]);
				}
			} 
			else if (args[0].equals("-g")) {
				if (args.length < 2) {
					System.out.println("java MatrixChainProduct -g <num_matrices> [<random-seed>]");
					System.exit(-1);
				}
				else {
					String suffix = "";
					if (args.length > 2) {
						r = new Random(Integer.parseInt(args[2]));
						suffix = "_" + args[2];
					}
					num = Integer.parseInt(args[1]);
					dim = RandomMatrixChain(num);
					String filename = "random" + num + suffix + ".mcp";
					WriteToFile(filename, dim);
				}
			}
			else {
				System.out.println("Wrong argument: " + args[0]);
				System.exit(-1);
			}
			
			System.out.println("Matrix dimensions ");
			System.out.println("=================");
			num = dim.size()-1;
			for (int i=0; i<num; i++)
				System.out.print("[" + dim.get(i) + "x" + dim.get(i+1) + "]    ");
			System.out.println("\n");
			
			System.out.println("Random Solutions");
			System.out.println("================");

			for (int i=0; i<NUM_TEST; i++) {
				List<Integer> sol = RandomParenthesization(1, num);
			
				System.out.print("\nCost = " + ParenthesizationCost(dim, sol, 1, num) + "\t:\t");
				WriteParenthesization (sol, 1, num);
			}
			
			System.out.println("\n");
			System.out.println("Optimal Solution");
			System.out.println("================");
			
			List<Integer> sol = OptimalParenthesization(dim);
			
			System.out.print("Optimal Solution: ");
			WriteParenthesization (sol, 1, num);
			System.out.println("\nCost = " + ParenthesizationCost(dim, sol, 1, num));

			sol = OptimalParenthesization2(dim);
			
			System.out.print("Optimal Solution: ");
			WriteParenthesization (sol, 1, num);
			System.out.println("\nCost = " + ParenthesizationCost(dim, sol, 1, num));
		}

	}

	/**
	 * Computes the cost of a parenthesization for multiplying a sequence of matrices
	 * @param dim dimensions of each of the matrices
	 * @param sol  the parenthesization
	 * @param first first position in the sequence of matrices
	 * @param last last position in the sequence of matrices
	 * @return the cost of the chain product
	 */
	private static int ParenthesizationCost(List<Integer> dim, List<Integer> sol, int first, int last) {
		int cost = 0;
		
		if (first<last) {
			int k = sol.get(0);
			cost =  ParenthesizationCost(dim, sol.subList(1, k-first+1), first, k);
			cost += ParenthesizationCost(dim, sol.subList(k-first+1, sol.size()), k+1, last);
			cost += dim.get(first-1)*dim.get(k)*dim.get(last);
		}
		
		return cost;
	}

	/**
	 * Writes the parenthesization 
	 * @param sol  the parenthesization
	 * @param first first position in the sequence of matrices
	 * @param last last position in the sequence of matrices
	 */
	private static void WriteParenthesization(List<Integer> sol, int first, int last) {
		if (first == last)
			System.out.print("M"+first);
		else {
			int k = sol.get(0);
			System.out.print("(");
			WriteParenthesization(sol.subList(1, k-first+1), first, k);
			System.out.print(" x ");
			WriteParenthesization(sol.subList(k-first+1, sol.size()), k+1, last);
			System.out.print(")");
		}
		
	}

	/**
	 * Infinity value
	 */
	private static final int Infinity = Integer.MAX_VALUE;
	/**
	 * Computes the optimal parenthesization using dynamic programming
	 * @param dim dimensions of each of the matrices
	 * @return the optimal parenthesization
	 */
	private static List<Integer> OptimalParenthesization(List<Integer> dim) {
		int num = dim.size()-1;
		List<Integer> sol = new ArrayList<Integer>(num+1);
		int[][] C = new int[num][num];
		int[][] D = new int[num][num];
		
		// C[i][j] = minimum number of multiplications for the chain product of Mi...Mj
		// D[i][j] = k => the outer parentheses are (Mi...Mk)x(Mk+1...Mj)
		
		for (int i=0; i<num; i++)
			C[i][i] = 0;
		
		for (int i=num-2; i>=0; i--)
			for (int j=i+1; j<num; j++) {
				C[i][j] = Infinity;
				for (int k=i; k<j; k++) {
					int q = C[i][k] + C[k+1][j] + dim.get(i)*dim.get(k+1)*dim.get(j+1);
					if (q<C[i][j]) {
						C[i][j] = q;
						D[i][j] = k;
					}
				}
			}
		
		System.out.println("Optimal cost = " + C[0][num-1]);

		
		System.out.println("Cost matrix");
		System.out.print(" \t|");
		for (int j=0; j<num; j++)
			System.out.print("\t"+(j+1));
		System.out.print("\n-- \t+");
		for (int j=0; j<num; j++)
			System.out.print("\t--");
		for (int i=0; i<num; i++) {
			System.out.print("\n"+(i+1)+"\t|");
			for (int j=0; j<i; j++)
				System.out.print("\t");
			for (int j=i; j<num; j++)
				System.out.print("\t"+C[i][j]);
		}
		System.out.println();
		
		System.out.println("Decision matrix");
		System.out.print(" \t|");
		for (int j=0; j<num; j++)
			System.out.print("\t"+(j+1));
		System.out.print("\n-- \t+");
		for (int j=0; j<num; j++)
			System.out.print("\t--");
		for (int i=0; i<num; i++) {
			System.out.print("\n"+(i+1)+"\t|");
			for (int j=0; j<=i; j++)
				System.out.print("\t");
			for (int j=i+1; j<num; j++)
				System.out.print("\t"+(D[i][j]+1));
		}
		System.out.println();
		
		ReconstructSolution (sol, D, 0, num-1);
		return sol;
	}
	
	/**
	 * Computes the optimal parenthesization using dynamic programming
	 * @param dim dimensions of each of the matrices
	 * @return the optimal parenthesization
	 */
	private static List<Integer> OptimalParenthesization2(List<Integer> dim) {
		int num = dim.size()-1;
		List<Integer> sol = new ArrayList<Integer>(num+1);
		int[][] C = new int[num][num];
		int[][] D = new int[num][num];
		
		for (int i=0; i<num; i++)
			C[i][i] = 0;					// length-1 chains
		
		for (int l=2; l<=num; l++) {		// length-l chains
			for (int i=0; i<=num-l; i++) {	// i = first matrix in the chain
				int j = i + l - 1;			// j = last matrix in the chain
				C[i][j] = Infinity;
				for (int k=i; k<j; k++) {	// k = split point
					int q = C[i][k] + C[k+1][j] + dim.get(i)*dim.get(k+1)*dim.get(j+1);
					if (q<C[i][j]) {
						C[i][j] = q;
						D[i][j] = k;
					}
				}
			}
		}
		
		System.out.println("Optimal cost = " + C[0][num-1]);

		
		System.out.println("Cost matrix");
		System.out.print(" \t|");
		for (int j=0; j<num; j++)
			System.out.print("\t"+(j+1));
		System.out.print("\n-- \t+");
		for (int j=0; j<num; j++)
			System.out.print("\t--");
		for (int i=0; i<num; i++) {
			System.out.print("\n"+(i+1)+"\t|");
			for (int j=0; j<i; j++)
				System.out.print("\t");
			for (int j=i; j<num; j++)
				System.out.print("\t"+C[i][j]);
		}
		System.out.println();
		
		System.out.println("Decision matrix");
		System.out.print(" \t|");
		for (int j=0; j<num; j++)
			System.out.print("\t"+(j+1));
		System.out.print("\n-- \t+");
		for (int j=0; j<num; j++)
			System.out.print("\t--");
		for (int i=0; i<num; i++) {
			System.out.print("\n"+(i+1)+"\t|");
			for (int j=0; j<=i; j++)
				System.out.print("\t");
			for (int j=i+1; j<num; j++)
				System.out.print("\t"+(D[i][j]+1));
		}
		System.out.println();
		
		ReconstructSolution (sol, D, 0, num-1);
		return sol;
	}

	/**
	 * Reconstructs the optimal solution given the decision matrix
	 * @param sol the optimal solution
	 * @param D the decision matrix
	 * @param first first position in the sequence of matrices
	 * @param last last position in the sequence of matrices
	 */
	private static void ReconstructSolution(List<Integer> sol, int[][] D, int first, int last) {
		if (first < last) {
			int k = D[first][last];
			sol.add(k+1);
			ReconstructSolution (sol, D, first, k);
			ReconstructSolution (sol, D, k+1, last);
		}
		
	}

	/**
	 * Computes a random parenthesization
	 * @param i first position in the sequence of matrices
	 * @param j last position in the sequence of matrices
	 * @return a random parenthesization
	 */
	private static List<Integer> RandomParenthesization(int i, int j) {
		List<Integer> sol = new ArrayList<Integer>(j-i);
		if (i<j) {
			int k = r.nextInt(j-i) + i;
			sol.add(k);
			sol.addAll(RandomParenthesization(i, k));
			sol.addAll(RandomParenthesization(k+1, j));
		}
		return sol;
	}

	/**
	 * Writes the matrix dimensions to a file
	 * @param filename the name of the file
	 * @param dim the matrix dimensions
	 * @throws IOException if data cannot be written to file
	 */
	private static void WriteToFile(String filename, List<Integer> dim) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(filename));
     	
     	out.println(dim.size()-1);
     	for (int i: dim)
     		out.print(i + "\t");
     	out.println();
     	
     	out.close();		
     }

	/**
	 * Upper limit of the size of large matrices when randomizing the sequence
	 */
	private static final int LARGE_SIZE = 100;
	/**
	 * Upper limit of the size of small matrices when randomizing the sequence
	 */
	private static final int SMALL_SIZE = 5;
	/**
	 * Creates a random sequence of matrices of the given length
	 * @param num number of matrices in the sequence
	 * @return a list of the matrices dimensions
	 */
	private static List<Integer> RandomMatrixChain(int num) {
		List<Integer> matrixChain = new ArrayList<Integer>(num+1);

		for (int d=0; d<=num; d++)
			matrixChain.add((r.nextDouble()>.5)?(SMALL_SIZE + r.nextInt(LARGE_SIZE)):(1+r.nextInt(SMALL_SIZE)));
		return matrixChain;
	}

	/**
	 * Reads a matrix sequence from a file
	 * @param filename the name of the file
	 * @return the sequence of matrix dimensions
	 * @throws FileNotFoundException if the file cannot be found
	 */
	private static List<Integer> ReadDataFromFile(String filename) throws FileNotFoundException {
		Scanner inputFile = new Scanner (new File(filename));
		int num = inputFile.nextInt();
		List<Integer> matrixChain = new ArrayList<Integer>(num+1);
		
		for (int d=0; d<=num; d++)
			matrixChain.add(inputFile.nextInt());
		inputFile.close();		
		return matrixChain;
	}

}