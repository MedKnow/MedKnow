package org.example.service;

import org.example.dao.StudentDao;
import org.example.dao.ScoreDao;
import org.example.model.Student;
import java.util.List;
import java.util.stream.Collectors;

public class StudentService {
    private final StudentDao studentDao;
    private final ScoreDao scoreDao;
    public StudentService(StudentDao studentDao, ScoreDao scoreDao) {
        this.studentDao = studentDao;
        this.scoreDao = scoreDao;
    }
    public void addStudent(String id, String name, String className) {
        if (studentDao.getStudents().containsKey(id)) {
            throw new IllegalArgumentException("学号已存在");
        }
        studentDao.getStudents().put(id, new Student(id, name, className));
    }
    public void deleteStudent(String id) {
        if (!studentDao.getStudents().containsKey(id)) {
            throw new IllegalArgumentException("学号不存在");
        }
        studentDao.getStudents().remove(id);
        scoreDao.getScores().removeIf(s -> s.getStudentId().equals(id));
    }
    public void modifyStudentInfo(String id, String newName, String newClass) {
        Student stu = studentDao.getStudents().get(id);
        if (stu == null) {
            throw new IllegalArgumentException("学号不存在");
        }
        if (newName != null && !newName.isEmpty()) {
            stu.setName(newName);
        }
        if (newClass != null && !newClass.isEmpty()) {
            stu.setClassName(newClass);
        }
    }
    public void modifyStudentId(String oldId, String newId) {
        if (!studentDao.getStudents().containsKey(oldId)) {
            throw new IllegalArgumentException("原学号不存在");
        }
        if (studentDao.getStudents().containsKey(newId)) {
            throw new IllegalArgumentException("新学号已存在");
        }
        Student stu = studentDao.getStudents().remove(oldId);
        stu.setId(newId);
        studentDao.getStudents().put(newId, stu);
        scoreDao.getScores().stream()
                .filter(s -> s.getStudentId().equals(oldId))
                .forEach(s -> s.setStudentId(newId));
    }
    public Student queryById(String id) {
        return studentDao.getStudents().get(id);
    }
    public List<Student> fuzzyQueryByName(String keyword) {
        return studentDao.getStudents().values().stream()
                .filter(s -> s.getName().contains(keyword))
                .collect(Collectors.toList());
    }
}