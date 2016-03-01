import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Jack extends Thread {
	Orders orderQue;
	Plants plantQue;
	Stash stashQue;
	static boolean alive = true;
	private boolean deliver = true;
	private int count = 0;
	private int stashMax = 10000;
	private int stashMin = 1000;
	static String message = "";
	static boolean keepDeliver = true;
	private double mean = 120;
	private double std = 20;
	Random random = new Random();
	private long deliverTime = 0;
	private static long orderFillTime = 0;
	private static long orderMean = 0;
	private SimpleDateFormat dateStamp = new SimpleDateFormat("MM/dd/yyyy--HH:mm:ss");
	private long timer = 0;
	private static List<Long> dataSet = new ArrayList<Long>();
	private long holder = 0;

	public Jack(Stash stashQue, Plants plantQue, Orders orderQue) {
		this.orderQue = orderQue;
		this.plantQue = plantQue;
		this.stashQue = stashQue;
	}

	public void run() {
		try {
			Iterator<Integer> ripe;
			Iterator<Integer> stash;
			Thread.sleep(100000);

			// master loop
			while (alive) {
				count = 0;
				timer = System.currentTimeMillis();
				ripe = Plants.ripeQue.iterator();
				stash = Stash.que.iterator();
				// pick all ripe pumpkins
				while (ripe.hasNext() && keepDeliver) {
					plantQue.pick();
					stashQue.adds();
					count++;
				}
				if (count != 0) {
					message += dateStamp.format(System.currentTimeMillis())
							+ " {Jack Gathers Pumpkins} Stash size: " + Stash.que.size() + " Plants in field: "
							+ Plants.plantTotalQue.size() + "\n";
					Thread.sleep(2 * count);
					System.out.println("Jack Picked");
				}

				// if stash is under 1000, plant more plants
				if ((Stash.que.size() < stashMin) && (Customer.alive)) {
					int x = Plants.plantTotalQue.size();
					int y = x + 4;
					for (int i = x; i < y; i++) {
						plantQue.plant(i);
						message += dateStamp.format(System.currentTimeMillis())
								+ " {New Plant Sown}        Stash size: " + Stash.que.size() + " Plants in field: "
								+ Plants.plantTotalQue.size() + "\n";
						Thread planter = new Thread(new Planter(plantQue));
						planter.start();
						System.out.println("*********New Plant");
					}
				}

				// if stash is full, uproot
				if ((Stash.que.size() >= stashMax) && (Customer.alive)) {
					for (int i = 0; i < 5; i++) {
						plantQue.uproot();
						message += dateStamp.format(System.currentTimeMillis())
								+ " {Plant Uprooted}        Stash size: " + Stash.que.size() + " Plants in field: "
								+ Plants.plantTotalQue.size() + "\n";
						 System.out.println("******Uprooted: " + i);
					}

					// fill orders till stash is under 9000
					while ((Stash.que.size() > 9000) && keepDeliver) {

						// spin while waiting on orders to bring stash down
						while ((Orders.orderQue.isEmpty()) && (Customer.alive)) {
							 System.out.println("Jack Spining waiting for orders");
						}

						orderQue.fillOrder();						
						stashQue.retrieve();
						holder = System.currentTimeMillis() - timer;
						orderFillTime += holder;
						dataSet.add(holder);
						System.out.println("Time: " + orderFillTime);
						deliver = true;

						// if no orders and customer is not running, we can stop
						if ((Orders.orderQue.isEmpty() && (!Customer.alive)) && keepDeliver) {
							Planter.kill();
							Thread.sleep(5000);
							stopDeliver();
						}

						if (deliver) {
							deliver = false;
							deliverTime = (long) (mean + std * random.nextGaussian());
							message += dateStamp.format(System.currentTimeMillis())
									+ " {Delivering Pumpkins}   Stash size: " + Stash.que.size() + " Plants in field: "
									+ Plants.plantTotalQue.size() + "--Delivery Time: " + deliverTime + "\n";
							Thread.sleep(deliverTime);
							System.out.println("Stash Full Jack delivered");
						}
					}
				}

				// remove from stash and fill orders
				while ((stash.hasNext()) && (!Orders.orderQue.isEmpty())) {  
					orderQue.fillOrder();					
					stashQue.retrieve();
					holder = System.currentTimeMillis() - timer;
					orderFillTime += holder;
					dataSet.add(holder);
					System.out.println("Time: " + orderFillTime);
					deliver = true;		
					System.out.println("************Delivered");
				}

				// delivering orders
				if (deliver) {
					deliver = false;
					deliverTime = (long) (mean + std * random.nextGaussian());
					message += dateStamp.format(System.currentTimeMillis())
							+ " {Delivering Pumpkins}   Stash size: " + Stash.que.size() + " Plants in field: "
							+ Plants.plantTotalQue.size() + "--Delivery Time: " + deliverTime + "\n";
					Thread.sleep(deliverTime);
					System.out.println("****************Jack Delivered");
				}

				// stop delivering when all orders filled
				if ((Orders.orderQue.isEmpty() && (!Customer.alive)) && keepDeliver) {
					Planter.kill();
					Thread.sleep(5000);
					stopDeliver();
				}
				count = 0;
			}

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void kill() {
		// System.out.println("***Killed Jack***");
		alive = false;
	}

	public static void stopDeliver() {				
		ThreadControl.kill();
		long x = 0;
		long devTotal = 0;
		int standardDeviation = 0;
		Iterator<Long> stDev = Jack.dataSet.iterator();
		keepDeliver = false;
		orderMean = orderFillTime/Orders.orderCount;	
		while (stDev.hasNext()) {
			x = (stDev.next() - orderMean);
			devTotal += x * x;
		}
		standardDeviation = (int) Math.sqrt(devTotal/Orders.orderCount);
		message += "*******************************************************************************************************\n"
		        + "Pumpkins left in stash: " + Stash.que.size() + "\n" + "Ripe pumpkins left:     " 
		        + Plants.ripeQue.size() + "\n" + "Mean To Fill Order:     " + orderMean + "ms" + "\n"
		        + "Standard Deviation:     " + standardDeviation + "ms" + "\n";				
		kill();
		output();
	}

	public static void output() {
//		System.out.print(message);
		try {
			File outFile = new File("C:/temp/PumpkinPatchLog.txt");
			PrintStream out = new PrintStream(new FileOutputStream(outFile));

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(message);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				out.println(line);
			}
			out.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}
}
