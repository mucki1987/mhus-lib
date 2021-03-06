/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.test;

import java.math.BigDecimal;

import de.mhus.lib.core.MBigMath;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MMath;
import junit.framework.TestCase;

public class MMathTest extends TestCase {

	public void testByteAddRotate() {
		for (byte d = Byte.MIN_VALUE; d < Byte.MAX_VALUE; d++) {
			for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
				byte l = MMath.addRotate(b, d);
				byte r = MMath.subRotate(l, d);
				if (b != r) {
					System.out.println( b + " -> " + l + " -> " + r );
					System.out.println( MCast.toBitsString(b) + " -> " + MCast.toBitsString(l) + " -> " + MCast.toBitsString(r) );
				}
				assertEquals(b, r);
			}
		}
	}

	public void testByteRotate() {
		for (int d = 1; d < 8; d++) {
			for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
				byte l = MMath.rotl(b, d);
				byte r = MMath.rotr(l, d);
				if (b != r) {
					System.out.println( b + " -> " + l + " -> " + r );
					System.out.println( MCast.toBitsString(b) + " -> " + MCast.toBitsString(l) + " -> " + MCast.toBitsString(r) );
				}
				assertEquals(b, r);
			}
		}
	}

	public void testIntRotate() {
		for (int d = 1; d < 32; d++) {
			{
				int i = Integer.MIN_VALUE;
				int l = MMath.rotl(i, d);
				int r = MMath.rotr(l, d);
				if (i != r) System.out.println(i + " -> " + l + " -> " + r);
				assertEquals(i, r);
			}
			{
				int i = Integer.MAX_VALUE;
				int l = MMath.rotl(i, d);
				int r = MMath.rotr(l, d);
				if (i != r) System.out.println(i + " -> " + l + " -> " + r);
				assertEquals(i, r);
			}
			{
				int i = 1;
				int l = MMath.rotl(i, d);
				int r = MMath.rotr(l, d);
				if (i != r) System.out.println(i + " -> " + l + " -> " + r);
				assertEquals(i, r);
			}
			{
				int i = -1;
				int l = MMath.rotl(i, d);
				int r = MMath.rotr(l, d);
				if (i != r) System.out.println(i + " -> " + l + " -> " + r);
				assertEquals(i, r);
			}
			{
				int i = 0;
				int l = MMath.rotl(i, d);
				int r = MMath.rotr(l, d);
				if (i != r) System.out.println(i + " -> " + l + " -> " + r);
				assertEquals(i, r);
			}
		}
	}

	public void testBigMath() {
		BigDecimal a = BigDecimal.valueOf(10);
		BigDecimal b = BigDecimal.valueOf(20);
		
		assertEquals(a, MBigMath.min(a, b));
		assertEquals(b, MBigMath.max(a, b));
		
	}
	
}
