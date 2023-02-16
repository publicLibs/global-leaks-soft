package wcas.freedom1b2830.globalleaks.module;

public interface ModuleInterface {
	default boolean canProcess() {
		return true;
	}
}
