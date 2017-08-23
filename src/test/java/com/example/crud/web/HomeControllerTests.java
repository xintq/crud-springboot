/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 独立测试之前需要先打开数据库否则会出错
@RunWith(SpringRunner.class)
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        classes = Application.class)
@SpringBootTest
@AutoConfigureMockMvc
//@DataJpaTest
public class HomeControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void toIndex() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/")
                        .contentType(MediaType.TEXT_HTML)
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();
        Object customersCount = result.getModelAndView().getModelMap().get("customersCount");
        Object linksCount = result.getModelAndView().getModelMap().get("linksCount");
        System.out.println(customersCount);
        System.out.println(linksCount);
        Assert.assertNotNull(customersCount);
        Assert.assertNotNull(linksCount);
    }

    @Test
    public void about() throws Exception {
        MvcResult result = mockMvc.perform(post("/about").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
}
