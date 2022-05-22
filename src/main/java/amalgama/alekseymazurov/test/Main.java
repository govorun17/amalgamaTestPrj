package amalgama.alekseymazurov.test;

import amalgama.alekseymazurov.test.model.Intersection;
import amalgama.alekseymazurov.test.model.BuilderIntersection;

public class Main {
    public static void main(String[] args) {
        Intersection builder = new BuilderIntersection();
        System.out.println(builder.add("(-inf,10]u(20,+inf)").add("(-5,25)").calculate().getIntersection());
    }
}
