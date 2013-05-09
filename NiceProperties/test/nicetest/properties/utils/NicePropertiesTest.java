package nicetest.properties.utils;

import java.util.Properties;

import junit.framework.Assert;
import nice.properties.utils.NiceProperties;

import org.junit.Test;

public class NicePropertiesTest {

	@Test
	public void include() {
		Properties p = NiceProperties.load("properties", "test.properties",
				"test");
		String aaa = p.getProperty("name");
		Assert.assertNotNull(aaa);
		Assert.assertEquals("test & test2.properties", aaa);

		String osName = p.getProperty("os.name");
		Assert.assertNotNull(osName);
	}

	@Test
	public void singeFile() {
		Properties p = NiceProperties.load("properties", "test2.properties",
				null);
		String aaa = p.getProperty("name");
		Assert.assertNotNull(aaa);
		Assert.assertEquals("I am in test2.properties", aaa);

		String osName = p.getProperty("os.name");
		Assert.assertNotNull(osName);
	}

	@Test
	public void withNamespace() {
		Properties p = NiceProperties.load("properties", "test2.properties",
				"test");
		String aaa = p.getProperty("name");
		Assert.assertNotNull(aaa);
		Assert.assertEquals("test & test2.properties", aaa);

		String osName = p.getProperty("os.name");
		Assert.assertNotNull(osName);
	}
}
