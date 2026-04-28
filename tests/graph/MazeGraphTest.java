package graph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import graph.MazeGraph.Direction;
import graph.MazeGraph.MazeEdge;
import graph.MazeGraph.IPair;
import graph.MazeGraph.MazeVertex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.GameMap;
import util.MazeGenerator.TileType;

public class MazeGraphTest {

    /* Note, to conform to the precondition of the `MazeGraph` constructor, make sure that any
     * TileType arrays that you construct contain a `PATH` tile at index [2][2] and represent a
     * single, orthogonally connected component of `PATH` tiles. */

    /**
     * Create a game map with tile types corresponding to the letters on each line of `template`.
     * 'w' = WALL, 'p' = PATH, and 'g' = GHOSTBOX.  The letters of `template` must form a rectangle.
     * Elevations will be a gradient from the top-left to the bottom-right corner with a horizontal
     * slope of 2 and a vertical slope of 1.
     */
    GameMap createMap(String template) {
        Scanner lines = new Scanner(template);
        ArrayList<ArrayList<TileType>> lineLists = new ArrayList<>();

        while (lines.hasNextLine()) {
            ArrayList<TileType> lineList = new ArrayList<>();
            for (char c : lines.nextLine().toCharArray()) {
                switch (c) {
                    case 'w' -> lineList.add(TileType.WALL);
                    case 'p' -> lineList.add(TileType.PATH);
                    case 'g' -> lineList.add(TileType.GHOSTBOX);
                }
            }
            lineLists.add(lineList);
        }

        int height = lineLists.size();
        int width = lineLists.getFirst().size();

        TileType[][] types = new TileType[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                types[i][j] = lineLists.get(j).get(i);
            }
        }

