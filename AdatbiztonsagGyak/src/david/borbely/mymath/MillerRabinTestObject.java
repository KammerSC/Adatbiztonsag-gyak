package david.borbely.mymath;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static david.borbely.mymath.MyMath.isEqual;
import static david.borbely.mymath.MyMath.isItOdd;
import static david.borbely.mymath.MyMath.isGreater;
import static david.borbely.mymath.MyMath.fastModularExponentiation;

public class MillerRabinTestObject {
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = BigInteger.TWO;
	private static final BigInteger THREE = new BigInteger("3");
	private static final BigInteger FOUR = new BigInteger("4");

	private final BigInteger number;
	private int powerOfTwo;
	private BigInteger multiplier;
	private int testTimes;
	List<BigInteger> probedBases = new ArrayList<BigInteger>();
	private final BigInteger MINUS_ONE;

	private Random random = new Random();

	public MillerRabinTestObject(BigInteger number) {
		this.number = number;
		MINUS_ONE = number.subtract(ONE);
	}

	public boolean isPRime() {
		boolean prime = false;
		if (isGreater(number, THREE) && isItOdd(number)) {
			prime = false;
			findTwoPowerFrom();
			calculateTestTimes();
			prime = doMillerRAbinTest();
		}
		return prime;
	}

	private void findTwoPowerFrom() {
		BigInteger num = number.subtract(BigInteger.ONE);
		powerOfTwo = 0;
		while (!isItOdd(num)) {
			num = num.divide(TWO);
			powerOfTwo++;
		}
		multiplier = num;
	}

	private void calculateTestTimes() {
		int bitLength = number.bitLength();
		if (bitLength < 24) {
			testTimes = calculateSmallNumberTestTime();
		} else {
			testTimes = calculateBigNumberTestTime();
		}
	}

	private int calculateSmallNumberTestTime() {
		int maxBases = number.intValue() - 4;
		return maxBases < 100 ? maxBases : 100;
	}

	private int calculateBigNumberTestTime() {
		int bitLength = number.bitLength();
		int times;
		if (bitLength > 1024) {
			times = 3;
		} else if (bitLength > 756) {
			times = 5;
		} else if (bitLength > 512) {
			times = 9;
		} else if (bitLength > 256) {
			times = 18;
		} else if (bitLength > 128) {
			times = 27;
		} else if (bitLength > 64) {
			times = 36;
		} else if (bitLength > 32) {
			times = 50;
		} else {
			times = 80;
		}
		return times;
	}

	private boolean doMillerRAbinTest() {
		boolean result = true;
		for (int i = 0; i < testTimes && result; i++) {
			BigInteger base = getRandomUnusedBase();
			probedBases.add(base);
			if (doTestWithBase(base)) {
				result = false;
			}
		}
		return result;
	}

	private BigInteger getRandomUnusedBase() {
		BigInteger upperlimit = number.subtract(FOUR);
		BigInteger canditate;
		do {
			canditate = new BigInteger(upperlimit.bitLength(), random);
			canditate = canditate.mod(upperlimit).add(TWO);
		} while (probedBases.contains(canditate));
		return canditate;
	}

	private boolean doTestWithBase(BigInteger base) {
		boolean result = true;
		BigInteger x = fastModularExponentiation(base, multiplier, number);
		List<BigInteger> xes = new ArrayList<BigInteger>();
		xes.add(x);
		if (isEqual(x, ONE) || isEqual(x, MINUS_ONE)) {
			result = false;
		} else {
			for (int i = 1; i < powerOfTwo && result; i++) {
				x = x.multiply(x).mod(number);
				if (isEqual(x, MINUS_ONE) || xes.contains(x)) {
					result = false;
				}
				xes.add(x);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "MillerRabinTestObject [number=" + number + ", rounds=" + powerOfTwo + ", multiplier=" + multiplier
				+ "]";
	}

}
