package com.hoon.reactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    @GetMapping("/")
    Mono<String> home() {
        return Mono.just("home");   // 템플릿의 이름을 나타내는 문자열을 리액티브 컨테이너인 Mono에 담아서 반환한다.
                                    // 메소드는 홈 화면 템플릿을 나타내는 home이라는 문자열을 Mono.just()로 감싸서 반환한다.
    }

}
