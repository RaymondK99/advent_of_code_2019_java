import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;

public class Main {

    private static Day createDayObject(int day) {
        switch (day) {
            case 18:
                return new Day18();
            default:
                break;
        }

        return null;
    }

    public static void main(String[] args) {

        if ( args.length != 2 ) {
            System.exit(1);
        } else {
            int day = Integer.parseInt(args[0]);
            int part = Integer.parseInt(args[1]);

            try {
                Day dayObj = createDayObject(day);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(System.in);
                String input = new String(bufferedInputStream.readAllBytes());

                if (part == 1) {
                    System.out.println(dayObj.solvePart1(input));
                } else if (part == 2) {
                    System.out.println(dayObj.solvePart1(input));
                }

            } catch (IOException exception) {
                System.err.println("Failed to read stdinput.");
            }
        }
    }
}
