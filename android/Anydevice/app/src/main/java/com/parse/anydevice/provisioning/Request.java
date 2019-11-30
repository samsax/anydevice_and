package com.parse.anydevice.provisioning;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

class Request {
    private static final String UTF8_ENCODING = "UTF-8";

    private List<Pair> params = new LinkedList<>();
    private List<Pair> headers = new LinkedList<>();
    private String method, url;
    private String body = "";

    public Request() {}

    public Request url(String url) {
        this.url = url;
        return this;
    }

    public Request post() {
        method = "POST";
        return this;
    }

    public Request put() {
        method = "PUT";
        return this;
    }

    public Request param(String name, String value) {
        params.add(new Pair(name, value));
        return this;
    }

    public Request header(String name, String value) {
        headers.add(new Pair(name, value));
        return this;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    /**
     * Performs network request
     *
     * @return Pair containing status code and body
     *
     * @throws IOException
     */
    public Pair<Integer, String> execute() throws IOException {
        if (method == null || url == null) {
            throw new RuntimeException("Request cannot execute without a method and a URL");
        }

        final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod(method);
        for (Pair pair : headers) {
            urlConnection.setRequestProperty(pair.first.toString(), pair.second.toString());
        }
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        { // add body
            if (body.length() == 0 && params.size() > 0) {
                body = getQuery(params);
            }

            if (body.length() > 0) {
                urlConnection.setRequestProperty("Content-Length", Integer.toString(body.length()));
                final DataOutputStream stream = new DataOutputStream(urlConnection.getOutputStream());
                stream.writeBytes(body);
                stream.flush();
                stream.close();
            }
        }

        urlConnection.connect();
        final int responseCode = urlConnection.getResponseCode();
        final BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), UTF8_ENCODING));
        final StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        streamReader.close();
        urlConnection.disconnect();

        return new Pair<>(responseCode, responseStrBuilder.toString());
    }

    /**
     * Constructs the NAME=VALUE &-delimited string of parameters for sending to the server
     *
     * @param params The list of parameters to combine
     *
     * @return The constructed string
     *
     * @throws UnsupportedEncodingException
     */
    private static String getQuery(@NonNull final List<Pair> params) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.first.toString(), UTF8_ENCODING));
            result.append("=");
            result.append(URLEncoder.encode(pair.second.toString(), UTF8_ENCODING));
        }

        return result.toString();
    }


}
