package com.hoon.repository;

import static org.assertj.core.api.Assertions.*;

import com.hoon.domain.Item;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest // 스프링부트 기능 중 스프링 데이터 몽고디비 활용에 초점을 둔 몽고 디비 테스트 관련 기능을 활성화한다.
               // @ExtendWithd({SpringExtension.class}) 를 포함하며 JUnit5 기능을 사용할 수 있다.
public class MongoDbSliceTest {

    @Autowired
    ItemRepository repository;

    /**
     * 몽고디비 슬라이스 테스트
     */
    @Test
    void itemRepositorySavesItems() {
        Item sampleItem = new Item( //
                "name", "description", 1.99);

        repository.save(sampleItem) //
                .as(StepVerifier::create) //
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("name");
                    assertThat(item.getDescription()).isEqualTo("description");
                    assertThat(item.getPrice()).isEqualTo(1.99);

                    return true;
                }) //
                .verifyComplete();
    }
}