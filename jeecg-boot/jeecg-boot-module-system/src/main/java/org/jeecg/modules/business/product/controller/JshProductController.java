package org.jeecg.modules.business.product.controller;

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
import org.jeecg.modules.business.product.entity.JshProduct;
import org.jeecg.modules.business.product.service.IJshProductService;
import org.jeecg.modules.business.sequence.constant.SequenceConstant;
import org.jeecg.modules.business.sequence.service.IJshSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: jsh_product
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Api(tags="jsh_product")
@RestController
@RequestMapping("/business/product/jshProduct")
@Slf4j
public class JshProductController extends JeecgController<JshProduct, IJshProductService> {
     @Autowired
     private IJshSequenceService jshSequenceService;
	@Autowired
	private IJshProductService jshProductService;

	/**
	 * 分页列表查询
	 *
	 * @param jshProduct
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "jsh_product-分页列表查询")
	@ApiOperation(value="jsh_product-分页列表查询", notes="jsh_product-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(JshProduct jshProduct,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<JshProduct> queryWrapper = QueryGenerator.initQueryWrapper(jshProduct, req.getParameterMap());
		Page<JshProduct> page = new Page<JshProduct>(pageNo, pageSize);
		IPage<JshProduct> pageList = jshProductService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param jshProduct
	 * @return
	 */
	@AutoLog(value = "jsh_product-添加")
	@ApiOperation(value="jsh_product-添加", notes="jsh_product-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody JshProduct jshProduct) {
	    if (StringUtils.isEmpty(jshProduct.getBarcode())) {
            // 获取barcode
            String barCode = jshSequenceService.buildOnlyNumber(SequenceConstant.BAR_CODE_NUMBER_SEQ);
            jshProduct.setBarcode(barCode);
        }
		jshProductService.save(jshProduct);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param jshProduct
	 * @return
	 */
	@AutoLog(value = "jsh_product-编辑")
	@ApiOperation(value="jsh_product-编辑", notes="jsh_product-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody JshProduct jshProduct) {
		jshProductService.updateById(jshProduct);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jsh_product-通过id删除")
	@ApiOperation(value="jsh_product-通过id删除", notes="jsh_product-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Long id) {
		jshProductService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "jsh_product-批量删除")
	@ApiOperation(value="jsh_product-批量删除", notes="jsh_product-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jshProductService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jsh_product-通过id查询")
	@ApiOperation(value="jsh_product-通过id查询", notes="jsh_product-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Long id) {
		JshProduct jshProduct = jshProductService.getById(id);
		if(jshProduct==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jshProduct);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jshProduct
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JshProduct jshProduct) {
        return super.exportXls(request, jshProduct, JshProduct.class, "产品信息");
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
        return super.importExcel(request, response, JshProduct.class);
    }

}
