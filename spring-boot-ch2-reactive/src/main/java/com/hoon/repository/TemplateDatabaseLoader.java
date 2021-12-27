package com.hoon.repository;

import com.hoon.domain.Item;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * 블로킹 리포지토리의 대안으로 블로킹 리포지토리 사용 가능성을 낮추려면 아예 만들지 않아야한다.
 * BlockingItemRepository와 이를 사용하는 RepositoryDatabaseLoader를 제거하자
 * 그리고 MongoTemplate을 사용하자. 자동 설정 기능 덕분에 MongoTemplate(블로킹버전)과 ReactiveMongoTemplate을 모두 사용할 수 있다.
 */
@Component
public class TemplateDatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations repository) { // MongoOperations 사용하여 계약과 세부 구현 분리
        return args -> {
            repository.save(new Item("Alf alarm clock", "kids clock", 19.99));
            repository.save(new Item("Smurf TV tray", "kids TV tray", 24.99));
        };
    }

}
