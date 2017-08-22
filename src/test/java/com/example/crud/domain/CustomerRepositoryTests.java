package com.example.crud.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Provide a bridge between Spring Boot test features and JUnit.
// Whenever we are using any Spring Boot testing features in out JUnit tests,
// this annotation will be required.
@RunWith(SpringRunner.class)

// Provide some standard setup needed for testing the persistence layer:
//  - configuring H2, an in-memory database
//  - setting Hibernate, Spring Data, and the DataSource
//  - performing an @EntityScan
//  - turning on SQL logging
@DataJpaTest
public class CustomerRepositoryTests {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Before
    public void setUp() throws IOException {
        registerRegions();

        Product p1 = new Product("SGD");
        Product p2 = new Product("VDI");
        Product p3 = new Product("SunRay");

        Customer c1 = new Customer("Huawei", "华为");
        c1.setIndustry("Telcom");
        c1.setRegion(regionRepository.findByCode("CN"));
        c1.getProducts().addAll(Arrays.asList(p1,p2));

        Customer c2 = new Customer("ORCL", "Oracle Systems");
        c2.setIndustry("IT");
        c2.setRegion(regionRepository.findByCode("US"));
        c2.getProducts().addAll(Arrays.asList(p2,p3));

        Customer c3 = new Customer("Panasonic", "松下電気");
        c3.setIndustry("Manufacture");
        c3.setRegion(regionRepository.findByCode("JP"));
        c3.getProducts().addAll(Arrays.asList(p1,p3));

        customerRepository.save(Arrays.asList(c1, c2, c3));
    }

    private void registerRegions() throws IOException {
        Resource resource = new ClassPathResource("/regions.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line = null;
        Region r = null;
        List<Region> regionList = new ArrayList<>();
        while ((line = br.readLine()) != null){
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
    public void tearDown() throws Exception {
        customerRepository.deleteAll();
        productRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    public void findAllCustomers(){
        assertThat(customerRepository.findAll()).hasSize(3);
        assertThat(productRepository.findAll()).hasSize(3);
    }

    @Test
    public void findCustomerByName(){
        assertThat(customerRepository.findByName("Huawei")).isNotNull();
        assertThat(customerRepository.findByName("Huawei").getProducts()).hasSize(2);

        assertThat(productRepository.findByName("SGD")).isNotNull();

        assertThat(customerRepository.findByName("Huawei").getRegion().getCode()).isEqualTo("CN");
    }

    @Test
    public void findCustomerByAlias () {
        assertThat(customerRepository.findByAlias("华为")).hasSize(1);
        assertThat(customerRepository.findByAlias("Oracle Systems")).hasSize(1);
    }

    @Test
    public void findCustomerByRegion(){
        assertThat(customerRepository.findByRegion("CN")).hasSize(1);
    }

    @Test
    public void findCustomerByIndustry(){
        assertThat(customerRepository.findByIndustry("IT")).hasSize(1);
    }

    @Test
    public void findCustomerByPage(){
        Page<Customer> page =  customerRepository.findByNameContainingOrAliasContainingAllIgnoringCase(
                "wei",
                "wei",
                new PageRequest(0, 10));
        assertThat(page.getTotalPages()).isEqualTo(1);

        page = customerRepository.findAny(
           "wei",
           "wei",
           "wei",
           "wei",
                new PageRequest(0, 10)
        );
        assertThat(page.getTotalPages()).isEqualTo(1);
    }

}
