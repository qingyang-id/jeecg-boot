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
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.service.IJshOrderProductDetailService;
import org.jeecg.modules.business.order.service.IJshOrderProductExtendService;
import org.jeecg.modules.business.order.service.IJshOrderProductService;
import org.jeecg.modules.business.order.service.IJshOrderService;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.jeecg.modules.business.sequence.constant.SequenceConstant;
import org.jeecg.modules.business.sequence.service.IJshSequenceService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
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

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Api(tags = "jsh_order")
@RestController
@RequestMapping("/business/order/jshOrder")
@Slf4j
public class JshOrderController extends JeecgController<JshOrder, IJshOrderService> {
    @Autowired
    private IJshSequenceService jshSequenceService;
    @Autowired
    private IJshOrderService jshOrderService;
    @Autowired
    private IJshOrderProductService jshOrderProductService;
    @Autowired
    private IJshOrderProductExtendService jshOrderProductExtendService;
    @Autowired
    private IJshOrderProductDetailService jshOrderProductDetailService;

    /**
     * 分页列表查询
     *
     * @param jshOrder
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "jsh_order-分页列表查询")
    @ApiOperation(value = "jsh_order-分页列表查询", notes = "jsh_order-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(JshOrder jshOrder,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<JshOrder> queryWrapper = QueryGenerator.initQueryWrapper(jshOrder, req.getParameterMap());
        Page<JshOrder> page = new Page<JshOrder>(pageNo, pageSize);
        IPage<JshOrder> pageList = jshOrderService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param jshOrderPage
     * @return
     */
    @AutoLog(value = "jsh_order-添加")
    @ApiOperation(value = "jsh_order-添加", notes = "jsh_order-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JshOrderPage jshOrderPage) {
        JshOrder jshOrder = new JshOrder();
        BeanUtils.copyProperties(jshOrderPage, jshOrder);
        // 获取order code
        String orderCode = jshSequenceService.buildOnlyNumber(SequenceConstant.ORDER_NUMBER_SEQ);
        jshOrderPage.setOrderCode(String.format("T%s", orderCode));
        jshOrderService.saveMain(jshOrderPage);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param jshOrderPage
     * @return
     */
    @AutoLog(value = "jsh_order-编辑")
    @ApiOperation(value = "jsh_order-编辑", notes = "jsh_order-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody JshOrderPage jshOrderPage) {
        JshOrder jshOrder = new JshOrder();
        BeanUtils.copyProperties(jshOrderPage, jshOrder);
        JshOrder jshOrderEntity = jshOrderService.getById(jshOrder.getId());
        if (jshOrderEntity == null) {
            return Result.error("未找到对应数据");
        }
        jshOrderService.updateMain(jshOrderPage);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order-通过id删除")
    @ApiOperation(value = "jsh_order-通过id删除", notes = "jsh_order-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
        jshOrderService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "jsh_order-批量删除")
    @ApiOperation(value = "jsh_order-批量删除", notes = "jsh_order-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jshOrderService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order-通过id查询")
    @ApiOperation(value = "jsh_order-通过id查询", notes = "jsh_order-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        JshOrder jshOrder = jshOrderService.getById(id);
        if (jshOrder == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(jshOrder);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product通过主表ID查询")
    @ApiOperation(value = "jsh_order_product主表ID查询", notes = "jsh_order_product-通主表ID查询")
    @GetMapping(value = "/queryJshOrderProductByMainId")
    public Result<?> queryJshOrderProductListByMainId(@RequestParam(name = "id", required = true) Long id) {
        List<JshOrderProduct> jshOrderProductList = jshOrderProductService.selectByMainId(id);
        return Result.OK(jshOrderProductList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param jshOrder
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JshOrder jshOrder) {
//    // Step.1 组装查询条件查询数据
//    QueryWrapper<JshOrder> queryWrapper = QueryGenerator.initQueryWrapper(jshOrder, request.getParameterMap());
//    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//
//    //Step.2 获取导出数据
//    List<JshOrder> queryList = jshOrderService.list(queryWrapper);
//    // 过滤选中数据
//    String selections = request.getParameter("selections");
//    List<JshOrder> jshOrderList;
//    if (oConvertUtils.isEmpty(selections)) {
//      jshOrderList = queryList;
//    } else {
//      List<String> selectionList = Arrays.asList(selections.split(","));
//      jshOrderList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
//    }
//
//    // Step.3 组装pageList
//    List<JshOrderPage> pageList = new ArrayList<JshOrderPage>();
//    for (JshOrder main : jshOrderList) {
//      JshOrderPage vo = new JshOrderPage();
//      BeanUtils.copyProperties(main, vo);
//
//      List<JshOrderProductPage> jshOrderProductPageList = new ArrayList<>();
//      List<JshOrderProduct> jshOrderProductList = jshOrderProductService.selectByMainId(main.getId());
//      for (JshOrderProduct jshOrderProduct : jshOrderProductList) {
//        JshOrderProductPage jshOrderProductPage = new JshOrderProductPage();
//        BeanUtils.copyProperties(jshOrderProduct, jshOrderProductPage);
//
//        List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductDetailService.selectByMainId(main.getId());
//        jshOrderProductPage.setJshOrderProductDetailList(jshOrderProductDetailList);
//        List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductExtendService.selectByMainId(main.getId());
//        jshOrderProductPage.setJshOrderProductExtendList(jshOrderProductExtendList);
//        jshOrderProductPageList.add(jshOrderProductPage);
//      }
//      vo.setJshOrderProductPageList(jshOrderProductPageList);
//
//      pageList.add(vo);
//    }
//
//    // Step.4 AutoPoi 导出Excel
//    ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//    mv.addObject(NormalExcelConstants.FILE_NAME, "jsh_order列表");
//    mv.addObject(NormalExcelConstants.CLASS, JshOrderPage.class);
//    mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("jsh_order数据", "导出人:" + sysUser.getRealname(), "jsh_order"));
//    mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//    return mv;
        return super.exportXls(request, jshOrder, JshOrder.class, "订单信息");
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<JshOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), JshOrderPage.class, params);
                for (JshOrderPage page : list) {
                    jshOrderService.saveMain(page);
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
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
        return Result.OK("文件导入失败！");
    }

}
