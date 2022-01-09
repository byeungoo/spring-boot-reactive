package com.hoon.web;

import com.hoon.domain.Customer;
import com.hoon.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient; // 비동기로 http 요청

    @MockBean
    private CustomerRepository customerRepository;

    @Test
    public void 한건찾기_테스트() {

        // given
        Customer customer = new Customer("gil", "dong");
        Mockito.when(customerRepository.findById(1L)).thenReturn(Mono.just(customer));  // stub -> 행동 지식

        // when && then
        webTestClient.get().uri("/customer/1").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("gil")
                .jsonPath("$.lastName").isEqualTo("dong")
                ;
    }

}
