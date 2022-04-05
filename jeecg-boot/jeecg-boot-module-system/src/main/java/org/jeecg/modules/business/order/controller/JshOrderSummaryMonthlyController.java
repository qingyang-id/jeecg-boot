package org.jeecg.modules.business.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.business.order.entity.JshOrderSummaryMonthly;
import org.jeecg.modules.business.order.service.IJshOrderSummaryMonthlyService;
import org.jeecg.modules.business.order.vo.JshOrderSummaryMonthlyExportVo;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
       // Step.1 组装查询条件查询数据
       QueryWrapper<JshOrderSummaryMonthly> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderSummaryMonthly, request.getParameterMap());
       LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

       //Step.2 获取导出数据
       List<JshOrderSummaryMonthly> queryList = jshOrderSummaryMonthlyService.list(queryWrapper);
       // 过滤选中数据
       String selections = request.getParameter("selections");
       List<JshOrderSummaryMonthly> jshOrderSummaryMonthlyList;
       if (oConvertUtils.isEmpty(selections)) {
           jshOrderSummaryMonthlyList = queryList;
       } else {
           List<String> selectionList = Arrays.asList(selections.split(","));
           jshOrderSummaryMonthlyList = queryList.stream().filter(item -> selectionList.contains(String.valueOf(item.getId()))).collect(Collectors.toList());
       }

       // Step.3 组装pageList
       List<JshOrderSummaryMonthlyExportVo> pageList = new ArrayList<>();
       for (JshOrderSummaryMonthly main : jshOrderSummaryMonthlyList) {
           JshOrderSummaryMonthlyExportVo jshOrderSummaryMonthlyExportVo = new JshOrderSummaryMonthlyExportVo();
           BeanUtils.copyProperties(main, jshOrderSummaryMonthlyExportVo);
           // 格式化面积/资金
           jshOrderSummaryMonthlyExportVo.setTotalArea(BigDecimal.valueOf(main.getTotalArea()).divide(new BigDecimal("1000000"), 3, RoundingMode.CEILING));
           jshOrderSummaryMonthlyExportVo.setTotalPrice(BigDecimal.valueOf(main.getTotalPrice()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
           pageList.add(jshOrderSummaryMonthlyExportVo);
       }

       // Step.4 AutoPoi 导出Excel
       ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
       mv.addObject(NormalExcelConstants.FILE_NAME, "订单月数据");
       mv.addObject(NormalExcelConstants.CLASS, JshOrderSummaryMonthlyExportVo.class);
       mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("订单月数据", "导出人:" + sysUser.getRealname(), "订单月数据"));
       mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
       return mv;
   }
   /*---------------------------------主表处理-end-------------------------------------*/
}
