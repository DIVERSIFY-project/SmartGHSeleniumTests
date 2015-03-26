package org.diversify.sgh.test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
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
public class TestParameterizedPath {
	
	private String baseUrl;
	private boolean acceptNextAlert = true;

	public String from; // = "Henrietta street";
	public String to; // = "Synge street";
	public double expectedDistance;// = 3.0;
	public String expectedIntermediatePoint;// = "Grattan Bridge";
	public String vehicle; // = "foot"
	public String pathType; // = "Least Noisy"

	

	public TestParameterizedPath(String from, String to,
			double expectedDistance, String expectedIntermediatePoint,
			String vehicle, String type) {
		super();
		this.from = from;
		this.to = to;
		this.expectedDistance = expectedDistance;
		this.expectedIntermediatePoint = expectedIntermediatePoint;
		this.vehicle = vehicle;
		this.pathType = type;
	}
	
	@Parameters(name = "{index}: Path from {0} to {1} with {4} using {5}")
	public static Collection<Object[]> data() {
		ArrayList<Object[]> datas = new ArrayList<Object[]>();
		String line;
		try (
		    InputStream fis = new FileInputStream(DataSetGeneration.fileName);
		    InputStreamReader isr = new InputStreamReader(fis);
		    BufferedReader br = new BufferedReader(isr)
		) {
		    while ((line = br.readLine()) != null) {
		        String[] strings = line.split("; ");
		        Object[] objects = new Object[strings.length];
		        for (int i=0; i<strings.length;i++){
		        	if (i!=2)
		        		objects[i] = strings[i];
		        	else
		        		objects[i] = new Double(strings[i]);
				}
		        datas.add(objects);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collection<Object[]> randomData = new ArrayList<Object[]>();
		Random r = new Random();
		
		for (int i=0; i<AllTests.numberOfRandomTests; i++){
			randomData.add(datas.remove(r.nextInt(datas.size())));
		}

		return randomData;
	}

	@Before
	public void setUp() throws Exception {
		baseUrl = AllTests.baseUrl;
		AllTests.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testParameterizedPath() throws Exception {
		AllTests.driver.get(baseUrl + "/");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if ("".equals(AllTests.driver.findElement(By.id("foot")).getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if ("Least Noisy".equals(AllTests.driver.findElement(
						By.cssSelector("option[value=\"least_noisy\"]"))
						.getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		new Select(AllTests.driver.findElement(By.id("weightingSelect")))
				.selectByVisibleText(pathType);
		AllTests.driver.findElement(By.id(vehicle)).click();
		AllTests.driver.findElement(By.id("fromInput")).click();
		AllTests.driver.findElement(By.id("fromInput")).clear();
		AllTests.driver.findElement(By.id("fromInput")).sendKeys(from);
		AllTests.driver.findElement(By.id("toInput")).clear();
		AllTests.driver.findElement(By.id("toInput")).sendKeys(to);
		AllTests.driver.findElement(By.id("searchButton")).click();
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (!"Search Route ...".equals(AllTests.driver.findElement(
						By.cssSelector("#info > div")).getText()))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
		double distance = new Double(AllTests.driver
				.findElement(By.cssSelector("#info > div")).getText()
				.split("km")[0]);
		double minDistance = expectedDistance
				- (expectedDistance * AllTests.distanceTolerance);
		double maxDistance = expectedDistance
				+ (expectedDistance * AllTests.distanceTolerance);
		Assert.assertTrue("Distance check between " + minDistance + " and " + maxDistance,
				(minDistance <= distance && distance <= maxDistance));

		// do we need to check the checkpoint?
		if (AllTests.checkPathChackpoint) {
			boolean foundIntermediatePoint = false;
			for (WebElement we : AllTests.driver.findElements(By.className("instr_title"))) {
				if (we.getText().contains(expectedIntermediatePoint)){
					foundIntermediatePoint = true;
					break;
				}
			}
			Assert.assertTrue("Intermediate path checkpoint to " + expectedIntermediatePoint,
					foundIntermediatePoint);
		}

	}

	@After
	public void tearDown() throws Exception {
		
	}

	private boolean isElementPresent(By by) {
		try {
			AllTests.driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isAlertPresent() {
		try {
			AllTests.driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText() {
		try {
			Alert alert = AllTests.driver.switchTo().alert();
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
