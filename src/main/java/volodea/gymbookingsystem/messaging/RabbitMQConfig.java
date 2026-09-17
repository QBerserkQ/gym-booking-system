package volodea.gymbookingsystem.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String STALE_BOOKING_QUEUE = "stale-booking-queue";
    public static final String STALE_BOOKING_DELAY_QUEUE = "stale-booking-delay-queue";
    public static final String STALE_BOOKING_EXCHANGE = "stale-booking-exchange";
    public static final String STALE_BOOKING_DELAY_EXCHANGE = "stale-booking-delay-exchange";

    @Value("${rabbitmq.ttl_time}")
    private long delay;

    @Bean
    public Queue staleBookingQueue() {
        return QueueBuilder.durable(STALE_BOOKING_QUEUE).build();
    }

    @Bean
    public DirectExchange staleBookingExchange() {
        return new DirectExchange(STALE_BOOKING_EXCHANGE);
    }

    @Bean
    public Binding staleBookingBinding() {
        return BindingBuilder.bind(staleBookingQueue())
                .to(staleBookingExchange())
                .with(STALE_BOOKING_QUEUE);
    }

    @Bean
    public Queue staleBookingDelayQueue() {
        return QueueBuilder.durable(STALE_BOOKING_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", STALE_BOOKING_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", STALE_BOOKING_QUEUE)
                .withArgument("x-message-ttl", delay)
                .build();
    }

    @Bean
    public DirectExchange staleBookingDelayExchange() {
        return new DirectExchange(STALE_BOOKING_DELAY_EXCHANGE);
    }

    @Bean
    public Binding staleBookingDelayBinding() {
        return BindingBuilder.bind(staleBookingDelayQueue())
                .to(staleBookingDelayExchange())
                .with(STALE_BOOKING_DELAY_QUEUE);
    }
}
