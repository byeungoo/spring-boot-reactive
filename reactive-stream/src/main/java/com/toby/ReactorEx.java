package com.toby;

import reactor.core.publisher.Flux;

public class ReactorEx {

    public static void main(String[] args) {
        // Flux는 Publisher라고 생각하면 됨
        Flux.<Integer>create(e -> {
                e.next(1);
                e.next(2);
                e.next(3);
                e.complete();
        })
        .log()
        .map(s -> s * 10)
        .log()
        .subscribe(System.out::println);
    }

}
