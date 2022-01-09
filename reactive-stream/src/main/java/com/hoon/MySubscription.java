package com.hoon;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;

/**
 * 구독 정보(구독자, 어떤 데이터를 구독할지)
 */
public class MySubscription implements Subscription {

    private Subscriber subscriber;
    private Iterator<Integer> it;

    public MySubscription(Subscriber subscriber, Iterable<Integer> its) {
        this.subscriber = subscriber;
        this.it = its.iterator();
    }

    @Override
    public void request(long n) {   // 갯수를 넣으면됨
        while( n > 0 ) {
            if(it.hasNext()) {
                subscriber.onNext(it.next());
            } else {
                subscriber.onComplete();
                break;
            }
            n--;
        }
    }

    @Override
    public void cancel() {

    }
}
