package me.wilsonhu.appipswitch.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDownloadUtil {
 
    private HttpURLConnection httpConn;
    private InputStream inputStream;
 
    private String fileName;
    private int contentLength;
    
    public void downloadFile(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                String index = disposition.split("filename=")[1];
                fileName = index;
            } else {
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            inputStream = httpConn.getInputStream();
 
        } else {
            throw new IOException(
                    "No file to download. Server replied HTTP code: "
                            + responseCode);
        }
    }
 
    public void disconnect() throws IOException {
        inputStream.close();
        httpConn.disconnect();
    }
 
    public String getFileName() {
        return this.fileName;
    }
 
    public int getContentLength() {
        return this.contentLength;
    }
 
    public InputStream getInputStream() {
        return this.inputStream;
    }
}