package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.AdjustRequestDto;
import com.art.schedule.entity.AdjustRequest;
import com.art.schedule.entity.Clazz;
import com.art.schedule.entity.User;
import com.art.schedule.mapper.AdjustRequestMapper;
import com.art.schedule.mapper.ClazzMapper;
import com.art.schedule.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdjustService {

    private final AdjustRequestMapper adjustMapper;
    private final UserMapper userMapper;
    private final ClazzMapper clazzMapper;

    public AdjustService(AdjustRequestMapper adjustMapper, UserMapper userMapper, ClazzMapper clazzMapper) {
        this.adjustMapper = adjustMapper;
        this.userMapper = userMapper;
        this.clazzMapper = clazzMapper;
    }

    // applicantId != null 时只返回该用户自己的申请（教师场景）
    public PageResult<AdjustRequest> list(int page, int size, String status,
                                          String startDate, String endDate, Long applicantId) {
        LambdaQueryWrapper<AdjustRequest> q = new LambdaQueryWrapper<>();
        if (applicantId != null) q.eq(AdjustRequest::getApplicantId, applicantId);
        if (StringUtils.hasText(status)) q.eq(AdjustRequest::getStatus, status);
        if (StringUtils.hasText(startDate))
            q.ge(AdjustRequest::getApplyTime, LocalDate.parse(startDate).atStartOfDay());
        if (StringUtils.hasText(endDate))
            q.le(AdjustRequest::getApplyTime, LocalDate.parse(endDate).atTime(23, 59, 59));
        q.orderByDesc(AdjustRequest::getApplyTime);

        Page<AdjustRequest> p = adjustMapper.selectPage(new Page<>(page, size), q);
        fillExtra(p.getRecords());
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public AdjustRequest create(AdjustRequestDto dto, User applicant) {
        // 校验班级必须属于当前教师（admin 不限制）
        if ("teacher".equals(applicant.getRole())) {
            Clazz clazz = clazzMapper.selectById(dto.getClassId());
            if (clazz == null) throw new IllegalArgumentException("班级不存在");
            if (applicant.getTeacherId() == null || !applicant.getTeacherId().equals(clazz.getTeacherId())) {
                throw new IllegalArgumentException("只能申请自己班级的调课");
            }
        }

        AdjustRequest req = new AdjustRequest();
        req.setApplicantId(applicant.getId());
        req.setClassId(dto.getClassId());
        req.setScheduleId(dto.getScheduleId());
        req.setOriginalTime(dto.getOriginalTime());
        req.setNewDayOfWeek(dto.getNewDayOfWeek());
        if (StringUtils.hasText(dto.getNewStartTime()))
            req.setNewStartTime(LocalTime.parse(dto.getNewStartTime()));
        if (StringUtils.hasText(dto.getNewEndTime()))
            req.setNewEndTime(LocalTime.parse(dto.getNewEndTime()));
        req.setReason(dto.getReason());
        req.setStatus("待审核");
        adjustMapper.insert(req);
        fillExtra(List.of(req));
        return req;
    }

    public AdjustRequest approve(Long id, Long reviewerId) {
        AdjustRequest req = getOrThrow(id);
        req.setStatus("已通过");
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewerId(reviewerId);
        adjustMapper.updateById(req);
        fillExtra(List.of(req));
        return req;
    }

    public AdjustRequest reject(Long id, String rejectReason, Long reviewerId) {
        AdjustRequest req = getOrThrow(id);
        req.setStatus("已拒绝");
        req.setRejectReason(rejectReason);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewerId(reviewerId);
        adjustMapper.updateById(req);
        fillExtra(List.of(req));
        return req;
    }

    public void delete(Long id, User operator) {
        AdjustRequest req = getOrThrow(id);
        // 教师只能撤销自己的申请
        if ("teacher".equals(operator.getRole()) && !req.getApplicantId().equals(operator.getId())) {
            throw new IllegalArgumentException("只能撤销自己的申请");
        }
        if (!"待审核".equals(req.getStatus()))
            throw new IllegalArgumentException("只能撤销待审核的申请");
        adjustMapper.deleteById(id);
    }

    private AdjustRequest getOrThrow(Long id) {
        AdjustRequest req = adjustMapper.selectById(id);
        if (req == null) throw new IllegalArgumentException("调课申请不存在");
        return req;
    }

    private void fillExtra(List<AdjustRequest> list) {
        list.forEach(req -> {
            User u = userMapper.selectById(req.getApplicantId());
            if (u != null) req.setApplicantName(u.getName());
            Clazz c = clazzMapper.selectById(req.getClassId());
            if (c != null) {
                req.setClassName(c.getName());
                req.setCourseName(c.getCourseName());
            }
        });
    }
}
