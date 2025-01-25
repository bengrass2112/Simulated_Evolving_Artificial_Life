package buffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;

public class RingBuffer<T> implements BlockingQueue<T> {
	
	/** An array of elements. */
	Object[] elements;
	
	/** The current index of the head. */
	int head;
	
	/** The current index of the tail. */
	int tail;
	
	/** Current number of elements in the queue. */
	int size;
	
	public RingBuffer(int capacity) {
		elements = new Object[capacity];
		head = 0;
		tail = 0;
		size = 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T remove() {
		if(isEmpty())
			throw new NoSuchElementException();
		T temp = (T) elements[head];
		head = (head + 1) % elements.length;
		size--;
		return temp;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T poll() {
		if(isEmpty())
			return null;
		T temp = (T) elements[head];
		head = (head + 1) % elements.length;
		size--;
		return temp;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T element() {
		if(isEmpty())
			throw new NoSuchElementException();
		return (T) elements[head];
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peek() {
		if(isEmpty())
			return null;
		return (T) elements[head];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean add(T e) {
		if (e == null) throw new NullPointerException();
		if (size < elements.length) {
			elements[tail] = e;
			tail = (tail + 1) % elements.length;
			size++;
			return true;
		} else {
			throw new IllegalStateException();
		}
			
	}

	@Override
	public boolean offer(T e) {
		if (e == null) throw new NullPointerException();
		if (size < elements.length) {
			elements[tail] = e;
			tail = (tail + 1) % elements.length;
			size++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void put(T e) throws InterruptedException {
		// TODO Fix me!
		if (e == null) throw new NullPointerException();
		if (size < elements.length) {
			elements[tail] = e;
			tail = (tail + 1) % elements.length;
			size++;
		} 
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public T take() throws InterruptedException {
		// TODO Fix me!
		if(isEmpty())
			return null;
		T temp = (T) elements[head];
		head = (head + 1) % elements.length;
		return temp;
	}
	
	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < size; i ++) {
			if(elements[(head + i) % elements.length].equals(o)) return true;
		}
		return false;
	}
	
	@Override
	public int remainingCapacity() {
		return elements.length - size();
	}

	@Override
	public boolean remove(Object o) {
		for (int i = 0; i < size; i ++) {
			int ind = (head + i) % elements.length;
			if(elements[ind].equals(o)) {
				head = (head + 1) % elements.length;
				size--;
				
				//shift everything over by one
				for(int j = ind; j != tail; j = (j - 1 + elements.length) % elements.length) {
					elements[j] = elements[(j - 1 + elements.length) % elements.length];
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection c, int maxElements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator iterator() {
		throw new UnsupportedOperationException();
	}

}
