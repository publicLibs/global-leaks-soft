package wcas.freedom1b2830.globalleaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.jetbrains.annotations.NotNull;

import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;

public class HostUtils {
	private static final String IP_V4_REGEX_STRING = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
	private static final String IP_V6_REGEX_STRING = "([a-z0-9]*:){1,9}([a-z0-9]*)";

	public static @NotNull GeoipData geoiplookup(final String host) throws AntiPidorException, IOException {
		var cmd = "geoiplookup";
		final int hostType = testHost(host);
		if (hostType == 1) {
			cmd = cmd + "6";
		}

		final var geoipData = new GeoipData();
		final var processBuilder = new ProcessBuilder(cmd, host);
		final var process = processBuilder.start();

		try (BufferedReader ir = process.inputReader(StandardCharsets.UTF_8)) {
			String line;
			while ((line = ir.readLine()) != null) {
				if (line.contains("Country Edition")) {
					final String dat = line.split("Edition: ")[1].split(", ")[0];
					geoipData.country = dat;
				} else if (line.contains("ASNum")) {
					final String dat = line.split("Edition: ")[1].split(" ")[0];
					geoipData.ASN = dat;
				} else {
					geoipData.noParsedData.add(line);
				}
			}
		}
		return geoipData;
	}

	private static int testHost(final String host) throws AntiPidorException {
		if (host.matches(IP_V4_REGEX_STRING)) {
			return 0;
		}
		if (host.matches(IP_V6_REGEX_STRING)) {
			try {
				final File ipv6File = new File("ipv6.txt");
				if (!ipv6File.exists()) {
					ipv6File.createNewFile();
				}
				Files.writeString(ipv6File.toPath(), host + '\n', StandardOpenOption.APPEND);
			} catch (final IOException e) {
				e.printStackTrace();
			}

			return 1;
		}

		try {
			final File pidFile = new File("antip.txt");
			if (!pidFile.exists()) {
				pidFile.createNewFile();
			}
			Files.writeString(pidFile.toPath(), host + '\n', StandardOpenOption.APPEND);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		throw new AntiPidorException(host);
	}

}
