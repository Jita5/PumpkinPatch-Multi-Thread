import java.util.LinkedList;
import java.util.Queue;

class Stash {

	static Queue<Integer> que = new LinkedList<Integer>();
	private int count = 0;
	
	//used to add ripe pumpkins
	synchronized void adds() throws InterruptedException {
		que.add(count);	
		count++;
		notifyAll();
		System.out.println("Add Stash");
	}
	
	//used to remove when delivering
	synchronized void retrieve() throws InterruptedException {
		while (que.isEmpty()) {
			wait();
		}
		que.remove();		
		notifyAll();
		System.out.println("Remove Stash");
	}
}