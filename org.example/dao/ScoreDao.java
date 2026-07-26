package org.example.dao;

import org.example.model.Score;
import org.example.util.BackupUtil;
import org.example.util.Constants;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDao {
    private final List<Score> scores = new ArrayList<>();
    public List<Score> getScores() {
        return scores;
    }
    public void loadScores() {
        File file = new File(Constants.SCORE_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String studentId = parts[0].trim();
                    String course = parts[1].trim();
                    int score = Integer.parseInt(parts[2].trim());
                    scores.add(new Score(studentId, course, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("加载成绩数据失败: " + e.getMessage());
        }
    }
    public void saveScores() {
        BackupUtil.backupFile(Constants.SCORE_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.SCORE_FILE))) {
            for (Score s : scores) {
                pw.println(s.getStudentId() + "," + s.getCourse() + "," + s.getScore());
            }
        } catch (IOException e) {
            System.err.println("保存成绩数据失败: " + e.getMessage());
        }
    }
}