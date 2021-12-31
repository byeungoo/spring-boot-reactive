package com.hoon;

public class App {

    public static void main(String[] args) {
        MyPub pub = new MyPub();  // 신문사 생성
        MySub sub = new MySub();  // 구독자 생성

        pub.subscribe(sub); // 구독 시작
    }

}
