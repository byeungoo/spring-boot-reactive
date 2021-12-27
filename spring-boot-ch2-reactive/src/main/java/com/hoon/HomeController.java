package com.hoon;

import com.hoon.domain.Cart;
import com.hoon.domain.CartItem;
import com.hoon.domain.Item;
import com.hoon.repository.CartRepository;
import com.hoon.repository.ItemRepository;
import com.hoon.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
public class HomeController {

    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private InventoryService inventoryService;

    public HomeController(ItemRepository itemRepository,
                          CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.itemRepository.findAll().doOnNext(System.out::println))
                .modelAttribute("cart", this.cartRepository.findById("My Cart").defaultIfEmpty(new Cart("My Cart")))
                .build());
    }

    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        return this.cartRepository.findById("My Cart")
                .defaultIfEmpty(new Cart("My Cart"))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem()
                                .getId().equals(id))
                        .findAny()  // Optional<CartItem> 반환
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        })
                        .orElseGet(() -> { // 새로 장바구니에 담은 상품이 장바구니에 없을 경우
                            return this.itemRepository.findById(id)
                                    .map(item -> new CartItem(item))
                                    .map(cartItem -> {
                                        cart.getCartItems().add(cartItem);
                                        return cart;
                                    });
                        }))
                .flatMap(cart -> this.cartRepository.save(cart))    // flatMap을 사용해야 Mono<Cart>가 반환된다. 안그러면 Mono<Mono<Cart>>가 반환된다.
                                                                    // flatMap은 이것의 스트림을 다른 크기로 된 저것의 스트림으로 바꾸는 함수형 도구
                .thenReturn("redirect:/");  // 웹플럭스가 다시 '/' 위치로 리다이렉트
    }

    @PostMapping
    Mono<String> createItem(@ModelAttribute Item newItem) {
        return this.itemRepository.save(newItem)
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    Mono<String> deleteItem(@PathVariable String id) {
        return this.itemRepository.deleteById(id)
                .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    Mono<Rendering> search(@RequestParam(required = false) String name,
                           @RequestParam(required = false) String description,
                           @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("results", inventoryService.searchByExample(name, description, useAnd))
        .build());
    }
}