package blog.yrol.unit;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


/**
 * Playground for the Sinks API
 * Documentation: https://projectreactor.io/docs/core/release/reference/#sinks
 * **/
public class SinksTest {

    @Test
    void sink_replay() {


        /**
         * Many
         * In this case all subscribers will receive the values emitted regardless at point they've subscribed to the producer.
         * **/
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        // Emitting the events
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Subscriber 1: Subscribing and printing the events
        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        // Subscriber 2: Subscribing and printing the events
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });

        // Emit another integer after subscribing to 1 & 2
        replaySink.tryEmitNext(3);


        /**
         * Adding another subscriber
         * Subscriber 3: Subscribing and printing the events
         * Since we're using Replay, this will get all the values (1,2 & 3)
         * **/
        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 3 : " + i);
        });
    }

    /**
     * Multicast
     * The original events will only be received by the first subscriber only, ex: Subscriber 1 will receive both 1 & 2 values but not Subscriber 2
     * The late events emitted will be received by all subscribers, ex: both  Subscriber 1 &  Subscriber 2 will receive value 3.
     * **/
    @Test
    void sinks_multicast() {

        // Honoring the subscriber's back pressure
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();


        // Emitting the events
        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Subscriber 1: Subscribing and printing the events
        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        // Subscriber 2: Subscribing and printing the events
        Flux<Integer> integerFlux1 = multicast.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });

        // Emit another integer after subscribing to 1 & 2
        multicast.tryEmitNext(3);

    }


    /**
     * Unicast
     * Test will be successful for the"Subscriber 1 :" but will throw an exception for the "Subscriber 2" due since only one subscriber is allowed in unicast.
     * "Subscriber 2 : " will not receive anything and will cause an exception. The unicast will only allow one subscriber
     * **/
    @Test
    void sinks_unicast() {

        // Honoring the subscriber's back pressure
        Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();


        // Emitting the events
        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Subscriber 1: Subscribing and printing the events
        Flux<Integer> integerFlux = unicast.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        // Subscriber 2: Subscribing and printing the events
        Flux<Integer> integerFlux1 = unicast.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });

        // Emit another integer after subscribing to 1 & 2
        unicast.tryEmitNext(3);

    }
}
