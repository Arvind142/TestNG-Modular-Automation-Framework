package com.core.framework.config;

import com.core.framework.constant.FrameworkConstants;
import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "file:${user.dir}/"+ FrameworkConstants.APPLICATION_GLOBAL_CONFIG,
})
public interface FrameworkConfig extends Config {
    @DefaultValue("edge")
    String browser();

    @DefaultValue("false")
    boolean isRemote();

    @DefaultValue("")
    String remoteUrl();
}
