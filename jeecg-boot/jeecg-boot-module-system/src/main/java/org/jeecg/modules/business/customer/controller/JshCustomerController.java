package org.jeecg.modules.business.customer.controller;

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
import org.jeecg.modules.business.customer.entity.JshCustomer;
import org.jeecg.modules.business.customer.service.IJshCustomerService;
import org.jeecg.modules.business.customer.entity.JshCustomerAddress;
import org.jeecg.modules.business.customer.service.IJshCustomerAddressService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: jsh_customer
 * @Author: jeecg-boot
 * @Date: 2022-02-03
 * @Version: V1.0
 */
@Api(tags = "jsh_customer")
@RestController
@RequestMapping("/business/customer/jshCustomer")
@Slf4j
public class JshCustomerController extends JeecgController<JshCustomer, IJshCustomerService> {
  @Autowired
  private IJshCustomerService jshCustomerService;

  @Autowired
  private IJshCustomerAddressService jshCustomerAddressService;


  /*---------------------------------主表处理-begin-------------------------------------*/

  /**
   * 分页列表查询
   *
   * @param jshCustomer
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "jsh_customer-分页列表查询")
  @ApiOperation(value = "jsh_customer-分页列表查询", notes = "jsh_customer-分页列表查询")
  @GetMapping(value = "/list")
  public Result<?> queryPageList(JshCustomer jshCustomer,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 HttpServletRequest req) {
    QueryWrapper<JshCustomer> queryWrapper = QueryGenerator.initQueryWrapper(jshCustomer, req.getParameterMap());
    Page<JshCustomer> page = new Page<JshCustomer>(pageNo, pageSize);
    IPage<JshCustomer> pageList = jshCustomerService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param jshCustomer
   * @return
   */
  @AutoLog(value = "jsh_customer-添加")
  @ApiOperation(value = "jsh_customer-添加", notes = "jsh_customer-添加")
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody JshCustomer jshCustomer) {
    jshCustomerService.save(jshCustomer);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param jshCustomer
   * @return
   */
  @AutoLog(value = "jsh_customer-编辑")
  @ApiOperation(value = "jsh_customer-编辑", notes = "jsh_customer-编辑")
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody JshCustomer jshCustomer) {
    jshCustomerService.updateById(jshCustomer);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "jsh_customer-通过id删除")
  @ApiOperation(value = "jsh_customer-通过id删除", notes = "jsh_customer-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id") Long id) {
    jshCustomerService.delMain(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "jsh_customer-批量删除")
  @ApiOperation(value = "jsh_customer-批量删除", notes = "jsh_customer-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
    this.jshCustomerService.delBatchMain(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "jsh_customer-通过id查询")
  @ApiOperation(value = "jsh_customer-通过id查询", notes = "jsh_customer-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    JshCustomer jshCustomer = jshCustomerService.getById(id);
    if (jshCustomer == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(jshCustomer);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param jshCustomer
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, JshCustomer jshCustomer) {
    return super.exportXls(request, jshCustomer, JshCustomer.class, "客户信息");
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
    return super.importExcel(request, response, JshCustomer.class);
  }
  /*---------------------------------主表处理-end-------------------------------------*/


  /*--------------------------------子表处理-jsh_customer_address-begin----------------------------------------------*/

  /**
   * 通过主表ID查询
   *
   * @return
   */
  @AutoLog(value = "jsh_customer_address-通过主表ID查询")
  @ApiOperation(value = "jsh_customer_address-通过主表ID查询", notes = "jsh_customer_address-通过主表ID查询")
  @GetMapping(value = "/listJshCustomerAddressByMainId")
  public Result<?> listJshCustomerAddressByMainId(JshCustomerAddress jshCustomerAddress,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
    QueryWrapper<JshCustomerAddress> queryWrapper = QueryGenerator.initQueryWrapper(jshCustomerAddress, req.getParameterMap());
    Page<JshCustomerAddress> page = new Page<JshCustomerAddress>(pageNo, pageSize);
    IPage<JshCustomerAddress> pageList = jshCustomerAddressService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param jshCustomerAddress
   * @return
   */
  @AutoLog(value = "jsh_customer_address-添加")
  @ApiOperation(value = "jsh_customer_address-添加", notes = "jsh_customer_address-添加")
  @PostMapping(value = "/addJshCustomerAddress")
  public Result<?> addJshCustomerAddress(@RequestBody JshCustomerAddress jshCustomerAddress) {
    jshCustomerAddressService.save(jshCustomerAddress);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param jshCustomerAddress
   * @return
   */
  @AutoLog(value = "jsh_customer_address-编辑")
  @ApiOperation(value = "jsh_customer_address-编辑", notes = "jsh_customer_address-编辑")
  @PutMapping(value = "/editJshCustomerAddress")
  public Result<?> editJshCustomerAddress(@RequestBody JshCustomerAddress jshCustomerAddress) {
    jshCustomerAddressService.updateById(jshCustomerAddress);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "jsh_customer_address-通过id删除")
  @ApiOperation(value = "jsh_customer_address-通过id删除", notes = "jsh_customer_address-通过id删除")
  @DeleteMapping(value = "/deleteJshCustomerAddress")
  public Result<?> deleteJshCustomerAddress(@RequestParam(name = "id") Long id) {
    jshCustomerAddressService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "jsh_customer_address-批量删除")
  @ApiOperation(value = "jsh_customer_address-批量删除", notes = "jsh_customer_address-批量删除")
  @DeleteMapping(value = "/deleteBatchJshCustomerAddress")
  public Result<?> deleteBatchJshCustomerAddress(@RequestParam(name = "ids") String ids) {
    this.jshCustomerAddressService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 导出
   *
   * @return
   */
  @RequestMapping(value = "/exportJshCustomerAddress")
  public ModelAndView exportJshCustomerAddress(HttpServletRequest request, JshCustomerAddress jshCustomerAddress) {
    // Step.1 组装查询条件
    QueryWrapper<JshCustomerAddress> queryWrapper = QueryGenerator.initQueryWrapper(jshCustomerAddress, request.getParameterMap());
    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

    // Step.2 获取导出数据
    List<JshCustomerAddress> pageList = jshCustomerAddressService.list(queryWrapper);
    List<JshCustomerAddress> exportList = null;

    // 过滤选中数据
    String selections = request.getParameter("selections");
    if (oConvertUtils.isNotEmpty(selections)) {
      List<String> selectionList = Arrays.asList(selections.split(","));
      exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
    } else {
      exportList = pageList;
    }

    // Step.3 AutoPoi 导出Excel
    ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
    mv.addObject(NormalExcelConstants.FILE_NAME, "jsh_customer_address"); //此处设置的filename无效 ,前端会重更新设置一下
    mv.addObject(NormalExcelConstants.CLASS, JshCustomerAddress.class);
    mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("客户地址信息报表", "导出人:" + sysUser.getRealname(), "客户地址信息"));
    mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
    return mv;
  }

  /**
   * 导入
   *
   * @return
   */
  @RequestMapping(value = "/importJshCustomerAddress/{mainId}")
  public Result<?> importJshCustomerAddress(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") Long mainId) {
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
      MultipartFile file = entity.getValue();// 获取上传文件对象
      ImportParams params = new ImportParams();
      params.setTitleRows(2);
      params.setHeadRows(1);
      params.setNeedSave(true);
      try {
        List<JshCustomerAddress> list = ExcelImportUtil.importExcel(file.getInputStream(), JshCustomerAddress.class, params);
        for (JshCustomerAddress temp : list) {
          temp.setCustomerId(mainId);
        }
        long start = System.currentTimeMillis();
        jshCustomerAddressService.saveBatch(list);
        log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        return Result.OK("文件导入成功！数据行数：" + list.size());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return Result.error("文件导入失败:" + e.getMessage());
      } finally {
        try {
          file.getInputStream().close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return Result.error("文件导入失败！");
  }

  /*--------------------------------子表处理-jsh_customer_address-end----------------------------------------------*/
}
