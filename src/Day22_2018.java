import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Day22_2018 implements Day {

    static class InputData {
        final int depth, target_x,target_y;
        public InputData(int d, int x, int y) {
            this.depth = d;
            this.target_x = x;
            this.target_y = y;
        }
    }

    enum RegionType {
        Rocky,
        Wet,
        Narrow,
    }

    enum Tool {
        Torch,
        ClimbingGear,
        Neither,
    }

    static class Node {
        private final Tool tool;
        private final Position pos;
         public Node(Position pos, Tool tool) {
             this.tool = tool;
             this.pos = pos;
         }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (tool != node.tool) return false;
            return pos.equals(node.pos);
        }

        @Override
        public int hashCode() {
            int result = tool.hashCode();
            result = 31 * result + pos.hashCode();
            return result;
        }
    }

    static class QueueElement implements Comparable<QueueElement> {
        final int distance;
        final Node node;

        QueueElement(int distance, Node node) {
            this.distance = distance;
            this.node = node;
        }


        @Override
        public int compareTo(QueueElement o) {
            return Integer.compare(this.distance, o.distance);
        }
    }

    static class Position {
        final int x,y;
        public Position(int x,int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position position = (Position) o;

            if (x != position.x) return false;
            return y == position.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    static class CaveSystem {
        Map<Position,Integer> erosionLevel;
        InputData inputData;

        public CaveSystem(InputData inputData) {
            this.inputData = inputData;
            this.erosionLevel = new HashMap<>();
        }

        private boolean isPassable(int x, int y, Tool tool) {
            RegionType regionType = this.getRegionType(x,y);

            if (regionType == null) {
              return false;
            } else if (regionType == RegionType.Rocky && (tool == Tool.ClimbingGear || tool == Tool.Torch)) {
                return true;
            } else if (regionType == RegionType.Wet && (tool == Tool.ClimbingGear || tool == Tool.Neither)) {
                return true;
            } else if (regionType == RegionType.Narrow && (tool == Tool.Torch || tool == Tool.Neither)) {
                return true;
            }

            return false;
        }

        private Tool getOtherTool(int x, int y, Tool tool) {
            RegionType regionType = this.getRegionType(x,y);

            if (regionType == RegionType.Wet && tool == Tool.ClimbingGear ) {
                return Tool.Neither;
            } else if (regionType == RegionType.Wet && tool == Tool.Neither ) {
                return Tool.ClimbingGear;
            } else if (regionType == RegionType.Narrow && tool == Tool.Neither ) {
                return Tool.Torch;
            } else if (regionType == RegionType.Narrow && tool == Tool.Torch ) {
                return Tool.Neither;
            } else if (regionType == RegionType.Rocky && tool == Tool.ClimbingGear ) {
                return Tool.Torch;
            } else if (regionType == RegionType.Rocky && tool == Tool.Torch ) {
                return Tool.ClimbingGear;
            }

            throw new RuntimeException("..");
        }

        public RegionType getRegionType(int x, int y) {
            Integer erosion = erosionLevel.get(new Position(x,y));
            if (erosion == null) {
                return null;
            }
            switch (erosion.intValue() % 3) {
                case 0: return RegionType.Rocky;
                case 1: return RegionType.Wet;
                case 2: return RegionType.Narrow;
                default:
                    throw new RuntimeException("Unsupported type");
            }
        }

        public int calculateRiskLevel() {
            int riskLevel = 0;
            for (int x=0;x<=this.inputData.target_x;x++) {
                for (int y=0;y<=this.inputData.target_y;y++) {
                    riskLevel += erosionLevel.get(new Position(x,y)) % 3;
                }
            }

            return riskLevel;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (int y = 0; y <= this.inputData.target_y;y++) {
                for (int x = 0; x <= this.inputData.target_x; x++) {
                    if (x == 0 && y == 0 ) {
                        sb.append("M");
                    } else if (x == this.inputData.target_x && y == inputData.target_y) {
                        sb.append("T");
                    }
                    else if (this.getRegionType(x,y) == RegionType.Wet) {
                        sb.append("=");
                    } else if (this.getRegionType(x,y) == RegionType.Narrow) {
                        sb.append("|");
                    } else if (this.getRegionType(x,y) == RegionType.Rocky) {
                        sb.append(".");
                    }
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        public void prepareErosionLevel() {
            for (int y = 0; y <= this.inputData.target_y*4;y++) {
                for (int x = 0; x <= this.inputData.target_x*4; x++) {
                    int geoIndex = 0;
                    if ( (x == 0 && y == 0 ) || ( x == inputData.target_x && y == inputData.target_y)) {
                        geoIndex = 0;
                    } else if (y==0) {
                        geoIndex = x * 16807;
                    } else if (x == 0) {
                        geoIndex = y * 48271;
                    } else {
                        geoIndex = this.erosionLevel.get(new Position(x-1,y)) * this.erosionLevel.get(new Position(x,y-1));
                    }

                    int erosionLevel = (geoIndex + inputData.depth) % 20183;
                    this.erosionLevel.put(new Position(x,y), erosionLevel);
                }
            }
        }

        public int minutesToReachTarget() {
            HashMap<Node, Integer> visited = new HashMap<>();
            PriorityQueue<QueueElement> queue = new PriorityQueue<>();

            // Starting position
            Position startPos = new Position(0,0);
            Node startNode = new Node(startPos, Tool.Torch);

            // Add start node to queue
            queue.add(new QueueElement(0, startNode));
            visited.put(startNode, 0);

            while (!queue.isEmpty()) {

                // Poll item from queue
                QueueElement element = queue.poll();
                int distance = element.distance;
                Tool currentTool = element.node.tool;
                int x = element.node.pos.x;
                int y = element.node.pos.y;

                // Check exit criteria
                if (x == this.inputData.target_x && y == inputData.target_y &&  currentTool == Tool.Torch) {
                    // Found solution
                    System.out.println("Found solution at " + distance);
                    return distance;
                }

                // Create state for changing gear
                Tool otherTool = this.getOtherTool(x,y,currentTool);
                Node changeToolNode = new Node(element.node.pos, otherTool);
                int changedToolDist = distance + 7;

                Integer prevChangedToolDist = visited.get(changeToolNode);
                if (prevChangedToolDist == null || changedToolDist < prevChangedToolDist) {
                    // We found a shorter distance or unvisited
                    queue.add(new QueueElement(distance+7,changeToolNode));
                    visited.put(changeToolNode, changedToolDist);
                }

                // Check possible adjacent positions that are passable
                int nextDistance = distance+1;
                Position adjacentPos[] = {new Position(x-1,y), new Position(x+1,y), new Position(x,y-1), new Position(x,y+1)};
                Node adjacentNode[] = Arrays.stream(adjacentPos)
                        .filter( (p) ->  isPassable(p.x,p.y,currentTool))
                        .map( (pos) -> new Node(pos,currentTool))
                        .filter( (node) -> {
                            Integer prevDist = visited.get(node);
                            if (prevDist == null || prevDist > nextDistance) {
                                return true;
                            }
                            return false;
                        })
                        .toArray(Node[]::new);

                for (Node node : adjacentNode) {
                    visited.put(node,nextDistance);
                    queue.add(new QueueElement(nextDistance, node));
                }

            }

            // If queue is empty there was no solution found
            throw new RuntimeException("No solution");
        }
    }

    /**
     * depth: 3339
     * target: 10,715
     * @param input
     */
    public InputData parseInput(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        String line1 = lines[0];
        String pos = lines[1].split(" ")[1];

        int depth = Integer.parseInt(line1.split(" ")[1]);
        int x = Integer.parseInt(pos.split(",")[0]);
        int y = Integer.parseInt(pos.split(",")[1]);

        return new InputData(depth,x,y);
    }

    @Override
    public String solvePart1(String input) {
        InputData inputData = parseInput(input);
        CaveSystem caveSystem = new CaveSystem(inputData);
        caveSystem.prepareErosionLevel();
        int riskLevel = caveSystem.calculateRiskLevel();
        return Integer.toString(riskLevel);
    }

    @Override
    public String solvePart2(String input) {
        InputData inputData = parseInput(input);
        CaveSystem caveSystem = new CaveSystem(inputData);
        caveSystem.prepareErosionLevel();
        int minutes = caveSystem.minutesToReachTarget();
        return Integer.toString(minutes);    }
}
