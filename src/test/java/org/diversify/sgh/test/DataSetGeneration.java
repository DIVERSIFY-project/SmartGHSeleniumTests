package org.diversify.sgh.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
@SuiteClasses({TestDataSetGenerator.class})
public class DataSetGeneration {

	public final static double distanceTolerance = 0.05; // Tolerance to diff in
															// the distance.
	public final static boolean tolerateDifferentPathOrders = true; // Do we
																	// tolerate
																	// different
																	// orders in
																	// the path
	public static WebDriver driver;
	private static StringBuffer verificationErrors = new StringBuffer();
	private static StringBuffer output = new StringBuffer();
	public static final String fileName = "TestData.txt";
	
	public static final String baseUrl = "http://sgh.barais.fr/";

	@BeforeClass
	public static void setUp() {
		System.out.println("setup");
		File pathToBinary = new File("C:\\Program Files (x86)\\Mozilla Firefox\\Firefox.exe");
		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		driver = new FirefoxDriver(ffBinary,firefoxProfile);
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("tear down");
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
		try{
			PrintWriter out = new PrintWriter(fileName);
			out.print(output);
			out.flush();
			out.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public static void appendToOutput(String str){
		output.append(str);
	}
}
