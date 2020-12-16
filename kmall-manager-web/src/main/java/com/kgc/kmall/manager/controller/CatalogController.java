package com.kgc.kmall.manager.controller;


import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.service.PmsBaseCatalog1Service;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {
    @Reference
    PmsBaseCatalog1Service pmsBaseCatalog1Service;

    @RequestMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Service.selectAllPmsBaseCatalog1();
        return pmsBaseCatalog1s;
    }
}
