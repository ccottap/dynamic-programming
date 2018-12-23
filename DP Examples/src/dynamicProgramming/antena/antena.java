
package dynamicProgramming.antena;
/**
 * Resolución del problema de la antena de telefonía
 * Ejercicio 12, Sección 3.8, pp. 82-90
 * @author ccottap
 *
 */
public class antena {

	public static void main(String[] args) {
		int[] peso = {2, 3, 5, 7, 11};
		int[] altura = {7, 15, 23, 29, 31}; // bloques ordenados por altura
		int M = 51;
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
	 * Devuelve el peso mínimo de la antena y el número de bloques de cada tipo.
	 * @param M altura de la antena
	 * @param peso peso de cada tipo de bloque
	 * @param altura altura de cada bloque
	 * @param sol el número de bloques de cada tipo (parámetro de salida)
	 * @return el peso mínimo de la antena
	 */
	public static double AntenaPesoMinimo (int M, int[] peso, int[] altura, int[] sol)
	{
		int n = peso.length;
		double[][] tabla = new double[n][M+1];
		
		RellenaTablaAntena (tabla, M, n, peso, altura);
		ReconstruyeSolucion (tabla, M, n, altura, sol);
		
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
				if (tabla[i][j] == inf)
					System.out.print("\too");
				else
					System.out.print("\t"+tabla[i][j]);
		}
		System.out.println();
		
		return tabla[0][M];
				
	}
	
	static double inf = Double.POSITIVE_INFINITY;
	
	private static double RellenaTablaAntena(double[][] tabla, int m, int n, int[] p, int[] a) {
		for (int i=0; i<n; i++)
			tabla[i][0] = 0;
		
		for (int i=n-1; i>=0; i--)
			for (int j=1; j<=m; j++) 
				if (a[i]>j) {
					if (i==(n-1))
						tabla[i][j] = inf;
					else
						tabla[i][j] = tabla[i+1][j];
				} else {
					if (i==(n-1))
						tabla[i][j] = tabla[i][j-a[i]] + p[i];
					else
						tabla[i][j] = Math.min(tabla[i+1][j], tabla[i][j-a[i]] + p[i]);
				}
		
		return tabla[0][m];
		
	}
	
	
	private static void ReconstruyeSolucion(double[][] tabla, int m, int n, int[] a, int[] sol) {
		int i,j;
		
		for (i=0; i<n; i++)
			sol[i] = 0;
		
		i = 0; j = m;
		if (tabla[i][j] != inf) {
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
	 * Devuelve el peso mínimo de la antena y el número de bloques de cada tipo.
	 * @param M altura de la antena
	 * @param peso peso de cada tipo de bloque
	 * @param altura altura de cada bloque
	 * @param sol el número de bloques de cada tipo (parámetro de salida)
	 * @return el peso mínimo de la antena
	 */
	public static double AntenaPesoMinimo2 (int M, int[] peso, int[] altura, int[] sol)
	{
		int n = peso.length;
		double[] tabla = new double[M+1];
		int[] decision = new int[M+1];
		
		RellenaTablaAntena2 (tabla, decision, M, n, peso, altura);
		ReconstruyeSolucion2 (decision, M, n, altura, sol);

		System.out.println("Matriz de costes: ");
		for (int j=0; j<=M; j++)
			System.out.print("\t"+j);
		System.out.println();
		for (int j=0; j<=M; j++)
			System.out.print("\t--");
		System.out.println();
		for (int i=0; i<=M; i++)
			if (tabla[i] == inf)
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
	
	private static double RellenaTablaAntena2(double[] tabla, int[] decision, int m, int n, int[] p, int[] a) {
		tabla[0] = 0;
		decision[0] = -1;
		
		for (int j=1; j<=m; j++) {
			tabla[j] = inf;
			decision[j] = -1;
			for (int i=0; (i<n) && (a[i]<=j); i++) { // asume que los bloques están ordenados por altura
				double q = p[i] + tabla[j-a[i]];
				if (q<tabla[j]) {
					tabla[j] = q;
					decision[j] = i;
				}
			}
				
		}
		
		return tabla[m];
		
	}
	
	
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
