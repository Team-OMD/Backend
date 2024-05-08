package com.onmydesk.backend.product.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class ProductSearchService {

    @Value("${naver.api.client.id}")
    private String clientId;

    @Value("${naver.api.client.secret}")
    private String clientSecret;

    public String searchProduct(String query, int display) {
        String text = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String apiURL = "https://openapi.naver.com/v1/search/shop.json?query=" + text + "&display=" + display;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return transformResponse(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        }
    }

    public String transformProductUrl(String originalUrl) {
        // 'id=' 문자열 뒤의 값을 찾기 위한 로직
        int idIndex = originalUrl.indexOf("id=") + 3; // 'id=' 다음 위치의 인덱스
        if (idIndex > 2) { // 'id='가 존재하는 경우
            String id = originalUrl.substring(idIndex);
            if(id.contains("&")) { // '&'가 포함된 경우, '&' 이전까지의 문자열을 id로 사용
                id = id.substring(0, id.indexOf("&"));
            }
            return "https://search.shopping.naver.com/catalog/" + id; // 새로운 URL 생성
        } else {
            // 'id='가 없는 경우
            throw new RuntimeException("상품 id를 가져올 수 없습니다.");
        }
    }

    private String transformResponse(String responseBody) {

        try {
            // API 응답을 JSON 객체로 파싱
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray items = responseJson.getAsJsonArray("items");

            // 데이터를 저장할 리스트 생성
            JsonArray products = new JsonArray();

            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                String originalLink = item.get("link").getAsString();
                String modifiedLink = transformProductUrl(originalLink);

                Document doc = Jsoup.connect(modifiedLink).get();

                Elements priceElements = doc.select("a.productList_value__B_IxM.linkAnchor");
                Elements linkElements = doc.select("a.productList_mall_link__TrYxC.linkAnchor");
                Elements storeNameElements = doc.select("a.productList_mall_link__TrYxC.linkAnchor");

                JsonArray pages = new JsonArray();

                for (int j = 0; j < priceElements.size(); j++) {
                    Element priceElement = priceElements.get(j);
                    Element linkElement = linkElements.get(j);
                    Element storeNameElement = storeNameElements.get(j);

                    // 각 요소로부터 데이터 추출
                    Elements priceTag = priceElement.select("em");
                    String priceText = priceTag.first().text();
                    int price = Integer.parseInt(priceText.replaceAll(",", ""));
                    String link = linkElement.attr("href");
                    Elements imgTag = storeNameElement.select("img");
                    String storeName = imgTag.attr("alt").isEmpty() ? storeNameElement.select("span").first().text() : imgTag.attr("alt");

                    // 추출한 데이터로 JSON 객체 생성
                    JsonObject page = new JsonObject();
                    page.addProperty("price", price);
                    page.addProperty("link", link);
                    page.addProperty("storeName", storeName);

                    // 리스트에 JSON 객체 추가
                    pages.add(page);
                }

                // 상품 정보와 페이지 리스트를 포함하는 최종 JSON 객체 생성
                JsonObject productInfo = new JsonObject();
                productInfo.addProperty("productName", item.get("title").getAsString());
                productInfo.addProperty("img", item.get("image").getAsString());
                productInfo.addProperty("productCode", item.get("productId").getAsString());
                productInfo.addProperty("lprice", item.get("lprice").getAsInt());
                productInfo.addProperty("brand", item.get("brand").getAsString());
                productInfo.addProperty("maker", item.get("maker").getAsString());
                productInfo.addProperty("category1", item.get("category1").getAsString());
                productInfo.addProperty("category2", item.get("category2").getAsString());
                productInfo.addProperty("category3", item.get("category3").getAsString());
                productInfo.addProperty("category4", item.get("category4").getAsString());
                productInfo.add("pages", pages);

                // 최종 데이터 리스트에 추가
                products.add(productInfo);
            }

            return new Gson().toJson(products);
        } catch (Exception e) {
            throw new RuntimeException("크롤링 실패 : " + e);
        }
    }
}
