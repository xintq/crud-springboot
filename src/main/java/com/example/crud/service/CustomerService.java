package com.example.crud.service;

import com.example.crud.domain.Customer;
import com.example.crud.util.ChartsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    Page<Customer> findAll(String keyword, Pageable page);

    Page<Customer> findAll(Pageable page);

    Customer findCustomer(Long id);

    Customer  saveCustomer(Customer customer);

    void deleteCustomer(Long id);

    int importCustomers(List<String> lines);

    long totalCount();

    byte[] findAllAsByteArray(String q);

    public ChartsResponse getChartData();
}
