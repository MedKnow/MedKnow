package org.example.service;

import org.example.dao.StudentDao;
import org.example.dao.ScoreDao;
import org.example.model.Score;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScoreService {
    private final StudentDao studentDao;
    private final ScoreDao scoreDao;
    public ScoreService(StudentDao studentDao, ScoreDao scoreDao) {
        this.studentDao = studentDao;
        this.scoreDao = scoreDao;
    }
    public void addScore(String studentId, String course, int score) {
        if (!studentDao.getStudents().containsKey(studentId)) {
            throw new IllegalArgumentException("学号不存在，请先添加学生");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("分数必须在0-100之间");
        }
        boolean exists = scoreDao.getScores().stream()
                .anyMatch(s -> s.getStudentId().equals(studentId) && s.getCourse().equals(course));
        if (exists) {
            throw new IllegalArgumentException("该学生已有此课程的成绩，请使用修改功能");
        }
        scoreDao.getScores().add(new Score(studentId, course, score));
    }
    public void modifyScore(String studentId, String course, int newScore) {
        if (!studentDao.getStudents().containsKey(studentId)) {
            throw new IllegalArgumentException("学号不存在");
        }
        if (newScore < 0 || newScore > 100) {
            throw new IllegalArgumentException("分数必须在0-100之间");
        }
        Optional<Score> opt = scoreDao.getScores().stream()
                .filter(s -> s.getStudentId().equals(studentId) && s.getCourse().equals(course))
                .findFirst();
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("未找到该学生此课程的成绩");
        }
        opt.get().setScore(newScore);
    }
    public void deleteScore(String studentId, String course) {
        boolean removed = scoreDao.getScores().removeIf(s ->
                s.getStudentId().equals(studentId) && s.getCourse().equals(course));
        if (!removed) {
            throw new IllegalArgumentException("未找到对应成绩记录");
        }
    }
    public int deleteCourse(String course) {
        int before = scoreDao.getScores().size();
        scoreDao.getScores().removeIf(s -> s.getCourse().equals(course));
        return before - scoreDao.getScores().size(); // 返回删除条数
    }
    public List<Score> getStudentScores(String studentId) {
        return scoreDao.getScores().stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
    public double getAverage(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }
        return scores.stream().mapToInt(Score::getScore).average().orElse(0.0);
    }
}