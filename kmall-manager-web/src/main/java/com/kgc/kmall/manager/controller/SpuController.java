package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseSaleAttr;
import com.kgc.kmall.bean.PmsProductImage;
import com.kgc.kmall.bean.PmsProductInfo;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.service.SpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "商品spu管理-显示spu列表", description = "提供用户相关的Rest API")
public class SpuController {
    @Reference
    SpuService spuService;
    @Value("${fileServer.url}")
    String fileUrl;

    @ApiOperation("显示spu列表")
    @GetMapping("/spuList")
    @ApiImplicitParam(name = "catalog3Id",value = "3级分类Id",required = true)
//    @RequestMapping("/spuList")
    public List<PmsProductInfo> supList(Long catalog3Id){
        return spuService.spuList(catalog3Id);
    }
    @ApiOperation("显示销售属性表")
    @PostMapping("/baseSaleAttrList")
//    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
       return spuService.baseSaleAttrList();
    }
    @ApiOperation("图片上传")
    @PostMapping("/fileUpload")
    //@ApiImplicitParam(name = "file",value = "图片上传")
    //@RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file")MultipartFile file) throws Exception{
        //文件上传
        //返回文件上传后的路径
        String imgUrl=fileUrl;
        if(file!=null){
            System.out.println("multipartFile="+file.getName()+"|"+file.getSize());
            String configFile=this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getTrackerServer();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            String filename=    file.getOriginalFilename();
            String extName = FilenameUtils.getExtension(filename);

            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            imgUrl=fileUrl ;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl+="/"+path;
            }
        }
        System.out.println(imgUrl);
        return imgUrl;
    }
    @ApiOperation("显示销售属性列表")
    @PostMapping("/saveSpuInfo")
    @ApiImplicitParam(name = "PmsProductInfo",value = "平台属性")
//    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        //保存数据库
        Integer integer = spuService.saveSpuInfo(pmsProductInfo);
        return integer>0?"success":"fail";
    }
    @ApiOperation("显示销售属性和属性值")
    @PostMapping("/spuSaleAttrList")
    @ApiImplicitParam(name = "spuId",value = "spuId")
//    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(Long spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }
    @ApiOperation("显示图片列表")
    @GetMapping("/spuImageList")
    @ApiImplicitParam(name = "spuId",value = "spuId")
//    @RequestMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(Long spuId){
        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }

}
