package application;

import java.util.Locale;

public class Gene {

    private Course course;
    private Room room;
    private int day;
    private int slot;

    public Gene(Course course, Room room, int day, int slot) {
        this.course = course;
        this.room = room;
        this.day = day;
        this.slot = slot;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        String instructorName = (course.getInstructor() != null) ? course.getInstructor().getName() : "No Instructor";
        String roomName = (room != null) ? room.getRoomId() : "No Room";
        return String.format(Locale.US, "%s (%s) -> Room: %s, Day: %d, Slot: %d", 
                course.getCourseName(), instructorName, roomName, day, slot);
    }
}