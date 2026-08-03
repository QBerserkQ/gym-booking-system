package volodea.gymbookingsystem.exception;

public class GymClassNotFoundException extends RuntimeException {
    public GymClassNotFoundException(Long id) {
        super("Gym not found with id " + id);
    }
}
