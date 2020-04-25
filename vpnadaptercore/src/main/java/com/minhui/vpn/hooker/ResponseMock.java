package com.minhui.vpn.hooker;

public class ResponseMock {
    String url;
    String header;
    String body;
    public ResponseMock(String url, String header,String body) {
        this.url = url;
        this.header = header;
        this.body = body;
    }
}
