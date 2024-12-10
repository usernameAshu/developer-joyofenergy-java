package uk.tw.energy.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.tw.energy.Utility.EnergyCalculationUtil;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsageService {

    private final MeterReadingService meterReadingService;

    public final AccountService accountService;

    public final PricePlanService pricePlanService;

    public UsageService(MeterReadingService meterReadingService, AccountService accountService, PricePlanService pricePlanService) {
        this.meterReadingService = meterReadingService;
        this.accountService = accountService;
        this.pricePlanService = pricePlanService;
    }


    public ResponseEntity weekly(String smartMeterId) {

        //check whether price plan attached (Error scenario)
        String pricePlanIdForSmartMeterId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        if (!Objects.nonNull(pricePlanIdForSmartMeterId)) {
            return new ResponseEntity("No price plan attached", HttpStatus.NOT_FOUND);
        }

        //Actual working case
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        //calculate the total cost
//        TemporalAccessor temp = LocalDate.now().minusWeeks(1L);
        Instant sevenDaysBefore = Instant.now().minus(7, ChronoUnit.DAYS);
        List<ElectricityReading> weeklyReadings = readings.get()
                .stream()
                .filter(electricityReading -> electricityReading.time().isAfter(sevenDaysBefore))
                .toList();

        BigDecimal avgRead = EnergyCalculationUtil.calculateAverageReading(weeklyReadings);
        BigDecimal hours = EnergyCalculationUtil.calculateTimeElapsed(weeklyReadings);
        BigDecimal unitsConsumed = avgRead.multiply(hours);

        BigDecimal totalCost = unitsConsumed.multiply(
                pricePlanService.getPricePlanFromId(pricePlanIdForSmartMeterId)
                        .getUnitRate());

        return new ResponseEntity("Total Usage=" + totalCost.doubleValue(), HttpStatus.OK);
    }
}
