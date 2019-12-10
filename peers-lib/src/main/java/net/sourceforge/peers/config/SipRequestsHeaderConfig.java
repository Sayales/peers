package net.sourceforge.peers.config;


import java.util.List;


/**
 * @author opokin.p
 */
public interface SipRequestsHeaderConfig
{
    List<String> getAllowedMethods();

    List<String> getSupported();

    String getUserAgent();

    Integer getUnregisterExpiresTime();
}
