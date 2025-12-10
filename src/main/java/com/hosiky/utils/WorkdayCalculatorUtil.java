package com.hosiky.utils;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Component
public class WorkdayCalculatorUtil {

    /**
     * 计算两个日期之间的工作日天数（基础版，仅排除周末）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 工作日天数
     */
    public static long calculateWorkdays(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        long workdays = 0;
        LocalDate currentDate = startDate;

        // 遍历从开始日期到结束日期的每一天
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            // 判断是否为周一至周五
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workdays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return workdays;
    }

    /**
     * 计算两个日期之间的非工作日天数（基础版，仅包含周末）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 非工作日天数
     */
    public static long calculateNonWorkdays(LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // 总天数包含起止日
        long workdays = calculateWorkdays(startDate, endDate);
        return totalDays - workdays;
    }

    // ---------------- 增强版：考虑节假日和调休 ----------------
    // 这里使用静态Set存储，实际项目中可从数据库或配置文件中加载
    private static Set<LocalDate> holidays = new HashSet<>(); // 存储法定节假日
    private static Set<LocalDate> specialWorkdays = new HashSet<>(); // 存储调休补班日（周末上班）

    /**
     * （示例）初始化一些节假日和补班日，实际应用需从外部数据源获取
     */
    static {
        // 示例：添加一些法定节假日（假设这些日期是节假日）
        holidays.add(LocalDate.of(2025, 1, 1)); // 元旦
        holidays.add(LocalDate.of(2025, 1, 28)); // 春节假期的某一天
        holidays.add(LocalDate.of(2025, 4, 5)); // 清明节
        holidays.add(LocalDate.of(2025, 5, 1)); // 劳动节

        // 示例：添加一些调休补班日（假设这些周末需要上班）
        specialWorkdays.add(LocalDate.of(2025, 2, 1)); // 春节前的补班
    }

    /**
     * 判断某天是否为工作日（增强版）
     * @param date 待判断的日期
     * @return true - 工作日, false - 非工作日
     */
    public static boolean isWorkday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

        // 核心逻辑：
        // 1. 如果在补班日列表中，即使周末也是工作日
        if (specialWorkdays.contains(date)) {
            return true;
        }
        // 2. 如果在节假日列表中，即使周一到周五也是非工作日
        if (holidays.contains(date)) {
            return false;
        }
        // 3. 最后，非周末的日子就是工作日
        return !isWeekend;
    }

    /**
     * 计算两个日期之间的工作日天数（增强版，考虑节假日和调休）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 工作日天数
     */
    public static long calculateWorkdaysAdvanced(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        long workdays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (isWorkday(currentDate)) {
                workdays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return workdays;
    }

    /**
     * 计算两个日期之间的非工作日天数（增强版，考虑节假日和调休）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 非工作日天数
     */
    public static long calculateNonWorkdaysAdvanced(LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long workdays = calculateWorkdaysAdvanced(startDate, endDate);
        return totalDays - workdays;
    }

    // 提供一个方法来更新节假日和补班日数据（例如从数据库加载后更新）
    public static void updateHolidayData(Set<LocalDate> newHolidays, Set<LocalDate> newSpecialWorkdays) {
        holidays = new HashSet<>(newHolidays);
        specialWorkdays = new HashSet<>(newSpecialWorkdays);
    }

//    public static void main(String[] args) {
//        // 测试示例
//        LocalDate start = LocalDate.of(2025, 1, 1);
//        LocalDate end = LocalDate.of(2025, 1, 7); // 测试1月1日到1月7日
//
//        System.out.println("基础版计算（仅考虑周末）:");
//        System.out.println("工作日: " + calculateWorkdays(start, end));
//        System.out.println("非工作日: " + calculateNonWorkdays(start, end));
//
//        System.out.println("\n增强版计算（考虑节假日和调休）:");
//        System.out.println("工作日: " + calculateWorkdaysAdvanced(start, end));
//        System.out.println("非工作日: " + calculateNonWorkdaysAdvanced(start, end));
//    }
}