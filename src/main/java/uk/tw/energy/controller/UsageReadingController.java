package uk.tw.energy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.UsageService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usage")
public class UsageReadingController {

    private final UsageService usageService;

    public UsageReadingController(UsageService usageService) {
        this.usageService = usageService;
    }


    //dependecies

    @GetMapping("/weekly/{smart-meter-id}")
    public ResponseEntity weekly(@PathVariable(value = "smart-meter-id") String smartMeterId) {

        //add validation of inputs

        return usageService.weekly(smartMeterId);

    }
}
