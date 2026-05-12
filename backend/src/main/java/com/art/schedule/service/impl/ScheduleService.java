package com.art.schedule.service.impl;

import com.art.schedule.dto.ManualScheduleRequest;
import com.art.schedule.entity.Clazz;
import com.art.schedule.entity.Schedule;
import com.art.schedule.mapper.ClazzMapper;
import com.art.schedule.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ClazzMapper clazzMapper;

    public ScheduleService(ScheduleMapper scheduleMapper, ClazzMapper clazzMapper) {
        this.scheduleMapper = scheduleMapper;
        this.clazzMapper = clazzMapper;
    }

    public List<Schedule> getWeekSchedule(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        return scheduleMapper.selectWeekSchedule(monday, sunday);
    }

    public List<Schedule> getTeacherSchedule(Long teacherId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        return scheduleMapper.selectTeacherSchedule(teacherId, monday, sunday);
    }

    public List<Schedule> getStudentSchedule(Long studentId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        return scheduleMapper.selectStudentSchedule(studentId, monday, sunday);
    }

    public List<Schedule> getClassroomSchedule(Long classroomId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        return scheduleMapper.selectClassroomSchedule(classroomId, monday, sunday);
    }

    @Transactional
    public void cancel(Long id) {
        Schedule s = scheduleMapper.selectById(id);
        if (s == null) throw new IllegalArgumentException("课程不存在");
        scheduleMapper.deleteById(id);
    }

    @Transactional
    public List<Schedule> createManual(ManualScheduleRequest req) {
        Clazz clazz = clazzMapper.selectById(req.getClassId());
        if (clazz == null) throw new IllegalArgumentException("班级不存在");

        // 删除该班级已有的排课记录，重新排课
        scheduleMapper.deleteByClassId(req.getClassId());

        LocalDate startDate = LocalDate.parse(req.getStartDate());
        LocalTime startTime = LocalTime.parse(req.getStartTime());
        LocalTime endTime = LocalTime.parse(req.getEndTime());

        // 找到从 startDate 起第一个符合 dayOfWeek 的日期
        DayOfWeek targetDow = DayOfWeek.of(req.getDayOfWeek());
        LocalDate firstDate = startDate.with(TemporalAdjusters.nextOrSame(targetDow));

        int weeks = "weekly".equals(req.getRepeatType()) && req.getWeekCount() != null
                ? req.getWeekCount() : 1;

        List<Schedule> created = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            Schedule s = new Schedule();
            s.setClassId(req.getClassId());
            s.setCourseId(clazz.getCourseId());
            s.setClassroomId(req.getClassroomId());
            s.setTeacherId(clazz.getTeacherId());
            s.setDate(firstDate.plusWeeks(i));
            s.setDayOfWeek(req.getDayOfWeek());
            s.setStartTime(startTime);
            s.setEndTime(endTime);
            s.setStatus("scheduled");
            scheduleMapper.insert(s);
            created.add(scheduleMapper.selectByIdWithExtra(s.getId()));
        }
        return created;
    }
}
