package application;

import java.util.Random;

public class Population {
    private Schedule[] schedules;

    public Population(int populationSize, Course[] courses, Room[] rooms) {
        schedules = new Schedule[populationSize];
        for (int i = 0; i < populationSize; i++) {
            schedules[i] = new Schedule(courses, rooms);
        }
    }

    public Population(int populationSize) {
        schedules = new Schedule[populationSize];
    }

    public Schedule[] getSchedules() {
        return schedules;
    }

    public void setSchedule(int index, Schedule schedule) {
        schedules[index] = schedule;
    }

    public void calculatePopulationFitness(Student[] students) {
        for (Schedule schedule : schedules) {
            if (schedule != null) {
                schedule.calculateFitness(students);
            }
        }
    }

    public Schedule getBestSchedule() {
        Schedule best = schedules[0];
        for (Schedule schedule : schedules) {
            if (schedule != null && (best == null || schedule.getFitness() > best.getFitness())) {
                best = schedule;
            }
        }
        return best;
    }

    public Schedule tournamentSelection() {
        int tournamentSize = 3;
        Population tournament = new Population(tournamentSize);
        Random random = new Random();

        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(schedules.length);
            tournament.setSchedule(i, schedules[randomIndex]);
        }

        return tournament.getBestSchedule();
    }

    public Schedule rouletteWheelSelection() {
        double totalFitness = 0.0;
        for (Schedule s : schedules) {
            if (s != null) {
                totalFitness += s.getFitness();
            }
        }

        if (totalFitness <= 0) {
            return schedules[new Random().nextInt(schedules.length)];
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0.0;

        for (Schedule s : schedules) {
            if (s != null) {
                cumulativeFitness += s.getFitness();
                if (cumulativeFitness >= randomValue) {
                    return s;
                }
            }
        }
        return schedules[schedules.length - 1];
    }

    public Schedule onePointCrossover(Schedule parent1, Schedule parent2, double crossoverRate) {
        Random random = new Random();
        Gene[] parent1Genes = parent1.getGenes();
        Gene[] parent2Genes = parent2.getGenes();
        Schedule child = new Schedule(parent1Genes.length);

        if (parent1Genes.length <= 1 || random.nextDouble() >= crossoverRate) {
            for (int i = 0; i < parent1Genes.length; i++) {
                Gene g = parent1Genes[i];
                child.setGene(i, new Gene(g.getCourse(), g.getRoom(), g.getDay(), g.getSlot()));
            }
            return child;
        }

        int crossoverPoint = random.nextInt(parent1Genes.length - 1) + 1;

        for (int i = 0; i < parent1Genes.length; i++) {
            Gene selectedGene = (i < crossoverPoint) ? parent1Genes[i] : parent2Genes[i];
            child.setGene(i, new Gene(selectedGene.getCourse(), selectedGene.getRoom(), selectedGene.getDay(), selectedGene.getSlot()));
        }
        return child;
    }

    public Schedule twoPointCrossover(Schedule parent1, Schedule parent2, double crossoverRate) {
        Random random = new Random();
        Gene[] parent1Genes = parent1.getGenes();
        Gene[] parent2Genes = parent2.getGenes();
        Schedule child = new Schedule(parent1Genes.length);

        if (parent1Genes.length <= 1 || random.nextDouble() >= crossoverRate) {
            for (int i = 0; i < parent1Genes.length; i++) {
                Gene g = parent1Genes[i];
                child.setGene(i, new Gene(g.getCourse(), g.getRoom(), g.getDay(), g.getSlot()));
            }
            return child;
        }

        int point1 = random.nextInt(parent1Genes.length - 1);
        int point2 = random.nextInt(parent1Genes.length - 1);

        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        for (int i = 0; i < parent1Genes.length; i++) {
            Gene selectedGene;
            if (i < point1 || i >= point2) {
                selectedGene = parent1Genes[i];
            } else {
                selectedGene = parent2Genes[i];
            }
            child.setGene(i, new Gene(selectedGene.getCourse(), selectedGene.getRoom(), selectedGene.getDay(), selectedGene.getSlot()));
        }
        return child;
    }

    public Schedule crossover(Schedule parent1, Schedule parent2, double crossoverRate, String crossoverType) {
        if (crossoverType.equalsIgnoreCase("TwoPoint")) {
            return twoPointCrossover(parent1, parent2, crossoverRate);
        } else {
            return onePointCrossover(parent1, parent2, crossoverRate);
        }
    }

    public Schedule copySchedule(Schedule original) {
        Gene[] originalGenes = original.getGenes();
        Schedule copy = new Schedule(originalGenes.length);

        for (int i = 0; i < originalGenes.length; i++) {
            Gene originalGene = originalGenes[i];
            Gene copiedGene = new Gene(
                originalGene.getCourse(),
                originalGene.getRoom(),
                originalGene.getDay(),
                originalGene.getSlot()
            );
            copy.setGene(i, copiedGene);
        }

        return copy;
    }

    public void randomMutation(Schedule schedule, double mutationRate, Room[] rooms) {
        Random random = new Random();
        Gene[] genes = schedule.getGenes();

        for (Gene gene : genes) {
            if (gene != null && random.nextDouble() < mutationRate) {
                int newDay = random.nextInt(ScheduleConfig.MAX_DAYS) + 1;
                int newSlot = random.nextInt(ScheduleConfig.MAX_SLOTS) + 1;
                Room newRoom = rooms[random.nextInt(rooms.length)];

                gene.setDay(newDay);
                gene.setSlot(newSlot);
                gene.setRoom(newRoom);
            }
        }
    }

    public void swapMutation(Schedule schedule, double mutationRate) {
        Random random = new Random();
        Gene[] genes = schedule.getGenes();

        if (genes.length < 2) return;

        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                int index2 = random.nextInt(genes.length);
                while (i == index2) {
                    index2 = random.nextInt(genes.length);
                }

                Gene g1 = genes[i];
                Gene g2 = genes[index2];

                if (g1 != null && g2 != null) {
                    int tempDay = g1.getDay();
                    int tempSlot = g1.getSlot();
                    Room tempRoom = g1.getRoom();

                    g1.setDay(g2.getDay());
                    g1.setSlot(g2.getSlot());
                    g1.setRoom(g2.getRoom());

                    g2.setDay(tempDay);
                    g2.setSlot(tempSlot);
                    g2.setRoom(tempRoom);
                }
            }
        }
    }

    public void mutate(Schedule schedule, double mutationRate, Room[] rooms, String mutationType) {
        if (mutationType.equalsIgnoreCase("Swap")) {
            swapMutation(schedule, mutationRate);
        } else {
            randomMutation(schedule, mutationRate, rooms);
        }
    }
}