package volodea.gymbookingsystem;

import org.springframework.boot.SpringApplication;

public class TestGymBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(GymBookingSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
