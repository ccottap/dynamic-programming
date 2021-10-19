
package es.uma.lcc.dynamicprogramming.playoffs;

import java.util.Locale;

/**
 * Resolución del problema "Los playoffs de la Liga Mundial"
 * Ejercicio 17, sección 3.13, pp. 113-118 (1ra ed.); pp. 135-140 (2da ed.)
 * @author ccottap
 *
 */
public class PlayOffs {

	/**
	 * Computes the probability a team A wins n games before team B
	 * @param args command-line parameters: p and n (both optional)
	 */
	public static void main(String[] args) {
		double p = (args.length<1)?0.56:Double.parseDouble(args[0]);
		int    n = (args.length<2)?10:Integer.parseInt(args[1]);
		
		double v = winningProbability(p, n);
		
		System.out.println("Winning probability (" + p + ", " + n + ") = " + v);

	}

	/**
	 * Computes the probability A wins n matches before B
	 * @param p probability A wins a game
	 * @param n number of games to win the playoff
	 * @return the probability A wins the playoff
	 */
	public static double winningProbability(double p, int n) {
		double[][] P = new double[n+1][n+1];
		double q = 1.0 - p;
		
		// P[i][j] = probability that A wins the playoff given that they
		// have already won i games and B has won j games
		
		for (int i=n-1; i>=0; i--) {
			P[i][n] = 0.0;	// if B has won n games, A cannot win the playoff
			P[n][i] = 1.0;	// if A has won n games, A has won the playoff
		}

		for (int i=n-1; i>=0; i--) 
			for (int j=n-1; j>=0; j--) 
				P[i][j] = p*P[i+1][j] + q*P[i][j+1];
		

		System.out.print(" \t|");
		for (int j=0; j<=n; j++)
			System.out.print("\t"+j);
		System.out.print("\n------ \t+");
		for (int j=0; j<=n; j++)
			System.out.print("\t------");
		for (int i=0; i<n; i++) {
			System.out.print("\n"+i+"\t|\t");
			for (int j=0; j<=n; j++) {
				System.out.format(Locale.US, "%1.4f\t", P[i][j]);
			}
		}
		System.out.print("\n"+n+"\t|\t");
		for (int j=0; j<n; j++) {
			System.out.format(Locale.US, "%1.4f\t", P[n][j]);
		}
		System.out.println("\n");

		
		return P[0][0];
	}

}
