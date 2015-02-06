/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * @author jvallabh, saket, nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	LinkedList<Token> tList;
	ListIterator<Token> iterator;
	Token tCurrent;
	
	public TokenStream() {
		tList = new LinkedList<Token>();
		iterator = null;
	}
	
	public int getSize() {
		return tList.size();
	}
	
	public void add(Token token) {
		tList.add(token);
	}
	
	public void insert(Token token) {
		iterator.add(token);
	}
	
	public void add(TokenStream stream) {
		if (stream == null) return;
		ListIterator<Token> itr = stream.getIterator();
		stream.reset();
		while (itr.hasNext())
			tList.add(itr.next());
	}
	
	public ListIterator<Token> getIterator() {
		if (iterator == null) iterator = tList.listIterator();
		return iterator;
	}
	
	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS
		return getIterator().hasNext();
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		if (getIterator().hasNext()) {
			tCurrent = iterator.next();
		} else {
			tCurrent = null;
		}
		return tCurrent;
	}
	
	public void insert(int index, Token element) {
		tList.add(index, element);
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		tCurrent = null;
		if (getIterator().nextIndex() == 0) return;
		iterator.remove();
	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		iterator = tList.listIterator();
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		int curIndex = getIterator().nextIndex();
		iterator = null;
		add(stream);
		iterator = tList.listIterator(curIndex);
	}
	
	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		return tCurrent;
	}
	
}
