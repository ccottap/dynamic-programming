
package dynamicProgramming.antena;


/**
 * Resolución del problema de la antena de telefonía
 * Ejercicio 12, Sección 3.8, pp. 82-90
 * @author ccottap
 *
 */
public class antena {
	/**
	 * Main function solving a problem instance
	 * @param args antenna height (optional)
	 */
	public static void main(String[] args) {
		int[] peso = {2, 3, 5, 7, 11};		// peso de cada tipo de bloque
		int[] altura = {7, 15, 23, 29, 31}; // bloques ordenados por altura
		int M = (args.length==0)?51:Integer.parseInt(args[0]); // altura de la antena
		int n = peso.length;
		
		System.out.println(n + " tipos de bloques");
		System.out.println("----------------------");
		System.out.println(" \tpeso\taltura");
		System.out.println("----------------------");

		for (int i=0; i<n; i++)
			System.out.println(i + "\t" + peso[i] + "\t" + altura[i]);
		System.out.println("----------------------");

		System.out.println("Altura objetivo: " + M);
		
		System.out.println("\n\nEnfoque #1");
		System.out.println("----------\n");

		int[] sol = new int[n];
		double minPeso = AntenaPesoMinimo (M, peso, altura, sol);
		
		System.out.println("Peso mínimo: " + minPeso);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			System.out.print(" " + sol[i]);
		System.out.println();

		System.out.println("\n\nEnfoque #2");
		System.out.println("----------\n");

		minPeso = AntenaPesoMinimo2 (M, peso, altura, sol);
		
		System.out.println("Peso mínimo: " + minPeso);
		System.out.print("Solución");
		for (int i=0; i<n; i++)
			System.out.print(" " + sol[i]);
		System.out.println();
	}
	
	/**
	 * Computes the optimal weight of the antenna and the number of blocks of each type.
	 * @param M height of the antenna
	 * @param p weight of each block type
	 * @param a height of each block type
	 * @param sol number of blocks of each type (output parameter)
	 * @return optimal weight of the antenna
	 */
	public static double AntenaPesoMinimo (int M, int[] p, int[] a, int[] sol)
	{
		int n = p.length;
		double[][] tabla = new double[n][M+1];
		
		RellenaTablaAntena (tabla, M, n, p, a);
		ReconstruyeSolucion (tabla, M, n, a, sol);
		
		System.out.println("Matriz de costes");
		System.out.print(" \t|");
		for (int j=0; j<=M; j++)
			System.out.print("\t"+j);
		System.out.print("\n-- \t+");
		for (int j=0; j<=M; j++)
			System.out.print("\t--");
		for (int i=0; i<n; i++) {
			System.out.print("\n"+i+"\t|");
			for (int j=0; j<=M; j++)
				if (tabla[i][j] == Infinity)
					System.out.print("\too");
				else
					System.out.print("\t"+tabla[i][j]);
		}
		System.out.println();
		
		return tabla[0][M];
				
	}
	
	/**
	 * Infinity is used as the cost of subproblems w/o feasible solution
	 */
	private static final double Infinity = Double.POSITIVE_INFINITY;
	/**
	 * Fills the table with the optimal solution to each subproblem
	 * @param tabla the table of optimal solutions (output parameter)
	 * @param m the height of the antenna
	 * @param n the number of block types
	 * @param p the weight of each block type
	 * @param a the height of each block type
	 * @return the optimal weight of the antenna
	 */
	private static double RellenaTablaAntena(double[][] tabla, int m, int n, int[] p, int[] a) {
		// tabla[i][j] = optimal weight of an antenna of height j
		// built with block types i..n-1
		
		for (int i=0; i<n; i++)
			tabla[i][0] = 0;							// height = 0 -> weight = 0
		
		for (int i=n-1; i>=0; i--)
			for (int j=1; j<=m; j++) 
				if (a[i]>j) {
					if (i==(n-1))						
						tabla[i][j] = Infinity;			// no more block types -> no solution
					else
						tabla[i][j] = tabla[i+1][j];	// block type i is skipped
				} else {
					if (i==(n-1))
						tabla[i][j] = tabla[i][j-a[i]] + p[i]; // block type i is used
					else
						tabla[i][j] = Math.min(tabla[i+1][j], tabla[i][j-a[i]] + p[i]); // a choice is made
				}
		
		return tabla[0][m];
		
	}
	
