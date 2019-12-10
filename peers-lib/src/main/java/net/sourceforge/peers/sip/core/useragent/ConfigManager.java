package net.sourceforge.peers.sip.core.useragent;


import net.sourceforge.peers.config.Config;
import net.sourceforge.peers.config.DefaultSipHeadersConfig;
import net.sourceforge.peers.config.SipRequestsHeaderConfig;


/**
 * @author opokin.p
 */
public class ConfigManager
{

    private Config config;

    private SipRequestsHeaderConfig sipRequestsHeaderConfig = new DefaultSipHeadersConfig();

    public static ConfigManager getInstance()
    {
        return ConfigManagerHolder.INSTANCE;
    }

    private static class ConfigManagerHolder
    {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    private ConfigManager()
    {

    }

    void registerConfig(Config config)
    {
        this.config = config;
    }

    void registerSipRequestHeadersConfig(SipRequestsHeaderConfig sipRequestsHeaderConfig)
    {
        this.sipRequestsHeaderConfig = sipRequestsHeaderConfig;
    }


    public Config getConfig()
    {
        return config;
    }


    public SipRequestsHeaderConfig getSipRequestsHeaderConfig()
    {
        return sipRequestsHeaderConfig;
    }
}
