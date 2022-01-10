package com.toby;

import java.util.concurrent.Flow;

public class DelegateSub implements Flow.Subscriber<Integer> {

    Flow.Subscriber sub;

    public DelegateSub(Flow.Subscriber sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        sub.onSubscribe(subscription);
    }

    @Override
    public void onNext(Integer i) {
        sub.onNext(i);
    }

    @Override
    public void onError(Throwable throwable) {
        sub.onError(throwable);
    }

    @Override
    public void onComplete() {
        sub.onComplete();
    }
}
