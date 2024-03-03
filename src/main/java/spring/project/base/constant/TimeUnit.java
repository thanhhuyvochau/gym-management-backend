package spring.project.base.constant;

public enum TimeUnit {
    MONTH("Tháng", 30L), YEAR("Năm", 365L);

    TimeUnit(String label, Long dayNumber) {
        this.label = label;
        this.dayNumber = dayNumber;
    }

    private final String label;
    private final Long dayNumber;

    public String getLabel() {
        return label;
    }

    public Long getDayNumber() {
        return dayNumber;
    }
}
