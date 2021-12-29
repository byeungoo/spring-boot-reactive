package com.hoon.controller;

import com.hoon.repository.CartRepository;
import com.hoon.repository.ItemRepository;
import com.hoon.service.InventoryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 테스트할 때 임의의 포트에 내장 컨테이너를 바인딩한다.
@AutoConfigureWebTestClient // 애플리케이션에 요청을 날리는 WebTestClient 인스턴스를 생성한다.
public class LoadingWebSiteIntegrationTest {

    @Autowired
    WebTestClient client;

    /**
     * 실제 웹 컨테이너를 사용하는 테스트 케이스
     */
    @Test
    void test() {
        client.get().uri("/").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .consumeWith(exchangeResult -> {
                    Assertions.assertThat(exchangeResult.getResponseBody()).contains("<a href=\"/add");
                });
    };

}
