package org.gem.indo.dooit.helpers;

import android.os.Build;

import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Created by Wimpie Victor on 2017/09/04.
 *
 * @link https://github.com/square/okhttp/issues/2372
 * <p>
 * Enables TLS v1.2 when creating SSLSockets.
 * <p/>
 * For some reason, android supports TLS v1.2 from API 16, but enables it by
 * default only from API 20.
 * @link https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
 * @see SSLSocketFactory
 */
public class Tls12SocketFactory extends SSLSocketFactory {
    private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

    // From okhttp3.ConnectionSpec
    // This is a subset of the cipher suites supported in Chrome 46, current as of 2015-11-05.
    // All of these suites are available on Android 5.0; earlier releases support a subset of
    // these suites. https://github.com/square/okhttp/issues/330
    public static final String[] CIPHER_SUITS = {
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256.javaName(),
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256.javaName(),
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256.javaName(),

            // Note that the following cipher suites are all on HTTP/2's bad cipher suites list. We'll
            // continue to include them until better suites are commonly available. For example, none
            // of the better cipher suites listed above shipped with Android 4.4 or Java 7.
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA.javaName(),
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA.javaName(),
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA.javaName(),
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA.javaName(),
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA.javaName(),
            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA.javaName(),
            CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256.javaName(),
            CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA.javaName(),
            CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA.javaName(),
            CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA.javaName(),
    };

    private final SSLSocketFactory delegate;

    public Tls12SocketFactory(SSLSocketFactory base) {
        this.delegate = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return patch(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return patch(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket s) {
        if (s instanceof SSLSocket) {
            ((SSLSocket) s).setEnabledProtocols(TLS_V12_ONLY);
            ((SSLSocket) s).setEnabledCipherSuites(CIPHER_SUITS);
        }
        return s;
    }

    /**
     * Prevent fallback to SSLv3 on old Android devices. Attempt to force TLSv1.2.
     * <p>
     * https://github.com/square/okhttp/issues/2372
     *
     * @param httpClient
     */
    public static void forceTLS(OkHttpClient.Builder httpClient) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            try {
                // SSLv3, TLSv1, TLSv1.1, TLSv1.2 etc.
                // Log which protocols are available
                CrashlyticsHelper.log("SSL Default Protocols", "onCreate",
                        Arrays.toString(SSLContext.getDefault()
                                .getDefaultSSLParameters().getProtocols()));
                SSLParameters sslParameters = SSLContext.getDefault()
                        .getSupportedSSLParameters();
                CrashlyticsHelper.log("SSL Supported Protocols", "onCreate",
                        Arrays.toString(sslParameters.getProtocols()));

                // Scan for TLSv1.2 support
                boolean found = false;
                for (String protocol : sslParameters.getProtocols()) {
                    if (protocol.equals(TlsVersion.TLS_1_2.javaName())) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    SSLContext sc = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName());
                    sc.init(null, null, null);
                    SSLSocketFactory factory = new Tls12SocketFactory(sc.getSocketFactory());
                    httpClient.sslSocketFactory(factory);

                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .cipherSuites(Tls12SocketFactory.CIPHER_SUITS)
                            .build());
                    specs.add(ConnectionSpec.CLEARTEXT); // HTTP Fallback

                    httpClient.connectionSpecs(specs);
                } else {
                    CrashlyticsHelper.log("TLS Support:", "onCreate", "TLSv1.2 not supported!");
                }
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                CrashlyticsHelper.logException(e);
            }
        }
    }
}