package com.hoon.asynchronous;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FutureEx2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService es = Executors.newCachedThreadPool();   // 맥시멈 제한이 없고, 다 사용한 스레드는 여기에 들어가있음. 처음에는 0개임.

        FutureTask<String> f = new FutureTask<String>(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        }) {
            @Override
            public void done() {
                try {
                    System.out.println(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        es.execute(f);
        es.shutdown();
    }

}
