package volodea.gymbookingsystem.service;

import org.springframework.stereotype.Service;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.exception.GymClassNotFoundException;
import volodea.gymbookingsystem.repository.GymRepository;

import java.util.List;
import java.util.Optional;

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

    public GymClass getGymClassById(Long id) {
        return gymRepository.findById(id).orElseThrow(
                () -> new GymClassNotFoundException(id)
        );
    }

    public GymClass findGymClassByIdForUpdate(Long gymClassId) {
        return gymRepository.findByIdForUpdate(gymClassId).orElseThrow(
                () -> new GymClassNotFoundException(gymClassId)
        );
    }
}
