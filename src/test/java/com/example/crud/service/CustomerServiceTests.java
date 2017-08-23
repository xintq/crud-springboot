/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.service;

import com.example.crud.domain.*;
import com.example.crud.util.ChartsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Service测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest // 用来替代 @ContextConfiguration，这个必须加上，否则使用mvn test时会报错
@DataJpaTest
@ComponentScan(basePackages = {"com.example.crud"})
public class CustomerServiceTests {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Before
    public void setup() throws IOException {
        registerRegions();
    }

    /**
     * 从CSV文件导入region数据
     *
     * @throws IOException
     */
    private void registerRegions() throws IOException {
        Resource resource = new ClassPathResource("/regions.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line = null;
        Region r = null;
        List<Region> regionList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) continue;
            String[] csv = line.split(",");
            if (csv.length != 5) continue;
            // NAME,LOCALNAME,CODE,TELCODE,TIMEZONE
            r = new Region(csv[2], csv[0]);
            r.setLocalName(csv[1]);
            r.setTelCode(csv[3]);
            r.setTimeZone(csv[4]);
            regionList.add(r);
        }
        br.close();
        regionRepository.save(regionList);
    }

    @After
    public void tearDown() {
        customerRepository.deleteAll();
        productRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    public void crudCustomer() {
        Product p1 = new Product("SGD");
        Product p2 = new Product("VDI");
        Product p3 = new Product("SunRay");

        Customer c1 = new Customer("Huawei", "华为");
        c1.setIndustry("Telcom");
        c1.setRegion(regionRepository.findByCode("CN"));
        c1.getProducts().addAll(Arrays.asList(p1, p2));

        customerService.saveCustomer(c1);
        assertThat(customerService.findCustomer(c1.getId())).isNotNull();
        assertThat(productRepository.count()).isEqualTo(2);

        Customer c2 = new Customer("ORCL", "Oracle Systems");
        c2.setIndustry("IT");
        c2.setRegion(regionRepository.findByCode("US"));
        c2.getProducts().addAll(Arrays.asList(p2, p3));
        customerService.saveCustomer(c2);
        assertThat(customerService.findCustomer(c2.getId()).getName()).isEqualTo("ORCL");
        assertThat(productRepository.count()).isEqualTo(3);

        Customer c3 = new Customer("Panasonic", "松下電気");
        c3.setIndustry("Manufacture");
        c3.setRegion(regionRepository.findByCode("JP"));
        c3.getProducts().addAll(Arrays.asList(p1, p3));
        customerService.saveCustomer(c3);
        assertThat(customerService.findAll(new PageRequest(0, 10))).hasSize(3);
        assertThat(productRepository.count()).isEqualTo(3);

        customerService.deleteCustomer(c3.getId());
        assertThat(customerService.findAll(new PageRequest(0, 10))).hasSize(2);

        assertThat(customerService.totalCount()).isEqualTo(2L);

        assertThat(customerService.findAll("Oracle", new PageRequest(0, 10)).getContent()).hasSize(1);

        ChartsResponse chartsResponse = customerService.getChartData();
        assertThat(chartsResponse.getDatasets()).hasSize(1);
        assertThat(chartsResponse.getLabels()).hasSize(3);

        byte[] content = customerService.findAllAsByteArray("Oracle");
        assertThat(new String(content)).isEqualTo("ORCL\tOracle Systems\tIT\tVDI/SunRay\tUS\n");
    }

    @Test
    public void importCustomers() {
        List<String> lines = new ArrayList<>();
        //   NAME		ALIAS	INDUSTRY		PRODUCT1/PRODUCT2	REGION
        lines.add("Test1	Test1Alias	Education	SGD/VDI	CN");
        lines.add("Test2\tTest2Alias\tEducation\tVDI\tCN");
        lines.add("Test3\tTest3Alias\tIT\tVDI/SunRay\tJP");
        lines.add("Test4\tTest4Alias\tIT\tVDI/SunRay\tJP");
        lines.add("Test5\tTest5Alias\tAirlines\tSGD/SunRay/VDI\tUSA");
        int result = customerService.importCustomers(lines);
        assertThat(result).isEqualTo(5);
        assertThat(productRepository.count()).isEqualTo(3);
        assertThat(customerService.totalCount()).isEqualTo(5);
    }

}
