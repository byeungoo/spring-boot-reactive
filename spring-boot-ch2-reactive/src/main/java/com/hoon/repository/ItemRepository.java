package com.hoon.repository;

import com.hoon.domain.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * 모든 메소드 반환 타입이 Mono나 Flux 둘 중 하나이다.
 * Mono나 Flux를 구독하고 있다가 몽고디비가 데이터를 제공할 준비가 됐을 때 데이터를 받을 수 있게 된다.
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, String> {



}
