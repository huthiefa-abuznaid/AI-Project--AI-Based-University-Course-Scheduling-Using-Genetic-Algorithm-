package application;

public class Instructor {

    private String instructorId;
    private String name;
    private boolean[][] availability; 

    public Instructor(String instructorId, String name, int totalDays, int slotsPerDay) {
        this.instructorId = instructorId;
        this.name = name;
        this.availability = new boolean[totalDays + 1][slotsPerDay + 1];
        
        for (int d = 1; d <= totalDays; d++) {
            for (int s = 1; s <= slotsPerDay; s++) {
                this.availability[d][s] = true;
            }
        }
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean[][] getAvailability() {
        return availability;
    }

    public void setAvailability(int day, int slot, boolean isAvailable) {
        if (day >= 1 && day < availability.length && slot >= 1 && slot < availability[0].length) {
            availability[day][slot] = isAvailable;
        }
    }

    public boolean isAvailable(int day, int slot) {
        if (day >= 1 && day < availability.length && slot >= 1 && slot < availability[0].length) {
            return availability[day][slot];
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " (" + instructorId + ")";
    }
}