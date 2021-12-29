package com.hoon;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BlockHoundUnitTest {

    /**
     * 단위 테스트 블로킹 코드 검출
     */
    @Test
    void threadSleepIsABlockingCall() {
        Mono.delay(Duration.ofSeconds(1)) // 전체 플로우를 리액터 스레드에서 실행되게 만든다. 블록하운드는 리액터 스레드 안에서 사용되는 블로킹 코드를 검출할 수 있다.
                .flatMap(tick -> {
                    try {
                        Thread.sleep(10); // 현재 스레드를 멈추게하는 블로킹 호출
                        return Mono.just(true);
                    } catch (InterruptedException e) {
                        return Mono.error(e);
                    }
                }) //
                .as(StepVerifier::create) //
                .verifyComplete();

    }

}