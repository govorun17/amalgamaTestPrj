package amalgama.alekseymazurov.test;

import java.util.Objects;

/**
 * Класс точка <br>
 *
 * Double value - <br>
 * Boolean isIn - true означает, что значение включено во множество (скобка "[") <br>
 *              - false означает, что значение не включено во множество (скобка "(") <br>
 *
 * @author Aleksey Mazurov
 */
public class Point implements Cloneable{
    private Double value;
    private Boolean isIn;
    private Boolean isLeft;

    public Point(Double value, Boolean isIn, Boolean isLeft) {
        Objects.requireNonNull(value, "Value could not be null");
        Objects.requireNonNull(isIn, "Value could not be null");
        this.value = value;
        this.isIn = isIn;
        this.isLeft = isLeft;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        Objects.requireNonNull(value, "Value could not be null");
        this.value = value;
    }

    public Boolean getIn() {
        return isIn;
    }

    public void setIn(Boolean in) {
        Objects.requireNonNull(isIn, "Value could not be null");
        isIn = in;
    }

    public Boolean getLeft() {
        return isLeft;
    }

    public void setLeft(Boolean left) {
        isLeft = left;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (!value.equals(point.value)) return false;
        return isIn.equals(point.isIn);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + isIn.hashCode();
        return result;
    }

    @Override
    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
