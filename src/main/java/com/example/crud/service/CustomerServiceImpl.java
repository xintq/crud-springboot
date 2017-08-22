package com.example.crud.service;

import com.example.crud.domain.*;
import com.example.crud.util.ChartsResponse;
import com.example.crud.util.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ProductRepository productRepository;

    /**
     * 按关键字查询Customer，并以分页的形式返回
     * @param keyword
     * @param page
     * @return
     */
    @Override
    public Page<Customer> findAll(String keyword, Pageable page) {
        if (keyword.isEmpty()) {
            return findAll(page);
        } else {
            return customerRepository.findAny(
                    keyword,
                    keyword,
                    keyword,
                    keyword,
                    page);
        }
    }

    /**
     * 查询所有Customer，并以分页的形式返回
     * @param page
     * @return
     */
    @Override
    public  Page<Customer> findAll(Pageable page) {
        return customerRepository.findAll(page);
    }

    /**
     * 以ID查询Customer
     * @param id
     * @return
     */
    @Override
    public Customer findCustomer(Long id) {
        return customerRepository.findOne(id);
    }

    /**
     * 保存Customer对象，主要检查绑定好的Product是否已经存在，
     * 若存在则查询出来，并为Customer重新绑定所有Product对象。
     */
    @Override
    public Customer saveCustomer(Customer customer) {
        List<Product> products = new ArrayList<>();
        for (Product p : customer.getProducts()) {
            // 去重：对于重复了的Product对象，忽略
            // 使用Set存储Product的话这一步是不需要做的，
            // 但是在Form上不能绑定Set集合，只能使用List，所以这里还是需要去重
            if (products.contains(p) || p.getName().trim().isEmpty()) continue;
            // 将已经存在的Product查询出来
            Product existingProduct = productRepository.findByName(p.getName());
            if (existingProduct != null) {
                p = existingProduct;
            }
            products.add(p);
        }
        customer.getProducts().clear();
        customer.getProducts().addAll(products);
        customer.setRegion(validateRegion(customer.getRegion()));

        return customerRepository.save(customer);
    }

    /**
     * 验证Region对象，若不存在则新建一个缺省的Region对象并返回
     * @param regionCode
     * @return 一定存在的Region对象
     */
    private Region validateRegion(String regionCode){
        Region region = regionRepository.findByCode(regionCode);
        if (null == region) region = regionRepository.findByCode("-");
        if (null == region) {
            region = regionRepository.save(new Region("-", "N/A"));
        }
        return region;
    }

    /**
     * 验证Region对象，若不存在则新建一个缺省的Region对象并返回
     * @param region 要验证的对象
     * @return 一定存在的Region对象
     */
    private Region validateRegion(Region region){
        return  validateRegion(region.getCode());
    }

    /**
     * 删除Customer对象
     * @param id
     */
    @Override
    public void deleteCustomer(Long id) {
        customerRepository.delete(id);
    }

    /**
     * 把以Tab或者「,」分割的数据流导入到数据库中，每一行的数据格式如下：
     *  NAME		ALIAS	INDUSTRY		PRODUCT1/PRODUCT2	REGION
     * @param lines 要导入的数据流
     * @return 成功导入的对象个数，-1表示导入出错
     */
    @Override
    public int importCustomers(List<String> lines) {
        if (lines.isEmpty()) { // 没有数据则退出
            return 0;
        } else {
            Set<Customer> customers = new HashSet<>();
            for (String line : lines) {
                line = line.trim();

                // 如果行中包含TAB，则以TAB分割，否则以『,』分割
                String[] fields = line.split(line.contains("\t") ? "\t" : ",");

                // 解析到的字段个数不够5个，则错误退出
                if (fields.length != 5) return -1;

                Customer customer = new Customer(fields[0].trim(), fields[1].trim());
                customer.setIndustry(fields[2].trim());

                // 验证Region对象，确保其一定存在
                customer.setRegion(validateRegion(fields[4].trim()));

                // 解析产品名称，将已经存在的产品查询出来，不存在的产品则新建对象，
                // 最后绑定到Customer对象上
                // 多个产品名称之间以『/』分割
                String[] prodctNames = fields[3].trim().split("/");
                Set<Product> products = new HashSet<>();
                for (String productName : prodctNames) {
                    Product product = productRepository.findByName(productName);
                    /*
                    if (null == product) {
                    		product = productRepository.save(new Product(productName));
                    }
                    */
                    products.add(null == product ? new Product(productName) : product);
                }
                // 将产品列表绑定到Customer对象上
                customer.getProducts().addAll(products);

                customers.add(customer);
            }

            // 持久化到数据库中
            return customerRepository.save(customers).size();
        }
    }

    /**
     * 统计全部Customer个数
     * @return
     */
    @Override
    public long totalCount() {
        return customerRepository.count();
    }

    /**
     * 检索所有Customer并以CSV文件的形式，以字节数组的方式输出
     * @param q 关键字
     * @return 以字节数组组成的CSV文件
     */
    public byte[] findAllAsByteArray(String q) {
        List<Customer> customers = customerRepository.findAny(q,q,q,q);
        StringBuilder content = new StringBuilder("");
        for (Customer customer : customers){
            content.append(customer.getName()).append("\t")
                    .append(customer.getAlias()).append("\t")
                    .append(customer.getIndustry()).append("\t");
            List<Product> products = customer.getProducts();
            for (int i = 0; i < products.size(); i++){
                content.append(products.get(i).getName());
                if (i != products.size() - 1){
                    content.append("/");
                }
                if (i == products.size() - 1){
                    content.append("\t");
                }
            }
            content.append(customer.getRegion().getCode()).append("\n");
        }
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取图标数据：每种Product对应的Customer数量
     * @return
     */
    public ChartsResponse getChartData(){
        ChartsResponse chartsResponse = new ChartsResponse();
        chartsResponse.setChartName("Customer Chart");

        List<String> lables = new ArrayList<>();
        List<Dataset> datasets = new ArrayList<>();
        Dataset dataset = new Dataset();
        List<Integer> data = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            lables.add(product.getName());
            data.add(product.getCustomers().size());
        }
        chartsResponse.setLabels(lables);
        dataset.setLabel("Customers");
        //dataset.setFillColor("rgba(255, 99, 132, 0.2)");
        //dataset.setStrokeColor("rgba(255,99,132,1)");
        dataset.setData(data);
        datasets.add(dataset);

        chartsResponse.setDatasets(datasets);
        return chartsResponse;
    }
}
