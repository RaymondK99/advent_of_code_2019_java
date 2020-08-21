import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class Day18Test {

    Day day;

    @BeforeEach
    public void setup() {
        day = new Day18();
    }

    @org.junit.jupiter.api.Test
    void test1() {
        String input =  "########################\n" +
                "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                "######################.#\n" +
                "#d.....................#\n" +
                "########################";
        String res = day.solvePart1(input);

        System.out.println("Res=" + res);
    }

    @org.junit.jupiter.api.Test
    void test2() {
        String input =  "#################\n" +
                "#i.G..c...e..H.p#\n" +
                "########.########\n" +
                "#j.A..b...f..D.o#\n" +
                "########@########\n" +
                "#k.E..a...g..B.n#\n" +
                "########.########\n" +
                "#l.F..d...h..C.m#\n" +
                "#################";
        String res = day.solvePart1(input);

        System.out.println("Res=" + res);
    }

    @org.junit.jupiter.api.Test
    void solvePart2() {
    }
}