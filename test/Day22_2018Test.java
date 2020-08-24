import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day22_2018Test {

    Day day;

    @BeforeEach
    public void setup() {
        day = new Day22_2018();
    }

    @Test
    void test1() {
        String input =  "depth: 510\n" +
                "target: 10,10";
        String res = day.solvePart1(input);

        System.out.println("Res=" + res);
        assertEquals("114", res);
    }

    @Test
    void test_part1() {
        String input =  "depth: 3339\n" +
                "target: 10,715";
        String res = day.solvePart1(input);

        System.out.println("Res=" + res);
        assertEquals("7915", res);
    }

    @Test
    void test2() {
        String input =  "depth: 510\n" +
                "target: 10,10";
        String res = day.solvePart2(input);

        System.out.println("Res=" + res);
        assertEquals("45", res);
    }

    @Test
    void test_part2() {
        String input =  "depth: 3339\n" +
                "target: 10,715";
        String res = day.solvePart2(input);

        System.out.println("Res=" + res);
        assertEquals("980", res);
    }
}