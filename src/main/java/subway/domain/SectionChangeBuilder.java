package subway.domain;

public class SectionChangeBuilder {
    private Long id;
    private Line line;
    private Station previousStation;
    private Station nextStation;
    private Distance distance;

    public SectionChangeBuilder(Long id, Line line, Station previousStation, Station nextStation, Distance distance) {
        this.id = id;
        this.line = line;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public SectionChangeBuilder id(Long id) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, distance);
    }

    public SectionChangeBuilder line(Line line) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, distance);
    }

    public SectionChangeBuilder previousStation(Station previousStation) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, distance);
    }

    public SectionChangeBuilder nextStation(Station nextStation) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, distance);
    }

    public SectionChangeBuilder subtractDistance(Distance distance) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, this.distance.subtract(distance));
    }

    public SectionChangeBuilder addDistance(Distance distance) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, this.distance.add(distance));
    }

    public SectionChangeBuilder distance(Distance distance) {
        return new SectionChangeBuilder(id, line, previousStation, nextStation, distance);
    }

    public Section done() {
        return new Section(id, line, previousStation, nextStation, distance);
    }
}