package nice.properties.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NiceProperties {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NiceProperties.class);

	public static Properties load(String rootPath, String fileName) {
		return load(rootPath, fileName);
	}

	public static Properties load(String rootPath, String fileName, String namespace) {
		Properties propsFromFile = readFromFile(rootPath, fileName);

		// Check for instance specifics configuration
		Properties newConfiguration = new Properties();
		Pattern pattern = Pattern.compile("^%([a-zA-Z0-9_\\-]+)\\.(.*)$");
		for (Object key : propsFromFile.keySet()) {
			Matcher matcher = pattern.matcher(key + "");
			if (!matcher.matches()) {
				newConfiguration.put(key, propsFromFile.get(key).toString()
						.trim());
			}
		}

		// Overwrite instance specifics
		for (Object key : propsFromFile.keySet()) {
			Matcher matcher = pattern.matcher(key + "");
			if (matcher.matches()) {
				String instance = matcher.group(1);
				if (instance.equals(namespace)) {
					newConfiguration.put(matcher.group(2),
							propsFromFile.get(key).toString().trim());
				}
			}
		}
		propsFromFile = newConfiguration;
		
        // Resolve ${..} with system properties or system environment
        pattern = Pattern.compile("\\$\\{([^}]+)}");
        for (Object key : propsFromFile.keySet()) {
            String value = propsFromFile.getProperty(key.toString());
            Matcher matcher = pattern.matcher(value);
            StringBuffer newValue = new StringBuffer(100);
            while (matcher.find()) {
                String jp = matcher.group(1);
                String r = System.getProperty(jp);
                if (r == null) {
                    r = System.getenv(jp);
                }
                if (r == null) {
                	LOGGER.warn("Cannot replace %s in configuration (%s=%s)", jp, key, value);
                    continue;
                }
                matcher.appendReplacement(newValue, r.replaceAll("\\\\", "\\\\\\\\"));
            }
            matcher.appendTail(newValue);
            propsFromFile.setProperty(key.toString(), newValue.toString());
        }

		// Include other files
		Map<Object, Object> toInclude = new HashMap<Object, Object>(16);
		for (Object key : propsFromFile.keySet()) {
			if (key.toString().startsWith("@include.")) {
				try {
					String filenameToInclude = propsFromFile.getProperty(key
							.toString());
					toInclude.putAll(load(rootPath, filenameToInclude,
							namespace));
				} catch (Exception ex) {
					 LOGGER.warn("Missing include: %s", key);
				}
			}
		}
		propsFromFile.putAll(toInclude);

		return propsFromFile;
	}

	private static Properties readFromFile(String rootPath, String fileName) {
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(getResource(rootPath, fileName));
			properties.load(is);
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return properties;
	}

	
	private static File getResource(String rootPath, String fileName) {
		String filePath = rootPath + "/" + fileName;
		File file = null;
		
		file = new File(filePath);
		if(file.exists()) 
			return file;
		
		file = new File(NiceProperties.class.getClassLoader().getResource(filePath).getFile());
		if(file.exists()) 
			return file;
		
		return file;
	}
}
