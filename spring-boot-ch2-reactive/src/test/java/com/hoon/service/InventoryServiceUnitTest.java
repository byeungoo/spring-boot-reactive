package com.hoon.service;

import com.hoon.domain.Cart;
import com.hoon.domain.CartItem;
import com.hoon.domain.Item;
import com.hoon.repository.CartRepository;
import com.hoon.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)  // 테스트 핸들러 지정. SpringExtension 은 스프링에 특화된 테스트 기능을 사용할 수 있게 해준다.
class InventoryServiceUnitTest {

    InventoryService inventoryService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private ReactiveFluentMongoOperations reactiveFluentMongoOperations;

    @MockBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        Item sampleItem = new Item("item1", "TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, reactiveFluentMongoOperations, cartRepository); // <4>
    }

    /**
     * 리액티브 코드를 테스트할 때는 기능뿐만 아니라 리액티브 스트림 시그널도 함께 검사해야한다.
     * 리액티브 스트림은 onSubscribe, onNext, onError, onComplete를 말한다.
     * StepVerifier가 구독을하고 값을 확인할 수 있게해준다.
     * 탑레밸 방식의 테스트 (4-6)
     */
    @Test
    void addItemToEmptyCartShouldProduceOneCartItem() {
        inventoryService.addItemToCart("My Cart", "item1")

                .as(StepVerifier::create)   // 테스트 대상 메소드의 반환 타입인 Mono<Cart>를 리액터 테스트 모듈의 정적 메소드인 StepVerifier.create()에 메소드 레퍼런스로 연결해서
                                            // 테스트 기능을 전담하는 리액터 타입 핸들러를 생성한다.

                .expectNextMatches(cart -> {    // 함수와 람다식을 사용하여 결과를 검증한다.
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item("item1", "TV tray", 19.99));

                    return true;
                })
                .verifyComplete(); // 리액티브 스트림의 complete 시그널이 발생하고 리액터 플로우가 성공적으로 완료됐음을검증
    }

    /**
     * 탑레밸 방식과는 다른 방식으로 작성한 테스트 코드 (4-7)
     *
     */
    @Test
    void alternativeWayToTest() {
        StepVerifier.create(
                inventoryService.addItemToCart("My Cart", "item1"))
                .expectNextMatches(
                        cart -> {    // 함수와 람다식을 사용하여 결과를 검증한다.
                            assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                                    .containsExactlyInAnyOrder(1);

                            assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                                    .containsExactly(new Item("item1", "TV tray", 19.99));

                            return true;
                        })
                        .verifyComplete();
    }

}