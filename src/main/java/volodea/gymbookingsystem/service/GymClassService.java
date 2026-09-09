package volodea.gymbookingsystem.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import volodea.gymbookingsystem.dto.GymClassRequest;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.exception.GymClassNotFoundException;
import volodea.gymbookingsystem.repository.GymRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GymClassService {
    private final GymRepository gymRepository;

    public GymClassService(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Cacheable("gymClasses")
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

    @CacheEvict(value = "gymClasses", allEntries = true)
    public GymClassResponse createNewGymClass(GymClassRequest gymClassRequest) {
        GymClass gymClass = GymClass.builder()
                .title(gymClassRequest.title())
                .startTime(gymClassRequest.startTime())
                .capacity(gymClassRequest.capacity())
                .build();

        GymClass saved = gymRepository.save(gymClass);

        LocalDateTime startTime = saved.getStartTime();
        LocalDateTime endTime = startTime.plusHours(2);

        return new GymClassResponse(saved.getId(), saved.getTitle(), startTime, endTime, saved.getCapacity());
    }
}
