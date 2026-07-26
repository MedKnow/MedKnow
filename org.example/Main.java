package org.example;

import org.example.dao.StudentDao;
import org.example.dao.ScoreDao;
import org.example.service.StudentService;
import org.example.service.ScoreService;
import org.example.service.StatisticsService;
import org.example.ui.ConsoleUI;
import org.example.util.Constants;

public class Main {
    public static void main(String[] args) {
        if (!ConsoleUI.login(Constants.PASSWORD)) {
            System.out.println("登录失败，程序退出。");
            return;
        }
        StudentDao studentDao = new StudentDao();
        ScoreDao scoreDao = new ScoreDao();
        studentDao.loadStudents();
        scoreDao.loadScores();
        StudentService studentService = new StudentService(studentDao, scoreDao);
        ScoreService scoreService = new ScoreService(studentDao, scoreDao);
        StatisticsService statisticsService = new StatisticsService(studentDao, scoreDao);
        ConsoleUI ui = new ConsoleUI(studentService, scoreService, statisticsService);
        ui.run();
        studentDao.saveStudents();
        scoreDao.saveScores();
    }
}