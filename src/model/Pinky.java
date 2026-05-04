package model;

import graph.MazeGraph;

import java.awt.*;

public class Pinky extends Ghost {

    public Pinky(GameModel model) {
        super(model, Color.pink,4000);
    }
    @Override
    protected MazeGraph.MazeVertex target() {
        if (this.state() == GhostState.CHASE) {
            MazeGraph.MazeVertex target = model.pacMann().nearestVertex();
            MazeGraph.Direction direction = model.pacMann().currentEdge().direction();
            int iPos = target.loc().i();
            int jPos = target.loc().j();
            switch(direction) {
                case LEFT -> iPos -= 3;
                case RIGHT -> iPos += 3;
                case UP -> jPos -= 3;
                case DOWN -> jPos += 3;
            }
            return model.graph().closestTo(iPos,jPos);
        }
        else {
            return model.graph().closestTo(model.width()-3,2);
        }
    }
}
