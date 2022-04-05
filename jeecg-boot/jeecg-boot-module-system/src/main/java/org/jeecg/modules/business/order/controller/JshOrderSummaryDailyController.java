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
import org.jeecg.modules.business.order.entity.JshOrderSummaryDaily;
import org.jeecg.modules.business.order.service.IJshOrderSummaryDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
* @Description: jsh_order_summary_daily
* @Author: jeecg-boot
* @Date:   2022-04-05
* @Version: V1.0
*/
@Api(tags="jsh_order_summary_daily")
@RestController
@RequestMapping("/business/order/jshOrderSummaryDaily")
@Slf4j
public class JshOrderSummaryDailyController extends JeecgController<JshOrderSummaryDaily, IJshOrderSummaryDailyService> {

   @Autowired
   private IJshOrderSummaryDailyService jshOrderSummaryDailyService;


   /*---------------------------------主表处理-begin-------------------------------------*/

   /**
    * 分页列表查询
    * @param jshOrderSummaryDaily
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "jsh_order_summary_daily-分页列表查询")
   @ApiOperation(value="jsh_order_summary_daily-分页列表查询", notes="jsh_order_summary_daily-分页列表查询")
   @GetMapping(value = "/list")
   public Result<?> queryPageList(JshOrderSummaryDaily jshOrderSummaryDaily,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<JshOrderSummaryDaily> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderSummaryDaily, req.getParameterMap());
       if (jshOrderSummaryDaily.getCustomerId() == null) {
           queryWrapper.ne("customer_id", 0L);
       }
       Page<JshOrderSummaryDaily> page = new Page<JshOrderSummaryDaily>(pageNo, pageSize);
       IPage<JshOrderSummaryDaily> pageList = jshOrderSummaryDailyService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    * 导出
    * @return
    */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, JshOrderSummaryDaily jshOrderSummaryDaily) {
       return super.exportXls(request, jshOrderSummaryDaily, JshOrderSummaryDaily.class, "订单日数据");
   }
   /*---------------------------------主表处理-end-------------------------------------*/
}
