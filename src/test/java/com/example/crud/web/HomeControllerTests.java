package com.example.crud.web;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.crud.Application;
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

import java.util.logging.Logger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@AutoConfigureMockMvc
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
        System.out.println(customersCount);
        Assert.assertNotNull(customersCount);
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
