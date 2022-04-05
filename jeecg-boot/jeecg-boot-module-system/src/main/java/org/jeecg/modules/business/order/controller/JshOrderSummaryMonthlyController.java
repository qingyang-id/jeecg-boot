package org.jeecg.modules.business.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.business.order.entity.JshOrderSummaryMonthly;
import org.jeecg.modules.business.order.service.IJshOrderSummaryMonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
* @Description: jsh_order_summary_monthly
* @Author: jeecg-boot
* @Date:   2022-04-05
* @Version: V1.0
*/
@Api(tags="jsh_order_summary_monthly")
@RestController
@RequestMapping("/business/order/jshOrderSummaryMonthly")
@Slf4j
public class JshOrderSummaryMonthlyController extends JeecgController<JshOrderSummaryMonthly, IJshOrderSummaryMonthlyService> {

   @Autowired
   private IJshOrderSummaryMonthlyService jshOrderSummaryMonthlyService;


   /*---------------------------------主表处理-begin-------------------------------------*/

   /**
    * 分页列表查询
    * @param jshOrderSummaryMonthly
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "jsh_order_summary_monthly-分页列表查询")
   @ApiOperation(value="jsh_order_summary_monthly-分页列表查询", notes="jsh_order_summary_monthly-分页列表查询")
   @GetMapping(value = "/list")
   public Result<?> queryPageList(JshOrderSummaryMonthly jshOrderSummaryMonthly,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<JshOrderSummaryMonthly> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderSummaryMonthly, req.getParameterMap());
       if (jshOrderSummaryMonthly.getCustomerId() == null) {
           queryWrapper.ne("customer_id", 0L);
       }
       Page<JshOrderSummaryMonthly> page = new Page<>(pageNo, pageSize);
       IPage<JshOrderSummaryMonthly> pageList = jshOrderSummaryMonthlyService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    * 导出
    * @return
    */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, JshOrderSummaryMonthly jshOrderSummaryMonthly) {
       return super.exportXls(request, jshOrderSummaryMonthly, JshOrderSummaryMonthly.class, "订单月数据");
   }
   /*---------------------------------主表处理-end-------------------------------------*/
}
