package wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.ApiException;

@DatabaseTable(tableName = "proxys")
public class ProxyData {

	private static final ObjectMapper MAPPER_json = new ObjectMapper();

	private static final ObjectMapper MAPPER_yaml = new ObjectMapper(new YAMLFactory());

	public static ProxyData parse(final String input) throws ApiException {
		final var string = input.trim().strip().replaceAll("[\n ]+", "");
		if (string.contains("YouhavereachedyourhourlymaximumAPIrequestsof750")
				|| string.contains("Wehavetotemporarilystopyou")) {
			throw new ApiException(input);
		}

		if (string.contains("@")) {
			final var proxyData = new ProxyData();
			final var data = string.split("@");

			// String auth = data[0];

			final var connection = data[1];
			final var connectionData = connection.split(":");
			proxyData.host = connectionData[0];
			proxyData.port = Integer.valueOf(connectionData[1]);
			proxyData.beforDB();
			throw new UnsupportedOperationException("auth not supported");
		}

		try {
			final var proxyData = new ProxyData();
			final var data = string.split(":");
			proxyData.host = data[0];
			proxyData.port = Integer.valueOf(data[1]);
			proxyData.beforDB();
			return proxyData;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(input);
	}

	public @DatabaseField(id = true) Integer proxyID;
	public @DatabaseField(canBeNull = false) String host;

	public @DatabaseField(canBeNull = false) Integer port;

	public @DatabaseField String country;
	public @DatabaseField String ASN;
	public @DatabaseField String user;
	public @DatabaseField String password;
	public @DatabaseField ProxyType type = ProxyType.RAW;

	public @DatabaseField long lastChecked;

	public @DatabaseField String hostExit;

	public @DatabaseField String countryExit;
	public @DatabaseField String ASNExit;

	public @DatabaseField Boolean socketConnection;
	public @DatabaseField Boolean http;

	public @DatabaseField int httpCode;
	public @DatabaseField String httpReason;
	public @DatabaseField Boolean socks;

	public @DatabaseField String httpResponse;

	public @DatabaseField boolean cloudflare;

	public @DatabaseField boolean tor;

	public @DatabaseField boolean mikrotikhttpproxy;

	public ProxyData() {
	}

	public Integer beforDB() {
		this.proxyID = hashCode();
		return Integer.valueOf(proxyID);
	}

	public @Override boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final var other = (ProxyData) obj;
		return Objects.equals(host, other.host) && Objects.equals(port, other.port);
	}

	public @Override int hashCode() {
		return Objects.hash(host, port);
	}

	public String toJsonString() throws JsonProcessingException {
		return MAPPER_json.writeValueAsString(this);
	}

	public @Override String toString() {
		final var builder = new StringBuilder();
		builder.append("ProxyData [");
		if (proxyID != null) {
			builder.append("proxyID=").append(proxyID).append(", ");
		}
		if (host != null) {
			builder.append("host=").append(host).append(", ");
		}
		if (port != null) {
			builder.append("port=").append(port).append(", ");
		}
		if (country != null) {
			builder.append("country=").append(country).append(", ");
		}
		if (ASN != null) {
			builder.append("ASN=").append(ASN).append(", ");
		}
		if (user != null) {
			builder.append("user=").append(user).append(", ");
		}
		if (password != null) {
			builder.append("password=").append(password).append(", ");
		}
		if (type != null) {
			builder.append("type=").append(type).append(", ");
		}
		builder.append("lastChecked=").append(lastChecked).append(", ");
		if (hostExit != null) {
			builder.append("hostExit=").append(hostExit).append(", ");
		}
		if (countryExit != null) {
			builder.append("countryExit=").append(countryExit).append(", ");
		}
		if (ASNExit != null) {
			builder.append("ASNExit=").append(ASNExit).append(", ");
		}
		if (socketConnection != null && socketConnection) {
			builder.append("socketConnection=").append(socketConnection).append(", ");
		}
		if (http != null && http) {
			builder.append("http=").append(http).append(", ");
			builder.append("httpCode=").append(httpCode).append(", ");
			if (httpReason != null) {
				builder.append("httpReason=").append(httpReason).append(", ");
			}
		}
		if (socks != null && socks) {
			builder.append("socks=").append(socks).append(", ");
		}
		if (httpResponse != null) {
			builder.append("httpResponse=").append(httpResponse).append(", ");
		}
		if (cloudflare) {
			builder.append("cloudflare=").append(cloudflare).append(", ");
		}
		if (tor) {
			builder.append("tor=").append(tor);
		}
		if (mikrotikhttpproxy) {
			builder.append(", mikrotikhttpproxy=").append(mikrotikhttpproxy);
		}
		builder.append("]");
		return builder.toString();
	}

	public String toYamlString() throws JsonProcessingException {
		return MAPPER_yaml.writeValueAsString(this);
	}
}
