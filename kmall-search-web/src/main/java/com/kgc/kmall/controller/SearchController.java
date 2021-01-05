package com.kgc.kmall.controller;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;

    @RequestMapping("/index.html")
    public String index(){
        return "index";
    }
    @RequestMapping("/list.html")
    public String list(PmsSearchSkuParam pmsSearchSkuParam, Model model){
    //进行数据查询
        List<PmsSearchSkuInfo> list = searchService.list(pmsSearchSkuParam);
        model.addAttribute("skuLsInfoList",list);
        //给平台属性去重
        Set<Long> valueIdSet=new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : list) {
          for (int i=0;i<pmsSearchSkuInfo.getSkuAttrValueList().size();i++){
              Map<Object,Object> pmSkuAttrValue= (Map<Object, Object>) pmsSearchSkuInfo.getSkuAttrValueList().get(i);
              System.out.println(pmsSearchSkuInfo);
              valueIdSet.add(Long.parseLong(pmSkuAttrValue.get("valueId").toString()));
          }
        }
        System.out.println(valueIdSet);
        return "list";
    }
}
