package org.example.model;

public class Score {
    private String studentId;
    private String course;
    private int score;
    public Score(String studentId, String course, int score) {
        this.studentId = studentId;
        this.course = course;
        this.score = score;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    @Override
    public String toString() {
        return String.format("Score{studentId='%s', course='%s', score=%d}", studentId, course, score);
    }
}