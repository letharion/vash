package test;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

import vash.Vash;


public class TestRegressions {
	@Test
	public void testNonSquareLinGradVerticals() {
		String algorithm = "1";
		String data = "foo";
		int width = 64;
		int height = 32;

		BufferedImage img1 = null;
		try { img1 = Vash.createImage(algorithm, data, width, height);
		} catch(NoSuchAlgorithmException e) { Assert.assertFalse(true); }
		
		Assert.assertTrue("this should not crash", img1 != null);
	}
}
