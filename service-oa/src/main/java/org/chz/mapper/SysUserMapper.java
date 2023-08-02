package org.chz.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chz.model.system.SysUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author chz
 * @since 2023-07-12
 */

@Repository
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
