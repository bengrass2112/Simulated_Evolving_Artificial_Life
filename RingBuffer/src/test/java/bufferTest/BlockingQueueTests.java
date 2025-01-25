package bufferTest;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import util.RingBufferFactory;
import org.junit.jupiter.api.Test;

public class BlockingQueueTests {
	private long sleep = 500;
	boolean failed = false;

	private <T> BlockingQueue<T> getQueue(int cap){
		return new RingBufferFactory<T>().getSynchronizedBuffer(cap);
	}

	//Non-threaded tests
	
	/**
	 * Creates a queue, adds to it, and tests the size
	 */
	@Test
	public void testAdd(){
		BlockingQueue<String> q = getQueue(5);
		assertTrue(q.add("A"));
		assertTrue(q.add("B"));
		assertTrue(q.add("C"));
		assertEquals(3, q.size());
		q= getQueue(1);
		boolean except= false;

		try{
			q.add("A");
			q.add("B");
		} catch(IllegalStateException e){
			except= true;
		} finally {
			assertTrue(except, "Expected exception");
		}
	}

	/**
	 * Creates a queue, adds to it, and tests the size
	 */
	@Test
	public void testOffer(){
		BlockingQueue<String> q = getQueue(5);
		assertTrue(q.offer("A"));
		assertTrue(q.offer("B"));
		assertTrue(q.offer("C"));
		assertEquals(3, q.size());
	}

	@Test
	public void testPoll(){
		BlockingQueue<String> q = getQueue(5);
		assertNull(q.poll());
		q.offer("A");
		assertEquals("A",q.poll());
		assertNull(q.poll());
	}


	@Test
	public void testPeek(){
		BlockingQueue<String> q = getQueue(5);
		assertNull(q.peek());
		q.offer("A");
		assertEquals("A",q.peek());
		q.remove();
		assertNull(q.peek());
	}

	@Test
	public void testElement(){
		BlockingQueue<String> q = getQueue(5);

		boolean except = false;
		try{
			q.element();
		} catch(NoSuchElementException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}		

		q.offer("A");
		assertEquals("A",q.element());
		q.remove();

		except = false;
		try{
			q.element();
		} catch(NoSuchElementException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}		
	}

	/**
	 * Creates a queue, adds to it, removes from it, and tests the size
	 */
	@Test
	public void testRemove(){
		BlockingQueue<String> q = getQueue(5);
		q.add("A");
		q.add("B");
		q.add("C");
		assertEquals("A",q.remove());
		assertEquals("B",q.remove());
		q.add("D");
		assertEquals("C",q.remove());
		assertEquals("D",q.remove());
		boolean except= false;
		try{
			q.remove();
		} catch(NoSuchElementException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}
		assertEquals(0, q.size());
	}

	@Test
	public void testRemoveObject(){
		BlockingQueue<String> q = getQueue(5);
		assertFalse(q.remove("A"));
		q.add("A");
		assertTrue(q.remove("A"));
		assertFalse(q.remove("A"));

		q.add("B");
		q.add("C");
		q.add("D");
		q.remove("C");
		assertEquals("B",q.poll());
		assertEquals("D",q.poll());
		assertNull(q.poll());
	}

	@Test
	public void testIsEmpty(){
		BlockingQueue<String> q = getQueue(5);
		assertTrue(q.isEmpty());
		q.add("A");
		assertFalse(q.isEmpty());
		q.remove();
		assertTrue(q.isEmpty());
	}

