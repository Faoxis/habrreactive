package com.example.habrreactive;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class HabrreactiveApplicationTests {

	private User peter = new User("Peter", "Griffin");
	private User lois = new User("Lois", "Griffin");
	private User brain = new User("Brain", "Griffin");

	@Test
	public void mono() {
		// Создаем объект
		Mono<User> monoPeter = Mono.just(peter);

		// Блокируем текущий поток до тех пор пока не получим объект
		User peter2 = monoPeter.block();

		// Проверяем, что мы получили ожидаемый объект
		assertEquals(peter, peter2);
	}

	@Test
	public void blockMono() {
		Mono<User> monoPeter = Mono.just(peter);

		// Блокируем текущий поток до тех пока мы не получим и не обработаем данные
		String name = monoPeter.map(User::getFirstName).block();
		assertEquals(name, "Peter");
	}

	@Test
	public void flux() {
		// Создаем поток данных для выгрузки наших
		Flux<User> fluxUsers = Flux.just(peter, lois, brain);

		// Получаем данные и обрабатываем по мере поступления
		fluxUsers.subscribe(System.out::println);
	}

	@Test
	public void fluxFilter() {
		Flux<User> userFlux = Flux.just(peter, lois, brain);

		// Фильтруем и оставляем одного Питера
		userFlux
				.filter(user -> user.getFirstName().equals("Peter"))
				.subscribe(user -> assertEquals(user, peter));
	}

	@Test
	public void fluxMap() {
		Flux<User> userFlux = Flux.just(peter, lois, brain);

		// Преобразуем тип User в String
		userFlux
				.map(User::getFirstName)
				.subscribe(System.out::println);
	}

	@Test
	public void fluxDelayElements() {
		Flux<User> userFlux = Flux.just(peter, lois, brain);

		// Ожидаем получение данных 1 секунду и только после этого производим обработку событий
		userFlux.delayElements(Duration.ofSeconds(1))
				.subscribe(System.out::println);
	}

	@Test
	public void fluxDelayElementsCountDownLatch() throws Exception {
		// Создаем счечик и заводим его на единицу
		CountDownLatch countDownLatch = new CountDownLatch(1);

		Flux<User> userFlux = Flux.just(peter, lois, brain);

		// Запускаем userFlux со срабатыванием по прошествию одной секунды
		// и устанавлием сбрасывание счетчика при завершении
		userFlux
				.delayElements(Duration.ofSeconds(1))
				.doOnComplete(countDownLatch::countDown)
				.subscribe(System.out::println); // вывод каждую секунду

		// Ожидаем сброса счетчика
		countDownLatch.await();
	}

}
