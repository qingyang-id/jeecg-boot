package org.jeecg.modules.business.version.controller;

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
import org.jeecg.modules.business.version.entity.JshVersion;
import org.jeecg.modules.business.version.service.IJshVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: jsh_version
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Api(tags = "jsh_version")
@RestController
@RequestMapping("/business/version")
@Slf4j
public class JshVersionController extends JeecgController<JshVersion, IJshVersionService> {
    @Autowired
    private IJshVersionService jshVersionService;

    /**
     * 最新版本查询
     *
     * @param versionNo
     * @param platform
     * @return
     */
    @AutoLog(value = "jsh_version-最新版本查询")
    @ApiOperation(value = "jsh_version-最新版本查询", notes = "jsh_version-最新版本查询")
    @GetMapping(value = "/new")
    public Result<?> checkNewVersion(@RequestParam(name = "versionNo", defaultValue = "1001000000") Long versionNo,
                                   @RequestParam(name = "platform", defaultValue = "mac") String platform) {
        QueryWrapper<JshVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("version_no", versionNo)
                .eq("platform", platform)
                .orderByDesc("version_no")
                .last("LIMIT 1");
        JshVersion jshVersion = this.jshVersionService.getOne(queryWrapper);
        if (jshVersion == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(jshVersion);
    }

    /**
     * 分页列表查询
     *
     * @param jshVersion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "jsh_version-分页列表查询")
    @ApiOperation(value = "jsh_version-分页列表查询", notes = "jsh_version-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(JshVersion jshVersion,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<JshVersion> queryWrapper = QueryGenerator.initQueryWrapper(jshVersion, req.getParameterMap());
        Page<JshVersion> page = new Page<>(pageNo, pageSize);
        IPage<JshVersion> pageList = jshVersionService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param jshVersion
     * @return
     */
    @AutoLog(value = "jsh_version-添加")
    @ApiOperation(value = "jsh_version-添加", notes = "jsh_version-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JshVersion jshVersion) {
        jshVersionService.save(jshVersion);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param jshVersion
     * @return
     */
    @AutoLog(value = "jsh_version-编辑")
    @ApiOperation(value = "jsh_version-编辑", notes = "jsh_version-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody JshVersion jshVersion) {
        jshVersionService.updateById(jshVersion);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_version-通过id删除")
    @ApiOperation(value = "jsh_version-通过id删除", notes = "jsh_version-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
        jshVersionService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "jsh_version-批量删除")
    @ApiOperation(value = "jsh_version-批量删除", notes = "jsh_version-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jshVersionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "jsh_version-通过id查询")
    @ApiOperation(value = "jsh_version-通过id查询", notes = "jsh_version-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) Long id) {
        JshVersion jshVersion = jshVersionService.getById(id);
        if (jshVersion == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(jshVersion);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param jshVersion
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JshVersion jshVersion) {
        return super.exportXls(request, jshVersion, JshVersion.class, "产品信息");
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
        return super.importExcel(request, response, JshVersion.class);
    }

}
