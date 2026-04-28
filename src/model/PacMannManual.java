package model;

import graph.MazeGraph.MazeEdge;
import graph.MazeGraph.MazeVertex;

public class PacMannManual extends PacMann {

    /**
     * Construct a PacMannManual character associated to the given `model`.
     */
    public PacMannManual(GameModel model) {
        super(model);
    }

    public MazeEdge nextEdge() {
        MazeVertex currentVertex = nearestVertex();
        if (model.playerCommand() != null && currentVertex.edgeInDirection(model.playerCommand())
                != null) {
            return currentVertex.edgeInDirection(model.playerCommand());
        } else if (currentVertex.edgeInDirection(currentEdge().direction()) != null) {
            return currentVertex.edgeInDirection(currentEdge().direction());
        } else {
            return null;
        }
    }


}
