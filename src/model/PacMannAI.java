package model;

import java.util.List;

import graph.MazeGraph;
import graph.Pathfinding;

public class PacMannAI extends PacMann {

    public PacMannAI(GameModel model) {
        super(model);
    }

    @Override
    public MazeGraph.MazeEdge nextEdge() {
        MazeGraph.MazeVertex current = this.nearestVertex();
        for (MazeGraph.MazeEdge edge : current.outgoingEdges()) {
            double weighting = edge.weight();
        }
        return currentEdge(); //placeholder
    }

    private double weight(MazeGraph.MazeEdge edge) {
        MazeGraph.MazeVertex next = edge.head();

        // check the distance to nearest ghost
        for (Actor a : model.actors()) {
            List<MazeGraph.MazeEdge> path =
                    Pathfinding.shortestNonBacktrackingPath(edge.tail(), edge.head(), null);
            int size = path.size();
        }
        return 0;
    }

}
