package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "平台属性，平台属性值", description = "提供用户相关的Rest API")
public class AttrController {
    @Reference
    AttrService attrService;
    @ApiOperation("显示平台属性列表")
    @GetMapping("/attrInfoList")
    @ApiImplicitParam(name = "catalog3Id",value = "3级分类Id",required = true)
    //@RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> spuList(Long catalog3Id) {
        return attrService.attrInfoList(catalog3Id);
    }
    @ApiOperation("添加平台属性，修改平台属性")
    @PostMapping("/saveAttrInfo")
    @ApiImplicitParam(name = "PmsBaseAttrInfo",value = "平台属性",required = true)
//    @RequestMapping("saveAttrInfo")
    public int saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        return attrService.saveAttrInfo(pmsBaseAttrInfo);
    }
    @ApiOperation("显示平台属性列表值")
    @PostMapping("/getAttrValueList")
    @ApiImplicitParam(name = "attrId",value = "平台属性Id",required = true)
//    @RequestMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId) {
        return attrService.getAttrValueList(attrId);
    }
}
