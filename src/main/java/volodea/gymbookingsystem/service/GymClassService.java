package volodea.gymbookingsystem.service;

import org.springframework.stereotype.Service;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.repository.GymRepository;

import java.util.List;

@Service
public class GymClassService {
    private final GymRepository gymRepository;

    public GymClassService(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public List<GymClassResponse> getAllGymClasses() {
        return gymRepository.findAll()
                .stream()
                .map(gymClass -> new GymClassResponse(
                        gymClass.getId()
                        , gymClass.getTitle()
                        , gymClass.getStartTime()
                        , gymClass.getStartTime().plusHours(2)
                        , gymClass.getCapacity()
                ))
                .toList();
    }
}
