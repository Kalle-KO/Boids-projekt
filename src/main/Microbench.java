package main;

import main.simulation.FlockSimulation;
import main.model.BoidType;
import main.spatial.*;

public class Microbench {
    public static void main(String[] args) {
        int width = 1200;
        int height = 800;
        int boidCount = 1000;
        int iterations = 200;
        double radius = 50.0;

        // Naive er klart mest langsom, og dette bliver tydeligt når boid-antallet når højt op. Når det er (meget) lavt,
        // er der ikke den store forskel.
        // Den hurtigste er QuadTree (og KD-Tree er tæt op af, specielt i lavere boid count).
        // Radius påvirker også Naive ganske negativt når den kommer højt op. Her vinder QuadTree tydeligere overfor KD-Tree.

        System.out.println("Microbenchmark af Spatial Index datastrukturer");
        System.out.println("Parametre: " + boidCount + " boids, " + iterations + " iterationer, radius: " + radius);
        System.out.println();

        // Test Naive
        System.out.println("Testing Naive O(n²)...");
        testSpatialIndex(new NaiveSpatialIndex(), width, height, boidCount, radius, iterations);

        // Test KD-Tree
        System.out.println("Testing KD-Tree...");
        testSpatialIndex(new KDTreeSpatialIndex(), width, height, boidCount, radius, iterations);

        // Test Spatial Hashing
        System.out.println("Testing Spatial Hashing...");
        testSpatialIndex(new SpatialHashIndex(width, height, 50), width, height, boidCount, radius, iterations);

        // Test QuadTree
        System.out.println("Testing QuadTree...");
        testSpatialIndex(new QuadTreeSpatialIndex(width, height), width, height, boidCount, radius, iterations);
    }

    private static void testSpatialIndex(SpatialIndex spatialIndex, int width, int height,
                                         int boidCount, double radius, int iterations) {
        FlockSimulation simulation = new FlockSimulation(width, height);
        simulation.setSpatialIndex(spatialIndex);
        simulation.setBoidCount(boidCount);
        simulation.setNeighborRadius(radius);

        // Warmup
        for (int i = 0; i < 50; i++) {
            simulation.update();
        }

        // Måling
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            simulation.update();
        }
        long endTime = System.nanoTime();

        double totalTimeMs = (endTime - startTime) / 1_000_000.0;
        double avgTimeMs = totalTimeMs / iterations;

        System.out.println("  Total tid: " + String.format("%.2f", totalTimeMs) + " ms");
        System.out.println("  Gennemsnit: " + String.format("%.3f", avgTimeMs) + " ms per iteration");
        System.out.println();
    }
}