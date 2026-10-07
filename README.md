# University Course Scheduling Using a Genetic Algorithm

A simple Java program that builds a university timetable automatically.
It uses a **Genetic Algorithm (GA)**, written from scratch, to find a schedule with no conflicts.

Built for the **Artificial Intelligence (COMP338)** course at Birzeit University.

## What It Does

The program gives each course an instructor, a room, a day, and a time.
It tries to follow these rules:

**Hard rules (must be followed):**
- An instructor cannot teach two courses at the same time
- A room cannot hold two courses at the same time
- A student group cannot have two courses at the same time
- The room must be big enough for the course
- Lab courses must be in a lab room
- The instructor must be available at that time

**Soft rules (nice to have):**
- Avoid very early or very late classes
- Avoid long gaps between classes for students
- Follow instructor time preferences
- Use fewer working days for each student group

## How the Genetic Algorithm Works

| Part | What we use |
|------|-------------|
| Chromosome | One full timetable |
| Gene | One course with its day, time, and room |
| Fitness | `1 / (1 + Penalty)` where `Penalty = 100 × hard + 10 × soft` |
| Selection | Tournament selection |
| Crossover | Two-point crossover |
| Mutation | Random change of day, time, or room |
| Elitism | The best timetables are kept in the next generation |
| Stop when | No hard violations, 500 generations, or no progress for 50 generations |

## Experiments

The project tests how these settings change the result:

- Population size (20, 50, 100, 200)
- Mutation rate (1%, 5%, 10%, 20%)
- Crossover rate (60%, 80%, 90%)
- Dataset size (10 to 50 courses)

Results are saved as CSV files, and a graph shows **Generation vs Best Fitness**.

## Project Files

| File | Purpose |
|------|---------|
| `Course.java` | Course data |
| `Instructor.java` | Instructor data and availability |
| `Room.java` | Room size and type |
| `Student.java` | Student group data |
| `Gene.java` | One course assignment |
| `Schedule.java` | One timetable (chromosome) and its fitness |
| `Population.java` | A group of timetables |
| `ScheduleConfig.java` | GA settings |
| `CSVWriter.java` | Saves results to CSV |
| `CourseSchedulingSystem.java` | Main program |

## How to Run

1. Install **Java (JDK 11 or newer)**.
2. Open the project in an IDE such as Eclipse or IntelliJ.
3. Run `CourseSchedulingSystem.java`.
4. The final timetable, the number of violations, and the best fitness are shown when it finishes.

## Example Output
