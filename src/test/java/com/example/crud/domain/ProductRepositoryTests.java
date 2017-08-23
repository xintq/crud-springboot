/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() throws Exception {
        Product p1 = new Product("SGD");
        Product p2 = new Product("VDI");
        Product p3 = new Product("SunRay");
        productRepository.save(Arrays.asList(p1, p2, p3));
    }

    @After
    public void tearDown() throws Exception {
        productRepository.deleteAll();
    }

    @Test
    public void findByName() throws Exception {
        assertThat(productRepository.findByName("SGD")).isNotNull();
        assertThat(productRepository.findByName("XXX")).isNull();
    }
}
