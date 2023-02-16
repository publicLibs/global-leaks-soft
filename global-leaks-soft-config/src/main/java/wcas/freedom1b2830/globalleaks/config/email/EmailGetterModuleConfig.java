package wcas.freedom1b2830.globalleaks.config.email;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmailGetterModuleConfig {
	public CopyOnWriteArrayList<String> sources = new CopyOnWriteArrayList<>(
			Arrays.asList("http://sources.com/1", "http://sources.com/2"));

}