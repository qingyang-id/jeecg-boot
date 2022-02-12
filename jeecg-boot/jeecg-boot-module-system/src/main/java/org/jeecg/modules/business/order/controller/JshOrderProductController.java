package org.jeecg.modules.business.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.service.IJshOrderProductDetailService;
import org.jeecg.modules.business.order.service.IJshOrderProductExtendService;
import org.jeecg.modules.business.order.service.IJshOrderProductService;
import org.jeecg.modules.business.order.vo.JshOrderProductPage;
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
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date: 2022-02-06
 * @Version: V1.0
 */
@Api(tags = "jsh_order_product")
@RestController
@RequestMapping("/business/order/jshOrderProduct")
@Slf4j
public class JshOrderProductController extends JeecgController<JshOrderProduct, IJshOrderProductService> {
    @Autowired
    private IJshOrderProductService jshOrderProductService;
    @Autowired
    private IJshOrderProductDetailService jshOrderProductDetailService;
    @Autowired
    private IJshOrderProductExtendService jshOrderProductExtendService;

    /**
     * 分页列表查询
     *
     * @param jshOrderProduct
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "jsh_order_product-分页列表查询")
    @ApiOperation(value = "jsh_order_product-分页列表查询", notes = "jsh_order_product-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(JshOrderProduct jshOrderProduct,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(name = "orderIds", required = false) String orderIds,
                                   HttpServletRequest req) {
        QueryWrapper<JshOrderProduct> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderProduct, req.getParameterMap());
        Page<JshOrderProduct> page = new Page<JshOrderProduct>(pageNo, pageSize);
        if (StringUtils.isNotEmpty(orderIds)) {
            List<String> ids = Arrays.asList(orderIds.split(","));
            if (!ids.isEmpty()) {
                queryWrapper.in("order_id", ids);
            }
        }
        IPage<JshOrderProduct> pageList = jshOrderProductService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param jshOrderProductPage
     * @return
     */
    @AutoLog(value = "jsh_order_product-添加")
    @ApiOperation(value = "jsh_order_product-添加", notes = "jsh_order_product-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JshOrderProductPage jshOrderProductPage) {
        JshOrderProduct jshOrderProduct = new JshOrderProduct();
        BeanUtils.copyProperties(jshOrderProductPage, jshOrderProduct);
        jshOrderProductService.saveMain(jshOrderProduct, jshOrderProductPage.getJshOrderProductDetailList(), jshOrderProductPage.getJshOrderProductExtendList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param jshOrderProductPage
     * @return
     */
    @AutoLog(value = "jsh_order_product-编辑")
    @ApiOperation(value = "jsh_order_product-编辑", notes = "jsh_order_product-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody JshOrderProductPage jshOrderProductPage) {
        JshOrderProduct jshOrderProduct = new JshOrderProduct();
        BeanUtils.copyProperties(jshOrderProductPage, jshOrderProduct);
        JshOrderProduct jshOrderProductEntity = jshOrderProductService.getById(jshOrderProduct.getId());
        if (jshOrderProductEntity == null) {
            return Result.error("未找到对应数据");
        }
        jshOrderProductService.updateMain(jshOrderProduct, jshOrderProductPage.getJshOrderProductDetailList(), jshOrderProductPage.getJshOrderProductExtendList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product-通过id删除")
    @ApiOperation(value = "jsh_order_product-通过id删除", notes = "jsh_order_product-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
        jshOrderProductService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "jsh_order_product-批量删除")
    @ApiOperation(value = "jsh_order_product-批量删除", notes = "jsh_order_product-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jshOrderProductService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product-通过id查询")
    @ApiOperation(value = "jsh_order_product-通过id查询", notes = "jsh_order_product-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        JshOrderProduct jshOrderProduct = jshOrderProductService.getById(id);
        if (jshOrderProduct == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(jshOrderProduct);

    }

    /**
     * 通过id查询
     *
     * @param orderId
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-通过主表ID查询")
    @ApiOperation(value = "jsh_order_product_detail-通过主表ID查询", notes = "jsh_order_product_detail-通过主表ID查询")
    @GetMapping(value = "/queryJshOrderProductDetailByOrderId")
    public Result<?> queryJshOrderProductDetailListByOrderId(@RequestParam(name = "id", required = true) Long orderId) {
        List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductDetailService.selectByOrderId(orderId);
        IPage<JshOrderProductDetail> page = new Page<>();
        page.setRecords(jshOrderProductDetailList);
        page.setTotal(jshOrderProductDetailList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product_extend-通过主表ID查询")
    @ApiOperation(value = "jsh_order_product_extend-通过主表ID查询", notes = "jsh_order_product_extend-通过主表ID查询")
    @GetMapping(value = "/queryJshOrderProductExtendByOrderId")
    public Result<?> queryJshOrderProductExtendListByOrderId(@RequestParam(name = "id", required = true) Long id) {
        List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductExtendService.selectByOrderId(id);
        IPage<JshOrderProductExtend> page = new Page<>();
        page.setRecords(jshOrderProductExtendList);
        page.setTotal(jshOrderProductExtendList.size());
        return Result.OK(page);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param jshOrderProduct
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JshOrderProduct jshOrderProduct) {
//        // Step.1 组装查询条件查询数据
//        QueryWrapper<JshOrderProduct> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderProduct, request.getParameterMap());
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//
//        //Step.2 获取导出数据
//        List<JshOrderProduct> queryList = jshOrderProductService.list(queryWrapper);
//        // 过滤选中数据
//        String selections = request.getParameter("selections");
//        List<JshOrderProduct> jshOrderProductList = new ArrayList<JshOrderProduct>();
//        if (oConvertUtils.isEmpty(selections)) {
//            jshOrderProductList = queryList;
//        } else {
//            List<String> selectionList = Arrays.asList(selections.split(","));
//            jshOrderProductList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
//        }
//
//        // Step.3 组装pageList
//        List<JshOrderProductPage> pageList = new ArrayList<JshOrderProductPage>();
//        for (JshOrderProduct main : jshOrderProductList) {
//            JshOrderProductPage vo = new JshOrderProductPage();
//            BeanUtils.copyProperties(main, vo);
//            List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductDetailService.selectByMainId(main.getId());
//            vo.setJshOrderProductDetailList(jshOrderProductDetailList);
//            List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductExtendService.selectByMainId(main.getId());
//            vo.setJshOrderProductExtendList(jshOrderProductExtendList);
//            pageList.add(vo);
//        }
//
//        // Step.4 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        mv.addObject(NormalExcelConstants.FILE_NAME, "jsh_order_product列表");
//        mv.addObject(NormalExcelConstants.CLASS, JshOrderProductPage.class);
//        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("jsh_order_product数据", "导出人:" + sysUser.getRealname(), "jsh_order_product"));
//        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//        return mv;
        return super.exportXls(request, jshOrderProduct, JshOrderProduct.class, "原尺寸信息");
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
                List<JshOrderProductPage> list = ExcelImportUtil.importExcel(file.getInputStream(), JshOrderProductPage.class, params);
                for (JshOrderProductPage page : list) {
                    JshOrderProduct po = new JshOrderProduct();
                    BeanUtils.copyProperties(page, po);
                    jshOrderProductService.saveMain(po, page.getJshOrderProductDetailList(), page.getJshOrderProductExtendList());
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
