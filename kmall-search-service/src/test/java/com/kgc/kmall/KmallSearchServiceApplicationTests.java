package com.kgc.kmall;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.dubbo.config.annotation.Reference;
import org.assertj.core.internal.bytebuddy.description.type.TypeDefinition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class KmallSearchServiceApplicationTests {
    @Resource
    JestClient jestClient;
    @Reference
    SkuService skuService;
    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Test
    void contextLoads() {
        List<PmsSkuInfo> allSku = skuService.getAllSku();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : allSku) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfo.setProductId(pmsSkuInfo.getSpuId());
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index index = new Index.Builder(pmsSearchSkuInfo).index("kmall").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId() + "").build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void test01() {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        String json = "{\n" +
                "  \"query\": {\n" +
                "    \"range\": {\n" +
                "      \"price\": {\n" +
                "        \"gte\": 4000,\n" +
                "        \"lte\": 999999999\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(json).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                pmsSearchSkuInfos.add(source);
                System.out.println(source.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test02() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", 39);
        TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId", 43);
        boolQueryBuilder.filter(termQueryBuilder);
        boolQueryBuilder.filter(termQueryBuilder2);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "iphone");
        boolQueryBuilder.must(matchQueryBuilder);
        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        searchSourceBuilder.sort("id", SortOrder.DESC);
        //返回查询语句
        System.out.println(searchSourceBuilder.toString());
//查询
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                System.out.println(source.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test03() {
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        boolQueryBuilder.must(new MatchQueryBuilder("skuName","iphone"));
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery,PmsSearchSkuInfo.class);
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            System.out.println(pmsSearchSkuInfo.toString());
        }
     /*   BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        boolQueryBuilder.must(new MatchQueryBuilder("skuName","iphone"));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery, PmsSearchSkuInfo.class);
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            System.out.println(pmsSearchSkuInfo.toString());
        }*/
    }
}
