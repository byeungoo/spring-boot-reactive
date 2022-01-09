package com.toby;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

    /*
    1. Complete 를 어떻게 할 것인지
    2. Error가 발생할 경우
    90년대에 나온 옵저버 패턴에 이 두가지가 빠짐
    */

    // Iterable <--->  Observable (duality)
    // Pull     <--->  Push
//    public static void main(String[] args) {
//
//        Iterable<Integer> iter = () ->
//            new Iterator<Integer>() {   // iterable 구현한 object
//                int i = 0;
//                final static int MAX = 10;
//                public boolean hasNext() {
//                    return i < MAX;
//                }
//
//                public Integer next() {
//                    return ++i;
//                }
//            };
//
//        //Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);  // LIst는 Iterable 인터페이스의 하위 타입임
//        for (Integer i : iter) {    // iterable을 구현하고 있으면 for-each를 사용하 수 있음
//            System.out.println(i);
//        }
//
//        for(Iterator<Integer> it = iter.iterator(); it.hasNext();) {
//            System.out.println(it.next());  // 내부에서 pulling 방식으로 데이터 가져온다.
//        }
//
//    }

    static class IntObservable extends Observable implements Runnable{ // pub이라고 생각

        @Override
        public void run() {
            for(int i=0;i<=10;i++) {
                setChanged();
                notifyObservers(i);
            }
        }

    }

    public static void main(String[] args) {
        Observer ob = new Observer() {  // Source -> Event/Data -> Observer
            @Override
            public void update(Observable o, Object arg) {  // Observable의 notifyObservers가 호출되면 update가 호출됨.  sub이라고 생각
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);
        //io.run();   // 이벤트가 언제 일어날지 모르는데, 메인 스레드를 막고 있으면 안되니까 별도의 스레드에서 비동기적으로 동작을 하라고 할 수 있음
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() +  " EXIT");
        es.shutdown();
    }

}
