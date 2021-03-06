package amalgama.alekseymazurov.test;

import java.util.Objects;

public class Segment {
    private Point leftPoint;
    private Point rightPoint;

    public Segment() {
    }

    public Segment(
            Point leftPoint,
            Point rightPoint
    ) {
        Objects.requireNonNull(leftPoint, "Value could not be null");
        Objects.requireNonNull(rightPoint, "Value could not be null");
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
    }

    public Segment(
            Point leftPoint
    ) {

    }

    public Point getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(Point leftPoint) {
        this.leftPoint = leftPoint;
    }

    public Point getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(Point rightPoint) {
        this.rightPoint = rightPoint;
    }

    @Override
    public String toString() {
        if (leftPoint.equals(rightPoint)) {
            return "[" + leftPoint.getValue() + "]";
        } else {
            return (leftPoint.getIn() ? "[" : "(") + leftPoint.getValue() + "," + rightPoint.getValue() + (rightPoint.getIn() ? "]" : ")");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        if (!leftPoint.equals(segment.leftPoint)) return false;
        return rightPoint.equals(segment.rightPoint);
    }

    @Override
    public int hashCode() {
        int result = leftPoint.hashCode();
        result = 31 * result + rightPoint.hashCode();
        return result;
    }

    public void selfCheck() {
        if (rightPoint == null && leftPoint != null && leftPoint.getIn()) {
            Point rp = leftPoint.clone();
            rp.setLeft(false);
            rightPoint = rp;
        }
        else if (leftPoint == null) throw new IllegalArgumentException("Check entered values");
    }

    /**
     * входит ли точка в множество
     * @param pointIn точка поиска
     * @return 0, если входит
     * <br> -1, если левее мнжества
     * <br> 1, если правее множества
     */
    public Integer comparePoint(Double pointIn) {
        if (pointIn < leftPoint.getValue() || pointIn.equals(leftPoint.getValue()) && !leftPoint.getIn()) {
            return -1;
        }
        else if (pointIn > rightPoint.getValue() || pointIn.equals(rightPoint.getValue()) && !rightPoint.getIn()) {
            return 1;
        } else {
            return 0;
        }
    }
}
