package wcas.freedom1b2830.globalleaks.module;

import org.jetbrains.annotations.NotNull;

public interface EmailGetterModuleWorkerListener {
	void httpClientException(Exception e);

	void parsedInputEmail(@NotNull String line);

	void rawInputWebPage(@NotNull String line);

	void sourceException(Exception e);

}
