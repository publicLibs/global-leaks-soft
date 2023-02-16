package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl;

import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;

public abstract class AVC extends GlobalLeakModule {

	protected AVC(GlobalLeakConfig config) {
		super(config, AVC.class.getSimpleName());
	}

}
