package org.example.service;

import org.example.dao.StudentDao;
import org.example.dao.ScoreDao;
import org.example.model.Student;
import org.example.model.Score;
import org.example.util.GradeUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {
    private final StudentDao studentDao;
    private final ScoreDao scoreDao;
    public StatisticsService(StudentDao studentDao, ScoreDao scoreDao) {
        this.studentDao = studentDao;
        this.scoreDao = scoreDao;
    }
    public List<String[]> getAllStudentsAverage(boolean sortDesc) {
        Map<String, Double> avgMap = new HashMap<>();
        for (Student stu : studentDao.getStudents().values()) {
            List<Score> stuScores = scoreDao.getScores().stream()
                    .filter(s -> s.getStudentId().equals(stu.getId()))
                    .collect(Collectors.toList());
            double avg = stuScores.isEmpty() ? 0.0 :
                    stuScores.stream().mapToInt(Score::getScore).average().orElse(0.0);
            avgMap.put(stu.getId(), avg);
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(avgMap.entrySet());
        if (sortDesc) {
            entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        }
        List<String[]> result = new ArrayList<>();
        for (Map.Entry<String, Double> e : entries) {
            Student stu = studentDao.getStudents().get(e.getKey());
            result.add(new String[]{stu.getId(), stu.getName(), stu.getClassName(),
                    String.format("%.2f", e.getValue())});
        }
        return result;
    }
    public double getClassAvgByCourse(String className, String course) {
        List<Student> classStudents = studentDao.getStudents().values().stream()
                .filter(s -> s.getClassName().equals(className))
                .collect(Collectors.toList());
        if (classStudents.isEmpty()) {
            return -1.0;
        }
        double total = 0;
        int count = 0;
        for (Student stu : classStudents) {
            Optional<Score> opt = scoreDao.getScores().stream()
                    .filter(s -> s.getStudentId().equals(stu.getId()) && s.getCourse().equals(course))
                    .findFirst();
            if (opt.isPresent()) {
                total += opt.get().getScore();
                count++;
            }
        }
        return count == 0 ? -1.0 : total / count;
    }
    public void exportToFile(String exportFileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(exportFileName))) {
            pw.println("学号\t姓名\t班级\t课程\t分数\t等级");
            for (Score s : scoreDao.getScores()) {
                Student stu = studentDao.getStudents().get(s.getStudentId());
                if (stu != null) {
                    pw.printf("%s\t%s\t%s\t%s\t%d\t%s\n",
                            stu.getId(), stu.getName(), stu.getClassName(),
                            s.getCourse(), s.getScore(), GradeUtil.toGrade(s.getScore()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("导出成绩单失败: " + e.getMessage());
        }
    }
    public List<String[]> rankByCourse(String course) {
        List<Score> sorted = scoreDao.getScores().stream()
                .filter(s -> s.getCourse().equals(course))
                .sorted(Comparator.comparingInt(Score::getScore).reversed())
                .collect(Collectors.toList());
        List<String[]> result = new ArrayList<>();
        int rank = 1;
        for (Score s : sorted) {
            Student stu = studentDao.getStudents().get(s.getStudentId());
            String name = (stu != null) ? stu.getName() : "未知";
            result.add(new String[]{
                    String.valueOf(rank++),
                    s.getStudentId(),
                    name,
                    String.valueOf(s.getScore()),
                    GradeUtil.toGrade(s.getScore())
            });
        }
        return result;
    }
}