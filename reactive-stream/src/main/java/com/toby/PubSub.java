package com.toby;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.TimeUnit;

public class PubSub {

    public static void main(String[] args) throws InterruptedException {
        // Publisher <- Observable
        // Subscriber <- Observer

        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);   // db에서 가져온 컬렉션 데이터라고 생각해도됨
        ExecutorService es = Executors.newSingleThreadExecutor();


        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {

                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {   // request 갯수. 다 보내달라고하면 long의 맥시멈 넘겨주면됨. n은 백프레셔 역할을 해준다.
                        es.execute(() -> {
                            int i = 0;
                            try{
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {
                        System.out.println("cancel");
                    }
                });   // onSubscribe 필수로 호출 해야함
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {   // 필수
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {   // 옵셔널. 무제한까지 가능. 넘어오면 처리. 2개처리하고 나서 다음 2개 더 줘는 여기서 처리한다. 버퍼 사이즈.
                System.out.println(Thread.currentThread().getName() + ", onNext " + item);
                this.subscription.request(1);
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

        p.subscribe(subscriber);
        es.awaitTermination(10, TimeUnit.HOURS);
        es.shutdown();
    }

}