        double[][] elevations = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                elevations[i][j] = (2.0 * i + j);
            }
        }
        return new GameMap(types, elevations);
    }

    @DisplayName("WHEN a GameMap with exactly one path tile in position [2][2] is passed into the "
            + "MazeGraph constructor, THEN a graph with one vertex is created.")
    @Test
    void testOnePathCell() {
        GameMap map = createMap("""
                wwwww
                wwwww
                wwpww
                wwwww
                wwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        assertEquals(1, vertices.size());
        assertTrue(vertices.containsKey(new IPair(2, 2)));
    }

    @DisplayName("WHEN a GameMap with exactly two horizontally adjacent path tiles is passed into "
            + "the MazeGraph constructor, THEN a graph with two vertices is created in which the two "
            + "vertices are connected by two directed edges with weights determined by evaluating "
            + "`MazeGraph.edgeWeight` on their elevations.")
    @Test
    void testTwoPathCellsHorizontal() {
        GameMap map = createMap("""
                wwwww
                wwwww
                wwppw
                wwwww
                wwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        // graph contains two vertices with the correct locations
        assertEquals(2, vertices.size());
        IPair left = new IPair(2, 2);
        IPair right = new IPair(3, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        MazeVertex vl = vertices.get(left);
        MazeVertex vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        MazeEdge l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        double lElev = map.elevations()[2][2];
        double rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        MazeEdge r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());
    }


    @DisplayName("WHEN a GameMap with exactly two vertically adjacent path tiles is passed into "
            + "the MazeGraph constructor, THEN a graph with two vertices is created in which the "
            + "two vertices are connected by two directed edges with weights determined by "
            + "evaluating `MazeGraph.edgeWeight` on their elevations.")
    @Test
    void testTwoPathCellsVertical() {
        // TODO 2.2A: Complete this test case
        GameMap map = createMap("""
                wwwww
                wwwww
                wwpww
                wwpww
                wwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        // graph contains two vertices with the correct locations
        assertEquals(2, vertices.size());
        IPair up = new IPair(2, 2);
        IPair down = new IPair(2, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        MazeVertex vu = vertices.get(up);
        MazeVertex vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        MazeEdge u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        double UElev = map.elevations()[2][2];
        double DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        MazeEdge d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());
    }


    @DisplayName("WHEN a GameMap includes two path tiles in the first and last column of the same "
            + "row, THEN (tunnel) edges are created between these tiles with the correct properties.")
    @Test
    void testHorizontalTunnelEdgeCreation() {
        // TODO 2.2B: Complete this test case
        GameMap map = createMap("""
                wwwww
                wwwww
                ppppp
                wwwww
                wwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        // graph contains two vertices with the correct locations
        assertEquals(5, vertices.size());
        IPair left = new IPair(0, 2);
        IPair right = new IPair(4, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        MazeVertex vl = vertices.get(left);
        MazeVertex vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNotNull(vl.edgeInDirection(Direction.RIGHT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        MazeEdge l2r = vl.edgeInDirection(Direction.LEFT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        double lElev = map.elevations()[0][2];
        double rElev = map.elevations()[4][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.LEFT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNotNull(vr.edgeInDirection(Direction.LEFT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        MazeEdge r2l = vr.edgeInDirection(Direction.RIGHT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.RIGHT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());
    }

    @DisplayName("WHEN a GameMap includes a cyclic connected component of path tiles with a "
            + "non-path tile in the middle, THEN its graph includes edges between all adjacent "
            + "pairs of vertices.")
    @Test
    void testCyclicPaths() {
        GameMap map = createMap("""
                wwwwwww
                wwwwwww
                wwpppww
                wwpwpww
                wwpppww
                wwwwwww""");
        MazeGraph graph = new MazeGraph(map);
        // TODO 2.2C: Complete this test case
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        assertEquals(8, vertices.size());
        IPair one = new IPair(2, 2);
        IPair two = new IPair(3, 2);
        IPair three = new IPair(4, 2);
        IPair four = new IPair(2, 3);
        IPair six = new IPair(4, 3);
        IPair seven = new IPair(2, 4);
        IPair eight = new IPair(3, 4);
        IPair nine = new IPair(4, 4);
        assertTrue(vertices.containsKey(one));
        assertTrue(vertices.containsKey(two));
        assertTrue(vertices.containsKey(three));
        assertTrue(vertices.containsKey(four));
        assertTrue(vertices.containsKey(six));
        assertTrue(vertices.containsKey(seven));
        assertTrue(vertices.containsKey(eight));
        assertTrue(vertices.containsKey(nine));

        IPair left = new IPair(2, 2);
        IPair right = new IPair(3, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        MazeVertex vl = vertices.get(left);
        MazeVertex vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNotNull(vl.edgeInDirection(Direction.DOWN));
        MazeEdge l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        double lElev = map.elevations()[2][2];
        double rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNotNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        MazeEdge r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        left = new IPair(3, 2);
        right = new IPair(4, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        vl = vertices.get(left);
        vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNotNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        lElev = map.elevations()[2][2];
        rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNotNull(vr.edgeInDirection(Direction.DOWN));
        r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        left = new IPair(2, 4);
        right = new IPair(3, 4);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        vl = vertices.get(left);
        vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNotNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        lElev = map.elevations()[2][2];
        rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNotNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        left = new IPair(3, 4);
        right = new IPair(4, 4);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        vl = vertices.get(left);
        vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNotNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        lElev = map.elevations()[2][2];
        rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNotNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        IPair up = new IPair(2, 2);
        IPair down = new IPair(2, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        MazeVertex vu = vertices.get(up);
        MazeVertex vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNotNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        MazeEdge u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        double UElev = map.elevations()[2][2];
        double DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNotNull(vd.edgeInDirection(Direction.DOWN));
        MazeEdge d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(2, 3);
        down = new IPair(2, 4);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNotNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[2][2];
        DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNotNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(4, 2);
        down = new IPair(4, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNotNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[2][2];
        DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNotNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(4, 3);
        down = new IPair(4, 4);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNotNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[2][2];
        DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNotNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());
    }

    // TODO 2.2D: Add at least two additional test cases that cover other distinct scenarios that can
    //  arise in `GameMap`s. It is crucial that your graph is being linked together correctly,
    //  otherwise the later portions of this and the next assignment will break with strange
    //  behaviors.

    @DisplayName("WHEN a GameMap splits up at a given intersection "
            + "THEN its graph includes edges between all adjacent "
            + "pairs of vertices.")
    @Test
    void testTSplit() {
        GameMap map = createMap("""
                wwwwpww
                wwwwpww
                wwpppww
                wwwwpww
                wwwwpww
                wwwwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));

        assertEquals(7, vertices.size());
        IPair one = new IPair(2, 2);
        IPair two = new IPair(3, 2);
        IPair three = new IPair(4, 2);
        IPair six = new IPair(4, 3);
        IPair nine = new IPair(4, 4);
        IPair ten = new IPair(4, 1);
        IPair eleven = new IPair(4, 0);

        assertTrue(vertices.containsKey(one));
        assertTrue(vertices.containsKey(two));
        assertTrue(vertices.containsKey(three));
        assertTrue(vertices.containsKey(six));
        assertTrue(vertices.containsKey(nine));
        assertTrue(vertices.containsKey(ten));
        assertTrue(vertices.containsKey(eleven));

        IPair left = new IPair(2, 2);
        IPair right = new IPair(3, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        MazeVertex vl = vertices.get(left);
        MazeVertex vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        MazeEdge l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        double lElev = map.elevations()[2][2];
        double rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNotNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        MazeEdge r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        left = new IPair(3, 2);
        right = new IPair(4, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        vl = vertices.get(left);
        vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNotNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        lElev = map.elevations()[2][2];
        rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNotNull(vr.edgeInDirection(Direction.UP));
        assertNotNull(vr.edgeInDirection(Direction.DOWN));
        r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        IPair up = new IPair(4, 2);
        IPair down = new IPair(4, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        MazeVertex vu = vertices.get(up);
        MazeVertex vd = vertices.get(down);

        assertNotNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNotNull(vu.edgeInDirection(Direction.UP));
        MazeEdge u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        double UElev = map.elevations()[4][2];
        double DElev = map.elevations()[4][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNotNull(vd.edgeInDirection(Direction.DOWN));
        MazeEdge d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(4, 3);
        down = new IPair(4, 4);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNotNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[4][3];
        DElev = map.elevations()[4][4];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(4, 1);
        down = new IPair(4, 2);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNotNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[4][1];
        DElev = map.elevations()[4][2];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNotNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNotNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(4, 0);
        down = new IPair(4, 1);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[4][0];
        DElev = map.elevations()[4][1];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNotNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());
    }

    @DisplayName("WHEN a GameMap splits up at a given intersection "
            + "THEN its graph includes edges between all adjacent "
            + "pairs of vertices.")
    @Test
    void testSquarePath() {
        GameMap map = createMap("""
                wwwwwww
                wwwwwww
                wwppwww
                wwppwww
                wwwwwww
                wwwwwww""");
        MazeGraph graph = new MazeGraph(map);
        Map<IPair, MazeVertex> vertices = new HashMap<>();
        graph.vertices().forEach(v -> vertices.put(v.loc(), v));
        assertEquals(4, vertices.size());

        IPair up = new IPair(2, 2);
        IPair down = new IPair(2, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        MazeVertex vu = vertices.get(up);
        MazeVertex vd = vertices.get(down);

        assertNull(vu.edgeInDirection(Direction.LEFT));
        assertNotNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        MazeEdge u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        double UElev = map.elevations()[2][2];
        double DElev = map.elevations()[2][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNull(vd.edgeInDirection(Direction.LEFT));
        assertNotNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        MazeEdge d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        up = new IPair(3, 2);
        down = new IPair(3, 3);
        assertTrue(vertices.containsKey(up));
        assertTrue(vertices.containsKey(down));

        vu = vertices.get(up);
        vd = vertices.get(down);

        assertNotNull(vu.edgeInDirection(Direction.LEFT));
        assertNull(vu.edgeInDirection(Direction.RIGHT));
        assertNull(vu.edgeInDirection(Direction.UP));
        u2d = vu.edgeInDirection(Direction.DOWN);
        assertNotNull(u2d);

        UElev = map.elevations()[3][2];
        DElev = map.elevations()[3][3];
        assertEquals(vu, u2d.tail());
        assertEquals(vd, u2d.head());
        assertEquals(Direction.DOWN, u2d.direction());
        assertEquals(MazeGraph.edgeWeight(UElev, DElev), u2d.weight());

        assertNotNull(vd.edgeInDirection(Direction.LEFT));
        assertNull(vd.edgeInDirection(Direction.RIGHT));
        assertNull(vd.edgeInDirection(Direction.DOWN));
        d2u = vd.edgeInDirection(Direction.UP);
        assertNotNull(d2u);
        assertEquals(vd, d2u.tail());
        assertEquals(vu, d2u.head());
        assertEquals(Direction.UP, d2u.direction());
        assertEquals(MazeGraph.edgeWeight(DElev, UElev), d2u.weight());

        IPair left = new IPair(2, 2);
        IPair right = new IPair(3, 2);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        MazeVertex vl = vertices.get(left);
        MazeVertex vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNull(vl.edgeInDirection(Direction.UP));
        assertNotNull(vl.edgeInDirection(Direction.DOWN));
        MazeEdge l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        double lElev = map.elevations()[2][2];
        double rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNull(vr.edgeInDirection(Direction.UP));
        assertNotNull(vr.edgeInDirection(Direction.DOWN));
        MazeEdge r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());

        left = new IPair(2, 3);
        right = new IPair(3, 3);
        assertTrue(vertices.containsKey(left));
        assertTrue(vertices.containsKey(right));

        vl = vertices.get(left);
        vr = vertices.get(right);

        // left vertex has one edge to the vertex to its right
        assertNull(vl.edgeInDirection(Direction.LEFT));
        assertNotNull(vl.edgeInDirection(Direction.UP));
        assertNull(vl.edgeInDirection(Direction.DOWN));
        l2r = vl.edgeInDirection(Direction.RIGHT);
        assertNotNull(l2r);

        // edge from left to right has the correct fields
        lElev = map.elevations()[2][2];
        rElev = map.elevations()[3][2];
        assertEquals(vl, l2r.tail());
        assertEquals(vr, l2r.head());
        assertEquals(Direction.RIGHT, l2r.direction());
        assertEquals(MazeGraph.edgeWeight(lElev, rElev), l2r.weight());

        // right vertex has one edge to the vertex to its left with the correct fields
        assertNull(vr.edgeInDirection(Direction.RIGHT));
        assertNotNull(vr.edgeInDirection(Direction.UP));
        assertNull(vr.edgeInDirection(Direction.DOWN));
        r2l = vr.edgeInDirection(Direction.LEFT);
        assertNotNull(r2l);
        assertEquals(vr, r2l.tail());
        assertEquals(vl, r2l.head());
        assertEquals(Direction.LEFT, r2l.direction());
        assertEquals(MazeGraph.edgeWeight(rElev, lElev), r2l.weight());
    }
}
