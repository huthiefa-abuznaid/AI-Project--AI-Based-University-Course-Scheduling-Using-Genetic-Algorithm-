package application;

public class Student {

    private String studentName;
    private Course[] courses;
    private int courseCount;

    public Student(String studentName, int maxCourses) {
        this.studentName = studentName;
        this.courses = new Course[maxCourses];
        this.courseCount = 0;
    }

        public void addCourse(Course course) {
        if (courseCount < courses.length) {
            courses[courseCount] = course;
            courseCount++;
        } else {
            System.err.println("Warning: Maximum course limit reached for student " + studentName);
        }
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Course[] getCourses() {
        return courses;
    }

    public int getCourseCount() {
        return courseCount;
    }

    @Override
    public String toString() {
        return studentName + " (Enrolled Courses: " + courseCount + ")";
    }
}