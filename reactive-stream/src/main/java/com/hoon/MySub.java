package com.hoon;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MySub implements Subscriber<Integer> {

    private Subscription subscription;
    private int bufferSize = 3;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("구독자 : 구독 정보 잘 받았어");
        this.subscription = subscription;
        subscription.request(bufferSize);    // 신문 한개씩 매일 매일 줘!! (백프레셔) 소비자가 한번에 처리할 수 있는 개수를 요청
    }

    @Override
    public void onNext(Integer integer) {
        System.out.println("onNext() : : " + integer);
        bufferSize--;
        if(bufferSize == 0) {
            System.out.println("하루 지남");
            bufferSize = 3;
            subscription.request(bufferSize);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("구독중 에러");
    }

    @Override
    public void onComplete() {
        System.out.println("구독 완료");
    }
}
