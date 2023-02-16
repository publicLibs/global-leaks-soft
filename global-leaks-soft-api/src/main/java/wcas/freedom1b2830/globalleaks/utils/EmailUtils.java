package wcas.freedom1b2830.globalleaks.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmailUtils {

	public static final String regex = "[-a-z0-9_]*@[-.a-z0-9_]{1,}\\.[-.a-z0-9_]*";
	public static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

	public static List<String> parseEmail(final String data) {
		final var matcher = pattern.matcher(data);
		final ArrayList<String> retList = new ArrayList<>();

		while (matcher.find()) {
			retList.add(matcher.group(0));

			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}
		return retList;
	}
}
