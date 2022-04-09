package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductExtendMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductMapper;
import org.jeecg.modules.business.order.service.IJshOrderProductService;
import org.jeecg.modules.business.order.service.IJshOrderService;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.jeecg.modules.business.order.vo.JshOrderProductPage;
import org.jeecg.modules.business.order.vo.JshOrderProductVo;
import org.jeecg.modules.business.order.vo.JshOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderServiceImpl extends ServiceImpl<JshOrderMapper, JshOrder> implements IJshOrderService {

    @Autowired
    private JshOrderMapper jshOrderMapper;
    @Autowired
    private JshOrderProductMapper jshOrderProductMapper;
    @Autowired
    private JshOrderProductExtendMapper jshOrderProductExtendMapper;
    @Autowired
    private JshOrderProductDetailMapper jshOrderProductDetailMapper;
    @Resource
    IJshOrderProductService jshOrderProductService;

    void saveSubs(JshOrder jshOrder, List<JshOrderProductPage> jshOrderProductPageList) {
        if (jshOrderProductPageList != null && jshOrderProductPageList.size() > 0) {
            // 12位前缀 用于保存订单ID
            String prefix = "1" + StringUtils.leftPad(jshOrder.getId().toString(), 11, "0");
            long i = 0L;
            for (JshOrderProductPage jshOrderProductPage : jshOrderProductPageList) {
                i++;
                JshOrderProduct jshOrderProduct = new JshOrderProduct();
                BeanUtils.copyProperties(jshOrderProductPage, jshOrderProduct);
                // 设置主键 3位用于商品数量
                Long orderProductId = Long.parseLong(prefix + StringUtils.leftPad(String.valueOf(i), 3, "0"));
                jshOrderProduct.setId(orderProductId);
                // 外键设置
                jshOrderProduct.setOrderId(jshOrder.getId());
                jshOrderProduct.setCustomerId(jshOrder.getCustomerId());
                jshOrderProductMapper.insert(jshOrderProduct);

                List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductPage.getJshOrderProductExtendList();
                if (jshOrderProductExtendList != null && jshOrderProductExtendList.size() > 0) {
                    long j = 0L;
                    for (JshOrderProductExtend jshOrderProductExtend : jshOrderProductExtendList) {
                        j++;
                        // 设置主键
                        Long orderProductExtendId = j + Long.parseLong(StringUtils.rightPad(orderProductId.toString(), 18, "0"));
                        jshOrderProductExtend.setId(orderProductExtendId);
                        // 外键设置
                        jshOrderProductExtend.setOrderId(jshOrder.getId());
                        jshOrderProductExtend.setOrderProductId(jshOrderProduct.getId());
                        jshOrderProductExtendMapper.insert(jshOrderProductExtend);
                    }
                }

                List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductPage.getJshOrderProductDetailList();
                if (jshOrderProductDetailList != null && jshOrderProductDetailList.size() > 0) {
                    long j = 0L;
                    for (JshOrderProductDetail jshOrderProductDetail : jshOrderProductDetailList) {
                        j++;
                        // 设置主键
                        Long orderProductDetailId = j + Long.parseLong(StringUtils.rightPad(orderProductId.toString(), 18, "0"));
                        jshOrderProductDetail.setId(orderProductDetailId);
                        // 外键设置
                        jshOrderProductDetail.setOrderId(jshOrder.getId());
                        jshOrderProductDetail.setCustomerId(jshOrder.getCustomerId());
                        jshOrderProductDetail.setOrderProductId(jshOrderProduct.getId());
                        jshOrderProductDetail.setProductId(jshOrderProduct.getProductId());
                        jshOrderProductDetailMapper.insert(jshOrderProductDetail);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveMain(JshOrderPage jshOrderPage) {
        JshOrder jshOrder = new JshOrder();
        BeanUtils.copyProperties(jshOrderPage, jshOrder);
        jshOrderMapper.insert(jshOrder);

        // 2.子表数据重新插入
        this.saveSubs(jshOrder, jshOrderPage.getJshOrderProductPageList());
    }

    @Override
    @Transactional
    public void updateMain(JshOrderPage jshOrderPage) {
        JshOrder jshOrder = new JshOrder();
        BeanUtils.copyProperties(jshOrderPage, jshOrder);
        jshOrderMapper.updateById(jshOrder);

        // 1.先删除子表数据
        jshOrderProductMapper.deleteByMainId(jshOrder.getId());
        jshOrderProductDetailMapper.deleteByOrderId(jshOrder.getId());
        jshOrderProductExtendMapper.deleteByOrderId(jshOrder.getId());

        // 2.子表数据重新插入
        this.saveSubs(jshOrder, jshOrderPage.getJshOrderProductPageList());
    }

    @Override
    @Transactional
    public void delMain(Long id) {
        jshOrderProductDetailMapper.deleteByOrderId(id);
        jshOrderProductExtendMapper.deleteByOrderId(id);
        jshOrderProductMapper.deleteByMainId(id);
        jshOrderMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            Long mainId = Long.parseLong(id.toString());
            jshOrderProductDetailMapper.deleteByOrderId(mainId);
            jshOrderProductExtendMapper.deleteByOrderId(mainId);
            jshOrderProductMapper.deleteByMainId(mainId);
            jshOrderMapper.deleteById(id);
        }
    }

    private JshOrderProductVo formatJshOrderProductVo(JshOrderProduct jshOrderProduct) {
        JshOrderProductVo jshOrderProductVo = new JshOrderProductVo();
        BeanUtils.copyProperties(jshOrderProduct, jshOrderProductVo);
        // 格式化单价/面积/资金
        jshOrderProductVo.setPrice(BigDecimal.valueOf(jshOrderProduct.getPrice()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
        jshOrderProductVo.setExtendPrice(BigDecimal.valueOf(jshOrderProduct.getExtendPrice()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
        jshOrderProductVo.setTotalArea(BigDecimal.valueOf(jshOrderProduct.getTotalArea()).divide(new BigDecimal("1000000"), 3, RoundingMode.CEILING));
        jshOrderProductVo.setTotalPrice(BigDecimal.valueOf(jshOrderProduct.getTotalPrice()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
        return jshOrderProductVo;
    }

    @Override
    public List<JshOrderVo> getOrderExportList(QueryWrapper<JshOrder> queryWrapper) {
        List<JshOrder> jshOrderList = this.list(queryWrapper);
        List<Long> ids = jshOrderList.stream().map(JshOrder::getId).collect(Collectors.toList());
        // 查询订单明细
        HashMap<Long, List<JshOrderProductVo>> orderProductionsMap = new HashMap<>();
        if (!ids.isEmpty()) {
            LambdaUpdateWrapper<JshOrderProduct> jshOrderProductLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            jshOrderProductLambdaUpdateWrapper.in(JshOrderProduct::getOrderId, ids);
            List<JshOrderProduct> jshOrderProductList = jshOrderProductMapper.selectList(jshOrderProductLambdaUpdateWrapper);
            orderProductionsMap = (HashMap<Long, List<JshOrderProductVo>>) jshOrderProductList
                    .stream()
                    .collect(Collectors.toMap(JshOrderProduct::getOrderId,
                            jshOrderProduct -> {
                                List<JshOrderProductVo> jshOrderProductVoList = new ArrayList<>();
                                jshOrderProductVoList.add(this.formatJshOrderProductVo(jshOrderProduct));
                                return jshOrderProductVoList;
                            },
                            (List<JshOrderProductVo> jshOrderProductVoList1, List<JshOrderProductVo> jshOrderProductVoList2) -> {
                                jshOrderProductVoList1.addAll(jshOrderProductVoList2);
                                return jshOrderProductVoList1;
                            })
                    );
        }
        List<JshOrderVo> pageList = new ArrayList<>();
        for (JshOrder main : jshOrderList) {
            JshOrderVo vo = new JshOrderVo();
            BeanUtils.copyProperties(main, vo);
            // 格式化面积/资金
            vo.setTotalArea(BigDecimal.valueOf(main.getTotalArea()).divide(new BigDecimal("1000000"), 3, RoundingMode.CEILING));
            vo.setTotalPrice(BigDecimal.valueOf(main.getTotalPrice()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));

            List<JshOrderProductVo> jshOrderProductVoList = new ArrayList<>();
            if (orderProductionsMap.containsKey(vo.getId())) jshOrderProductVoList = orderProductionsMap.get(vo.getId());
            vo.setJshOrderProductVoList(jshOrderProductVoList);
            pageList.add(vo);
        }
        return pageList;
    }
}
