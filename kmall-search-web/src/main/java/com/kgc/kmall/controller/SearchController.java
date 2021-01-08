package com.kgc.kmall.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.service.AttrService;
import com.kgc.kmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;

    @RequestMapping("/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping("/list.html")
    public String list(PmsSearchSkuParam pmsSearchSkuParam, Model model) {
        //进行数据查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchSkuParam);
        model.addAttribute("skuLsInfoList", pmsSearchSkuInfos);
        //获取平台属性Id并给平台属性去重
        Set<Long> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (int i = 0; i < pmsSearchSkuInfo.getSkuAttrValueList().size(); i++) {
                Map<Object, Object> pmSkuAttrValue = (Map<Object, Object>) pmsSearchSkuInfo.getSkuAttrValueList().get(i);
                //   System.out.println(pmsSearchSkuInfo);
                valueIdSet.add(Long.parseLong(pmSkuAttrValue.get("valueId").toString()));
            }
        }
        System.out.println(valueIdSet);

        //商品筛选查询
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.selectAttrInfoValueListByValueId(valueIdSet);
        // 已选中的valueId
        String[] valueId = pmsSearchSkuParam.getValueId();

        //封装面包屑
        if (valueId != null) {
            List<PmsSearchCrumb> pmsSearchCrumbList = new ArrayList<>();
            for (String s : valueId) {
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setUrlParam(getURLParam(pmsSearchSkuParam, s));
                pmsSearchCrumb.setValueId(s);
                pmsSearchCrumb.setValueName(getValueName(pmsBaseAttrInfos,s));
                pmsSearchCrumbList.add(pmsSearchCrumb);
            }
            model.addAttribute("attrValueSelectedList", pmsSearchCrumbList);
        }

        if (valueId != null) {
            //利用迭代器排除已选中valueId平台属性，删除集合元素不能使用for循环，因为会出现数组越界
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while (iterator.hasNext()) {
                PmsBaseAttrInfo next = iterator.next();
                for (PmsBaseAttrValue pmsBaseAttrValue : next.getAttrValueList()) {
                    for (String s : valueId) {
                        if (s.equals(pmsBaseAttrValue.getId().toString())) {
                            //删除平台属性值
                            iterator.remove();
                        }
                    }
                }
            }
        }

        model.addAttribute("attrList", pmsBaseAttrInfos);

        //拼接平台属性URL
        String urlParam = getURLParam(pmsSearchSkuParam);
        model.addAttribute("urlParam", urlParam);

        //显示关键字
        model.addAttribute("keyword", pmsSearchSkuParam.getKeyword());
        return "list";
    }

    /**
     * 根据条件对象拼接URL
     *
     * @param pmsSearchSkuParam
     * @return
     */
    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam) {
        StringBuffer stringBuffer = new StringBuffer();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        //  List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuParam.getSkuAttrValueList();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if (StringUtils.isNotBlank(catalog3Id)) {
            stringBuffer.append("&catalog3Id=" + catalog3Id);
        }
        if (StringUtils.isNotBlank(keyword)) {
            stringBuffer.append("&keyword=" + keyword);
        }
        if (valueId != null) {
            for (String pmsSkuAttrValue : valueId) {
                stringBuffer.append("&valueId=" + pmsSkuAttrValue);
            }
        }
        return stringBuffer.substring(1);
    }

    /**
     * 拼接面包屑URL，urlParam=当前URL中的valueId-面包屑的valueId
     *
     * @param pmsSearchSkuParam
     * @param val
     * @return
     */
    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam, String val) {
        StringBuffer stringBuffer = new StringBuffer();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        //  List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuParam.getSkuAttrValueList();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if (StringUtils.isNotBlank(catalog3Id)) {
            stringBuffer.append("&catalog3Id=" + catalog3Id);
        }
        if (StringUtils.isNotBlank(keyword)) {
            stringBuffer.append("&keyword=" + keyword);
        }
        if (val != null) {
            for (String pmsSkuAttrValue : valueId) {
                if (val.equals(pmsSkuAttrValue) == false) {
                    stringBuffer.append("&valueId=" + pmsSkuAttrValue);
                }
            }
        }
        return stringBuffer.substring(1);
    }

    /**
     * 根据valueId查询valueName
     *
     * @param pmsBaseAttrInfos
     * @param valueId
     * @return
     */
    public String getValueName(List<PmsBaseAttrInfo> pmsBaseAttrInfos, String valueId) {
        String valueName="";
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                if(pmsBaseAttrValue.getId().toString().equals(valueId)){
                    valueName=pmsBaseAttrInfo.getAttrName()+":"+pmsBaseAttrValue.getValueName();
                   return valueName;
                }
            }
        }
        return valueName;
    }
}
