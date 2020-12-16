package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.service.PmsBaseCatalog1Service;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {
	@Reference
	PmsBaseCatalog1Service pmsBaseCatalog1Service;
	@Test
	void contextLoads() {
		List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Service.selectAllPmsBaseCatalog1();
		for (PmsBaseCatalog1 pmsBaseCatalog1 : pmsBaseCatalog1s) {
			System.out.println(pmsBaseCatalog1.toString());
		}
	}

}
