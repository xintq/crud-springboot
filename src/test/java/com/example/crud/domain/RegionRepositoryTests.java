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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class RegionRepositoryTests {
    @Autowired
    private RegionRepository regionRepository;

    @Before
    public void init() throws IOException {
        registerRegions();
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
    public void clean(){
        regionRepository.deleteAll();
    }

    @Test
    public void findAllRegions(){
        assertThat(regionRepository.findAll()).hasSize(192);
    }

    @Test
    public void findRegionByCode(){
        assertThat(regionRepository.findByCode("CN")).isNotNull();
        assertThat(regionRepository.findByCode("CN").getName()).isEqualTo("China");
    }

    @Test
    public void findRegionByNameAndCodingContaining(){
        assertThat(regionRepository.findByCodeContaining("C")).hasSize(20);
        assertThat(regionRepository.findByNameContaining("China")).hasSize(1);
    }

    @Test
    public void findAny() throws Exception {
        String q = "CN";
        Page page = regionRepository.findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
                q, q, q, q,
                new PageRequest(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
        List regions = regionRepository.findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
                q, q, q, q
        );
        assertThat(regions).hasSize(1);
    }
}
