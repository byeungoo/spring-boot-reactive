package com.hoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringBootCh2ReactiveApplication {

    public static void main(String[] args) {

        // 블로킹 메소드 검출 및 해당 스레드가 블로킹 메소드 호출을 허용하는지 검사 가능
        BlockHound.builder()
                .allowBlockingCallsInside(  // 너무 저수준의 메소드를 허용하지 말고, 좀 더 구체적인 일부 지점만 허용하는 것이 안전하다.
                        TemplateEngine.class.getCanonicalName(), "process"
                )
                .install();

        SpringApplication.run(SpringBootCh2ReactiveApplication.class, args);
    }

}
