package model;

import graph.MazeGraph;

import java.awt.Color;

public class Inky extends Ghost{

    public Inky(GameModel model) {
        super(model, Color.cyan,6000);
    }

    /**
     * Return the vertex that Inky is targeting. Targets itself to be equidistant from Blinky and Pac-Mann. When
     * FLEEing, returns the southwest corner.
     */
    @Override
    protected MazeGraph.MazeVertex target() {
        if (this.state() == GhostState.CHASE) {
            MazeGraph.IPair pacMannLoc = model.pacMann().nearestVertex().loc();
            MazeGraph.IPair blinkyLoc = model.blinky().nearestVertex().loc();

            int iPos = 2*pacMannLoc.i() - blinkyLoc.i();
            int jPos = 2*pacMannLoc.j() - blinkyLoc.j();


            return model.graph().closestTo(iPos,jPos);
        }
        else {
            return model.graph().closestTo(2,model.height()-3);
        }
    }
}
