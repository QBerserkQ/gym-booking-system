package volodea.gymbookingsystem.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaleBookingProducer {
    private final RabbitTemplate rabbitTemplate;

    public void scheduleStaleCheck(Long bookingId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.STALE_BOOKING_DELAY_EXCHANGE
                , RabbitMQConfig.STALE_BOOKING_DELAY_QUEUE
                , bookingId
        );
    }
}
