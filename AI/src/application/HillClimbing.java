package application;

import java.util.Random;

public class HillClimbing {

    public static double[] runHillClimbing(Course[] courses, Student[] students, Room[] rooms, int iterations) {
        double[] fitnessHistory = new double[iterations];

        Schedule currentSchedule = new Schedule(courses, rooms);
        currentSchedule.calculateFitness(students);

        double currentFitness = currentSchedule.getFitness();
        Random random = new Random();

        for (int i = 0; i < iterations; i++) {
            Schedule neighborSchedule = copySchedule(currentSchedule);
            Gene[] genes = neighborSchedule.getGenes();

            int randomGeneIndex = random.nextInt(genes.length);
            Gene g = genes[randomGeneIndex];

            g.setDay(random.nextInt(ScheduleConfig.MAX_DAYS) + 1);
            g.setSlot(random.nextInt(ScheduleConfig.MAX_SLOTS) + 1);
            g.setRoom(rooms[random.nextInt(rooms.length)]);

            neighborSchedule.calculateFitness(students);
            double neighborFitness = neighborSchedule.getFitness();

            if (neighborFitness >= currentFitness) {
                currentSchedule = neighborSchedule;
                currentFitness = neighborFitness;
            }

            fitnessHistory[i] = currentFitness;
        }

        return fitnessHistory;
    }

    private static Schedule copySchedule(Schedule original) {
        Gene[] originalGenes = original.getGenes();
        Schedule copy = new Schedule(originalGenes.length);

        for (int i = 0; i < originalGenes.length; i++) {
            Gene g = originalGenes[i];
            copy.setGene(i, new Gene(g.getCourse(), g.getRoom(), g.getDay(), g.getSlot()));
        }

        return copy;
    }
}