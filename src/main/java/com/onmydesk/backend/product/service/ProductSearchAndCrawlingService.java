package com.onmydesk.backend.product.service;

import com.google.gson.*;
import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.product.domain.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchAndCrawlingService {

    @Value("${naver.api.client.id}")
    private String clientId;

    @Value("${naver.api.client.secret}")
    private String clientSecret;

    @CircuitBreaker(name = "searchProductCircuitBreaker", fallbackMethod = "fallback")
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
            String responseBody = response.body();

            // Gson 객체 생성 시 setPrettyPrinting() 호출하여 인스턴스 생성
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // API 응답 문자열을 JsonObject로 파싱
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            // "items" JsonArray 추출
            JsonArray items = jsonResponse.getAsJsonArray("items");

            // 결과를 저장할 JsonArray 생성
            JsonArray products = new JsonArray();

            // items 내의 각 JsonObject에 대해 원하는 정보 추출
            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();

                JsonObject product = new JsonObject();
                // 상품 이름에서 HTML 태그 제거
                String productName = item.get("title").getAsString().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
                product.addProperty("productName", productName);
                product.addProperty("img", item.get("image").getAsString());
                product.addProperty("productCode", item.get("productId").getAsString());
                product.addProperty("lprice", item.get("lprice").getAsInt());
                product.addProperty("brand", item.get("brand").getAsString());
                product.addProperty("maker", item.get("maker").getAsString());
                product.addProperty("category1", item.get("category1").getAsString());
                product.addProperty("category2", item.get("category2").getAsString());
                product.addProperty("category3", item.get("category3").getAsString());
                product.addProperty("category4", item.get("category4").getAsString());

                // 추출한 정보를 결과 JsonArray에 추가
                products.add(product);
            }
            // 결과 JsonArray를 String으로 변환하여 반환
            return gson.toJson(products);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        }
    }

    public String fallback(String query, int display, Throwable t) {
        return "Fallback! exception type: " + t.getClass() + ", message: " + t.getMessage();
    }

    public List<Page> crawlPage(Product product) {
        try {
            // 상품 요청 DTO로부터 productCode를 가져옴
            String id = product.getProductCode();

            // 상품 페이지 URL 생성
            String modifiedLink = "https://search.shopping.naver.com/catalog/" + id;

            Document doc = Jsoup.connect(modifiedLink).get();

            Elements priceElements = doc.select("a.productList_value__B_IxM.linkAnchor");
            Elements linkElements = doc.select("a.productList_mall_link__TrYxC.linkAnchor");
            Elements storeNameElements = doc.select("a.productList_mall_link__TrYxC.linkAnchor");

            List<Page> pages = new ArrayList<>();

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

                Page page = Page.builder()
                        .product(product)
                        .price(price)
                        .link(link)
                        .storeName(storeName)
                        .build();

                pages.add(page);
            }

            return pages;
        } catch (Exception e) {
            throw new RestApiException(ProductErrorCode.CRAWLING_FAILED);
        }
    }
}
