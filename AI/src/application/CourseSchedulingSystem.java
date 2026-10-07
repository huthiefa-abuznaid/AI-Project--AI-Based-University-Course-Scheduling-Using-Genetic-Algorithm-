package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CourseSchedulingSystem {

    
    public static class GAResult {
        double[] fitnessHistory;
        int finalHardViolations;
        int finalSoftViolations;
        double bestFitness;
        long runtimeMs;
    }

    private static final List<String> LAB_COURSES = Arrays.asList("ComputerGraphics", "WebProgramming");

    private static boolean isLabCourse(String courseName) {
        return LAB_COURSES.contains(courseName) || courseName.toLowerCase().contains("lab");
    }

    public static void main(String[] args) {
        String csvFile = "Student_Courses.csv";

        try {
         

            Room[] rooms = loadRooms();
            Instructor[] instructors = loadInstructors();

            List<Course> allCourses = new ArrayList<>();
            List<Student> allStudents = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean firstRow = true;

                while ((line = br.readLine()) != null) {
                    if (firstRow) {
                        firstRow = false; 
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 2) {
                        String studentName = data[0].trim();
                        Student student = findStudent(allStudents, studentName);

                        if (student == null) {
                            student = new Student(studentName, 20);
                            allStudents.add(student);
                        }

                        for (int i = 1; i < data.length; i++) {
                            String courseName = data[i].trim();
                            if (courseName.isEmpty()) continue;

                            Course course = findCourse(allCourses, courseName);

                            if (course == null) {
                                Instructor assignedInstructor = instructors[allCourses.size() % instructors.length];
                                boolean requiresLab = isLabCourse(courseName);
                                course = new Course(courseName, courseName, assignedInstructor, 35, requiresLab);
                                allCourses.add(course);
                            }
                            student.addCourse(course);
                        }
                    }
                }
            }

            Course[] finalCourses = allCourses.toArray(new Course[0]);
            Student[] finalStudents = allStudents.toArray(new Student[0]);

            System.out.println("\nTOTAL COURSES = " + finalCourses.length);
            System.out.println("TOTAL STUDENTS = " + finalStudents.length);

            int populationSize = 50;
            int generations = 100;
            double mutationRate = 0.1;
            double crossoverRate = 0.8;

            Population population = new Population(populationSize, finalCourses, rooms);
            double[] fitnessHistory = new double[generations];

            for (int generation = 0; generation < generations; generation++) {
                population.calculatePopulationFitness(finalStudents);
                Schedule best = population.getBestSchedule();
                fitnessHistory[generation] = best.getFitness();
                System.out.println("Generation " + generation + " Best Fitness = " + best.getFitness());

                Population newPopulation = new Population(populationSize);

                Schedule elite = population.copySchedule(best);
                newPopulation.setSchedule(0, elite);

                for (int i = 1; i < populationSize; i++) {
                    Schedule parent1 = population.tournamentSelection();
                    Schedule parent2 = population.tournamentSelection();
                    Schedule child = population.crossover(parent1, parent2, crossoverRate, "OnePoint");

                    population.mutate(child, mutationRate, rooms, "Random");
                    child.calculateFitness(finalStudents);
                    newPopulation.setSchedule(i, child);
                }

                population = newPopulation;

                if (best.getFitness() >= 0.09) {
                    System.out.println("\nOptimal solution found.");
                    break;
                }
            }
            System.out.println("\n _______________________________________________");

       
            int experimentGenerations = 50; 

            experimentPopulationSize(finalCourses, finalStudents, rooms, experimentGenerations);
            System.out.println("\n _______________________________________________");

            experimentMutationRate(finalCourses, finalStudents, rooms, populationSize, experimentGenerations);
            System.out.println("\n _______________________________________________");

            experimentCrossoverRate(finalCourses, finalStudents, rooms, populationSize, experimentGenerations);
            System.out.println("\n _______________________________________________");

            experimentDatasetSize(finalCourses, finalStudents, rooms, populationSize, experimentGenerations);
            System.out.println("\n _______________________________________________");

            // Advanced Requirement A Experiment Compare Selection Methods
            System.out.println("\n Running Experiment A: Selection Comparison");
            
            double[] tournamentHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "OnePoint", "Random");
            CSVWriter.saveFitnessHistory("selection_tournament.csv", tournamentHistory);

            double[] rouletteHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Roulette", "OnePoint", "Random");
            CSVWriter.saveFitnessHistory("selection_roulette.csv", rouletteHistory);
            System.out.println("\n _______________________________________________");
            // Advanced Requirement B Experiment Compare Crossover Methods
            System.out.println("\n Running Experiment B: Crossover Comparison ");

            double[] onePointHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "OnePoint", "Random");
            CSVWriter.saveFitnessHistory("crossover_onepoint.csv", onePointHistory);

            double[] twoPointHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "TwoPoint", "Random");
            CSVWriter.saveFitnessHistory("crossover_twopoint.csv", twoPointHistory);
            System.out.println("\n _______________________________________________");
            // Advanced Requirement C Experiment Compare Mutation Strategies
            System.out.println("\n Running Experiment C: Mutation Comparison ");

            double[] randomMutHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "OnePoint", "Random");
            CSVWriter.saveFitnessHistory("mutation_random.csv", randomMutHistory);

            double[] swapMutHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "OnePoint", "Swap");
            CSVWriter.saveFitnessHistory("mutation_swap.csv", swapMutHistory);
            System.out.println("\n _______________________________________________");
            // Advanced Requirement D Experiment Genetic Algorithm vs. Hill Climbing
            System.out.println("\n Running Experiment D: GA vs Hill Climbing ");

            long gaStartTime = System.currentTimeMillis();
            double[] gaHistory = runGA(finalCourses, finalStudents, rooms, populationSize, generations, mutationRate, crossoverRate, "Tournament", "OnePoint", "Random");
            long gaEndTime = System.currentTimeMillis();
            long gaRuntime = gaEndTime - gaStartTime;

            CSVWriter.saveFitnessHistory("ga_fitness.csv", gaHistory);

            int hcIterations = populationSize * generations;
            long hcStartTime = System.currentTimeMillis();
            double[] hcHistory = HillClimbing.runHillClimbing(finalCourses, finalStudents, rooms, hcIterations);
            long hcEndTime = System.currentTimeMillis();
            long hcRuntime = hcEndTime - hcStartTime;

            CSVWriter.saveFitnessHistory("hill_climbing_fitness.csv", hcHistory);

            double gaBestFitness = gaHistory[gaHistory.length - 1];
            double hcBestFitness = hcHistory[hcHistory.length - 1];
            System.out.println("\n ----------------------------------------------------");
            System.out.println("            EXPERIMENT D COMPARISON RESULTS            ");
            System.out.println("\n ------------------------------------------------------");
            System.out.printf("%-20s | %-15s | %-15s%n", "Metric", "Genetic Algorithm", "Hill Climbing");
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-20s | %-15d | %-15d%n", "Runtime (ms)", gaRuntime, hcRuntime);
            System.out.printf("%-20s | %-15.6f | %-15.6f%n", "Final Best Fitness", gaBestFitness, hcBestFitness);
            System.out.printf("%-20s | %-15.2f | %-15.2f%n", "Total Penalty", ((1 / gaBestFitness) - 1), ((1 / hcBestFitness) - 1));
            System.out.println("\n _______________________________________________");
            population.calculatePopulationFitness(finalStudents);
            Schedule finalBest = population.getBestSchedule();

            analyzeFinalSchedule(finalBest, finalStudents);

            System.out.println("\n--- FINAL BEST COURSE SCHEDULE ---");
            System.out.println("Fitness = " + finalBest.getFitness());
            System.out.println();
            finalBest.printSchedule();

            CSVWriter.saveScheduleToCSV("Final_Course_Schedule.csv", finalBest);
            CSVWriter.saveFitnessHistory("Fitness_History.csv", fitnessHistory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Room[] loadRooms() {
        return new Room[] {
            new Room("R101", 50, false),
            new Room("R102", 40, false),
            new Room("R103", 35, false),
            new Room("R104", 25, false),
            new Room("LAB1", 40, true),
            new Room("LAB2", 40, true)
        };
    }

    public static Instructor[] loadInstructors() {
        Instructor[] instructors = new Instructor[] {
            new Instructor("I01", "Dr. Ahmad", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I02", "Dr. Sara", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I03", "Dr. Omar", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I04", "Prof. Mahmoud", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I05", "Dr. Mona", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I06", "Dr. Yousef", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I07", "Dr. Lina", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I08", "Dr. Khaled", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I09", "Dr. Huda", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS),
            new Instructor("I10", "Dr. Rania", ScheduleConfig.MAX_DAYS, ScheduleConfig.MAX_SLOTS)
        };

        instructors[0].setAvailability(2, 3, false);
        return instructors;
    }

    public static Course findCourse(List<Course> courses, String name) {
        for (Course c : courses) {
            if (c.getCourseName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public static Student findStudent(List<Student> students, String name) {
        for (Student s : students) {
            if (s.getStudentName().equals(name)) {
                return s;
            }
        }
        return null;
    }


    public static GAResult runGAForExperiment(Course[] courses, Student[] students, Room[] rooms, int populationSize, int generations, double mutationRate, double crossoverRate, String selectionType, String crossoverType, String mutationType) {
        long startTime = System.currentTimeMillis();

        Population population = new Population(populationSize, courses, rooms);
        double[] fitnessHistory = new double[generations];
        Schedule best = null;

        for (int generation = 0; generation < generations; generation++) {
            population.calculatePopulationFitness(students);
            best = population.getBestSchedule();
            fitnessHistory[generation] = best.getFitness();

            Population newPopulation = new Population(populationSize);
            Schedule elite = population.copySchedule(best);
            newPopulation.setSchedule(0, elite);

            for (int i = 1; i < populationSize; i++) {
                Schedule parent1, parent2;
                if (selectionType.equalsIgnoreCase("Roulette")) {
                    parent1 = population.rouletteWheelSelection();
                    parent2 = population.rouletteWheelSelection();
                } else {
                    parent1 = population.tournamentSelection();
                    parent2 = population.tournamentSelection();
                }

                Schedule child = population.crossover(parent1, parent2, crossoverRate, crossoverType);
                population.mutate(child, mutationRate, rooms, mutationType);
                child.calculateFitness(students);
                newPopulation.setSchedule(i, child);
            }
            population = newPopulation;
        }

        long endTime = System.currentTimeMillis();

        GAResult result = new GAResult();
        result.fitnessHistory = fitnessHistory;
        result.finalHardViolations = (best != null) ? best.getHardViolations() : -1;
        result.finalSoftViolations = (best != null) ? best.getSoftViolations() : -1;
        result.bestFitness = (best != null) ? best.getFitness() : 0.0;
        result.runtimeMs = endTime - startTime;
        return result;
    }


    public static Student[] filterStudentsForCourses(Student[] allStudents, Course[] subsetCourses) {
        List<String> subsetNames = new ArrayList<>();
        for (Course c : subsetCourses) {
            subsetNames.add(c.getCourseName());
        }

        List<Student> filtered = new ArrayList<>();
        for (Student s : allStudents) {
            Student copy = new Student(s.getStudentName(), Math.max(1, subsetCourses.length));
            Course[] originalCourses = s.getCourses();
            for (int i = 0; i < s.getCourseCount(); i++) {
                Course c = originalCourses[i];
                if (c != null && subsetNames.contains(c.getCourseName())) {
                    for (Course subC : subsetCourses) {
                        if (subC.getCourseName().equals(c.getCourseName())) {
                            copy.addCourse(subC);
                            break;
                        }
                    }
                }
            }
            if (copy.getCourseCount() > 0) {
                filtered.add(copy);
            }
        }
        return filtered.toArray(new Student[0]);
    }



    public static void experimentPopulationSize(Course[] courses, Student[] students, Room[] rooms, int generations) {
        System.out.println("\n Running Required Experiment 1: Population Size ");
        int[] populationSizes = {20, 50, 100, 200};
        List<String> rows = new ArrayList<>();

        for (int popSize : populationSizes) {
            GAResult r = runGAForExperiment(courses, students, rooms, popSize, generations, 0.05, 0.8, "Tournament", "OnePoint", "Random");
            String row = String.format(Locale.US, "%d,5%%,%.5f,%d,%dms", popSize, r.bestFitness, r.finalHardViolations, r.runtimeMs);
            rows.add(row);
            System.out.printf(Locale.US, "Population=%-5d MutationRate=5%%  BestFitness=%.5f  HardViolations=%-3d Runtime=%dms%n",
                    popSize, r.bestFitness, r.finalHardViolations, r.runtimeMs);
        }
        CSVWriter.saveResultsTable("experiment1_population_size.csv", "Population,MutationRate,BestFitness,HardViolations,Runtime", rows);
    }

    public static void experimentMutationRate(Course[] courses, Student[] students, Room[] rooms, int populationSize, int generations) {
        System.out.println("\n Running Required Experiment 2: Mutation Rate ");
        double[] mutationRates = {0.01, 0.05, 0.10, 0.20};
        List<String> rows = new ArrayList<>();

        for (double mRate : mutationRates) {
            GAResult r = runGAForExperiment(courses, students, rooms, populationSize, generations, mRate, 0.8, "Tournament", "OnePoint", "Random");
            String row = String.format(Locale.US, "%.0f%%,%.5f,%d,%dms", mRate * 100, r.bestFitness, r.finalHardViolations, r.runtimeMs);
            rows.add(row);
            System.out.printf(Locale.US, "MutationRate=%-4.0f%% BestFitness=%.5f  HardViolations=%-3d Runtime=%dms%n",
                    mRate * 100, r.bestFitness, r.finalHardViolations, r.runtimeMs);
        }
        CSVWriter.saveResultsTable("experiment2_mutation_rate.csv", "MutationRate,BestFitness,HardViolations,Runtime", rows);
    }

    public static void experimentCrossoverRate(Course[] courses, Student[] students, Room[] rooms, int populationSize, int generations) {
        System.out.println("\n Running Required Experiment 3: Crossover Rate ");
        double[] crossoverRates = {0.60, 0.80, 0.90};
        List<String> rows = new ArrayList<>();

        for (double cRate : crossoverRates) {
            GAResult r = runGAForExperiment(courses, students, rooms, populationSize, generations, 0.05, cRate, "Tournament", "OnePoint", "Random");
            String row = String.format(Locale.US, "%.0f%%,%.5f,%d,%dms", cRate * 100, r.bestFitness, r.finalHardViolations, r.runtimeMs);
            rows.add(row);
            System.out.printf(Locale.US, "CrossoverRate=%-4.0f%% BestFitness=%.5f  HardViolations=%-3d Runtime=%dms%n",
                    cRate * 100, r.bestFitness, r.finalHardViolations, r.runtimeMs);
        }
        CSVWriter.saveResultsTable("experiment3_crossover_rate.csv", "CrossoverRate,BestFitness,HardViolations,Runtime", rows);
    }

    public static void experimentDatasetSize(Course[] allCourses, Student[] allStudents, Room[] rooms, int populationSize, int generations) {
        System.out.println("\n Running Required Experiment 4: Dataset Size ");
        int[] sizes = {10, 20, 30, 40, 50};
        List<String> rows = new ArrayList<>();

        for (int size : sizes) {
            if (size > allCourses.length) {
                System.out.println("Skipping dataset size " + size + " (only " + allCourses.length + " courses available).");
                continue;
            }
            Course[] subset = Arrays.copyOf(allCourses, size);
            Student[] subsetStudents = filterStudentsForCourses(allStudents, subset);

            GAResult r = runGAForExperiment(subset, subsetStudents, rooms, populationSize, generations, 0.05, 0.8, "Tournament", "OnePoint", "Random");
            String row = String.format(Locale.US, "%d,%.5f,%d,%dms", size, r.bestFitness, r.finalHardViolations, r.runtimeMs);
            rows.add(row);
            System.out.printf(Locale.US, "Courses=%-4d BestFitness=%.5f  HardViolations=%-3d Runtime=%dms%n",
                    size, r.bestFitness, r.finalHardViolations, r.runtimeMs);
        }
        CSVWriter.saveResultsTable("experiment4_dataset_size.csv", "DatasetSize,BestFitness,HardViolations,Runtime", rows);
    }

    public static double[] runGA(Course[] courses, Student[] students, Room[] rooms, int populationSize, int generations, double mutationRate, double crossoverRate, String selectionType, String crossoverType, String mutationType) {
        Population population = new Population(populationSize, courses, rooms);
        double[] fitnessHistory = new double[generations];

        for (int generation = 0; generation < generations; generation++) {
            population.calculatePopulationFitness(students);
            Schedule best = population.getBestSchedule();

            fitnessHistory[generation] = best.getFitness();
            Population newPopulation = new Population(populationSize);

            Schedule elite = population.copySchedule(best);
            newPopulation.setSchedule(0, elite);

            for (int i = 1; i < populationSize; i++) {
                Schedule parent1, parent2;

                if (selectionType.equalsIgnoreCase("Roulette")) {
                    parent1 = population.rouletteWheelSelection();
                    parent2 = population.rouletteWheelSelection();
                } else {
                    parent1 = population.tournamentSelection();
                    parent2 = population.tournamentSelection();
                }

                Schedule child = population.crossover(parent1, parent2, crossoverRate, crossoverType);
                population.mutate(child, mutationRate, rooms, mutationType);
                child.calculateFitness(students);
                newPopulation.setSchedule(i, child);
            }
            population = newPopulation;
        }
        return fitnessHistory;
    }

    public static void analyzeFinalSchedule(Schedule schedule, Student[] students) {
        int maxDay = 0;
        for (Gene g : schedule.getGenes()) {
            if (g != null && g.getDay() > maxDay) {
                maxDay = g.getDay();
            }
        }
        System.out.println("-----------------");
        System.out.println("\nSchedule Analysis");
        System.out.println("-----------------");
        System.out.println("Days Used: " + maxDay);

        int timeConflicts = 0;
        int roomConflicts = 0;

        for (Student student : students) {
            if (student == null) continue;
            Course[] courses = student.getCourses();
            for (int i = 0; i < student.getCourseCount(); i++) {
                for (int j = i + 1; j < student.getCourseCount(); j++) {
                    Gene g1 = findGene(schedule, courses[i]);
                    Gene g2 = findGene(schedule, courses[j]);

                    if (g1 != null && g2 != null && g1.getDay() == g2.getDay() && g1.getSlot() == g2.getSlot()) {
                        timeConflicts++;
                    }
                }
            }
        }

        Gene[] genes = schedule.getGenes();
        for (int i = 0; i < genes.length; i++) {
            for (int j = i + 1; j < genes.length; j++) {
                if (genes[i] != null && genes[j] != null &&
                    genes[i].getRoom().getRoomId().equals(genes[j].getRoom().getRoomId()) &&
                    genes[i].getDay() == genes[j].getDay() &&
                    genes[i].getSlot() == genes[j].getSlot()) {
                    roomConflicts++;
                }
            }
        }

        System.out.println("Student Time Conflicts: " + timeConflicts);
        System.out.println("Room Assignment Conflicts: " + roomConflicts);
    }

    public static Gene findGene(Schedule schedule, Course course) {
        if (course == null || schedule == null) return null;
        for (Gene g : schedule.getGenes()) {
            if (g != null && g.getCourse() != null && g.getCourse().getCourseName().equals(course.getCourseName())) {
                return g;
            }
        }
        return null;
    }
}