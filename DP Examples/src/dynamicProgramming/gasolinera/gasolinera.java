
package dynamicProgramming.gasolinera;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Resolución del problema de el coche de alquiler.
 * Ejercicio 23, sección 3.18, pp. 173-185
 * @author ccottap
 *
 */
public class gasolinera {

	/**
	 * Main function solving a problem instance
	 * @param args tank capacity (optional)
	 */
	public static void main(String[] args) {
		//                  g0   g1   g2   g3   g4   g5   g6   g7   g8   g9
		int[] consumo   = {       6,   9,  15,  21,  33,  15,  12,   4,  20};
		double[] precio = {1.5, 1.2, 1.1, 1.8, 1.6, 1.5, 1.2, 1.1, 1.7, 1.9}; 
		int deposito = (args.length==0)?50:Integer.parseInt(args[0]);
		int n = consumo.length;		
		int[] sol = new int [n];
		
		System.out.println("Datos del problema");
		System.out.println("==================");

		System.out.print("\t\t");
		for (int i=0; i<=n; i++)
			System.out.print("\tg" + i);
		System.out.println();
		System.out.print("Consumo\t\t\t");
		for (int i=0; i<n; i++)
			System.out.print("\t" + consumo[i]);
		System.out.println();
		System.out.print("Precio l.\t");
		for (int i=0; i<=n; i++)
			System.out.print("\t" + precio[i]);
		System.out.println();
		System.out.println("Capacidad depósito: " + deposito);
		System.out.println();

		
		System.out.println("Cálculo del número mínimo de paradas");
		System.out.println("====================================");

		int minParadas = ParadasMinimas (consumo, deposito, sol);
		
		System.out.println("\n\nParadas mínimas: " + minParadas);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			if (sol[i]>0)
				System.out.print(" g" + (i+1));
		System.out.print("\n\n");

		System.out.println("Cálculo del coste mínimo de repostaje");
		System.out.println("=====================================");
		
		System.out.println("Enfoque bottom-up");
		System.out.println("-----------------");

		double minGasto = GastoMinimo (consumo, precio, deposito, sol);
		
		System.out.format("\n\nGasto mínimo: %.1f EUR\n", minGasto);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			System.out.print(" " + sol[i]);
		System.out.println("\n\n");

		System.out.println("Enfoque top-down");
		System.out.println("----------------");
		minGasto = GastoMinimoTopDown (consumo, precio, deposito, sol);
		
		System.out.format("\n\nGasto mínimo: %.1f EUR\n", minGasto);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			System.out.print(" " + sol[i]);
		System.out.println();
	}


	static double inf = Double.POSITIVE_INFINITY;

	private static double GastoMinimo(int[] consumo, double[] precio, int deposito, int[] sol) {
		int n = consumo.length;
		double[][] P = new double[n+1][deposito+1];
		int[][] D = new int[n+1][deposito+1];
		
		for (int j=0; j<=deposito; j++) {
			P[n][j] = 0;
			D[n][j] = 0;
		}
		
		for (int i=n-1; i>=0; i--)
			for (int j=0; j<=deposito; j++) {
				P[i][j] = inf;
				for (int k=Math.max(0, consumo[i]-j); k<=deposito-j; k++) {
					double r = k*precio[i] + P[i+1][j+k-consumo[i]];
					if (r < P[i][j]) {
						P[i][j] = r;
						D[i][j] = k;
					}
				}
			}

		System.out.println("\nMatriz de costes/decisión:\n");

		System.out.print(" \t|");
		for (int j=0; j<=deposito; j++)
			System.out.print("\t\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=deposito; j++)
			System.out.print("\t\t--");
		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t\t");
			for (int j=0; j<=deposito; j++) {
				System.out.format("%3.1f / %2d\t", P[i][j], D[i][j]);
			}
		}
		
		System.out.println();
		
		ReconstruyeSolucionGastoOptimo (consumo, precio, n, D, sol);

		return P[0][0];
	}
	
