package org.jeecg.modules.business.sequence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.sequence.entity.JshSequence;

/**
 * @Description: jsh_sequence
 * @Author: jeecg-boot
 * @Date:   2022-02-07
 * @Version: V1.0
 */
public interface JshSequenceMapper extends BaseMapper<JshSequence> {

  Integer updateBuildOnlyNumber(@Param("seqName") String seqName);

  /**
   * 获得一个全局唯一的数作为订单号的追加
   * */
  Long getBuildOnlyNumber(@Param("seqName") String seqName);
}
