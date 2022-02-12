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
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.service.IJshOrderProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: jsh_order_product_detail
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Api(tags = "jsh_order_product_detail")
@RestController
@RequestMapping("/business/order/jshOrderProductDetail")
@Slf4j
public class JshOrderProductDetailController extends JeecgController<JshOrderProductDetail, IJshOrderProductDetailService> {
    @Autowired
    private IJshOrderProductDetailService jshOrderProductDetailService;

    /**
     * 分页列表查询
     *
     * @param jshOrderProductDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-分页列表查询")
    @ApiOperation(value = "jsh_order_product_detail-分页列表查询", notes = "jsh_order_product_detail-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(JshOrderProductDetail jshOrderProductDetail,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(name = "orderIds", required = false) String orderIds,
                                   HttpServletRequest req) {
        QueryWrapper<JshOrderProductDetail> queryWrapper = QueryGenerator.initQueryWrapper(jshOrderProductDetail, req.getParameterMap());
        Page<JshOrderProductDetail> page = new Page<JshOrderProductDetail>(pageNo, pageSize);
        if (StringUtils.isNotEmpty(orderIds)) {
            List<String> ids = Arrays.asList(orderIds.split(","));
            if (!ids.isEmpty()) {
                queryWrapper.in("order_id", ids);
            }
        }
        IPage<JshOrderProductDetail> pageList = jshOrderProductDetailService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param jshOrderProductDetail
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-添加")
    @ApiOperation(value = "jsh_order_product_detail-添加", notes = "jsh_order_product_detail-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JshOrderProductDetail jshOrderProductDetail) {
        jshOrderProductDetailService.save(jshOrderProductDetail);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param jshOrderProductDetail
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-编辑")
    @ApiOperation(value = "jsh_order_product_detail-编辑", notes = "jsh_order_product_detail-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody JshOrderProductDetail jshOrderProductDetail) {
        jshOrderProductDetailService.updateById(jshOrderProductDetail);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-通过id删除")
    @ApiOperation(value = "jsh_order_product_detail-通过id删除", notes = "jsh_order_product_detail-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        jshOrderProductDetailService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-批量删除")
    @ApiOperation(value = "jsh_order_product_detail-批量删除", notes = "jsh_order_product_detail-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jshOrderProductDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_order_product_detail-通过id查询")
    @ApiOperation(value = "jsh_order_product_detail-通过id查询", notes = "jsh_order_product_detail-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        JshOrderProductDetail jshOrderProductDetail = jshOrderProductDetailService.getById(id);
        if (jshOrderProductDetail == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(jshOrderProductDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param jshOrderProductDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JshOrderProductDetail jshOrderProductDetail) {
        Integer type = null;
        if (jshOrderProductDetail != null) {
            type = jshOrderProductDetail.getType();
        }
        String title = "生产单明细";
        if (type != null) {
            title = type == 1 ? "铝材单明细" : "生产单明细";
        }
        return super.exportXls(request, jshOrderProductDetail, JshOrderProductDetail.class, title);
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
        return super.importExcel(request, response, JshOrderProductDetail.class);
    }

}
