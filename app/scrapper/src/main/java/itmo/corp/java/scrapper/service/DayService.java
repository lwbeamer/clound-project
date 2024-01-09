package itmo.corp.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import itmo.corp.java.scrapper.dto.CapacityResponse;
import itmo.corp.java.scrapper.model.jpa.DayEntity;
import itmo.corp.java.scrapper.repository.DayRepository;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayRepository dayRepository;

    public CapacityResponse getCapacity() {
        Optional<DayEntity> optionalDay = dayRepository.findByDate(LocalDate.now());
        return optionalDay.map(dayEntity -> new CapacityResponse(dayEntity.getCapacity()))
                .orElseGet(() -> new CapacityResponse(0L));
    }

    public void addCapacity(Long extraCapacity) {
        Optional<DayEntity> optionalDay = dayRepository.findByDate(LocalDate.now());
        if (optionalDay.isPresent()) {
            long capacity = optionalDay.get().getCapacity() + extraCapacity;
            DayEntity day = optionalDay.get();
            day.setCapacity(capacity);
            dayRepository.save(day);
        } else {
            dayRepository.save(DayEntity.builder()
                                   .date(LocalDate.now())
                                   .capacity(extraCapacity)
                                   .build());
        }
    }
}
