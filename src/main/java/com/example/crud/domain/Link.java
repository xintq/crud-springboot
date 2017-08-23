/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Cacheable
public class Link implements Serializable{
    private Long id;
    private String name;
    private String url;
    private Category category;

    public Link() {
    }

    public Link(String name, String url, Category category) {
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull(message = "名称不能为空")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "地址不能为空")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NotNull(message = "类别不能为空")
    @ManyToOne(cascade = CascadeType.PERSIST)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (name != null ? !name.equals(link.name) : link.name != null) return false;
        if (url != null ? !url.equals(link.url) : link.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
