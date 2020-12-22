package com.hkt.ruby.fuse.demo.utils;

import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;

public class SSLUtil {

    public static SSLContextParameters sslContextParameters(String keystorePath, String keystorePass){

        KeyStoreParameters store = new KeyStoreParameters();
        store.setResource(keystorePath);
        store.setPassword(keystorePass);

        KeyManagersParameters key = new KeyManagersParameters();
        key.setKeyPassword("");
        key.setKeyStore(store);

        TrustManagersParameters trust = new TrustManagersParameters();
        trust.setKeyStore(store);

        SSLContextParameters parameters = new SSLContextParameters();
        parameters.setTrustManagers(trust);
        parameters.setKeyManagers(key);

        return parameters;
    }
}
