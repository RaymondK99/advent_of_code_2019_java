import java.util.*;
import java.util.stream.Collectors;

public class Day18 implements Day {

    class Point {
        final public int x;
        final public int y;

        public Point(int x,int y) {
            this.x = x;
            this.y = y;
        }

        public Point up() {
            return new Point(x,y-1);
        }

        public Point down() {
            return new Point(x,y+1);
        }

        public Point left() {
            return new Point(x-1,y);
        }

        public Point right() {
            return new Point(x+1,y);
        }
        @Override
        public int hashCode() {
            return Integer.hashCode(this.x + this.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }
    }

    /**
     * This structure is used to keep track of already visited nodes
     * and determine if there is a shorter distance to the specific position and collected keys
     */
    class Node {
        final Point point;
        final HashSet<Character> keys;

        public Node(Point point, HashSet<Character> keys) {
            this.point = point;
            this.keys = keys;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(point.hashCode() + keys.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node)o;
            return keys.equals(node.keys) && point.equals(node.point);
        }
    }

    /**
     * State node in the search tree
     */
    class State {
        final int dist;
        final Point point;
        final HashSet<Character> keys;

        public State(int dist, Point point, HashSet keys) {
            this.dist = dist;
            this.point = point;
            this.keys = keys;
        }

        public Node getNode() {
            return new Node(this.point, keys);
        }

    }


    /**
     * Comparator in order to sort priority queue
     */
    class StateComparator implements Comparator<State> {
        @Override
        public int compare(State o1, State o2) {
            int cmp1 = Integer.valueOf(o1.dist).compareTo(o2.dist);
            if (cmp1 == 0) {
                return Integer.valueOf(o2.keys.size()).compareTo(o1.keys.size());
            }
            return cmp1;
        }
    }

    /*
     * Build hashmap that represents the map with position as key and
     * a char as value
     */
    public HashMap createMap(String input) {
        HashMap map = new HashMap<Point, Character>();
        String[] lines = input.lines().toArray(String[]::new);
        for (int y=0;y< lines.length;y++) {
            for (int x=0;x<lines[y].length();x++) {
                char ch = lines[y].charAt(x);
                map.put(new Point(x,y),ch);

            }
        }

        return map;
    }

    /**
     * Print current map
     */
    public void printMap(HashMap<Point,Character> map) {
        int max_x = map.entrySet().stream().map( (entry) -> entry.getKey().x).max(Integer::compareTo).get();
        int max_y = map.entrySet().stream().map( (entry) -> entry.getKey().y).max(Integer::compareTo).get();
        int min_x = map.entrySet().stream().map( (entry) -> entry.getKey().x).min(Integer::compareTo).get();
        int min_y = map.entrySet().stream().map( (entry) -> entry.getKey().y).min(Integer::compareTo).get();

        for (int y=min_y;y<=max_y;y++) {
            for (int x=min_x;x<=max_x;x++) {
                System.out.print(map.get(new Point(x,y)));
            }
            System.out.println();
        }

    }

    @Override
    public String solvePart1(String input) {

        HashMap<Point,Character> map = createMap(input);
        HashMap<Node, Integer> visited = new HashMap<>();

        printMap(map);

        // Find start position
        Point start_pos = map.entrySet().stream().filter( (entry) -> entry.getValue() == '@').map(entry -> entry.getKey()).findFirst().get();
        long num_keys = map.entrySet().stream().filter(entry -> Character.isLowerCase(entry.getValue().charValue())).count();

        // Create priority queue which should be ordered by distance from origin and number of keys collected
        // smallest distance from origin and state which most keys should be processed first
        PriorityQueue<State> queue = new PriorityQueue(10, new StateComparator());
        State initialState = new State(0,start_pos, new HashSet());

        // Add initial state to queue and visited map
        queue.add(initialState);
        visited.put(initialState.getNode(),0);

        // Keep track of number of iterations to find a solution
        int iterations = 0;
        while (!queue.isEmpty()) {

            // De-queue first item
            State state = queue.poll();
            int dist = state.dist;
            iterations++;

            // If all keys are found, the solution is found...
            if (state.keys.size() == num_keys) {
                System.out.println("Iterations = " + iterations);
                return Integer.valueOf(dist).toString();
            }

            // Build a list of adjacent points of the current position/state
            Point points[] = {state.point.up(), state.point.down(), state.point.left(), state.point.right()};
            ArrayList<State> nextStates = new ArrayList<>();

            Arrays.stream(points).forEach( point -> {
                Character pos = map.get(point);
                if (pos == '@' || pos == '.' || (Character.isUpperCase(pos) && state.keys.contains(Character.toLowerCase(pos)))) {
                    // Open space or door to which we have a key
                    HashSet set = new HashSet();
                    state.keys.stream().forEach( item -> set.add(item));

                    State next = new State(dist+1, point, set );
                    nextStates.add(next);
                } else if ( pos == '#') {
                    // Closed space...

                } else if ( Character.isLowerCase(pos) ) {
                    // Found new key, create a new state node that contains the new key
                    HashSet set = new HashSet();
                    state.keys.stream().forEach( item -> set.add(item));
                    set.add(pos);
                    State next = new State(dist+1, point, set );
                    nextStates.add(next);
                }
            });

            // Check of the potential adjacent states are already visited with a shorter distance
            nextStates.forEach( nextState -> {
                Node nextNode = nextState.getNode();
                Integer prev_dist = visited.get(nextNode);

                if (prev_dist == null || prev_dist > nextState.dist) {
                    visited.put(nextNode, nextState.dist);
                    queue.add(nextState);
                }
            });
        }

        return "No solution";
    }

    @Override
    public String solvePart2(String input) {
        return "Not implemented";
    }
}
