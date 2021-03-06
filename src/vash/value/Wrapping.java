/*
 * Copyright 2011, Zettabyte Storage LLC
 * 
 * This file is part of Vash.
 * 
 * Vash is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vash is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Vash.  If not, see <http://www.gnu.org/licenses/>.
 */
package vash.value;

import vash.Seed;

/**
 * A value whose semantic meaning does not change as it overflows the border 
 * of its range.
 * e.g. For a sin function, going from a (PI - epsilon) to a (-PI + epsilon) phase
 * does not wildly change the output value.
 * Values in a Wrapped overflow into the opposite side of their range when 
 * modified.
 */
public class Wrapping implements Value {
	/**
	 * Value of this bounded value.  Exists in range [-1,1].
	 */
	private double v;

	/**
	 * Default constructor creates at origin.
	 */
	public Wrapping() {
		v = 0.0;
	}

	/**
	 * Lower bound of value.
	 */
	private double lower = -1.0;
	
	/**
	 * Upper bound of value.
	 */
	private double upper = 1.0;

	/**
	 * Construct from the seed with random values.
	 * @param s
	 * @param lower
	 * @param upper
	 */
	public Wrapping(Seed s, double lower, double upper) {
		double range = (upper - lower);
		this.v = (s.nextDouble() * range) + lower;
		this.lower = lower;
		this.upper = upper;
	}
	
	/**
	 * Construct from given position. 
	 * @param v
	 */
	public Wrapping(double v) {
		this(v, -1.0, 1.0);
	}

	/**
	 * Construct from given position and bounds. 
	 * @param v
	 * @param lower
	 * @param upper
	 */
	public Wrapping(double v, double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		if(v < this.lower || v > this.upper)
			throw new IllegalArgumentException("Wrapping value '" + Double.toString(v) + "' is out of range [" + 
					this.lower + "," + this.upper + "]");
		this.v = v;
	}

	@Override
	public Wrapping clone() {
		return new Wrapping(v, lower, upper);
	}

	public double getV() {
		return v;
	}

	public void setV(double v) {
		this.v = v;
	}

	public double getLower() {
		return lower;
	}

	public double getUpper() {
		return upper;
	}

	public boolean hasBounds(double l, double u) {
		return this.lower == l && this.upper == u;
	}

	@Override
	public String toString() {
		return String.format("B[%.2f]", v); 
	}

	@Override
	public void createKeyFrame(double t, Seed s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTime(double t, double dt) {
		// TODO Auto-generated method stub

	}

}
