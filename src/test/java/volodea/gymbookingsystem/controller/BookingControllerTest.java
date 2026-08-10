package volodea.gymbookingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import volodea.gymbookingsystem.service.BookingService;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookingService bookingService;
}