	/**
	 * Creates a queue, tries to add too many elements, removes some (uses offer)
	 */
	@Test
	public void testCapacityOffer(){
		BlockingQueue<String> q = getQueue(5);
		assertEquals(5,q.remainingCapacity());
		assertTrue(q.offer("A"));
		assertEquals(4,q.remainingCapacity());
		assertTrue(q.offer("B"));
		assertEquals(3,q.remainingCapacity());
		assertTrue(q.offer("C"));
		assertEquals(2,q.remainingCapacity());
		assertTrue(q.offer("D"));
		assertEquals(1,q.remainingCapacity());
		assertTrue(q.offer("E"));
		assertEquals(0,q.remainingCapacity());
		for(int i= 0; i != 20; i++){
			assertFalse(q.offer(Integer.toString(i)));
		}
		assertEquals("A",q.remove());
		assertEquals(1,q.remainingCapacity());
		assertTrue(q.offer("F"));
		assertEquals(0,q.remainingCapacity());
		assertEquals("B",q.remove());
		assertEquals(1,q.remainingCapacity());
		assertEquals("C",q.remove());
		assertEquals(2,q.remainingCapacity());
		assertEquals("D",q.remove());
		assertEquals(3,q.remainingCapacity());
		assertEquals("E",q.remove());
		assertEquals(4,q.remainingCapacity());
		assertEquals("F",q.remove());
		assertEquals(5,q.remainingCapacity());
	}

	@Test
	public void testNullOperations() throws InterruptedException{
		BlockingQueue<String> q = getQueue(5);

		boolean except= false;
		try{
			q.add(null);
		} catch(NullPointerException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}

		except= false;
		try{
			q.offer(null);
		} catch(NullPointerException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}

		except= false;
		try{
			q.put(null);
		} catch(NullPointerException e){
			except= true;
		} finally {
			assertTrue(except,"Expected exception");
		}
	}

	@Test
	public void testContains(){
		BlockingQueue<String> q = getQueue(5);
		assertFalse(q.contains("A"));
		q.offer("A");
		assertTrue(q.contains("A"));
		assertFalse(q.contains("B"));
		q.offer("B");
		assertTrue(q.contains("B"));
		q.remove("A");
		assertFalse(q.contains("A"));
		assertTrue(q.contains("B"));
	}


	//Threaded tests

	@Test
	public void testPut() throws InterruptedException{
		final BlockingQueue<String> q = getQueue(1);

		Thread t=  new Thread(
				new Runnable() {
					public void run() {
						try {
							Thread.sleep(sleep);
							assertEquals("A", q.poll());
						} catch (InterruptedException | AssertionError e) {
							failed = true;
							e.printStackTrace();
						}
					}
				});
		t.start();
		q.offer("A");
		q.put("B");
		assertEquals(q.peek(),"B");
		Thread.sleep(sleep);
		
		if(failed) {
			failed = false;
			fail();
		}
	}

	@Test
	public void testTake() throws InterruptedException{
		final BlockingQueue<String> q = getQueue(1);

		Thread t=  new Thread(
				new Runnable() {
					public void run() {
						try {
							assertEquals("A", q.take());
						} catch (InterruptedException | AssertionError e) {
							failed = true;
							e.printStackTrace();
						}
					}
				});
		t.start();
		Thread.sleep(sleep);
		q.offer("A");
		Thread.sleep(sleep);
		
		if(failed) {
			failed = false;
			fail();
		}
	}

	@Test
	public void testPutAndTake() throws InterruptedException{
		final BlockingQueue<String> q = getQueue(1);

		Thread t=  new Thread(
				new Runnable() {
					public void run() {
						try {
							assertEquals("A", q.take());
						} catch (InterruptedException | AssertionError e) {
							failed = true;
							e.printStackTrace();
						}
					}
				});
		t.start();
		Thread.sleep(sleep);
		q.offer("A");
		q.put("B");
		assertEquals(q.peek(),"B");
		Thread.sleep(sleep);
		
		if(failed) {
			failed = false;
			fail();
		}
	}
	
	@Test
	public void produceAndConsume() throws InterruptedException{
		final BlockingQueue<Integer> q = getQueue(10);
		
		
		Thread t=  new Thread(
				new Runnable() {
					public void run() {
						try {
							for(int i = 0; i < 10000; i++) {
								assertEquals(i, q.take());
							}
						} catch (AssertionError | InterruptedException e) {
							failed = true;
							e.printStackTrace();
						}
					}
				});
		t.start();
		Thread.sleep(sleep);
		for(int i = 0; i < 10000; i++) {
			q.put(i);
		}
		Thread.sleep(sleep);
		
		if(failed) {
			failed = false;
			fail();
		}
	}


}
