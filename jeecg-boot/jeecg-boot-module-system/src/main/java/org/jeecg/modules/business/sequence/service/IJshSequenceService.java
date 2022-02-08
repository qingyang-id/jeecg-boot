package org.jeecg.modules.business.sequence.service;

import org.jeecg.modules.business.sequence.entity.JshSequence;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: jsh_sequence
 * @Author: jeecg-boot
 * @Date: 2022-02-07
 * @Version: V1.0
 */
public interface IJshSequenceService extends IService<JshSequence> {
  String buildOnlyNumber(String seqName);
}
