package org.diversify.sgh.test;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

@RunWith(Suite.class)
@SuiteClasses({TestParameterizedPath.class})
public class AllTests {

	public final static double distanceTolerance = 0.5; // Tolerance to diff in the distance.
	public final static boolean tolerateDifferentPathOrders = true; // Do we tolerate different orders in the path
	public final static boolean checkPathChackpoint = false; // Do we check the path checkpoint
	public final static int numberOfRandomTests = 50;
	
	public static final String baseUrl = "http://vivek-pc.dsg.cs.tcd.ie:8989";
	
	public static WebDriver driver;
	private static StringBuffer verificationErrors = new StringBuffer();

	@BeforeClass
	public static void setUp() {
		File pathToBinary = new File("/usr/bin/firefox");
		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
                ffBinary.setEnvironmentProperty("DISPLAY", ":20");
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		driver = new FirefoxDriver(ffBinary,firefoxProfile);
	}

	@AfterClass
	public static void tearDown() {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			//fail(verificationErrorString);
		}
	}
}
