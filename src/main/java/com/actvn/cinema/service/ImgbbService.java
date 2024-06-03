package com.actvn.cinema.service;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface ImgbbService {
    HttpResponse<String> upload(byte[] imageBytes) throws IOException, InterruptedException;

    String getDisplayUrl(String responseBody);
}
