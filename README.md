Nice Properties
================================
A simple tool to read properties which is like the way Play Framework 1.x does.

Features
--------------------------------
* Namespace is support
* Including other files
* Replacing property values with system environment variables or system properties

Usage
--------------------------------

There are two config files in classpath
properties/test.properties
```
    name=Nice Properties
    %test.Name=test & test.properties
```

properties/test2.properties
```
    name=I am in test2.properties
    %test.name=test & test2.properties

    os.name=${sun.desktop}
```

JAVA:
```
	@Test
	public void include() {
		Properties p = NiceProperties.load("properties", "test.properties",	"test");
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
```