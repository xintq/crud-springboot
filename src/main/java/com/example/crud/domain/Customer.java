/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA定义了one-to-one、one-to-many、many-to-one、many-to-many 4种关系。
 * 对于数据库来说，通常在一个表中记录对另一个表的外键关联；
 * 对应到实体对象，持有关联数据的一方称为owning-side，另一方称为inverse-side。
 * 关系类型                      Owning-Side     Inverse-Side
 * one-to-one	                @OneToOne       @OneToOne(mappedBy="othersideName")
 * one-to-many / many-to-one	@ManyToOne	    @OneToMany(mappedBy="xxx")
 * many-to-many	                @ManyToMany	    @ManyToMany(mappedBy="xxx")
 *
 * 其中 many-to-many关系的owning-side可以使用@JoinTable声明自定义关联表。
 *
 * 关联关系还可以定制延迟加载和级联操作的行为（owning-side和inverse-side可以分别设置）：
 * 通过设置fetch=FetchType.LAZY 或 fetch=FetchType.EAGER来决定关联对象是延迟加载或立即加载。
 * 通过设置cascade={options}可以设置级联操作的行为(给当前设置的实体操作另一个实体的权限)，
 * 其中options可以是以下组合：
 * CascadeType.MERGE 级联更新
 * CascadeType.PERSIST 级联保存，保存当前实体时，与他有映射关系的实体也会保存
 * CascadeType.REFRESH 级联刷新
 * CascadeType.REMOVE 级联删除, 删除当前实体时，与它有映射关系的实体也会跟着被删除。
 * CascadeType.DETACH 级联脱管/游离，如果你要删除一个实体，但是它有外键无法删除，你就需要这个级联权限了。它会撤销所有相关的外键关联。
 * CascadeType.ALL 级联上述所有操作,慎用级联关系，不要随便给ALL权限操作
 *
 * 注意： 在一个实体文件中，注解要么全部放在字段上，要么全部放在get方法上，不能混合使用！
 * 关于注解的位置：在成员变量上，还是在Getter方法上？根据EJB3规范的建议，最好将注解用在Getter方法上。
 * 而且Hibernate注解参考上也是将注解使用在Getter方法上，而且特别强调了不能混合使用：
 * http://docs.jboss.org/hibernate/stable/annotations/reference/en/html_single/
 */

@Entity // 对实体注释。任何Hibernate映射对象都要有这个注释
public class Customer implements Serializable{
    private Long id;
    private String name;

    private String alias;

    private String industry;

    private Region region;

    // 这里使用List而不是Set的原因是为了能与前台Form绑定
    // 确点是在保存Customer对象时，需要对Product列表进行去重
    private List<Product> products = new ArrayList<>();

    public Customer() {
        this.products = new ArrayList<>();
    }


    public Customer(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public Customer(String name, String alias) {
        super();
        this.name = name;
        this.alias = alias;
        this.products = new ArrayList<>();
    }

    public Customer(String name, String alias, String industry, Region region, List<Product> products) {
        this.name = name;
        this.alias = alias;
        this.industry = industry;
        this.region = region;
        this.products = products;
    }

    //@NotBlank(message = "区域不能为空")
    //@Size(min = 2, message = "区域长度不小于2个字符")
    @NotNull(message = "区域不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    // 声明此属性为主键。该属性值可以通过应该自身创建，但是Hibernate推荐通过Hibernate生成
    @Id
    // 指定主键的生成策略。有如下四个值
    // TABLE：使用表保存id值
    // IDENTITY：identitycolumn
    // SEQUENCR ：sequence
    // AUTO：根据数据库的不同使用上面三个
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull(message = "客户名称不能为空")
    @Size(min = 2, max = 30, message = "客户名称长度需要在2到30个字符之内")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @NotBlank(message = "行业不能为空")
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "customer_product",
            joinColumns = {@JoinColumn(name = "customer_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")}
    )
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!id.equals(customer.id)) return false;
        if (name != null ? !name.equals(customer.name) : customer.name != null) return false;
        if (alias != null ? !alias.equals(customer.alias) : customer.alias != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
