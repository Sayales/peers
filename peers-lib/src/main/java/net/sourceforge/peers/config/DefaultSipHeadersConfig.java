package net.sourceforge.peers.config;


import java.util.Arrays;
import java.util.List;

import net.sourceforge.peers.sip.RFC3261;


/**
 * @author opokin.p
 */
public class DefaultSipHeadersConfig implements SipRequestsHeaderConfig
{
    @Override
    public List<String> getAllowedMethods()
    {
        return Arrays.asList(RFC3261.METHOD_INVITE, RFC3261.METHOD_ACK, RFC3261.METHOD_BYE,
            RFC3261.METHOD_CANCEL, RFC3261.METHOD_INFO, RFC3261.METHOD_MESSAGE, RFC3261.METHOD_REFER,
            RFC3261.METHOD_UPDATE, RFC3261.METHOD_PRACK);
    }


    @Override
    public List<String> getSupported()
    {
        return Arrays.asList("100rel", "replaces", "from-change", "gruu");
    }


    @Override
    public String getUserAgent()
    {
        return "PEERS Sipper";
    }


    @Override
    public Integer getUnregisterExpiresTime()
    {
        return 0;
    }
}
