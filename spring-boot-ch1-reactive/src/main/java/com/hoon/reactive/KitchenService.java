package com.hoon.reactive;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class KitchenService {

    private List<Dish> menu = Arrays.asList(
            new Dish("Sesame chicken"),
            new Dish("Lo mein noodles, plain"),
            new Dish("Sweet & sour beef"));

    private Random picker = new Random();

    /**
     * 요리 스트림 생성
     * @return
     */
    Flux<Dish> getDishes() {
      return Flux.<Dish> generate(sink -> sink.next(randomDish()))  // sink는 무작위로 제공되는 요리를 둘러싸는 Flux의 핸들로서,
                                                                    // Flux에 포함될 원소를 동적으로 발행할 수 있게 해준다
              .delayElements(Duration.ofMillis(250));               // 세 가지 요리 중에서 무작위로 선택된 1개의 요리를 250밀리초(ms) 간격으로 계속 제공한다.
    }

    /**
     * 요리 무작위 선택
     * @return
     */
    private Dish randomDish() {
        return menu.get(picker.nextInt(menu.size()));
    }

}
