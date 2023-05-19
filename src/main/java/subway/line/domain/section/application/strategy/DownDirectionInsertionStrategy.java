package subway.line.domain.section.application.strategy;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import subway.common.exception.ExceptionMessages;
import subway.line.domain.section.Section;
import subway.line.domain.section.application.SectionRepository;

@Component
@Order(3)
public class DownDirectionInsertionStrategy implements SectionInsertionStrategy {
    private final SectionRepository sectionRepository;

    public DownDirectionInsertionStrategy(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public boolean support(Section section) {
        final var previousSection = sectionRepository.findByPreviousStation(section.getPreviousStation(), section.getLine());
        return previousSection.isPresent() && !previousSection.get().isNextStationEmpty();
    }

    @Override
    public long insert(Section section) {
        final var sectionToUpdate = sectionRepository.findByPreviousStation(section.getPreviousStation(), section.getLine())
                .orElseThrow(() -> new IllegalStateException(ExceptionMessages.STRATEGY_MAPPING_FAILED));

        final var sectionId = sectionRepository.insert(section.change()
                .previousStation(section.getNextStation())
                .nextStation(sectionToUpdate.getNextStation())
                .distance(sectionToUpdate.getDistance().subtract(section.getDistance()))
                .done()).getId();

        sectionRepository.update(sectionToUpdate.change()
                .nextStation(section.getNextStation())
                .distance(section.getDistance())
                .done());

        return sectionId;
    }
}
