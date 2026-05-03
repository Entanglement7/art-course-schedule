package com.art.schedule.mapper;

import com.art.schedule.entity.Classroom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {

    @Select("SELECT equipment_name FROM classroom_equipment WHERE classroom_id = #{classroomId}")
    List<String> selectEquipment(Long classroomId);

    @Delete("DELETE FROM classroom_equipment WHERE classroom_id = #{classroomId}")
    void deleteEquipment(Long classroomId);

    @Insert("<script>INSERT INTO classroom_equipment (classroom_id, equipment_name) VALUES " +
            "<foreach collection='equipment' item='e' separator=','>(#{classroomId}, #{e})</foreach></script>")
    void insertEquipment(@Param("classroomId") Long classroomId, @Param("equipment") List<String> equipment);
}
