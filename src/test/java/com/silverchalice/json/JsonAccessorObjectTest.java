package com.silverchalice.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class JsonAccessorObjectTest {

	public static final String SAMPLE_NEWS_ITEM_JSON = "src/test/resources/sample-news-item.json";
	
	protected JsonAccessor jsonTool;
	protected JSONObject newsItemJson;
	protected String newsItemText;

	@Before
	public void setUp() throws Exception {
		jsonTool = new JsonAccessor();
		newsItemText = FileUtils.readFileToString(new File(SAMPLE_NEWS_ITEM_JSON));
		newsItemJson = new JSONObject(newsItemText);
	}

	@Test
	public void validateSetup() throws Exception {
		File f = new File(SAMPLE_NEWS_ITEM_JSON);
		assertTrue("File did not exist : " + f, f.exists());
		
		assertNotNull(jsonTool);
		assertNotNull(newsItemJson);
		assertTrue(newsItemText.length() > 0);
	}
	
	@Test
	public void getKeyStringTest_firstLevel() {
		Object newsHeadline = jsonTool.getKeyPath("headline", newsItemJson);
		assertNotNull(newsHeadline);
		assertTrue("The object returned for the 'headline' key was the wrong type. Expected: String\tActual:" + newsHeadline.getClass().getSimpleName(),
				newsHeadline instanceof String);
		assertEquals("32 years later, Pitino and Krzyzewski meet again", newsHeadline);
		
		Object newsAssets = jsonTool.getKeyPath("assets", newsItemJson);
		assertNotNull(newsAssets);
		assertTrue("The object returned for the 'type' key was the wrong type. Expected: String\tActual:" + newsAssets.getClass().getSimpleName(),
				newsAssets instanceof JSONArray);
		int length = ((JSONArray) newsAssets).length();
		assertEquals("Incorrect number of assets in the story. Expected: 5\tActual: " + length, 6, length);
	}
	
	@Test
	public void getKeyString_nested() {
		Object shortName = jsonTool.getKeyPath("source.name.shortName", newsItemJson);
		assertNotNull("Searching for a nested keystring returned a null value", shortName);
		assertEquals("AP", shortName);
	}
	
	@Test
	public void getKeyString_byType() {
		String shortName = jsonTool.getKeyPath("source.name.shortName", newsItemJson, String.class);
		assertNotNull("Searching for a nested keystring returned a null value", shortName);
		assertEquals("AP", shortName);
	}
	
	@Test
	public void getKeyString_byCoersion_Integer() {
		Integer changes = jsonTool.getKeyPath("source.name.nameChanges", newsItemJson, Integer.class);
		assertNotNull("Searching for a nested keystring returned a null value", changes);
		assertTrue(5 == changes);
	}
	
	@Test
	public void getKeyString_byCoersion_Boolean() {
		Boolean isFree = jsonTool.getKeyPath("source.name.isFree", newsItemJson, Boolean.class);
		assertNotNull("Searching for a nested keystring returned a null value", isFree);
		assertFalse(isFree);
	}

}
