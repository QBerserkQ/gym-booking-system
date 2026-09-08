package volodea.gymbookingsystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import volodea.gymbookingsystem.entity.BookingStatus;

@Service
@Slf4j
public class EmailService {

    @Async
    public void sendEmail(String toEmail, String gymClassTitle, BookingStatus status) {

        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }

        log.info("Email sent to {}: your booking for {} is now {}", toEmail,  gymClassTitle, status);
    }
}
