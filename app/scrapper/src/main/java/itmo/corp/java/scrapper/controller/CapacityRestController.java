package itmo.corp.java.scrapper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import itmo.corp.java.scrapper.dto.CapacityResponse;
import itmo.corp.java.scrapper.service.DayService;

@RestController
@RequestMapping("/capacity")
@RequiredArgsConstructor
@Slf4j
public class CapacityRestController {

    private final DayService dayService;

    @GetMapping
    public CapacityResponse getCapacity(){
        log.info("--> GET /capacity");
        return dayService.getCapacity();
    }

    @PutMapping("/add")
    public void addCapacity(@RequestParam long value){
        log.info("--> PUT /capacity/add");
        dayService.addCapacity(value);
    }
}
