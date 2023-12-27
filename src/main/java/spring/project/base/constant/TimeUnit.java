package spring.project.base.constant;

public enum TimeUnit {
    MONTH("Tháng"), YEAR("Năm");

    TimeUnit(String label) {
        this.label = label;
    }

    private final String label;

    public String getLabel() {
        return label;
    }
}
