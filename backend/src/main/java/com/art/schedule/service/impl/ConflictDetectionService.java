package com.art.schedule.service.impl;

import com.art.schedule.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConflictDetectionService {

    private final ScheduleMapper scheduleMapper;

    public ConflictDetectionService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * 检查教师在指定时间是否有冲突
     */
    public boolean hasTeacherConflict(Long teacherId, int dayOfWeek,
                                     LocalTime startTime, LocalTime endTime) {
        return scheduleMapper.countTeacherConflicts(teacherId, dayOfWeek, startTime, endTime) > 0;
    }

    /**
     * 检查教室在指定时间是否有冲突
     */
    public boolean hasClassroomConflict(Long classroomId, int dayOfWeek,
                                       LocalTime startTime, LocalTime endTime) {
        return scheduleMapper.countClassroomConflicts(classroomId, dayOfWeek, startTime, endTime) > 0;
    }

    /**
     * 检查学生列表在指定时间是否有冲突
     */
    public boolean hasStudentConflict(List<Long> studentIds, int dayOfWeek,
                                     LocalTime startTime, LocalTime endTime) {
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }
        return scheduleMapper.countStudentConflicts(studentIds, dayOfWeek, startTime, endTime) > 0;
    }

    /**
     * 综合检测所有冲突
     */
    public boolean hasAnyConflict(Long teacherId, Long classroomId,
                                 List<Long> studentIds, int dayOfWeek,
                                 LocalTime startTime, LocalTime endTime) {
        return hasTeacherConflict(teacherId, dayOfWeek, startTime, endTime) ||
               hasClassroomConflict(classroomId, dayOfWeek, startTime, endTime) ||
               hasStudentConflict(studentIds, dayOfWeek, startTime, endTime);
    }
}
