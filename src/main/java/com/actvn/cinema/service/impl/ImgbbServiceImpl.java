package com.actvn.cinema.service.impl;

import com.actvn.cinema.service.ImgbbService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ImgbbServiceImpl implements ImgbbService {
    @Value("${imgbb.api-key}")
    private String API_KEY;

    @Value("${imgbb.base-upload-url}")
    private String BASE_UPLOAD_URL;

    @Override
    public HttpResponse<String> upload(byte[] imageBytes) throws IOException, InterruptedException {
        // Chuyển đổi dữ liệu nhị phân của hình ảnh thành dữ liệu Base64
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String uploadUrl = BASE_UPLOAD_URL + "key=" + API_KEY;

        // Gửi yêu cầu POST đến API của ImgBB để tải lên hình ảnh
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("image=" + URLEncoder.encode(base64Image, StandardCharsets.UTF_8)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    @Override
    public String getDisplayUrl(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        return dataObject.getString("display_url");
    }
}
