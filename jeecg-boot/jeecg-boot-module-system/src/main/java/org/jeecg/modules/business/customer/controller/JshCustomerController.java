package org.jeecg.modules.business.customer.controller;

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
import org.jeecg.modules.business.customer.entity.JshCustomer;
import org.jeecg.modules.business.customer.service.IJshCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: jsh_customer
 * @Author: jeecg-boot
 * @Date:   2022-02-03
 * @Version: V1.0
 */
@Api(tags="jsh_customer")
@RestController
@RequestMapping("/business/customer/jshCustomer")
@Slf4j
public class JshCustomerController extends JeecgController<JshCustomer, IJshCustomerService> {
	@Autowired
	private IJshCustomerService jshCustomerService;

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
	@ApiOperation(value="jsh_customer-分页列表查询", notes="jsh_customer-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(JshCustomer jshCustomer,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<JshCustomer> queryWrapper = QueryGenerator.initQueryWrapper(jshCustomer, req.getParameterMap());
		Page<JshCustomer> page = new Page<JshCustomer>(pageNo, pageSize);
		IPage<JshCustomer> pageList = jshCustomerService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param jshCustomer
	 * @return
	 */
	@AutoLog(value = "jsh_customer-添加")
	@ApiOperation(value="jsh_customer-添加", notes="jsh_customer-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody JshCustomer jshCustomer) {
		jshCustomerService.save(jshCustomer);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param jshCustomer
	 * @return
	 */
	@AutoLog(value = "jsh_customer-编辑")
	@ApiOperation(value="jsh_customer-编辑", notes="jsh_customer-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody JshCustomer jshCustomer) {
		jshCustomerService.updateById(jshCustomer);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jsh_customer-通过id删除")
	@ApiOperation(value="jsh_customer-通过id删除", notes="jsh_customer-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		jshCustomerService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "jsh_customer-批量删除")
	@ApiOperation(value="jsh_customer-批量删除", notes="jsh_customer-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jshCustomerService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jsh_customer-通过id查询")
	@ApiOperation(value="jsh_customer-通过id查询", notes="jsh_customer-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		JshCustomer jshCustomer = jshCustomerService.getById(id);
		if(jshCustomer==null) {
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
        return super.exportXls(request, jshCustomer, JshCustomer.class, "jsh_customer");
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

}
