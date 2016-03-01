import java.text.SimpleDateFormat;

public class PumpkinPatch {
	public static void main(String[] args) throws InterruptedException {
		// New stash
		Stash stashQue = new Stash();
		// New plant que
		Plants plantQue = new Plants();
		// New order que
		Orders orderQue = new Orders();
		// Date formatter
		SimpleDateFormat dateStamp = new SimpleDateFormat("MM/dd/yyyy--HH:mm:ss");
		// initial starting plants
		int startingPlants = 1000;
		// thread for killing threads at end
		ThreadControl threadControl = new ThreadControl();
		threadControl.start();

		// Initial Planting
		for (int i = 0; i < startingPlants; i++) {
			plantQue.plant(i);
			Jack.message += dateStamp.format(System.currentTimeMillis()) + " {New Plant Sown}        Stash size: "
					+ Stash.que.size() + " Plants in field: " + Plants.plantTotalQue.size() + "\n";
			Planter planter = new Planter(plantQue);
			planter.start();
		}
		// thread for creating customer orders
		Customer custOrders = new Customer(orderQue);
		custOrders.start();
		// thread for jack to do stuff
		Jack jack = new Jack(stashQue, plantQue, orderQue);
		jack.start();		
	}
}