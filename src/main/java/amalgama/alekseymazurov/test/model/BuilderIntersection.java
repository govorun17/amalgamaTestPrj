package amalgama.alekseymazurov.test.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class BuilderIntersection implements Intersection{
    private BunchLinkedList<Segment> iterator;
    private BunchLinkedList<Segment> firstElement;
    private BunchLinkedList<Segment> endElement;
    private List<Segment> intersection;

    public BuilderIntersection() {
    }

    /**
     * добавить множество в кучу
     * @param bunches набор строк типа (-inf,5]u[10]u(11.5,20.765)
     * @return builder
     */
    @Override
    public final BuilderIntersection add(String... bunches) {
        for (String bunch : bunches) {
            List<Segment> segments = parseFromString(bunch);

            return this.add(segments);
        }
        return this;
    }

    /**
     * добавить множество в кучу
     * @param segmentsList набор массивов сегментов, где один массив есть объединение множеств
     * @return builder
     */
    @Override
    @SafeVarargs
    public final BuilderIntersection add(List<Segment>... segmentsList) {
        for (List<Segment> segments : segmentsList) {

            if (segments.size() != 0) {

                if (this.iterator == null) {
                    this.iterator = new BunchLinkedList<>();
                    this.firstElement = this.iterator;
                    this.endElement = this.iterator;
                } else {
                    this.endElement.setNextSeg(new BunchLinkedList<>());
                    this.endElement = this.endElement.getNextSeg();
                    this.iterator = this.endElement;
                }

                for (Segment segment : segments) {
                    if (this.iterator.getValue() != null) {
                        this.iterator.setUnionSeg(new BunchLinkedList<>());
                        this.iterator = this.iterator.getUnionSeg();
                    }
                    this.iterator.setValue(segment);
                }

                this.iterator = this.firstElement;
            }
        }
        return this;
    }

    private List<Segment> parseFromString(String bunch) {
        Function<String, Double> convertStrToDouble = (strPoint) -> {
            Double point;
            try {
                point = Double.valueOf(strPoint);
            }
            catch (NumberFormatException e) {
                if (strPoint.contains("inf")) {
                    if (strPoint.contains("-")) {
                        point = Double.NEGATIVE_INFINITY;
                    }
                    else {
                        point = Double.POSITIVE_INFINITY;
                    }
                }
                else {
                    throw new IllegalArgumentException();
                }
            }
            return point;
        };
        bunch = bunch.replaceAll("\\s+","");
        List<Segment> segs = new LinkedList<>();
        String[] segments = bunch.toLowerCase().split("u");
        for (String segment : segments) {
            Segment seg = new Segment();
            String[] points = segment.split(",");
            for (String point : points) {
                // [a или [a]
                if (point.contains("[")) {
                    point = point.replace("[", "");
                    // [a]
                    if (point.contains("]")) {
                        point = point.replace("]", "");
                    }
                    Point p = new Point(Double.valueOf(point), true);
                    seg.setLeftPoint(p);
                    continue;
                }
                // a]
                if (point.contains("]")) {
                    point = point.replace("]", "");
                    Point p = new Point(Double.valueOf(point), true);
                    seg.setRightPoint(p);
                    continue;
                }
                // (a или (a)
                if (point.contains("(")) {
                    point = point.replace("(", "");
                    // [(a)
                    if (point.contains(")")) {
                        point = point.replace(")", "");
                    }
                    Point p = new Point(convertStrToDouble.apply(point), false);
                    seg.setLeftPoint(p);
                    continue;
                }
                // a)
                if (point.contains(")")) {
                    point = point.replace(")", "");
                    Point p = new Point(convertStrToDouble.apply(point), false);
                    seg.setRightPoint(p);
                }
            }
            seg.selfCheck();
            segs.add(seg);
        }
        return segs;
    }

    /**
     * Считает пересечение ранее добавленных множеств
     * @return builder
     */
    @Override
    public BuilderIntersection calculate() {
        BunchLinkedList<Segment> union;

        // Пустой список - ошибка
        if (iterator == null) {
            throw new IllegalArgumentException("No data to calculate! Please add data!");
        }

        if (this.intersection == null) {
            this.intersection = new LinkedList<>();
        } else if (this.intersection.size() != 0){
            this.intersection.clear();
        }

        // Добавялем все объединения к первому элементу
        union = firstElement;
        for (; union != null; union = union.getUnionSeg()) {
            this.intersection.add(union.getValue());
        }

        this.iterator = this.iterator.getNextSeg();
        List<Segment> toRemove = new LinkedList<>();

        for (; iterator != null; iterator = iterator.getNextSeg()) {

            union = iterator;

            for (; union != null; union = union.getUnionSeg()) {

                toRemove.clear();
                for (Segment inter : intersection) {

                    Integer compLeftLeft = comparePoints(inter.getLeftPoint(), true, union.getValue().getLeftPoint(), true);
                    Integer compRightRight = comparePoints(inter.getRightPoint(), false, union.getValue().getRightPoint(), false);
                    // если левая т 1го ЛЕВЕЕ левой т 2го
                    if (compLeftLeft == -1) {

                        Integer compRightLeft = comparePoints(inter.getRightPoint(), false, union.getValue().getLeftPoint(), true);
                        // если правая т 1го НЕ ЛЕВЕЕ левой т 2го - смещаем левую точку отрезка
                        if (compRightLeft >= 0) {
                            inter.setLeftPoint(union.getValue().getLeftPoint());
                        }
                        // если правая т 1го ЛЕВЕЕ левой т 2го - удаляем из результата отрезок
                        else {
                            toRemove.add(inter);
                            continue;
                        }

                        if (compRightRight == 1) {
                            inter.setRightPoint(union.getValue().getRightPoint());
                        }
                    }
                    // если левая т 1го ПРАВЕЕ левой т 2го
                    else if (compLeftLeft == 1) {

                        Integer compLeftRight = comparePoints(inter.getLeftPoint(), true, union.getValue().getRightPoint(), false);
                        // если левая т 1го НЕ ПРАВЕЕ правой т 2го
                        if (compLeftRight <= 0) {
                            // если правая т 1го правее правой т 2го - смещаем точку отрезка
                            if (compRightRight == 1) {
                                inter.setRightPoint(union.getValue().getRightPoint());
                            }
                        }
                        // если левая т 1го ПРАВЕЕ правой т 2го
                        else {
                            toRemove.add(inter);
                            break;
                        }
                    }
                    // если левые точки равны
                    else {
                        if (compRightRight == 1) {
                            inter.setRightPoint(union.getValue().getRightPoint());
                        }
                    }
                }
                if (toRemove.size() > 0) {
                    intersection.removeAll(toRemove);
                }
            }
        }

        iterator = firstElement;
        return this;
    }

    /**
     * Сравнивает точки
     *
     * @param f первая точка
     * @param s вторая точка
     * @param isFLeft 1 точка левая
     * @param isSLeft 2 точка левая
     *
     * @return 1 если первая точка правее второй
     * <br> 0 если точки идентичны
     * <br> -1 если первая точка левее второй
     */
    private Integer comparePoints(Point f, boolean isFLeft, Point s, boolean isSLeft) {
        if (f.getValue() > s.getValue()) {
            return 1;
        }
        else if (f.getIn() && !s.getIn() && f.getValue().equals(s.getValue())) {
            if (isFLeft && isSLeft || !isFLeft && isSLeft) return -1;
            else return 1;
        }
        else if (f.getValue() < s.getValue()) {
            return -1;
        }
        else if (!f.getIn() && s.getIn() && f.getValue().equals(s.getValue())) {
            if (isFLeft) return 1;
            else return -1;
        }
        else {
            return 0;
        }
    }

    @Override
    public List<Segment> getIntersection() {
        if (this.intersection == null)
            return null;
        else
            return this.intersection.size() == 0 ? null : this.intersection;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        BunchLinkedList<Segment> union;

        for (; iterator != null; iterator = iterator.getNextSeg()) {

            union = iterator;

            for (; union != null; union = union.getUnionSeg()) {

                result.append(union.getValue());
                if (union.hasUnion()) result.append("u");

            }

            if (iterator.hasNext()) result.append("\n");

        }

        iterator = firstElement;
        return result.toString();
    }

    /**
     * очистка указателей кучи
     */
    @Override
    public void clear() {
        BunchLinkedList<Segment> union;
        BunchLinkedList<Segment> tmp;

        while (iterator != null) {

            union = iterator;

            while (union != null) {
                if (union.hasUnion()) {
                    tmp = union;
                    union = union.getUnionSeg();
                    tmp.setUnionSeg(null);
                }
                else {
                    union = null;
                }
            }

            if (iterator.hasNext()) {
                tmp = iterator;
                iterator = iterator.getNextSeg();
                tmp.setNextSeg(null);
            }
            else {
                iterator = null;
            }
        }

        this.firstElement = null;
        this.endElement = null;
        this.intersection.clear();
    }

    @Override
    public Double getNearPoint(Double point) {
        if (intersection == null || intersection.size() == 0) {
            throw new ArithmeticException("Please, calculate intersection of segments");
        }

        Integer compare;
        Double rpoint;
        Double lpoint;
        Double result = point;

        Segment tmpSeg = intersection.get(0);
        compare = tmpSeg.comparePoint(point);
        if (compare == 0) {
            return result;
        }
        else if (compare == -1) {
            return tmpSeg.getLeftPoint().getValue();
        }
        else {
            lpoint = tmpSeg.getRightPoint().getValue();
        }

        tmpSeg = intersection.get(intersection.size() - 1);
        compare = tmpSeg.comparePoint(point);
        if (compare == 0) {
            return result;
        }
        else if (compare == 1) {
            return tmpSeg.getRightPoint().getValue();
        }
        else {
            rpoint = tmpSeg.getLeftPoint().getValue();
        }

        int i = 0;
        for (Segment segment : intersection) {

            if (i == 0) {
                i++;
                continue;
            }

            compare = segment.comparePoint(point);

            if (compare == 0) {
                return result;
            }

            if (compare == 1 && lpoint < segment.getRightPoint().getValue()) {
                lpoint = segment.getRightPoint().getValue();
            }

            if (compare == -1 && rpoint < segment.getLeftPoint().getValue()) {
                rpoint = segment.getLeftPoint().getValue();

                if (point - lpoint < rpoint - point) {
                    result = lpoint;
                }
                else {
                    result = rpoint;
                }
            }
        }

        return result;
    }

    private static class BunchLinkedList<T> {
        private T t;
        private BunchLinkedList<T> nextSeg;
        private BunchLinkedList<T> unionSeg;

        protected BunchLinkedList() {
            this.nextSeg = null;
            this.unionSeg = null;
        }

        protected Boolean hasNext() {
            return this.nextSeg != null;
        }

        protected Boolean hasUnion() {
            return this.unionSeg != null;
        }

        protected T getValue() {
            return this.t;
        }

        protected void setValue(T t) {
            this.t = t;
        }

        protected BunchLinkedList<T> getNextSeg() {
            return this.nextSeg;
        }

        protected void setNextSeg(BunchLinkedList<T> nextSeg) {
            this.nextSeg = nextSeg;
        }

        protected BunchLinkedList<T> getUnionSeg() {
            return this.unionSeg;
        }

        protected void setUnionSeg(BunchLinkedList<T> unionSeg) {
            this.unionSeg = unionSeg;
        }
    }
}
