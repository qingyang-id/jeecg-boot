package org.jeecg.modules.business.sequence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.sequence.constant.SequenceConstant;
import org.jeecg.modules.business.sequence.entity.JshSequence;
import org.jeecg.modules.business.sequence.mapper.JshSequenceMapper;
import org.jeecg.modules.business.sequence.service.IJshSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: jsh_sequence
 * @Author: jeecg-boot
 * @Date: 2022-02-07
 * @Version: V1.0
 */
@Service
public class JshSequenceServiceImpl extends ServiceImpl<JshSequenceMapper, JshSequence> implements IJshSequenceService {
  @Autowired
  JshSequenceMapper jshSequenceMapper;

  @Override
  @Transactional
  public String buildOnlyNumber(String seqName) {
    Long buildOnlyNumber;
    synchronized (this) {
      jshSequenceMapper.updateBuildOnlyNumber(seqName); // 编号+1
      buildOnlyNumber = jshSequenceMapper.getBuildOnlyNumber(seqName);
    }
    if (buildOnlyNumber < SequenceConstant.SEQ_TO_STRING_MIN_LENGTH) {
      StringBuffer sb = new StringBuffer(buildOnlyNumber.toString());
      int len = SequenceConstant.SEQ_TO_STRING_MIN_LENGTH.toString().length() - sb.length();
      for (int i = 0; i < len; i++) {
        sb.insert(0, SequenceConstant.SEQ_TO_STRING_LESS_INSERT);
      }
      return sb.toString();
    } else {
      return buildOnlyNumber.toString();
    }
  }
}