	/**
	 * Reconstruct the optimal solution given the table computed in {@link RellenaTablaAntena}
	 * @param tabla the table of optimal solutions
	 * @param m the height of the antenna
	 * @param n the number of block types
	 * @param a the height of each block type
	 * @param sol the number of copies of each block type used (output parameter)
	 */
	private static void ReconstruyeSolucion(double[][] tabla, int m, int n, int[] a, int[] sol) {
		int i,j;
		
		for (i=0; i<n; i++)
			sol[i] = 0;
		
		i = 0; j = m;
		if (tabla[i][j] != Infinity) {
			while (j>0) {
				if ((i==(n-1)) || (tabla[i][j] != tabla[i+1][j])) {
					sol[i]++;
					j -= a[i];
				}
				else
					i++;
			}
		}
	}


	/**
	 * Computes the optimal weight of the antenna and the number of blocks of each type.
	 * @param M height of the antenna
	 * @param p weight of each block type
	 * @param a height of each block type
	 * @param sol number of blocks of each type (output parameter)
	 * @return optimal weight of the antenna
	 */
	public static double AntenaPesoMinimo2 (int M, int[] p, int[] a, int[] sol)
	{
		int n = p.length;
		double[] tabla = new double[M+1];
		int[] decision = new int[M+1];
		
		RellenaTablaAntena2 (tabla, decision, M, n, p, a);
		ReconstruyeSolucion2 (decision, M, n, a, sol);

		System.out.println("Matriz de costes: ");
		for (int j=0; j<=M; j++)
			System.out.print("\t"+j);
		System.out.println();
		for (int j=0; j<=M; j++)
			System.out.print("\t--");
		System.out.println();
		for (int i=0; i<=M; i++)
			if (tabla[i] == Infinity)
				System.out.print("\too");
			else
				System.out.print("\t"+tabla[i]);
		System.out.println();
		System.out.println("Matriz de decisiones: ");
		for (int j=0; j<=M; j++)
			System.out.print("\t"+j);
		System.out.println();
		for (int j=0; j<=M; j++)
			System.out.print("\t--");
		System.out.println();
		for (int i=0; i<=M; i++)
			System.out.print("\t" + decision[i]);
		System.out.println();		
		
		return tabla[M];
				
	}
	
	/**
	 * Fills the table with the optimal solution to each subproblem
	 * @param tabla the table of optimal solutions (output parameter)
	 * @param decision table with the optimal decision associated to each subproblems (output parameter)
	 * @param m the height of the antenna
	 * @param n the number of block types
	 * @param p the weight of each block type
	 * @param a the height of each block type
	 * @return the optimal weight of the antenna
	 */
	private static double RellenaTablaAntena2(double[] tabla, int[] decision, int m, int n, int[] p, int[] a) {
		
		// tabla [j] = optimal weight of an antenna of height j using any block
		// type with height a_i<=j
		
		tabla[0] = 0;		// no height -> no weight
		decision[0] = -1;	// no block is used
		
		for (int j=1; j<=m; j++) {
			tabla[j] = Infinity;
			decision[j] = -1;
			for (int i=0; (i<n) && (a[i]<=j); i++) { // blocks assumed to be sorted by height
				double q = p[i] + tabla[j-a[i]];
				if (q<tabla[j]) {
					tabla[j] = q;
					decision[j] = i;
				}
			}
				
		}
		
		return tabla[m];
		
	}
	
	/**
	 * Reconstruct the optimal solution given the table computed in {@link RellenaTablaAntena2}
	 * @param d the table of optimal decisions
	 * @param m the height of the antenna
	 * @param n the number of block types
	 * @param a the height of each block type
	 * @param sol the number of copies of each block type used (output parameter)
	 */
	private static void ReconstruyeSolucion2(int[] d, int m, int n, int[] a, int[] sol) {
		int j;
		
		for (int i=0; i<n; i++)
			sol[i] = 0;
		
		j = m;
		while (d[j]>=0) {
			sol[d[j]]++;
			j-=a[d[j]];
		}
	}	

}
