package com.kgc.kmall.controller;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        return "list";
    }
}
