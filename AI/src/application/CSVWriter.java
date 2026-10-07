package application;

import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

public class CSVWriter {

    
    public static void saveResultsTable(String fileName, String header, List<String> rows) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(header + "\n");
            for (String row : rows) {
                writer.write(row + "\n");
            }
            System.out.println("Saved experiment results to: " + fileName);
        } catch (Exception e) {
            System.err.println("Error writing results table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveFitnessHistory(String fileName, double [] fitnessHistory) {
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write("Generation,Fitness\n");

            for (int i = 0; i < fitnessHistory.length; i++) {
                writer.write(i + "," + fitnessHistory[i] + "\n");
            }

            System.out.println("Saved Fitness History to: " + fileName);

        } catch (Exception e) {
            System.err.println("Error writing fitness history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveScheduleToCSV(String fileName, Schedule schedule) {
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write("Course_Code,Course_Name,Instructor,Room,Day,Slot\n");

            for (Gene gene : schedule.getGenes()) {
                Course course = gene.getCourse();
                Instructor instructor = course.getInstructor();
                Room room = gene.getRoom();

                String line = String.format(Locale.US, "%s,%s,%s,%s,%d,%d\n",
                        course.getCourseCode(),
                        course.getCourseName(),
                        (instructor != null ? instructor.getName() : "N/A"),
                        (room != null ? room.getRoomId() : "N/A"),
                        gene.getDay(),
                        gene.getSlot()
                );
                writer.write(line);
            }

            System.out.println("Saved Final Schedule to: " + fileName);

        } catch (Exception e) {
            System.err.println("Error writing schedule CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}