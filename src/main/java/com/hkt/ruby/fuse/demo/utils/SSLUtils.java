package com.hkt.ruby.fuse.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.camel.component.http4.HttpComponent;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Common utils
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
public class SSLUtils {

    public static SSLContextParameters sslContextParameters(String keystorePath, String keystorePass, String truststorePath, String truststorePass) {

        KeyStoreParameters keyParams = new KeyStoreParameters();
        keyParams.setResource(keystorePath);
        keyParams.setPassword(keystorePass);

        KeyManagersParameters keystore = new KeyManagersParameters();
        keystore.setKeyStore(keyParams);

        KeyStoreParameters trustParams = new KeyStoreParameters();
        trustParams.setResource(truststorePath);
        trustParams.setPassword(truststorePass);

        TrustManagersParameters truststore = new TrustManagersParameters();
        truststore.setKeyStore(trustParams);

        SSLContextParameters parameters = new SSLContextParameters();
        parameters.setTrustManagers(truststore);
        parameters.setKeyManagers(keystore);

        return parameters;
    }

    public static SSLContextParameters sslContextParameters(String keystorePath, String keystorePass) {
        return sslContextParameters(keystorePath, keystorePass, null, null);
    }

    public static HttpComponent skipHostnameCheck(HttpComponent httpComponent) {
        httpComponent.setX509HostnameVerifier(NoopHostnameVerifier.INSTANCE);

        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
        X509ExtendedTrustManager extendedTrustManager = new InsecureX509TrustManager();
        trustManagersParameters.setTrustManager(extendedTrustManager);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setTrustManagers(trustManagersParameters);
        httpComponent.setSslContextParameters(sslContextParameters);

        //This is important to make your cert skip CN/Hostname checks
        httpComponent.setX509HostnameVerifier((s, sslSession) -> {
            //I don't mind just return true for all or you can add your own logic
            log.info(s + sslSession);
            return true;
        });

        return httpComponent;
    }

    private static class InsecureX509TrustManager extends X509ExtendedTrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
            //Do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
            //Do nothing
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
            //Do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
            //Do nothing
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            //Do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            //Do nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
