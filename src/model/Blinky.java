package model;

import graph.MazeGraph;

import java.awt.Color;

public class Blinky extends Ghost{

    public Blinky(GameModel model) {
        super(model, Color.red,2000);
    }

    /**
     * Returns the vertex that Blinky is targeting. Targets Pac-Mann's location. When FLEEing, Blinky goes to the
     * southwest corner.
     */

    @Override
    protected MazeGraph.MazeVertex target() {
        if (this.state() == GhostState.CHASE) {
            return model.pacMann().nearestVertex();
        }
        else {
            return model.graph().closestTo(2,2);
        }
    }
}
