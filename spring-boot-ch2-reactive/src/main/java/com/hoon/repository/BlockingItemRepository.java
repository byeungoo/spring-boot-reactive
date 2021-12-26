package com.hoon.repository;

import com.hoon.domain.Item;
import org.springframework.data.repository.CrudRepository;

/**
 * 애플리케이션 시작 시점에 어떤 작업을 하려면, 블로킹 버전의 스프링 데이터 몽고디비를 사용하는 편이 좋다.
 * 테스트 데이터를 로딩하는 테스트 환경 구성 이런데서 약간의 블로킹 코드를 사용해도 문제가 되지 않는다.
 * 물론 블로킹 코드는 실제 운영환경에서는 절대로 사용하면 안된다.
 */
public interface BlockingItemRepository extends CrudRepository<Item, String> {

}
