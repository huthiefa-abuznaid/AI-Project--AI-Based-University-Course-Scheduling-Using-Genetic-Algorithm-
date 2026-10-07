package application;

public class Course {

    private String courseCode;     
    private String courseName;       
    private Instructor instructor;  
    private int enrolledStudents;  
    private boolean requiresLab;    

    public Course(String courseCode, String courseName, Instructor instructor, int enrolledStudents, boolean requiresLab) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructor = instructor;
        this.enrolledStudents = enrolledStudents;
        this.requiresLab = requiresLab;
    }

    public Course(String courseName) {
        this(courseName, courseName, null, 0, false);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public boolean isRequiresLab() {
        return requiresLab;
    }

    public void setRequiresLab(boolean requiresLab) {
        this.requiresLab = requiresLab;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}