package com.silverchalice.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JsonConverterTest {

	public static final String SAMPLE_JSON_FILE = "src/test/resources/sample.json";
	public static final String SAMPLE_JSON_URL = "http://api.silverchalice.co/sports/v1/schedules/stats-schedule-acc-m-footbl";
	
	public static String sampleJsonAbsolutePath; 
	protected JsonConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new JsonConverter();
		//gets the absolute path on this machine -kg
		sampleJsonAbsolutePath = new File(SAMPLE_JSON_FILE).toURI().toString();
	}
	
	@Test
	public void testGetURL_localFile() throws IOException {
		verifyGetUrl(sampleJsonAbsolutePath);
	}
	
	@Test
	public void testGetURL_remoteFile() throws IOException {
		verifyGetUrl(SAMPLE_JSON_URL);
	}
	
	@Test
	public void testFromUrl_localFile() throws Exception {
		Object result = converter.fromUrl(sampleJsonAbsolutePath, SimpleNews.class);
		assertNotNull(result);
		assertTrue(result instanceof SimpleNews);
		SimpleNews news = (SimpleNews) result;
		
		verifyMapping(news, "type",			"News");
		verifyMapping(news, "uid", 			"stats-news-acc-m-baskbl");
	}
	
	@Test
	public void testFromUrl_remoteFile() throws Exception {
		Object result = converter.fromUrl(SAMPLE_JSON_URL, SimpleEvent.class);
		assertNotNull(result);
		assertTrue(result instanceof SimpleEvent);
		SimpleEvent event = (SimpleEvent) result;
		
		verifyMapping(event, "schoolYear", 	"2012");
		verifyMapping(event, "type", 		"SportsSchedule");
		verifyMapping(event, "uid", 		"stats-schedule-acc-m-footbl");
	}
	
	@Test
	public void testFromUrl_keyPath() throws Exception {
		Object result = converter.fromUrl(sampleJsonAbsolutePath, "newsStories[0]", SimpleNews.class);
		assertNotNull(result);
		assertTrue(result instanceof SimpleNews);
		SimpleNews news = (SimpleNews) result;
		
		verifyMapping(news, "type",			"story");
		verifyMapping(news, "uid", 			"stats-news-item-20130330172427938780108");
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFromUrl_keyPathAsList() throws Exception {
		Object result = converter.fromUrlAsList(sampleJsonAbsolutePath, "newsStories", SimpleNews.class);
		assertNotNull(result);
		assertTrue(result instanceof List);
		List<SimpleNews> news = (List<SimpleNews>) result;
		
		verifyMapping(news.get(0), "type",			"story");
		verifyMapping(news.get(0), "uid", 			"stats-news-item-20130330172427938780108");
		verifyMapping(news.get(1), "type",			"story");
		verifyMapping(news.get(1), "uid", 			"stats-news-item-20130330033657582297608");
	}
	
	@Test
	public void testFromUrlWithJsonArray() throws Exception {
		String sampleJsonArray = sampleJsonAbsolutePath.replace("sample", "sample-array");
		Object result = converter.fromUrlWithJsonArray(sampleJsonArray, "[0].assets[0]", SimpleNews.class);
		assertNotNull(result);
		assertTrue(result instanceof SimpleNews);
		SimpleNews news = (SimpleNews) result;
		
		verifyMapping(news, "type",			"image");
		verifyMapping(news, "uid", 			null);
	}

	//--------------------------------------------------------------------------
	//
	// Helper Methods
	//
	//--------------------------------------------------------------------------	
	
	private void verifyGetUrl(String url) throws MalformedURLException,
			IOException {
		String contents = converter.getUrlAsString(url);
		assertNotNull(contents);
		assertTrue(contents.length() > 0);
		System.out.println(contents);
	}
	
	private void verifyMapping(Object result, String fieldName,
			Object fieldValue) throws Exception {
		Field field = result.getClass().getDeclaredField(fieldName);
		Object value = field.get(result);
		assertEquals("The " + fieldName + " value was not properly mapped.",
				fieldValue, value );
	}
	
	//--------------------------------------------------------------------------
	//
	// Helper Classes
	//
	//--------------------------------------------------------------------------	
	
	@SuppressWarnings("unused")
	public static class SimpleNews {
		protected String type;
		protected String uid;
	}

	@SuppressWarnings("unused")
	private static class SimpleEvent {
		protected String schoolYear;
		protected String type;
		protected String uid;
	}
	
}