	private static double GastoMinimoTopDown(int[] consumo, double[] precio, int deposito, int[] sol) {
		int n = consumo.length;
		double[][] P = new double[n+1][deposito+1];
		int[][] D = new int[n+1][deposito+1];
		
		for (int i=0; i<=n; i++)
			for (int j=0; j<=deposito; j++) 
				P[i][j] = -1;
		
		GastoMinimoTopDownRec(consumo, precio, deposito, n, P, D, 0, 0);

		System.out.println("\nMatriz de costes/decisión:\n");

		System.out.print(" \t|");
		for (int j=0; j<=deposito; j++)
			System.out.print("\t\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=deposito; j++)
			System.out.print("\t\t--");
		for (int i=0; i<=n; i++) {
			System.out.print("\n"+i+"\t|\t\t");
			for (int j=0; j<=deposito; j++) {
				if (P[i][j]<0)
					System.out.print("      /   \t");
				else
					System.out.format("%3.1f / %2d\t", P[i][j], D[i][j]);
			}
		}
		
		System.out.println();
		
		ReconstruyeSolucionGastoOptimo (consumo, precio, n, D, sol);
		
		return P[0][0];
	}


	

	private static void ReconstruyeSolucionGastoOptimo(int[] consumo, double[] precio, int n, int[][] d, int[] sol) {
		int j=0;	// combustible inicial
		System.out.println("\nReconstrucción de la solución:");
		for (int i=0; i<n; i++) {
			System.out.format("depósito = %2dl\t", j);
			sol[i] = d[i][j];
			j += sol[i] - consumo[i];
			System.out.format("g%d -> %2dl (%.1f EUR)\n", i, sol[i], sol[i]*precio[i]);
		}
		System.out.format("depósito = %2dl\tg%d\n", j, n);

	}

	private static double GastoMinimoTopDownRec(int[] consumo, double[] precio, int deposito, int n, double[][] p, int[][] d,
			int i, int j) {
		
		if (p[i][j]<0) {
			if (i==n) {
				p[i][j] = 0;
				d[i][j] = 0;
			}
			else {
				p[i][j] = inf;
				for (int k=Math.max(0, consumo[i]-j); k<=deposito-j; k++) {
					double r = k*precio[i] + GastoMinimoTopDownRec(consumo, precio, deposito, n, p, d, i+1, j+k-consumo[i]);
					if (r < p[i][j]) {
						p[i][j] = r;
						d[i][j] = k;
					}
				}
			}
		}
		
		return p[i][j];
	}

	private static int ParadasMinimas(int[] consumo, int deposito, int[] sol) {
		int n = consumo.length;
		int[] P = new int[n];
		int[] D = new int[n];
		ArrayList<HashSet<Integer>> F = new ArrayList<HashSet<Integer>>();
		
		System.out.println("Conjuntos de alcanzabilidad F_i = {gj | gj se puede alcanzar desde gi con depósito lleno}:");

		for (int i=0; i<n; i++) {
			HashSet<Integer> Fi = new HashSet<Integer>();
			int j = deposito;
			System.out.print("\t- F_"+i+" = { ");
			for (int k=i+1; (k<=n) && (j>=consumo[k-1]); k++) {
				Fi.add(k);
				j-=consumo[k-1];
				System.out.print("g" + k+" ");
			}
			System.out.println("}");
			F.add(Fi);
		}
		

		
		for (int i=n-1; i>=0; i--) {
			HashSet<Integer> Fi = F.get(i);
			if (Fi.contains(n)) {			// Llegamos al destino directamente desde g_i
				P[i] = 0;
				D[i] = 0;
			}
			else {							// Hay que parar por el camino
				P[i] = n+1;					// Inicializamos al peor valor posible. 
				for (int k : Fi) {
					if (P[i]>(1+P[k])) {	// Si parar en g_k es mejor que lo que teníamos, se actualiza.
						P[i] = 1+P[k];
						D[i] = k;
					}
				}
			}
		}
		
		// Reconstrucción de la solución óptima
		for (int i=0; i<n; i++)
			sol[i] = 0;
		for (int k=D[0]; k>0; k = D[k])
			sol[k-1] = 1;
		
		System.out.println("\nMatriz de costes:\n");

		System.out.print(" \t|");
		for (int j=0; j<n; j++)
			System.out.print("\tg"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<n; j++)
			System.out.print("\t--");
		System.out.print("\nP\t|\t");
		for (int j=0; j<n; j++) {
			System.out.print(P[j]+"\t");
		}
		
		System.out.println("\n\nMatriz de decisión:\n");

		System.out.print(" \t|");
		for (int j=0; j<n; j++)
			System.out.print("\tg"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<n; j++)
			System.out.print("\t--");
		System.out.print("\nD\t|\t");
		for (int j=0; j<n; j++) {
			System.out.print(D[j]+"\t");
		}
		System.out.println();
		
		return P[0];
	}
	
	
}
