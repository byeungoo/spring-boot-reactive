package com.hoon.scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEx2 {

    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(200))   // user스레드가 아니라 daemon 쓰레드로 만듬.
                .take(10)   // 10개만 받고 끝낸다.
                .subscribe(s -> log.debug("onNext: {}", s));
        log.debug("exit");
        TimeUnit.SECONDS.sleep(10); // 10초 동안 sleep 하니까 그 동안만 발생된다.
    }

}
