package com.toby;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub {

    public static void main(String[] args) throws InterruptedException {
        // Publisher <- Observable
        // Subscriber <- Observer

        /**
         * pub -> [Data1] -> mapPub -> [Data2] -> logSub
         *                <- subscribe(logsub)
         *                -> onSubscribe(logSub)
         *                -> onNext
         *                -> onNext
         *                -> onComplete
         */
        Publisher pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList())); // db에서 가져온 컬렉션 데이터라고 생각해도됨
        Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) s -> s * 10);
        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
        map2Pub.subscribe(logSub());
    }

    private static Publisher<Integer> mapPub(Publisher pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) { // logSub
                pub.subscribe(new DelegateSub(sub) {
                    @Override
                    public void onNext(Integer i) {
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe:");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {   // 옵셔널. 무제한까지 가능. 넘어오면 처리. 2개처리하고 나서 다음 2개 더 줘는 여기서 처리한다. 버퍼 사이즈.
                System.out.println("onNext " + i);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + " onComplete");
            }
        };
    }

    private static Publisher iterPub(final List<Integer> iter) {
        return new Publisher() {

            @Override
            public void subscribe(Subscriber subscriber) {

                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {   // request 갯수. 다 보내달라고하면 long의 맥시멈 넘겨주면됨. n은 백프레셔 역할을 해준다.
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {
                        System.out.println("cancel");
                    }
                });   // onSubscribe 필수로 호출 해야함
            }
        };
    }

}
