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
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.service.IJshOrderProductExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: jsh_order_product_extend
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Api(tags = "jsh_order_product_extend")
@RestController
@RequestMapping("/business/order/jshOrderProductExtend")
@Slf4j
public class JshOrderProductExtendController extends JeecgController<JshOrderProductExtend, IJshOrderProductExtendService> {
  @Autowired
  private IJshOrderProductExtendService jshOrderProductExtendService;

  /**
   * 分页列表查询
   *
   * @param jshOrderProductExtend
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-分页列表查询")
  @ApiOperation(value = "jsh_order_product_extend-分页列表查询", notes = "jsh_order_product_extend-分页列表查询")
  @GetMapping(value = "/list")
  public Result<?> queryPageList(JshOrderProductExtend jshOrderProductExtend,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 HttpServletRequest req) {
    QueryWrapper<JshOrderProductExtend> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderProductExtend, req.getParameterMap());
    Page<JshOrderProductExtend> page = new Page<JshOrderProductExtend>(pageNo, pageSize);
    IPage<JshOrderProductExtend> pageList = jshOrderProductExtendService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param jshOrderProductExtend
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-添加")
  @ApiOperation(value = "jsh_order_product_extend-添加", notes = "jsh_order_product_extend-添加")
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody JshOrderProductExtend jshOrderProductExtend) {
    jshOrderProductExtendService.save(jshOrderProductExtend);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param jshOrderProductExtend
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-编辑")
  @ApiOperation(value = "jsh_order_product_extend-编辑", notes = "jsh_order_product_extend-编辑")
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody JshOrderProductExtend jshOrderProductExtend) {
    jshOrderProductExtendService.updateById(jshOrderProductExtend);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-通过id删除")
  @ApiOperation(value = "jsh_order_product_extend-通过id删除", notes = "jsh_order_product_extend-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
    jshOrderProductExtendService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-批量删除")
  @ApiOperation(value = "jsh_order_product_extend-批量删除", notes = "jsh_order_product_extend-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.jshOrderProductExtendService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "jsh_order_product_extend-通过id查询")
  @ApiOperation(value = "jsh_order_product_extend-通过id查询", notes = "jsh_order_product_extend-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    JshOrderProductExtend jshOrderProductExtend = jshOrderProductExtendService.getById(id);
    if (jshOrderProductExtend == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(jshOrderProductExtend);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param jshOrderProductExtend
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, JshOrderProductExtend jshOrderProductExtend) {
    return super.exportXls(request, jshOrderProductExtend, JshOrderProductExtend.class, "jsh_order_product_extend");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
    return super.importExcel(request, response, JshOrderProductExtend.class);
  }

}
