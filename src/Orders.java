import java.util.LinkedList;
import java.util.Queue;

class Orders {

	static Queue<Integer> orderQue = new LinkedList<Integer>();
	static int orderCount = 0;

	synchronized void recOrder() throws InterruptedException {
		orderQue.add(orderCount);
		orderCount++;		
		notifyAll();
		System.out.println("Recieved Order: " + orderCount);
	}

	synchronized void fillOrder() throws InterruptedException {
		while (orderQue.isEmpty()) {
			wait();
		}
		orderQue.remove();		
		notifyAll();
		System.out.println("Filled Order");
	}
}