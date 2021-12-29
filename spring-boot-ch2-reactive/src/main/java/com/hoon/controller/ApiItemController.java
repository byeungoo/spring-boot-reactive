package com.hoon.controller;

import com.hoon.domain.Item;
import com.hoon.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ApiItemController {

    private final ItemRepository itemRepository;

    @GetMapping("/api/items")
    Flux<Item> findAll() {
        return this.itemRepository.findAll();
    }

    @GetMapping("/api/items/{id}")
    Mono<Item> findOne(@PathVariable String id) {
        return this.itemRepository.findById(id);
    }

    @PostMapping("/api/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(s -> this.itemRepository.save(s))
                .map(savedItem -> ResponseEntity.created(URI.create("/api/items/" + savedItem.getId()))
                .body(savedItem));
    }

    @PutMapping("/api/items/{id}")
    Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item, @PathVariable String id) {
        return item.map(content -> new Item(id, content.getName(), content.getPrice()))
                .flatMap(this.itemRepository::save)
                .map(ResponseEntity::ok);
    }

}
