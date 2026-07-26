package org.example.dao;

import org.example.model.Student;
import org.example.util.BackupUtil;
import org.example.util.Constants;
import java.io.*;
import java.util.*;

public class StudentDao {
    private Map<String, Student> students = new LinkedHashMap<>();
    public Map<String, Student> getStudents() { return students; }
    public void loadStudents() {
        File file = new File(Constants.STUDENT_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    students.put(parts[0].trim(),
                            new Student(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("加载学生数据失败: " + e.getMessage());
        }
    }
    public void saveStudents() {
        BackupUtil.backupFile(Constants.STUDENT_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.STUDENT_FILE))) {
            for (Student stu : students.values()) {
                pw.println(stu.getId() + "," + stu.getName() + "," + stu.getClassName());
            }
        } catch (IOException e) {
            System.err.println("保存学生数据失败: " + e.getMessage());
        }
    }
}