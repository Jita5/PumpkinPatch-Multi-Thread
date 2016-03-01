
class ThreadControl extends Thread {
	static boolean alive = true;

	public ThreadControl() {
	}

	public void run() {
		try {
			//Thread used just to stop orders coming in
			Thread.sleep(1000000);
			Customer.kill();
			
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void kill() {
//		System.out.println("***Killed ThreadControl***");
		alive = false;
	}
}