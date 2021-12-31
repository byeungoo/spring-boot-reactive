package com.hoon.web;

import com.hoon.domain.Customer;
import com.hoon.domain.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink; // A요청 -> Flux -> Stream, B요청 -> Flux -> Stream 이런 요청올 때
                                             // Flux.merge -> 2개의 스트림이 sink가 맞춰짐
                                             // sink는 이제 모든 클라이언트들이 접근할 수 있음.

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();   // 새로 push 된 데이터만 구독자한테 전송해주는 방식
    }

    /*
            로그 확인해보기
            1. 데이터베이스 데이터에 Subscribe. Subscribe로 응답이 됨
            2. request(unbounded)  -> unbounded는 니가 가지고 있는 데이터 다 달라고하는 것임
            3. onNext 5번 실행되면서 데이터 다 가져옴
            4. 다 받았으면 onComplete() 실행
            5. Complete 되는 순간 응답이 됨
        */
    @GetMapping("/customer")
    public Flux<Customer> findAll() {
        return customerRepository.findAll().log();
    }

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log(); // 5초 지나면 전체 응답이됨
    }

    /**
     *  요거는 onNext되면서 데이터 응답이 옴. 순차적으로 오는것을 볼 수 있음.
     * @return
     */
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> fluxstream() {
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log(); // 5초 지나면 전체 응답이됨
    }

    /**
     * 데이터 한건일 경우
     */
    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return customerRepository.findById(id).log();
    }

    /**
     * 자바스크립트에서 이벤트로 받을 수 있음
     * SSE 프로토콜 적용
     * 응답 형태에 data가 붙음  (ex: data:{"id":1,"firstName":"Jack","lastName":"Bauer"} )
     * 데이터가 다 던지고나서 멈춰짐
     * @return
     */
    @GetMapping(value = "/customer/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> findAllSSE() {
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    /**
     * sink의 데이터가 먼가가 합쳐지면 합쳐진 데이터를 응답해줌
     * @return
     */
    @GetMapping(value = "/customer/sse/sink")
    public Flux<ServerSentEvent<Customer>> findAllSSESink() {
        return sink.asFlux().map(c -> ServerSentEvent.builder(c).build())
                .doOnCancel(() -> {
                    sink.asFlux().blockLast();  // 취소됐을 때 강제로 마지막 데이터라고 알려줌.
                });
    }

    @PostMapping("/customer")
    public Mono<Customer> save() {
        return customerRepository.save(new Customer("goo", "hoon")).doOnNext(c -> {
            sink.tryEmitNext(c);    // 추가된 데이터를 sink에 추가
        });
    }

}
