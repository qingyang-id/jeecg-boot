package org.jeecg.modules.business.version.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: jsh_version
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
@TableName("jsh_version")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "jsh_version")
public class JshVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 平台
     */
    @Excel(name = "平台", width = 15)
    @Schema(description = "平台")
    private String platform;

    /**
     * 版本
     */
    @Excel(name = "版本", width = 15)
    @Schema(description = "版本")
    private String version;

    /**
     * 版本编号
     */
    @Excel(name = "版本编号", width = 15)
    @Schema(description = "版本编号")
    private Long versionNo;

    /**
     * 描述
     */
    @Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String description;

    /**
     * 下载地址
     */
    @Excel(name = "下载地址", width = 15)
    @Schema(description = "下载地址")
    private String downloadUrl;

    /**
     * 状态:1启用,0禁用
     */
    @Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @Schema(description = "状态")
    private Integer status;
}
