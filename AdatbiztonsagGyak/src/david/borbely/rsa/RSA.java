package david.borbely.rsa;

import java.math.BigInteger;

import static david.borbely.mymath.MyMath.generatePrime;
import static david.borbely.mymath.MyMath.extendedEuclideanAlgorithm;
import static david.borbely.mymath.MyMath.fastModularExponentiation;
import static david.borbely.mymath.MyMath.isEqual;
import static david.borbely.mymath.MyMath.isLess;
import static david.borbely.mymath.MyMath.isItPrime;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.ONE;

public class RSA {

	private static final int DEFAULT_MIN_LENGTH = 32;
	private static final int DEFAULT_MAX_LENGTH = 64;

	private final int minBitLength;
	private final int maxBitLength;

	private BigInteger primP;
	private BigInteger primQ;
	private BigInteger n;
	private BigInteger fiN;
	private BigInteger e = BigInteger.ONE;
	private BigInteger d;

	private int bitLength;

	public RSA(int bitLength) {
		this.bitLength = bitLength;
		this.minBitLength = (bitLength > 32) ? (int) (bitLength * 0.9) : DEFAULT_MIN_LENGTH;
		this.maxBitLength = (bitLength > 32) ? (int) (bitLength * 1.1) : DEFAULT_MAX_LENGTH;
	}

	public int getBitlength() {
		return bitLength;
	}

	public void getKeyFromInput(BigInteger a, BigInteger b) {
		primP = a;
		primQ = b;
		if (!isItPrime(a)) {
			primP = generatePrime(minBitLength);
		}
		if (!isItPrime(b)) {
			primQ = generatePrime(maxBitLength);
		}
		setNumbers();
	};

	public void generatePrimes() {
		primP = generatePrime(minBitLength);
		primQ = generatePrime(maxBitLength);
		while (primP.compareTo(primQ) == 0) {
			primQ = generatePrime(maxBitLength);
		}
		setNumbers();
	}

	private void setNumbers() {
		setNandFiN();
		setEandD();
	}

	private void setNandFiN() {
		n = primP.multiply(primQ);
		fiN = primP.subtract(ONE).multiply(primQ.subtract(ONE));
	}

	private void setEandD() {
		BigInteger[] euc;
		do {
			e = e.add(ONE);
			euc = extendedEuclideanAlgorithm(e, fiN);
		} while (!isEqual(euc[0], ONE));
		d = euc[1];
		if (isLess(d, ONE)) {
			d = d.add(fiN);
		}

	}

	public void printPrimes() {
		System.out.println("P: " + primP + ", bitlength:" + primP.bitLength());
		System.out.println("Q: " + primQ + ", bitlength:" + primQ.bitLength());
	}

	public void printKeys() {
		System.out.println("PK: (" + n + ", " + e + ")");
		System.out.println("SK: (" + d + ")");

	};

	public BigInteger encryptMessage(BigInteger message) {
		return fastModularExponentiation(message, e, n);
	};

	public BigInteger decryptMessage(BigInteger encryptedMessage) {
		BigInteger[] euc = extendedEuclideanAlgorithm(primP, primQ);
		BigInteger power = fastModularExponentiation(d, ONE, primP.subtract(ONE));
		BigInteger mp = fastModularExponentiation(encryptedMessage, power, primP);
		power = fastModularExponentiation(d, ONE, primQ.subtract(ONE));
		BigInteger mq = fastModularExponentiation(encryptedMessage, power, primQ);
		BigInteger result = mp.multiply(euc[2]).multiply(primQ).add(mq.multiply(euc[1]).multiply(primP));
		result = result.mod(n);
		if (isLess(result, ZERO)) {
			result = result.add(n);
		}
		return result;
	}

	public BigInteger[] getPubliKeys() {
		return new BigInteger[] { n, e };
	}
}