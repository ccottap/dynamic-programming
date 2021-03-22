package es.uma.lcc.datastructures.tuple;

import java.util.Objects;

/**
 * Pair of elements (a,b)
 * @author ccottap
 * @param <F> class of the first element in the pair
 * @param <S> class of the second element in the pair
 *
 */
public final class Pair<F, S> {
	private final F first;		// first element
	private final S second;		// second element
	
	/**
	 * Creates a pair
	 * @param first the first element
	 * @param second the second element
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first element in the pair
	 * @return the first element
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Gets the second element in the pair
	 * @return the second element
	 */
	public S getSecond() {
		return second;
	}

	
	@Override    
    public boolean equals(Object o) {        
    	if (this == o)
    		return true;
        if ((o == null) || (getClass() != o.getClass())) 
        	return false;  
        Pair<?,?> op = (Pair<?,?>)o;
        return first.equals(op.first) && second.equals(op.second);
    }
    
    @Override    
    public int hashCode() {     
    	return Objects.hash(first, second);
    }
    
    
	/**
	 * Returns a printable string representation of the pair
	 * @return a string representing the pair
	 */
	public String toString()
	{

		return "[" + first + ", " + second + "]";
	}

}
