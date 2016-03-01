import java.util.LinkedList;
import java.util.Queue;

class Plants {

	static Queue<Integer> ripeQue = new LinkedList<Integer>();
	static Queue<Integer> plantTotalQue = new LinkedList<Integer>();
	private int count = 0;
	
	//plants new plants
	synchronized void plant(int num) throws InterruptedException {
		plantTotalQue.add(num);		
		notifyAll();
		System.out.println("Planted: " + num);
	}
	
	//adds ripe que
	synchronized void ripen() throws InterruptedException {
		ripeQue.add(count);
		count++;		
		notifyAll();
		System.out.println("Ripe: " + count);
	}	
	
	//removes from ripe pumpkins
	synchronized void pick() throws InterruptedException {
		while (ripeQue.isEmpty()) {
			wait();
		}
		ripeQue.remove();		
		notifyAll();
		System.out.println("Picked" );
	}
	
	//removes plants when stash full
	synchronized void uproot() throws InterruptedException {
		plantTotalQue.remove();
		notifyAll();
	}
}