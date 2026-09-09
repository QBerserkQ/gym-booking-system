package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import volodea.gymbookingsystem.dto.GymClassRequest;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.service.GymClassService;

import java.util.List;

@RestController
@RequestMapping("/api/gym-classes")
public class GymClassController {

    private final GymClassService gymClassService;

    public GymClassController(GymClassService gymClassService) {
        this.gymClassService = gymClassService;
    }

    @GetMapping
    public List<GymClassResponse> getAllGymClasses() {
        return gymClassService.getAllGymClasses();
    }

    @PostMapping("/create")
    public ResponseEntity<GymClassResponse> createGymClass(@Valid @RequestBody GymClassRequest gymClassRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gymClassService.createNewGymClass(gymClassRequest));
    }
}
