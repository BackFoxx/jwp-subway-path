package subway.line;

import subway.line.domain.station.Station;

import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Station head;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.head = null;
    }

    public Line(Long id, String name, String color, Station head) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.head = head;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getHead() {
        return head;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(head, line.head);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, head);
    }
}