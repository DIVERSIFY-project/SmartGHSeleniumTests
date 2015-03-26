package org.diversify.sgh.test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@RunWith(Parameterized.class)
public class TestDataSetGenerator {
	
	// input for generating the set of tests
		private static final Collection<String> VEHICLES = Arrays
				.asList(new String[] { "foot", "bike", "car" });
		private static final Collection<String> PATH_TYPES = Arrays.asList(new String[] {
				"Least Noisy", "Fastest", "Shortest", "Least Air Pollution" });
		private static Collection<Object[]> PATHS = new ArrayList<Object[]>();
		private static final String StreetsFile = "StreetsFile.txt";
	
	private String baseUrl;
	private boolean acceptNextAlert = true;
	

	public String from; // = "Henrietta street";
	public String to; // = "Synge street";
	public double expectedDistance;// = 3.0;
	public String expectedIntermediatePoint;// = "Grattan Bridge";
	public String vehicle; // = "foot"
	public String pathType; // = "Least Noisy"

	

	public TestDataSetGenerator(String from, String to,
			String vehicle, String type) {
		super();
		this.from = from;
		this.to = to;
		this.vehicle = vehicle;
		this.pathType = type;
	}
	
	@Parameters(name = "{index}: Path from {0} to {1} with {2} using {3}")
	public static Collection<Object[]> data() {
		String line;
		Collection<String> streets = new ArrayList<String>();
		try (
		    InputStream fis = new FileInputStream(StreetsFile);
		    InputStreamReader isr = new InputStreamReader(fis);
		    BufferedReader br = new BufferedReader(isr)
		) {
		    while ((line = br.readLine()) != null) {
		        streets.add(line);
				}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String street1 : streets){
			for (String street2 : streets){
				if (!street1.equals(street2)){
					PATHS.add(new String[]{street1, street2});
				}
			}
		}
		Collection<Object[]> datas = new ArrayList<Object[]>();
		for (Object[] path : PATHS){
			for (String vehicleType : VEHICLES){
				for (String pathTypeLocal : PATH_TYPES){
					Object[] objects = new Object[path.length+2];
					for (int i=0; i<path.length;i++){
						objects[i] = path[i];
					}
					objects[path.length] = vehicleType;
					objects[path.length+1] = pathTypeLocal;
					datas.add(objects);
				}
			}
		}

		return datas;
	}

	@Before
	public void setUp() throws Exception {
		baseUrl = DataSetGeneration.baseUrl;
		DataSetGeneration.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testDataSetGenerator() throws Exception {
		System.out.println("Test");
		DataSetGeneration.driver.get(baseUrl + "/");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if ("".equals(DataSetGeneration.driver.findElement(By.id("foot")).getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if ("Least Noisy".equals(DataSetGeneration.driver.findElement(
						By.cssSelector("option[value=\"least_noisy\"]"))
						.getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		new Select(DataSetGeneration.driver.findElement(By.id("weightingSelect")))
				.selectByVisibleText(pathType);
		DataSetGeneration.driver.findElement(By.id(vehicle)).click();
		DataSetGeneration.driver.findElement(By.id("fromInput")).click();
		DataSetGeneration.driver.findElement(By.id("fromInput")).clear();
		DataSetGeneration.driver.findElement(By.id("fromInput")).sendKeys(from);
		DataSetGeneration.driver.findElement(By.id("toInput")).clear();
		DataSetGeneration.driver.findElement(By.id("toInput")).sendKeys(to);
		DataSetGeneration.driver.findElement(By.id("searchButton")).click();
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
//				System.out.println("----"
//						+ driver.findElement(By.cssSelector("#info > div"))
//								.getText());
				if (!"Search Route ...".equals(DataSetGeneration.driver.findElement(
						By.cssSelector("#info > div")).getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
		double distance = new Double(DataSetGeneration.driver
				.findElement(By.cssSelector("#info > div")).getText()
				.split("km")[0]);
//		double minDistance = expectedDistance
//				- (expectedDistance * DataSetGeneration.distanceTolerance);
//		double maxDistance = expectedDistance
//				+ (expectedDistance * DataSetGeneration.distanceTolerance);
//		Assert.assertTrue("Distance check between " + minDistance + " and " + maxDistance,
//				(minDistance <= distance && distance <= maxDistance));

		Collection<WebElement> webElements = DataSetGeneration.driver.findElements(By.className("instr_title"));
		Random rand = new Random();
	    int randomNum = rand.nextInt(webElements.size());
	    int i=0; 
	    String intermediatePoint="";
		for (WebElement we : webElements) {
			if(i == randomNum){
				intermediatePoint = we.getText();
			}
			i++;
		}
		
		
		
		DataSetGeneration.appendToOutput(from + "; " + to + "; " + distance  + "; " + intermediatePoint + "; " + vehicle + "; " + pathType + "\n");
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private boolean isElementPresent(By by) {
		try {
			DataSetGeneration.driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isAlertPresent() {
		try {
			DataSetGeneration.driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText() {
		try {
			Alert alert = DataSetGeneration.driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}
}
