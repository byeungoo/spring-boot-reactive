package com.hoon.service;


import com.hoon.domain.Cart;
import com.hoon.domain.CartItem;
import com.hoon.domain.Item;
import com.hoon.repository.CartRepository;
import com.hoon.repository.ItemRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
public class InventoryService {

    private ItemRepository itemRepository;
    private ReactiveFluentMongoOperations fluentOperations;
    private CartRepository cartRepository;

    InventoryService(ItemRepository itemRepository, //
                     ReactiveFluentMongoOperations fluentOperations, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.fluentOperations = fluentOperations;
        this.cartRepository = cartRepository;
    }

    public Flux<Item> getItems() {
        // imagine calling a remote service!
        return Flux.empty();
    }

    public Flux<Item> search(String partialName, String partialDescription, boolean useAnd) {
        if (partialName != null) {
            if (partialDescription != null) {
                if (useAnd) {
                    return itemRepository //
                            .findByNameContainingAndDescriptionContainingAllIgnoreCase( //
                                    partialName, partialDescription);
                } else {
                    return itemRepository.findByNameContainingOrDescriptionContainingAllIgnoreCase( //
                            partialName, partialDescription);
                }
            } else {
                return itemRepository.findByNameContaining(partialName);
            }
        } else {
            if (partialDescription != null) {
                return itemRepository.findByDescriptionContainingIgnoreCase(partialDescription);
            } else {
                return itemRepository.findAll();
            }
        }
    }

    public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0); // <1>

        ExampleMatcher matcher = (useAnd // <2>
                ? ExampleMatcher.matchingAll() //
                : ExampleMatcher.matchingAny()) //
                .withStringMatcher(StringMatcher.CONTAINING) // <3>
                .withIgnoreCase() // <4>
                .withIgnorePaths("price"); // <5>

        Example<Item> probe = Example.of(item, matcher); // <6>

        return itemRepository.findAll(probe); // <7>
    }

    /**
     * 평문형 api를 사용한 Item 검색 (2-31)
     * @param name
     * @param description
     * @return
     */
    public Flux<Item> searchByFluentExample(String name, String description) {
        return fluentOperations.query(Item.class) //
                .matching(query(where("TV tray").is(name).and("Smurf").is(description))) //
                .all();
    }

    /**
     * 평문형 API를 사용한 Example 쿼리 검색 구현 코드 (2-32)
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    public Flux<Item> searchByFluentExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd //
                ? ExampleMatcher.matchingAll() //
                : ExampleMatcher.matchingAny()) //
                .withStringMatcher(StringMatcher.CONTAINING) //
                .withIgnoreCase() //
                .withIgnorePaths("price");

        return fluentOperations.query(Item.class) //
                .matching(query(byExample(Example.of(item, matcher)))) //
                .all();
    }

    Mono<Cart> addItemToCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId)) //
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny() //
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        }) //
                        .orElseGet(() -> {
                            return this.itemRepository.findById(itemId) //
                                    .map(item -> new CartItem(item)) //
                                    .map(cartItem -> {
                                        cart.getCartItems().add(cartItem);
                                        return cart;
                                    });
                        }))
                .flatMap(cart -> this.cartRepository.save(cart));
    }

    Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.decrement();
                            return Mono.just(cart);
                        }) //
                        .orElse(Mono.empty()))
                .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getQuantity() > 0)
                        .collect(Collectors.toList())))
                .flatMap(cart -> this.cartRepository.save(cart));
    }

}