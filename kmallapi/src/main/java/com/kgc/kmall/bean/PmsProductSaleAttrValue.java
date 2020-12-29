package com.kgc.kmall.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
@ApiModel("spu销售属性值表")
public class PmsProductSaleAttrValue implements Serializable {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("商品id")
    private Long productId;
    @ApiModelProperty("销售属性id")
    private Long saleAttrId;
    @ApiModelProperty("销售属性值名称")
    private String saleAttrValueName;

    private Long isChecked;

    public Long getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Long isChecked) {
        this.isChecked = isChecked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(Long saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName == null ? null : saleAttrValueName.trim();
    }
}