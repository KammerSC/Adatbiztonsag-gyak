package david.borbely.mymath;

import java.math.BigInteger;

public class BasePowerReminder {
	BigInteger power;
	BigInteger reminder;

	public BasePowerReminder(BigInteger base, BigInteger power, BigInteger modulo) {
		reminder = base.mod(modulo);
		this.power = power;
	}

	@Override
	public String toString() {
		return "BasePowerReminder [power=" + power + ", reminder=" + reminder + "]";
	}
}
