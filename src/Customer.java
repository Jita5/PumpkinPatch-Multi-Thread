import java.text.SimpleDateFormat;
import java.util.Random;

class Customer extends Thread {
	Orders custOrder;
	static boolean alive = true;
	private double a = -120;
	private double u = 0;
	Random random = new Random();
	private long recTime = 0;
	private SimpleDateFormat dateStamp = new SimpleDateFormat("MM/dd/yyyy--HH:mm:ss");

	public Customer(Orders custOrder) {
		this.custOrder = custOrder;
	}

	public void run() {
		try {
			Thread.sleep(100000);
			//continue bringing new orders in
			while (alive) {
				u = random.nextDouble();
				recTime = (long) ((a) * Math.log(u));
				custOrder.recOrder();
				Jack.message += dateStamp.format(System.currentTimeMillis()) + " {Customer Placed Order} Stash size: "
						+ Stash.que.size() + " Plants in field: " + Plants.plantTotalQue.size() + "\n";
				Thread.sleep(recTime);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void kill() {
//		System.out.println("***Killed Customer***");
		alive = false;
	}
}