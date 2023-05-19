package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import subway.line.application.LineRepository;
import subway.line.domain.section.application.SectionRepository;
import subway.line.domain.station.application.StationRepository;
import subway.line.application.LineService;
import subway.line.domain.station.EmptyStation;
import subway.line.Line;
import subway.line.domain.station.Station;
import subway.line.domain.section.domain.Distance;
import subway.line.domain.section.domain.EmptyDistance;
import subway.line.domain.section.Section;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LineServiceTest {
    @Autowired
    private LineService lineService;
    @MockBean
    private StationRepository stationRepository;
    @MockBean
    private LineRepository lineRepository;
    @MockBean
    private SectionRepository sectionRepository;

    @BeforeEach
    void setup() {
        final var line7 = new Line(1L, "7호선", "green");
        final var lineBD = new Line(2L, "분당선", "yellow");

        Mockito.when(lineRepository.findAll())
                .thenReturn(List.of(
                        line7,
                        lineBD
                ));

        Mockito.when(sectionRepository.findAllByLine(line7))
                .thenReturn(List.of(
                        new Section(1L, line7, new Station(1L, "반포역"), new Station(2L, "논현역"), Distance.of(3)),
                        new Section(2L, line7, new Station(2L, "논현역"), new Station(3L, "학동역"), Distance.of(4)),
                        new Section(3L, line7, new Station(3L, "학동역"), new Station(4L, "강남구청"), Distance.of(3)),
                        new Section(4L, line7, new Station(4L, "강남구청"), new Station(5L, "청담역"), Distance.of(4)),
                        new Section(5L, line7, new Station(5L, "청담역"), new EmptyStation(), new EmptyDistance())
                ));

        Mockito.when(sectionRepository.findAllByLine(lineBD))
                .thenReturn(List.of(
                        new Section(6L, lineBD, new Station(4L, "강남구청"), new Station(6L, "압구정로데오"), Distance.of(4)),
                        new Section(7L, lineBD, new Station(6L, "압구정로데오"), new Station(7L, "서울숲"), Distance.of(5)),
                        new Section(8L, lineBD, new Station(7L, "서울숲"), new EmptyStation(), new EmptyDistance())
                ));

        Mockito.when(stationRepository.findByName("반포역")).thenReturn(new Station(1L, "반포역"));
        Mockito.when(stationRepository.findByName("청담역")).thenReturn(new Station(5L, "청담역"));
        Mockito.when(stationRepository.findByName("논현역")).thenReturn(new Station(2L, "논현역"));
        Mockito.when(stationRepository.findByName("서울숲")).thenReturn(new Station(7L, "서울숲"));
    }

    @Test
    @DisplayName("같은 노선 위에 있는 두 역의 최단거리를 구할 수 있다.")
    void shortestDistance() {
        assertThat(lineService.findShortestPath("반포역", "청담역").getShortestDistance())
                .isEqualTo(14);
    }

    @Test
    @DisplayName("서로 다른 노선 위에 있는 두 역의 최단거리를 구할 수 있다.")
    void shortestDistanceOnDifferentLine() {
        assertThat(lineService.findShortestPath("논현역", "서울숲").getShortestDistance())
                .isEqualTo(16);
    }

    @Test
    @DisplayName("같은 노선 위에 있는 두 역의 최단 경로를 구할 수 있다.")
    void shortestPath() {
        assertThat(lineService.findShortestPath("반포역", "청담역").getShortestPath())
                .extracting(Station::getName)
                .containsExactly("반포역", "논현역", "학동역", "강남구청", "청담역");
    }

    @Test
    @DisplayName("다른 노선 위에 있는 두 역의 최단 경로를 구할 수 있다.")
    void shortestPathOnDifferent() {
        assertThat(lineService.findShortestPath("논현역", "서울숲").getShortestPath())
                .extracting(Station::getName)
                .containsExactly("논현역", "학동역", "강남구청", "압구정로데오", "서울숲");
    }

    @Test
    @DisplayName("10km 이내의 거리에 대한 요금은 기본 운임으로 계산한다.")
    void defaultFare() {
        assertThat(lineService.calculateFare(9))
                .isEqualTo(new BigDecimal("1250"));
    }

    @Test
    @DisplayName("10km 이상의 거리에 대한 요금은 기본 운임 + 5km마다 100월씩 추가해 계산한다.")
    void middleFare1() {
        assertThat(lineService.calculateFare(11))
                .as("5km마다 추가요금이 부과되므로 아직 추가요금은 없다.")
                .isEqualTo(new BigDecimal("1250"));
    }

    @Test
    @DisplayName("50km 이하의 거리에 대한 요금은 기본 운임 + 5km마다 100월씩 추가해 계산한다.")
    void middleFare2() {
        assertThat(lineService.calculateFare(49))
                .as("10km + 5km*7 + 4km = 49km이므로 추가요금 700원이 부과된다.")
                .isEqualTo(new BigDecimal("1950"));
    }

    @Test
    @DisplayName("50km를 초과하는 거리에 대한 요금은 기본 운임 + 8km마다 100원씩 추가하여 부과한다.")
    void maxFare() {
        assertThat(lineService.calculateFare(51))
                .as("10km + 8km*5 + 1km = 51km이므로 추가요금 500원이 부과된다.")
                .isEqualTo(new BigDecimal("1750"));
    }
}