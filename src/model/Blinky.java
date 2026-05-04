package model;

import graph.MazeGraph;

import java.awt.Color;

public class Blinky extends Ghost{

    public Blinky(GameModel model) {
        super(model, Color.red,2000);
    }

    @Override
    protected MazeGraph.MazeVertex target() {
        // when in the chase state, blinky targets pac-mann's nearestVertex()
        if (this.state() == GhostState.CHASE) {
            return model.pacMann().nearestVertex();
        }
        else {
            return model.graph().closestTo(2,2);
        }
    }
}
