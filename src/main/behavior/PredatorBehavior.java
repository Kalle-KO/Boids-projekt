package main.behavior;

import main.model.Boid;
import main.simulation.Forces;
import main.simulation.Vector2D;

import java.util.List;

public class PredatorBehavior implements BehaviorStrategy {
    private static final double MAX_SPEED = 2.0;
    private static final double MAX_FORCE = 0.03;

    @Override
    public Forces calculateForces(Boid predator, List<Boid> neighbors) {
        if (neighbors.isEmpty()) {
            return new Forces();
        }

        // Find nearest boid
        Boid nearest = findNearestBoid(predator, neighbors);

        if (nearest == null) {
            return new Forces();
        }

        // Calculate pursuit force towards nearest boid
        Vector2D pursuit = calculatePursuit(predator, nearest);

        // Return only pursuit force (ignore separation, alignment, cohesion)
        return new Forces(pursuit, Vector2D.ZERO, Vector2D.ZERO);
    }

    private Boid findNearestBoid(Boid predator, List<Boid> neighbors) {
        Boid nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Boid neighbor : neighbors) {
            double distance = predator.distanceTo(neighbor);
            if (distance > 0 && distance < minDistance) {
                minDistance = distance;
                nearest = neighbor;
            }
        }

        return nearest;
    }

    private Vector2D calculatePursuit(Boid predator, Boid target) {
        // Calculate direction to target
        double steerX = target.getX() - predator.getX();
        double steerY = target.getY() - predator.getY();

        // Normalize and scale to max speed
        double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
        if (magnitude > 0) {
            steerX = (steerX / magnitude) * MAX_SPEED;
            steerY = (steerY / magnitude) * MAX_SPEED;

            // Calculate steering force (desired velocity - current velocity)
            steerX -= predator.getVx();
            steerY -= predator.getVy();

            // Limit to max force
            double force = Math.sqrt(steerX * steerX + steerY * steerY);
            if (force > MAX_FORCE) {
                steerX = (steerX / force) * MAX_FORCE;
                steerY = (steerY / force) * MAX_FORCE;
            }

            return new Vector2D(steerX, steerY);
        }

        return Vector2D.ZERO;
    }
}
