package volodea.gymbookingsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
