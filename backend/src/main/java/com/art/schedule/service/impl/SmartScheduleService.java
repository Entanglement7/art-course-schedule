package com.art.schedule.service.impl;

import com.art.schedule.dto.SmartScheduleRequest;
import com.art.schedule.dto.SmartScheduleResponse;
import com.art.schedule.entity.*;
import com.art.schedule.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class SmartScheduleService {

    private final ConflictDetectionService conflictDetectionService;
    private final ScheduleMapper scheduleMapper;
    private final ClazzMapper clazzMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;

    public SmartScheduleService(ConflictDetectionService conflictDetectionService,
                               ScheduleMapper scheduleMapper,
                               ClazzMapper clazzMapper,
                               CourseMapper courseMapper,
                               TeacherMapper teacherMapper) {
        this.conflictDetectionService = conflictDetectionService;
        this.scheduleMapper = scheduleMapper;
        this.clazzMapper = clazzMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
    }

    @Transactional
    public SmartScheduleResponse generateSchedule(SmartScheduleRequest request) {
        try {
            // 1. 验证教师
            Teacher teacher = teacherMapper.selectById(request.getTeacherId());
            if (teacher == null) {
                return SmartScheduleResponse.failure("教师不存在");
            }

            // 2. 创建或获取班级
            Clazz clazz = getOrCreateClass(request, null, teacher);
            if (clazz == null) {
                return SmartScheduleResponse.failure("班级不存在");
            }

            // 3. 从班级获取课程
            Course course = courseMapper.selectById(clazz.getCourseId());
            if (course == null) {
                return SmartScheduleResponse.failure("班级未关联课程，请先在班级管理中设置课程");
            }

            // 4. 生成可用时间槽（MVP: 只支持60分钟课程）
            List<TimeSlot> availableSlots = generateAvailableTimeSlots(course.getDuration());

            // 5. 选择最优时间槽
            List<TimeSlot> selectedSlots = selectBestTimeSlots(
                availableSlots,
                request.getSessionsPerWeek(),
                request.getTeacherId(),
                request.getClassroomId(),
                request.getStudentIds()
            );

            if (selectedSlots.size() < request.getSessionsPerWeek()) {
                return SmartScheduleResponse.failure(
                    String.format("无法找到足够的无冲突时间槽，只找到 %d 个，需要 %d 个",
                        selectedSlots.size(), request.getSessionsPerWeek())
                );
            }

            // 6. 删除该班级已有的排课记录，重新排课
            scheduleMapper.deleteByClassId(clazz.getId());

            // 7. 计算排课周数
            int weeks = calculateWeeks(request.getSchedulePeriod());

            // 8. 生成课表记录
            List<Schedule> schedules = createSchedules(
                clazz,
                clazz.getCourseId(),
                request.getClassroomId(),
                selectedSlots,
                LocalDate.parse(request.getStartDate()),
                weeks
            );

            // 9. 关联学生到班级
            associateStudentsToClass(clazz.getId(), request.getStudentIds());

            return SmartScheduleResponse.success(schedules);

        } catch (Exception e) {
            e.printStackTrace();
            return SmartScheduleResponse.failure("排课失败：" + e.getMessage());
        }
    }

    private Clazz getOrCreateClass(SmartScheduleRequest request, Course course, Teacher teacher) {
        if (request.getClassId() != null) {
            return clazzMapper.selectById(request.getClassId());
        }

        // 创建新班级
        Clazz clazz = new Clazz();
        clazz.setName(request.getClassName() != null ? request.getClassName() :
                     course.getName() + "班");
        clazz.setCategory(course.getCategory());
        clazz.setCourseName(course.getName());
        clazz.setTeacherId(request.getTeacherId());
        clazz.setStudentCount(request.getStudentIds().size());
        clazz.setSchedule("智能排课生成");
        clazz.setStartDate(LocalDate.parse(request.getStartDate()));
        clazzMapper.insert(clazz);
        return clazz;
    }

    private List<TimeSlot> generateAvailableTimeSlots(int courseDuration) {
        List<TimeSlot> slots = new ArrayList<>();

        // 定义时间段
        LocalTime[] startTimes = {
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)
        };

        // 周一到周五
        for (int day = 1; day <= 5; day++) {
            for (LocalTime startTime : startTimes) {
                LocalTime endTime = startTime.plusMinutes(courseDuration);

                // 确保不超过18:00
                if (endTime.isAfter(LocalTime.of(18, 0))) {
                    continue;
                }

                TimeSlot slot = new TimeSlot();
                slot.dayOfWeek = day;
                slot.startTime = startTime;
                slot.endTime = endTime;
                slot.score = calculateSlotScore(day, startTime);
                slots.add(slot);
            }
        }

        return slots;
    }

    private int calculateSlotScore(int dayOfWeek, LocalTime startTime) {
        int score = 0;

        // 上午时段优先（8:00-12:00）
        if (startTime.isBefore(LocalTime.of(12, 0))) {
            score += 10;
        }

        // 周一到周四优先
        if (dayOfWeek <= 4) {
            score += 5;
        }

        // 9:00-11:00 和 14:00-16:00 是黄金时段
        if ((startTime.equals(LocalTime.of(9, 0)) || startTime.equals(LocalTime.of(10, 0))) ||
            (startTime.equals(LocalTime.of(14, 0)) || startTime.equals(LocalTime.of(15, 0)))) {
            score += 3;
        }

        return score;
    }

    private List<TimeSlot> selectBestTimeSlots(
            List<TimeSlot> availableSlots,
            int count,
            Long teacherId,
            Long classroomId,
            List<Long> studentIds) {

        List<TimeSlot> result = new ArrayList<>();
        Set<Integer> usedDays = new HashSet<>();

        // 按得分排序
        availableSlots.sort((a, b) -> Integer.compare(b.score, a.score));

        for (TimeSlot slot : availableSlots) {
            if (result.size() >= count) {
                break;
            }

            // 检查冲突
            if (conflictDetectionService.hasAnyConflict(
                    teacherId, classroomId, studentIds,
                    slot.dayOfWeek, slot.startTime, slot.endTime)) {
                continue;
            }

            // 尽量选择不同的天（但如果已经接近目标数量，可以同一天）
            if (usedDays.contains(slot.dayOfWeek) && result.size() < count - 1) {
                // 先尝试找其他天，如果后面找不到再回来
                continue;
            }

            result.add(slot);
            usedDays.add(slot.dayOfWeek);
        }

        // 如果还不够，再次遍历允许同一天多节课
        if (result.size() < count) {
            for (TimeSlot slot : availableSlots) {
                if (result.size() >= count) {
                    break;
                }

                // 跳过已选择的
                if (result.contains(slot)) {
                    continue;
                }

                // 检查冲突
                if (conflictDetectionService.hasAnyConflict(
                        teacherId, classroomId, studentIds,
                        slot.dayOfWeek, slot.startTime, slot.endTime)) {
                    continue;
                }

                result.add(slot);
            }
        }

        return result;
    }

    private int calculateWeeks(String schedulePeriod) {
        return switch (schedulePeriod) {
            case "week" -> 1;
            case "two-weeks" -> 2;
            case "month" -> 4;
            default -> 1;
        };
    }

    private List<Schedule> createSchedules(
            Clazz clazz,
            Long courseId,
            Long classroomId,
            List<TimeSlot> timeSlots,
            LocalDate startDate,
            int weeks) {

        List<Schedule> schedules = new ArrayList<>();

        for (int week = 0; week < weeks; week++) {
            for (TimeSlot slot : timeSlots) {
                Schedule schedule = new Schedule();
                schedule.setClassId(clazz.getId());
                schedule.setCourseId(courseId);
                schedule.setTeacherId(clazz.getTeacherId());
                schedule.setClassroomId(classroomId);

                // 计算具体日期
                LocalDate date = startDate.plusWeeks(week)
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.of(slot.dayOfWeek)));

                schedule.setDate(date);
                schedule.setDayOfWeek(slot.dayOfWeek);
                schedule.setStartTime(slot.startTime);
                schedule.setEndTime(slot.endTime);
                schedule.setStatus("scheduled");

                scheduleMapper.insert(schedule);
                schedules.add(scheduleMapper.selectByIdWithExtra(schedule.getId()));
            }
        }

        return schedules;
    }

    private void associateStudentsToClass(Long classId, List<Long> studentIds) {
        // 先删除现有关联
        clazzMapper.deleteStudents(classId);

        // 添加新关联
        for (Long studentId : studentIds) {
            clazzMapper.insertStudent(classId, studentId);
        }
    }

    // 内部类：时间槽
    static class TimeSlot {
        int dayOfWeek;
        LocalTime startTime;
        LocalTime endTime;
        int score;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeSlot timeSlot = (TimeSlot) o;
            return dayOfWeek == timeSlot.dayOfWeek &&
                   Objects.equals(startTime, timeSlot.startTime) &&
                   Objects.equals(endTime, timeSlot.endTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayOfWeek, startTime, endTime);
        }
    }
}
