package es.uma.lcc.dynamicprogramming.wallstreet;


/**
 * Resolución del problema "El Inversor de Wall Street"
 * Ejercicio 22, sección 3.17, pp. 147-153
 * @author ccottap
 *
 */
public class WallStreet {

	/**
	 * Main method
	 * @param args (unused)
	 */
	public static void main(String[] args) {
		int[] prices = {8, 19, 1, 5, 8, 11, 1, 13, 20, 17, 4};
		int C = 2;
		int V = 3;
		int n = prices.length;
		int[] sol = new int[n];
			
		
		System.out.print("Precios");
		for (int i=0; i<n; i++)
			System.out.print(" " + prices[i]);
		System.out.println();
		System.out.println("Restricción de compras: " + C + " acciones/día");
		System.out.println("Restricción de ventas: " + V + " acciones/día");

		double maxBeneficio = maxBenefit (prices, C, V, sol);

		System.out.println("Beneficio máximo: " + maxBeneficio);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			System.out.print(" " + sol[i]);
		System.out.println();
	}

	/**
	 * Infinity value
	 */
	private static final int Infinity = Integer.MAX_VALUE;
	
	/**
	 * Computes the optimal stock-market strategy given the prices of the
	 * shares in each day and the sell/buy constraints
	 * @param prices array with the prices each day
	 * @param C maximum number of shares that can be bought any day
	 * @param V maximum number of shares that can be sold any day
	 * @param sol array with the optimal strategy
	 * @return maximum benefit attainable
	 */
	public static double maxBenefit(int[] prices, int C, int V, int[] sol) {
		int n = prices.length;					// number of days
		int maxShares = C*(n-1);				// maximum number of shares you can own the last day
		int [][] B = new int [n][maxShares+1];
		int [][] D = new int [n][maxShares+1];
		
		// B(i,j) = maximum benefit for day i to day (n-1) given you start with j shares
		// D(i,j) = optimal decision in each case (if negative, shares to buy; if positive, shares to sell)
		
		for (int j=0; j<=maxShares; j++) {
			D[n-1][j] = Math.min(j, V); 		// base case: the last day you sell as many
			B[n-1][j] = prices[n-1]*D[n-1][j];	// shares as you have (V at most).
		}
		
		for (int i=n-2; i>=0; i--) 
			for (int j=0; j<=(i*C); j++) {
				B[i][j] = -Infinity;
				for (int k=-C; k<=Math.min(j, V); k++) { // general case: buy 0..C or sell 1..min(j,V) 
					int b = k*prices[i] + B[i+1][j-k];

					if (b>B[i][j]) {
						B[i][j] = b;
						D[i][j] = k;
					}
				}
			}
		
		// Reconstruction of the solution
		int k = 0;
		for (int i=0; i<n; i++) {
			sol[i] = D[i][k];
			k -= D[i][k];
		}
		
		System.out.println("Matriz de costes:");
		System.out.print(" \t|");
		for (int j=0; j<=maxShares; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=maxShares; j++)
			System.out.print("\t--");
		for (int i=0; i<n; i++) {
			System.out.print("\n"+i+"\t|");
			for (int j=0; j<=i*C; j++)
					System.out.print("\t"+B[i][j]);
		}
		System.out.print("\n\n");

		System.out.println("Matriz de decisión:");
		System.out.print(" \t|");
		for (int j=0; j<=maxShares; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=maxShares; j++)
			System.out.print("\t--");
		for (int i=0; i<n; i++) {
			System.out.print("\n"+i+"\t|");
			for (int j=0; j<=i*C; j++)
					System.out.print("\t"+D[i][j]);
		}
		System.out.println();

		
		return B[0][0];
	}

}
