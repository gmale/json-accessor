package com.silverchalice.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class JsonAccessorArrayTest {

	public static final String SAMPLE_NEWS_JSON = "src/test/resources/sample.json";
	
	protected JsonAccessor jsonTool;
	protected JSONObject newsJson;
	protected String newsText;

	@Before
	public void setUp() throws Exception {
		jsonTool = new JsonAccessor();
		newsText = FileUtils.readFileToString(new File(SAMPLE_NEWS_JSON));
		newsJson = new JSONObject(newsText);
	}
	
	@Test
	public void validateSetup() throws Exception {
		File f = new File(SAMPLE_NEWS_JSON);
		assertTrue("File did not exist : " + f, f.exists());
		
		assertNotNull(jsonTool);
		assertNotNull(newsJson);
		assertTrue(newsText.length() > 0);
	}
	
	@Test
	public void getKeyStringTest_nestedArray() {
		Object newsUrl = jsonTool.getKeyPath("newsStories[0].assets[0].links[0].url", newsJson);
		assertNotNull(newsUrl);
		assertTrue("The object returned for the 'type' key was the wrong type. Expected: String\tActual:" + newsUrl.getClass().getSimpleName(),
				newsUrl instanceof String);
		assertEquals("http://ftp.acc.uglabs.net/acc/stats/cbk_editorial/201303301351498747698-p2.jpeg", newsUrl);
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void getKeyStringTest_outOfBounds() {
		jsonTool.getKeyPath("newsStories[0].assets[0].links[9].url", newsJson);
	}
	
	@Test
	public void getKeyStringTest_emptyString() {
		Object result = jsonTool.getKeyPath("", newsJson);
		assertEquals("An empty keypath should return the original object", result, newsJson);
	}
	
	@Test
	public void getKeyString_nullString() {
		Object result = jsonTool.getKeyPath(null, newsJson);
		assertEquals("An null keypath should return null", result, null);
	}

}
