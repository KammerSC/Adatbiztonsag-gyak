package david.borbely;

import david.borbely.rsa.RSA;
import david.borbely.view.View;

public class App {

	public static void main(String[] args) {
		RSA rsa = new RSA(64);
		View view = new View(System.in, rsa);
		view.start();
	}
}
