package volodea.gymbookingsystem.exception;

public class GymClassNotFoundException extends NotFoundException {
    public GymClassNotFoundException(Long id) {
        super("Gym not found with id " + id);
    }
}
