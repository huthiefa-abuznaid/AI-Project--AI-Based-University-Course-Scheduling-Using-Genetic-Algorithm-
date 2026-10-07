package application;

import java.util.Random;

public class Schedule {

    private Gene[] genes;
    private double fitness;
    private int hardViolations;
    private int softViolations;
    private int penalty;

    public Schedule(Course[] courses, Room[] rooms) {
        genes = new Gene[courses.length];
        Random random = new Random();

        for (int i = 0; i < courses.length; i++) {
            int randomDay = random.nextInt(ScheduleConfig.MAX_DAYS) + 1;
            int randomSlot = random.nextInt(ScheduleConfig.MAX_SLOTS) + 1;
            Room randomRoom = rooms[random.nextInt(rooms.length)];

            genes[i] = new Gene(courses[i], randomRoom, randomDay, randomSlot);
        }
    }

    public Schedule(int size) {
        genes = new Gene[size];
    }

    public Gene[] getGenes() {
        return genes;
    }

    public void setGene(int index, Gene gene) {
        genes[index] = gene;
    }

    public double getFitness() {
        return fitness;
    }

    public int getHardViolations() {
        return hardViolations;
    }

    public int getSoftViolations() {
        return softViolations;
    }

    public int getPenalty() {
        return penalty;
    }

    public void calculateFitness(Student[] students) {
        hardViolations = 0;
        softViolations = 0;

        // Reserve the same room for the different course on the same day and time 
        for (int i = 0; i < genes.length; i++) {
            for (int j = i + 1; j < genes.length; j++) {
                if (genes[i].getRoom().getRoomId().equals(genes[j].getRoom().getRoomId())
                        && genes[i].getDay() == genes[j].getDay()
                        && genes[i].getSlot() == genes[j].getSlot()) {
                    hardViolations++; 
                }
            }
        }
        
        // Reserve the same instructor the different course on the same day and time  
        for (int i = 0; i < genes.length; i++) {
            for (int j = i + 1; j < genes.length; j++) {
                Instructor inst1 = genes[i].getCourse().getInstructor();
                Instructor inst2 = genes[j].getCourse().getInstructor();

                if (inst1 != null && inst2 != null && inst1.getInstructorId().equals(inst2.getInstructorId())
                        && genes[i].getDay() == genes[j].getDay()
                        && genes[i].getSlot() == genes[j].getSlot()) {
                    hardViolations++; 
                }
            }
        }

        // search on student if found the course on the same time and day 
        for (int i = 0; i < students.length; i++) {
            Course[] studentCourses = students[i].getCourses();
            int courseCount = students[i].getCourseCount();

            for (int j = 0; j < courseCount; j++) {
                for (int k = j + 1; k < courseCount; k++) {
                    Gene g1 = findGene(studentCourses[j]);
                    Gene g2 = findGene(studentCourses[k]);

                    if (g1 != null && g2 != null && g1.getDay() == g2.getDay() && g1.getSlot() == g2.getSlot()) {
                        hardViolations++;
                    }
                }
            }
        }

        // if the number of student over the capacity for the room 
        for (int i = 0; i < genes.length; i++) {
            Course course = genes[i].getCourse();
            Room room = genes[i].getRoom();

            if (course.getEnrolledStudents() > room.getCapacity()) {
                hardViolations++; 
            }
        }

        // if the course has lab and on schedule not put lap 
        for (int i = 0; i < genes.length; i++) {
            Course course = genes[i].getCourse();
            Room room = genes[i].getRoom();

            if (course.isRequiresLab() && !room.isLab()) {
                hardViolations++; 
            }
        }
        
        // if the instructor not available on this time 
        for (int i = 0; i < genes.length; i++) {
            Instructor instructor = genes[i].getCourse().getInstructor();

            if (instructor != null && !instructor.isAvailable(genes[i].getDay(), genes[i].getSlot())) {
                hardViolations++;
            }
        }

        // if the time on 8:00 to 9:00
        for (int i = 0; i < genes.length; i++) {
            if (genes[i].getSlot() == 1) {
                softViolations++;
            }
        }

        // if the time on the last 
        for (int i = 0; i < genes.length; i++) {
            if (genes[i].getSlot() == ScheduleConfig.MAX_SLOTS) {
                softViolations++;
            }
        }

        // minumum days 
        int maxDay = 0;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i].getDay() > maxDay) {
                maxDay = genes[i].getDay();
            }
        }
        softViolations += maxDay;

        int weightHard = 100;
        int weightSoft = 10;

        this.penalty = (weightHard * hardViolations) + (weightSoft * softViolations);

        this.fitness = 1.0 / (1.0 + this.penalty);
    }

    private Gene findGene(Course course) {
        if (course == null) return null;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] != null && genes[i].getCourse() != null && 
                genes[i].getCourse().getCourseName().equals(course.getCourseName())) {
                return genes[i];
            }
        }
        return null;
    }

    public void printSchedule() {
        for (int i = 0; i < genes.length; i++) {
            System.out.println(genes[i]);
        }
    }
}