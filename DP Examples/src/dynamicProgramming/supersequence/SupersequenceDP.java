
package dynamicProgramming.supersequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/**
 * Shortest Common Supersequence Problem
 * Ejercicio 7, sección 3.5, pp. 64-69
 * @author ccottap
 *
 */
public class SupersequenceDP {

	/**
	 * class-level random generator
	 */
	static Random r = new Random(1);

	/**
	 * Finds the shortest common supersequence of two strings
	 * @param args command-line arguments (to read data from file or generate at random)
	 * @throws FileNotFoundException if an attempt is made to read from an inexistent file
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length<1) {
			System.out.println("java SupersequenceDP (-f|-g) <arguments>");
			System.out.println("\t-f: reads data from a file");
			System.out.println("\t-g: generates a problem instance at random with specified parameters");
		}
		else {
			String s1 = "";
			String s2 = "";
			
			if (args[0].equals("-f")) {
				if (args.length < 2) {
					System.out.println("java SupersequenceDP -f <filename>");
					System.exit(-1);
				}
				else {
					Scanner inputFile = new Scanner (new File(args[1]));
					s1 = inputFile.next();
					s2 = inputFile.next();
					inputFile.close();
				}
			}
			else if (args[0].equals("-g")) {
				if (args.length < 4) {
					System.out.println("java SupersequenceDP -g <lenght1> <lenght2> <alphabet_size> [<random seed>]");
					System.exit(-1);
				}
				else {
					if (args.length > 4)
						r = new Random(Integer.parseInt(args[4]));
					int alphabet_size = Integer.parseUnsignedInt(args[3]);
					s1 = RandomString(Integer.parseUnsignedInt(args[1]), alphabet_size);
					s2 = RandomString(Integer.parseUnsignedInt(args[2]), alphabet_size);
				}
			}
			else {
				System.out.println("Wrong argument: " + args[0]);
				System.exit(-1);
			}
			System.out.println("String #1 (length = " + s1.length() + "): " + s1);
			System.out.println("String #2 (length = " + s2.length() + "): " + s2);
			
			String s = ShortestCommonSupersequence (s1, s2);

			System.out.println("Superseq. (length = " + s.length() + "): " + s);
		}

	}

	/**
	 * Determines the shortest common supersequence of two strings
	 * @param s1 a string
	 * @param s2 another string
	 * @return the shortest common supersequence
	 */
	private static String ShortestCommonSupersequence(String s1, String s2) {
		int n1 = s1.length();
		int n2 = s2.length();
		int[][] S = new int[n1+1][n2+1];
		String s;
		
		// S[i][j] = shortest common supersequence of s1[i..n1-1] and s2[j..n2-1]
		
		for (int i=0; i<n1; i++)
			S[i][n2] = n1-i;
		for (int j=0; j<n2; j++)
			S[n1][j] = n2-j;
		S[n1][n2] = 0;
		
		for (int i=n1-1; i>=0; i--)
			for (int j=n2-1; j>=0; j--) {
				if (s1.charAt(i) == s2.charAt(j)) 
					S[i][j] = 1 + S[i+1][j+1];
				else
					S[i][j] = 1 + Math.min(S[i+1][j], S[i][j+1]);					
			}
		
		System.out.println("\nCost matrix:\n");

		System.out.print(" \t|");
		for (int j=0; j<=n2; j++)
			System.out.print("\t"+j);
		System.out.print("\n   \t|");
		for (int j=0; j<n2; j++)
			System.out.print("\t"+s2.charAt(j));
		System.out.print("\t[END]\n---- \t+");
		for (int j=0; j<=n2; j++)
			System.out.print("\t----");
		for (int i=0; i<=n1; i++) {
			if (i<n1)
				System.out.print("\n"+i + " " + s1.charAt(i)+"\t|\t");
			else
				System.out.print("\n" + i + "[END]\t|\t");
			for (int j=0; j<=n2; j++) {
				System.out.format("%d \t", S[i][j]);
			}
		}
		
		System.out.println("\n\nReconstruction:\n");
		System.out.println("S_ij\ti\tj\ts1_i\ts2_j\ts");
		System.out.println("----\t----\t----\t----\t----\t----");
		
		s = "";
		int i=0, j=0;
		for (i=0, j=0; (i<n1) && (j<n2); ) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(j);
			System.out.print(S[i][j] + "\t" + i + "\t" + j + "\t" + c1 + "\t" + c2);
			if (c1 == c2) {
				s = s + c1;
				i++;
				j++;
			}
			else if (S[i][j] == (1 + S[i+1][j])) {
				s = s + c1;
				i++;
			}
			else {
				s = s + c2;
				j++;
			}
			System.out.println("\t" + s);

		}
		
		int ls = s.length();
		if (i<n1) {
			s = s + s1.substring(i, n1);
			for (int k=i; k<n1; k++)
				System.out.println(S[k][j] + "\t" + k + "\t" + j + "\t" + s1.charAt(k) + "\t[END]\t" + s.substring(0,ls+k-i+1));
		}
		else if (j<n2) {
			s = s + s2.substring(j, n2);		
			for (int k=j; k<n2; k++)
				System.out.println(S[i][k] + "\t" + i + "\t" + k + "\t[END]\t" + s2.charAt(k) + "\t" + s.substring(0,ls+k-j+1));
		}
		System.out.println(S[n1][n2] + "\t" + n1 + "\t" + n2 + "\t[END]\t[END]\t" + s);
		System.out.println();
		return s;
	}

	/**
	 * Creates a random string of the given length and alphabet size
	 * @param len the length of the string
	 * @param alphabet_size the size of the alphabet from which symbols are drawn
	 * @return a string of the given length and alphabet size
	 * @throws RuntimeException if the alphabet size is greater than 256
	 */
	private static String RandomString(int len, int alphabet_size) throws RuntimeException {
		String s = "";
		int base = 0;
		
		if (alphabet_size <= 26) 
			base = (int)'a';
		else if (alphabet_size <= 94)
			base = (int)'!';
		else if (alphabet_size > 256)
			throw new RuntimeException("Alphabet size cannot exceed 256");
		
		for (int i=0; i<len; i++) {
			s = s + (char)(base + r.nextInt(alphabet_size));
		}
	
		return s;
	}

}
