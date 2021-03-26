package de.hglabor.plugins.training.challenges;

import de.hglabor.plugins.training.region.Area;

public interface Challenge {
    Area getArea();

    void start();

    void stop();
}
