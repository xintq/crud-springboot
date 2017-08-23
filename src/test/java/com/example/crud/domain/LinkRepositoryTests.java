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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LinkRepositoryTests {
    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        Category c1 = new Category("Internal");
        Category c2 = new Category("External");
        Category c3 = new Category("Local");
        //categoryRepository.save(Arrays.asList(c1,c2,c3));

        Link link = new Link();
        link.setCategory(c1);
        link.setName("Home1");
        link.setUrl("http://localhost/1");
        linkRepository.save(link);

        link = new Link();
        link.setCategory(c2);
        link.setName("Home2");
        link.setUrl("http://localhost/2");
        linkRepository.save(link);

        link = new Link();
        link.setCategory(c3);
        link.setName("Home3");
        link.setUrl("http://localhost/3");
        linkRepository.save(link);
    }

    @After
    public void tearDown() throws Exception {
        linkRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void findByName() throws Exception {
        assertThat(linkRepository.findByName("Home1")).isNotNull();
        assertThat(linkRepository.findByName("XXX")).isNull();
    }

    @Test
    public void findAny() throws Exception {
        String q = "home";
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q, new PageRequest(0, 10)).getContent()).hasSize(3);
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q)).hasSize(3);

        q = "localhost";
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q, new PageRequest(0, 10)).getContent()).hasSize(3);
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q)).hasSize(3);

        q = "xxx";
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q, new PageRequest(0, 10)).getContent()).hasSize(0);
        assertThat(linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(q, q)).hasSize(0);

    }
}
