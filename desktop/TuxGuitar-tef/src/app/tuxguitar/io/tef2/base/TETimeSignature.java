package app.tuxguitar.io.tef2.base;

public class TETimeSignature {

	private int numerator;
	private int denominator;

	public TETimeSignature(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public int getDenominator() {
		return this.denominator;
	}

	public int getNumerator() {
		return this.numerator;
	}
}
