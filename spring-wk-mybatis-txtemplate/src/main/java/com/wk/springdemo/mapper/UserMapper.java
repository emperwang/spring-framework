package com.wk.springdemo.mapper;

import com.wk.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * descripiton:
 *
 * @author: wk
 * @time: 19:10 2019/12/31
 * @modifier:
 */
public interface UserMapper {

    List<UserEntity> selectAll();

    /**
     * 使用 注解 指定参数名字
     * @param id
     * @return
     */
	UserEntity selectById(@Param("id") Integer id);

    /**
     * 使用 map 其中的key为参数的名字
     * @param
     * @return
     */
	UserEntity selectByMap(Map<String, String> map);

    /**
     * 根据条件选择性进行查询操作
     * @param user
     * @return
     */
    List<UserEntity> chooseSelect(UserEntity user);


    int updateAgeList(List<UserEntity> users);

    /**
     *  选择性的更新操作
     * @param user
     * @return
     */
    int updateSelectFieldTrim(UserEntity user);

    int updateSelectField(UserEntity user);

    int batchInsert(List<UserEntity> list);

    int insertOne(UserEntity user);

    // todo  此函数测试不通过
    int batchDeletes(List<UserEntity> list);

    int batchDelArray(Integer[] id);
}
