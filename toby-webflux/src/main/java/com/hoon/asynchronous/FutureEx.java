package com.hoon.asynchronous;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FutureEx {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();   // 맥시멈 제한이 없고, 다 사용한 스레드는 여기에 들어가있음. 처음에는 0개임.
        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });

        log.info("Exit");
        System.out.println(f.get());    // exit이 만약에 2~3초 걸리는 작업이면 f에서 데이터를 미리 가져와서 바로 값을 출력해줄 수 있다.
        System.out.println(f.isDone()); // LOOP를 돌면서 작업이 끝나면 처리해라 라고하는식으로 작업이 가능하다.
    }

}
