/*
 * Copyright (c) 2019 Felix Kopp <sandtler@sandtler.club>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sandtler.devid.lib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import club.sandtler.devid.BuildConfig;

/**
 * Utility class for performing authenticated HTTP requests to the backend.
 * All methods in this class are blocking, meaning you may not call them
 * from the UI thread (Android prohibits networking from the UI anyways).
 *
 * TODO: Add more HTTP request methods.
 */
public final class NetworkUtil {

    /** The default instance. */
    private static final NetworkUtil defaultInstance = new NetworkUtil(null);

    /** The authentication token. */
    private final String mAuthToken;

    /**
     * Return the default (unauthenticated) instance.
     *
     * @return The default instance.
     */
    @NonNull
    public static NetworkUtil getDefault() {
        return NetworkUtil.defaultInstance;
    }

    /**
     * Create a new authenticated network utility.
     * If the authentication token is null, the <code>Authentication</code>
     * request header will be left out.
     *
     * @param authToken The logged in user's authentication token.
     */
    public NetworkUtil(@Nullable String authToken) {
        /*
         * I know this check is duplicate.
         * However, this is definitively something that CAN NOT,
         * UNDER ALL CIRCUMSTANCES, get fucked up.  I'm paranoid.
         */
        if (BuildConfig.DEBUG) {
            disableAllSSLCertificateChecks();
        }

        this.mAuthToken = authToken;
    }

    /**
     * Preform a HTTP GET request and return the JSON response.
     *
     * @param path The request path.
     * @return The JSON response, or null if it was empty.
     * @throws IOException If an I/O error occurred.
     * @throws JSONException If the JSON response was malformed.
     */
    @Nullable
    public JSONObject get(@NonNull String path) throws IOException, JSONException {
        HttpsURLConnection conn = getConnection(path);
        JSONObject response;

        try {
            conn.setRequestMethod("GET");
            response = readResponse(conn);
        } finally {
            conn.disconnect();
        }

        return response;
    }

    /**
     * Perform a HTTP POST request and return the JSON response.
     *
     * @param path The absolute request path.
     * @param body The request body.
     * @return The JSON response.
     * @throws IOException If there was an error while transmitting data.
     * @throws JSONException If the JSON response was malformed.
     */
    @Nullable
    public JSONObject post(@NonNull String path, @NonNull JSONObject body)
    throws IOException, JSONException {
        HttpsURLConnection conn = getConnection(path);
        JSONObject response;

        try {
            conn.setDoOutput(true);

            OutputStream out = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(out, StandardCharsets.UTF_8)
            );
            writer.write(body.toString());
            writer.flush();
            writer.close();
            out.close();

            response = readResponse(conn);
        } finally {
            conn.disconnect();
        }

        return response;
    }

    /**
     * Instantiate a new {@link HttpsURLConnection} to the specified path
     * and set common headers.
     *
     * @param path The absolute request path (will be appended to
     *             {@link Constants#BACKEND_ROOT})
     * @return The new connection.
     * @throws IOException If the URL was malformed, or the connection could
     *                     not be established.
     */
    private HttpsURLConnection getConnection(String path) throws IOException {
        URL url = new URL(Constants.BACKEND_ROOT + path);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        if (this.mAuthToken != null) {
            conn.setRequestProperty("Authentication", "Bearer " + this.mAuthToken);
        }

        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        return conn;
    }

    /**
     * Read the response from a HTTP request after it was sent.
     *
     * @param conn The connection.
     * @return The JSON response, or null if it was empty.
     * @throws IOException If an error was encountered while trying to read the
     *                     response body.
     * @throws JSONException If the response body did not contain valid JSON.
     */
    private static JSONObject readResponse(HttpsURLConnection conn)
    throws IOException, JSONException {
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer responseBuf = new StringBuffer();

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            responseBuf.append(line);
        }
        reader.close();
        in.close();

        if (responseBuf.length() > 0) {
            return new JSONObject(responseBuf.toString());
        }

        return null;
    }

    /**
     * Disable any SSL certificate checks in the entire application.
     * This imposes a severe security risk and may only be used in private,
     * trusted networks.  Never call this without checking if we are on a
     * debug build first!
     */
    private static void disableAllSSLCertificateChecks() {
        // Do. not. remove. this.
        if (!BuildConfig.DEBUG) {
            return;
        }

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                }
        };
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

}
