package org.jeecg.modules.business.sequence.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.business.sequence.constant.SequenceConstant;
import org.jeecg.modules.business.sequence.entity.JshSequence;
import org.jeecg.modules.business.sequence.service.IJshSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: jsh_sequence
 * @Author: jeecg-boot
 * @Date:   2022-02-07
 * @Version: V1.0
 */
@Api(tags="jsh_sequence")
@RestController
@RequestMapping("/business/sequence")
@Slf4j
public class JshSequenceController extends JeecgController<JshSequence, IJshSequenceService> {
	@Autowired
	private IJshSequenceService jshSequenceService;

	/**
	 * 单据编号生成接口
	 *
	 * @param seqName
	 * @return
	 */
	@AutoLog(value = "jsh_sequence-单据编号生成接口")
	@ApiOperation(value="jsh_sequence-单据编号生成接口", notes="jsh_sequence-单据编号生成接口")
  @GetMapping(value = "/num")
	public Result<?> buildNumber(@RequestParam(name="seqName", required=true) String seqName) {
	  if (!SequenceConstant.ORDER_NUMBER_SEQ.equals(seqName)) {
      return Result.error("未找到对应数据");
    }
		String jshSequence = jshSequenceService.buildOnlyNumber(seqName);
		if(jshSequence==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jshSequence);
	}

}
