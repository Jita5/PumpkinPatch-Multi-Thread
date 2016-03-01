import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Random;

class Planter extends Thread {
	Plants plantQue;
	static boolean alive = true;
	private double mean = 50000;
	private double std = 10000;
	Random random = new Random();
	private long ripenTime = 0;
	private SimpleDateFormat dateStamp = new SimpleDateFormat("MM/dd/yyyy--HH:mm:ss");

	public Planter(Plants plantQue) {
		this.plantQue = plantQue;
	}

	public void run() {
		try {
			Iterator<Integer> it = Plants.plantTotalQue.iterator();
			while (alive) {				
				//iterate over the number of total plants, and ripen them appropriately 
				while (it.hasNext() && alive) {
					ripenTime = (long) (mean + std * random.nextGaussian());
					Thread.sleep(ripenTime);
					plantQue.ripen();
					Jack.message += dateStamp.format(System.currentTimeMillis())
							+ " {Pumpkin Ripens}        Stash size: " + Stash.que.size() + " Plants in field: "
							+ Plants.plantTotalQue.size() + "\n";
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void kill() {
//		System.out.println("***Killed Planter***");
		alive = false;
	}
}