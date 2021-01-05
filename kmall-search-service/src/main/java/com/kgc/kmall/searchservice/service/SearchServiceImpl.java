package com.kgc.kmall.searchservice.service;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    JestClient jestClient;
    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        //封装条件
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuParam.getSkuAttrValueList();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//判断catalog3Id是否为空
        if (catalog3Id != null) {
            //不为空就filter(catalog3Id)
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
//判断skuAttrValueList是否为空
        if (skuAttrValueList != null) {
            //不为空就循环并filter(skuAttrValueList.valueId)
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue.getValueId());
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
//判断keyword是否为空&&并且字符串不为空字符串
        if (keyword != null && keyword.isEmpty() == false) {
            //不为空&&并且字符串不为空字符串就循环并must(skuName)
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.postTags("</span>");



     /*   searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("id", SortOrder.DESC);
        String dsl = searchSourceBuilder.toString();
        Search search=new Search.Builder(dsl).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
                //获取高亮数据
            Map<String, List<String>> highlight = hit.highlight;
            if (highlight!=null){
                String skuName = highlight.get("skuName").get(0);
                //使用高亮的skuName替换原来的skuName
                pmsSearchSkuInfo.setSkuName(skuName);
            }
                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .build();
        pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery, PmsSearchSkuInfo.class);

        //实现高亮处理
        AggregatedPage<PmsSearchSkuInfo> pmsSearchSkuInfos1 = elasticsearchRestTemplate.queryForPage(searchQuery, PmsSearchSkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {

                ArrayList<PmsSearchSkuInfo> pmsSearchSkuInfos2 = new ArrayList<>();
                SearchHits hits = response.getHits();
                for (SearchHit searchHit : hits) {
                    if (hits.getHits().length <= 0) {
                        return null;
                    }

                    PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();

                    String highLightMessage = searchHit.getHighlightFields().get("skuName").fragments()[0].toString();
                    pmsSearchSkuInfo.setSkuName(highLightMessage);
                    //封装数据
                    pmsSearchSkuInfo.setId(Long.parseLong(searchHit.getId()));
                    pmsSearchSkuInfo.setProductId(Long.parseLong(searchHit.getSourceAsMap().get("productId").toString()));
                    pmsSearchSkuInfo.setCatalog3Id(Long.parseLong(searchHit.getSourceAsMap().get("catalog3Id").toString()));
                    pmsSearchSkuInfo.setPrice(Double.parseDouble(searchHit.getSourceAsMap().get("price").toString()));
                    pmsSearchSkuInfo.setSkuAttrValueList((List<PmsSkuAttrValue>) searchHit.getSourceAsMap().get("skuAttrValueList"));
                    pmsSearchSkuInfo.setSkuDefaultImg(searchHit.getSourceAsMap().get("skuDefaultImg").toString());
                    pmsSearchSkuInfo.setSkuDesc(searchHit.getSourceAsMap().get("skuDesc").toString());
                    pmsSearchSkuInfos2.add(pmsSearchSkuInfo);

                }
                if (pmsSearchSkuInfos2.size() > 0) {
                    return new AggregatedPageImpl<T>((List<T>) pmsSearchSkuInfos2);
                }
                return null;
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        if (pmsSearchSkuInfos1 != null) {
            return pmsSearchSkuInfos1.toList();
        }
        return null;
    }

}
