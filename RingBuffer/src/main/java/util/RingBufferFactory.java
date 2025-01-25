package util;

import java.util.concurrent.BlockingQueue;
import buffer.RingBuffer;

public class RingBufferFactory<T> {

	/**
	 * Returns an synchronized (thread-safe) ring buffer
	 * with the given capacity
	 * @param capacity the capacity of the new ring buffer
	 * @return a new synchronized ring buffer
	 */
	public BlockingQueue<T> getSynchronizedBuffer(int capacity){
		return new RingBuffer<T> (capacity); 
	}


}
