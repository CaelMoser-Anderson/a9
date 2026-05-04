package model;

import graph.MazeGraph;

import java.awt.Color;
import java.util.Random;

public class Clyde extends Ghost{

    private final Random randNum;

    public Clyde(GameModel model, Random randNum) {
        super(model, Color.orange,8000);
        this.randNum = randNum;
    }

    @Override
    protected MazeGraph.MazeVertex target() {
        if (this.state() == GhostState.CHASE) {
            MazeGraph.IPair pacMannLoc = model.pacMann().nearestVertex().loc();
            MazeGraph.IPair clyde = this.nearestVertex().loc();

            double euclidianDistance = Math.sqrt((clyde.i()-pacMannLoc.i())*(clyde.i()-pacMannLoc.i()) +
                    (clyde.j()-pacMannLoc.j())*(clyde.j()-pacMannLoc.j()));

            if (euclidianDistance >= 10) {
                return model.pacMann().nearestVertex();
            } else {
                return model.graph().closestTo(randNum.nextInt(model.width()),randNum.nextInt(model.height()));
            }
        }
        else {
            return model.graph().closestTo(model.width() - 3, model.height() - 3);
        }
    }
}
