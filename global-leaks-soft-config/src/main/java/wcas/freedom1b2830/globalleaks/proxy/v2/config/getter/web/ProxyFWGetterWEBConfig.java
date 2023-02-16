package wcas.freedom1b2830.globalleaks.proxy.v2.config.getter.web;

import java.util.ArrayList;
import java.util.Arrays;

public class ProxyFWGetterWEBConfig {
	public ArrayList<String> sources = new ArrayList<>(Arrays.asList(
			"https://api.proxyscrape.com/v2/?request=displayproxies&protocol=all&timeout=10000&country=all&ssl=all&anonymity=all"));

}
