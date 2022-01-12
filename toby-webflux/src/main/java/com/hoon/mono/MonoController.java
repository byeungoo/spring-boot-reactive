package com.hoon.mono;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class MonoController {

    /**
     * 미리 만들어둔 코드는 동기적으로 동작하고
     * 스프링에서 subscribe하면 그 때 비동기적으로 동작을 한다고 생각하면됨.
     * Publisher -> (Publisher) -> (Publisher) -> Subscriber
     *
     * Mono.just(myService.findById(1)) 요런식으로 동작한다고할 때 myService는 동기적으로 실햄됨. service가 먼저 실행되고 just로 들어간다.
     * 콜백스타일로 만들면 myService도 subscribe 되는 시점에 실행되게 할 수 있다.
     * @return
     */
    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono m = Mono.just("Hello WebFlux")
                .doOnNext(c -> log.info(c))
                .log();    // 어떤 object를 mono안에 넣는 가장 쉬운 방법은 just!
        log.info("pos2");
        return m;
    }

    /**
     * 로그찍는거 보면 pos1 다음에 generateHello()에 있는 log찍는걸 볼 수 있음
     * @return
     */
    @GetMapping("/hello2")
    Mono<String> hello2() {
        log.info("pos1");
        Mono m = Mono.just(generateHello())
                .doOnNext(c -> log.info(c))
                .log();    // 어떤 object를 mono안에 넣는 가장 쉬운 방법은 just!
        log.info("pos2");
        return m;
    }

    /**
     * pos1 -> pos2 이렇게가고 'method generateHello()' 는 subscribe하고나서 찍힌다.
     * mono가 subscribe 되고나서 실제 실행까지 지연하고 싶으면 이런식으로 람다식을 던지는  형태로 가능
     * @return
     */
    @GetMapping("/hello3")
    Mono<String> hello3() {
        log.info("pos1");
        Mono m = Mono.fromSupplier(() -> generateHello())   // supplier는 파라미터는 없는함수
                .doOnNext(c -> log.info(c))
                .log();    // 어떤 object를 mono안에 넣는 가장 쉬운 방법은 just!
        log.info("pos2");
        return m;
    }

    @GetMapping("/hello4")
    Mono<String> hello4() {
        log.info("pos1");
        String msg1 = generateHello();
        Mono<String> m = Mono.just(msg1)   // supplier는 파라미터는 없는함수
                .doOnNext(c -> log.info(c))
                .log();    // 어떤 object를 mono안에 넣는 가장 쉬운 방법은 just!
        String msg2 = m.block();    // 내부에서 subscribe를 한번해서 msg를 꺼내온다. block이 되버린다. 가능한 block을 사용하지 않는다.
        log.info("pos2 : {}", msg2);
        return Mono.just(msg2);
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

}
