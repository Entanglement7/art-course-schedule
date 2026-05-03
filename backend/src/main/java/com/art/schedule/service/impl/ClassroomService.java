package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.ClassroomRequest;
import com.art.schedule.entity.Classroom;
import com.art.schedule.mapper.ClassroomMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ClassroomService {

    private final ClassroomMapper classroomMapper;

    public ClassroomService(ClassroomMapper classroomMapper) {
        this.classroomMapper = classroomMapper;
    }

    public PageResult<Classroom> list(int page, int size, String search, String type) {
        LambdaQueryWrapper<Classroom> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) q.like(Classroom::getName, search);
        if (StringUtils.hasText(type)) q.eq(Classroom::getType, type);
        q.orderByDesc(Classroom::getCreatedAt);
        Page<Classroom> p = classroomMapper.selectPage(new Page<>(page, size), q);
        p.getRecords().forEach(c -> c.setEquipment(classroomMapper.selectEquipment(c.getId())));
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public Classroom getById(Long id) {
        Classroom c = classroomMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("教室不存在");
        c.setEquipment(classroomMapper.selectEquipment(id));
        return c;
    }

    @Transactional
    public Classroom create(ClassroomRequest req) {
        Classroom c = new Classroom();
        copyProps(req, c);
        classroomMapper.insert(c);
        if (req.getEquipment() != null && !req.getEquipment().isEmpty()) {
            classroomMapper.insertEquipment(c.getId(), req.getEquipment());
        }
        c.setEquipment(req.getEquipment());
        return c;
    }

    @Transactional
    public Classroom update(Long id, ClassroomRequest req) {
        Classroom c = classroomMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("教室不存在");
        copyProps(req, c);
        classroomMapper.updateById(c);
        classroomMapper.deleteEquipment(id);
        if (req.getEquipment() != null && !req.getEquipment().isEmpty()) {
            classroomMapper.insertEquipment(id, req.getEquipment());
        }
        c.setEquipment(req.getEquipment());
        return c;
    }

    @Transactional
    public void delete(Long id) {
        classroomMapper.deleteById(id);
        classroomMapper.deleteEquipment(id);
    }

    private void copyProps(ClassroomRequest req, Classroom c) {
        c.setName(req.getName());
        c.setType(req.getType());
        c.setCapacity(req.getCapacity());
        c.setFloor(req.getFloor());
        c.setStatus(req.getStatus() != null ? req.getStatus() : "空闲");
        c.setRemark(req.getRemark() != null ? req.getRemark() : "");
    }
}
