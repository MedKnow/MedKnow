package org.example.ui;

import org.example.model.Student;
import org.example.model.Score;
import org.example.service.StudentService;
import org.example.service.ScoreService;
import org.example.service.StatisticsService;
import org.example.util.GradeUtil;
import org.example.util.Constants;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final StudentService studentService;
    private final ScoreService scoreService;
    private final StatisticsService statisticsService;
    private final Scanner scanner;
    public ConsoleUI(StudentService studentService, ScoreService scoreService,
                     StatisticsService statisticsService) {
        this.studentService = studentService;
        this.scoreService = scoreService;
        this.statisticsService = statisticsService;
        this.scanner = new Scanner(System.in);
    }
    public static boolean login(String correctPwd) {
        Scanner sc = new Scanner(System.in);
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("请输入密码: ");
            String input = sc.nextLine().trim();
            if (correctPwd.equals(input)) {
                System.out.println("登录成功！\n");
                return true;
            }
            attempts--;
            System.out.println("密码错误，剩余尝试次数: " + attempts);
        }
        return false;
    }
    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": handleAddStudent(); break;
                case "2": handleDeleteStudent(); break;
                case "3": handleModifyStudentInfo(); break;
                case "4": handleQueryStudent(); break;
                case "5": handleAddScore(); break;
                case "6": handleModifyScore(); break;
                case "7": handleDeleteScore(); break;
                case "8": handleViewStudentScores(); break;
                case "9": handleShowAllAverage(); break;
                case "10": handleClassAvgByCourse(); break;
                case "11": handleModifyStudentId(); break;
                case "12": handleDeleteCourse(); break;
                case "13": handleExportToFile(); break;
                case "14": handleRankByCourse(); break;
                case "0":
                    System.out.println("程序退出，再见！");
                    return;
                default:
                    System.out.println("无效选项，请重新输入。");
            }
        }
    }
    private void printMenu() {
        System.out.println("\n========== 学生成绩管理系统 ==========");
        System.out.println("1. 添加学生");
        System.out.println("2. 删除学生");
        System.out.println("3. 修改学生信息");
        System.out.println("4. 查询学生（学号/模糊姓名）");
        System.out.println("5. 录入成绩");
        System.out.println("6. 修改成绩");
        System.out.println("7. 删除成绩");
        System.out.println("8. 查看学生成绩及平均分");
        System.out.println("9. 显示所有学生成绩单（可按平均分排序）");
        System.out.println("10. 按班级统计某课程平均分");
        System.out.println("11. 修改学号（同步成绩）");
        System.out.println("12. 删除课程（删除所有学生该课成绩）");
        System.out.println("13. 导出成绩单到文件");
        System.out.println("14. 单科成绩排名");
        System.out.println("0. 退出系统");
        System.out.println("=======================================");
    }
    private void handleAddStudent() {
        System.out.print("请输入学号: ");
        String id = scanner.nextLine().trim();
        System.out.print("请输入姓名: ");
        String name = scanner.nextLine().trim();
        System.out.print("请输入班级: ");
        String className = scanner.nextLine().trim();
        try {
            studentService.addStudent(id, name, className);
            System.out.println("学生添加成功。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleDeleteStudent() {
        System.out.print("请输入要删除的学号: ");
        String id = scanner.nextLine().trim();
        try {
            studentService.deleteStudent(id);
            System.out.println("学生及其成绩已删除。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleModifyStudentInfo() {
        System.out.print("请输入要修改的学号: ");
        String id = scanner.nextLine().trim();
        Student stu = studentService.queryById(id);
        if (stu == null) {
            System.out.println("错误: 学号不存在。");
            return;
        }
        System.out.print("请输入新姓名（直接回车保留原值[" + stu.getName() + "]）: ");
        String newName = scanner.nextLine().trim();
        System.out.print("请输入新班级（直接回车保留原值[" + stu.getClassName() + "]）: ");
        String newClass = scanner.nextLine().trim();
        try {
            studentService.modifyStudentInfo(id, newName, newClass);
            System.out.println("学生信息修改成功。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleQueryStudent() {
        System.out.print("请输入学号或姓名（支持模糊）: ");
        String keyword = scanner.nextLine().trim();
        Student stu = studentService.queryById(keyword);
        if (stu != null) {
            System.out.printf("学号: %s, 姓名: %s, 班级: %s\n", stu.getId(), stu.getName(), stu.getClassName());
            displayStudentScores(stu.getId());
            return;
        }
        List<Student> matched = studentService.fuzzyQueryByName(keyword);
        if (matched.isEmpty()) {
            System.out.println("未找到匹配的学生。");
        } else {
            System.out.println("找到 " + matched.size() + " 个学生:");
            for (Student s : matched) {
                System.out.printf("学号: %s, 姓名: %s, 班级: %s\n", s.getId(), s.getName(), s.getClassName());
            }
        }
    }
    private void handleAddScore() {
        System.out.print("请输入学号: ");
        String id = scanner.nextLine().trim();
        System.out.print("请输入课程名称: ");
        String course = scanner.nextLine().trim();
        System.out.print("请输入分数(0-100): ");
        String scoreStr = scanner.nextLine().trim();
        int score;
        try {
            score = Integer.parseInt(scoreStr);
        } catch (NumberFormatException e) {
            System.out.println("错误: 请输入有效的整数分数。");
            return;
        }
        try {
            scoreService.addScore(id, course, score);
            System.out.println("成绩录入成功。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleModifyScore() {
        System.out.print("请输入学号: ");
        String id = scanner.nextLine().trim();
        System.out.print("请输入课程名称: ");
        String course = scanner.nextLine().trim();
        System.out.print("请输入新分数(0-100): ");
        String scoreStr = scanner.nextLine().trim();
        int newScore;
        try {
            newScore = Integer.parseInt(scoreStr);
        } catch (NumberFormatException e) {
            System.out.println("错误: 请输入有效的整数分数。");
            return;
        }
        try {
            scoreService.modifyScore(id, course, newScore);
            System.out.println("成绩修改成功。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleDeleteScore() {
        System.out.print("请输入学号: ");
        String id = scanner.nextLine().trim();
        System.out.print("请输入课程名称: ");
        String course = scanner.nextLine().trim();
        try {
            scoreService.deleteScore(id, course);
            System.out.println("成绩删除成功。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleViewStudentScores() {
        System.out.print("请输入学号: ");
        String id = scanner.nextLine().trim();
        if (studentService.queryById(id) == null) {
            System.out.println("错误: 学号不存在。");
            return;
        }
        displayStudentScores(id);
    }
    private void displayStudentScores(String studentId) {
        List<Score> scores = scoreService.getStudentScores(studentId);
        if (scores.isEmpty()) {
            System.out.println("该学生暂无成绩记录。");
            return;
        }
        System.out.println("课程名称\t分数\t等级");
        for (Score s : scores) {
            System.out.printf("%s\t\t%d\t\t%s\n", s.getCourse(), s.getScore(), GradeUtil.toGrade(s.getScore()));
        }
        double avg = scoreService.getAverage(scores);
        System.out.printf("平均分: %.2f\n", avg);
    }
    private void handleShowAllAverage() {
        System.out.print("是否按平均分从高到低排序？(y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        boolean sortDesc = choice.equals("y");
        List<String[]> list = statisticsService.getAllStudentsAverage(sortDesc);
        System.out.println("学号\t姓名\t班级\t平均分");
        for (String[] row : list) {
            System.out.println(row[0] + "\t" + row[1] + "\t" + row[2] + "\t" + row[3]);
        }
    }
    private void handleClassAvgByCourse() {
        System.out.print("请输入班级名称: ");
        String className = scanner.nextLine().trim();
        System.out.print("请输入课程名称: ");
        String course = scanner.nextLine().trim();
        double avg = statisticsService.getClassAvgByCourse(className, course);
        if (avg < 0) {
            System.out.println("该班级没有学生或没有该课程的成绩记录。");
        } else {
            System.out.printf("班级 %s 的 %s 课程平均分: %.2f\n", className, course, avg);
        }
    }
    private void handleModifyStudentId() {
        System.out.print("请输入原学号: ");
        String oldId = scanner.nextLine().trim();
        System.out.print("请输入新学号: ");
        String newId = scanner.nextLine().trim();
        try {
            studentService.modifyStudentId(oldId, newId);
            System.out.println("学号修改成功，成绩记录已同步。");
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    private void handleDeleteCourse() {
        System.out.print("请输入要删除的课程名称: ");
        String course = scanner.nextLine().trim();
        int removedCount = scoreService.deleteCourse(course);
        System.out.println("已删除 " + removedCount + " 条成绩记录。");
    }
    private void handleExportToFile() {
        try {
            statisticsService.exportToFile(Constants.EXPORT_FILE);
            System.out.println("成绩单已导出至 " + Constants.EXPORT_FILE);
        } catch (RuntimeException e) {
            System.out.println("导出失败: " + e.getMessage());
        }
    }
    private void handleRankByCourse() {
        System.out.print("请输入课程名称: ");
        String course = scanner.nextLine().trim();
        List<String[]> ranking = statisticsService.rankByCourse(course);
        if (ranking.isEmpty()) {
            System.out.println("没有该课程的成绩记录。");
            return;
        }
        System.out.println("排名\t学号\t姓名\t分数\t等级");
        for (String[] row : ranking) {
            System.out.println(row[0] + "\t" + row[1] + "\t" + row[2] + "\t" + row[3] + "\t" + row[4]);
        }
    }
}