package com.bolingx.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bolingx.ai.entity.UserEntity;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author p
 * @since 2024-6-14
 */
public interface UserMapper extends BaseMapper<UserEntity> {

    @Update("update user set password = #{password} where id = #{id}")
    void updatePasswordById(Long id, String password);
}
