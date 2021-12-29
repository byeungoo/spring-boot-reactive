package com.hoon.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hoon.domain.Cart;
import com.hoon.domain.Item;
import com.hoon.service.InventoryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HomeController.class)  // 웹 플럭스 슬라이스 테스트를 사용하도록 설정한다. HomeController에만 국한
public class HomeControllerSliceTest {

    @Autowired
    private WebTestClient client;   // 웹플럭스 슬라이스 테스트의 일부로서 WebTestClient 인스턴스가 생성되고 주입된다.

    @MockBean
    InventoryService inventoryService;

    /**
     * 웹플럭스 슬라이스 테스트 (4-10)
     */
    @Test
    void homePage() {
        when(inventoryService.getInventory()).thenReturn(Flux.just( //
                new Item("id1", "name1", 1.99), //
                new Item("id2", "name2", 9.99) //
        ));
        when(inventoryService.getCart("My Cart")) //
                .thenReturn(Mono.just(new Cart("My Cart")));

        client.get().uri("/").exchange() //
                .expectStatus().isOk() //
                .expectBody(String.class) //
                .consumeWith(exchangeResult -> {
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
                });
    }
}