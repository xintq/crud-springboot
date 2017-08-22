package com.example.crud.web;

import com.example.crud.domain.*;
import com.example.crud.service.CustomerService;
import com.example.crud.util.ChartsResponse;
import com.example.crud.util.DateFormatter;
import com.example.crud.util.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ProductRepository productRepository;

    /***
     * /customer?page=1&size=10&q=keyword
     * @param model
     * @param page
     * @param size
     * @param q
     * @return
     */
    @RequestMapping("/customer")
    public String index(ModelMap model,
                      @RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "10") int size,
                      @RequestParam(value = "q", defaultValue = "") String q) {
        PageWrapper<Customer> pageWrapper = new PageWrapper<>(
                customerService.findAll(q, new PageRequest(page, size)),
                "/customer?q=" + q
        );
        model.put("page", pageWrapper);
        model.put("list", pageWrapper.getContent());
        model.put("customer", new Customer());
        model.put("q", q);
        return "/customer/index";
    }

    /***
     * Find all the region objects and bind them with the model
     * @return
     */
    @ModelAttribute("allRegions")
    public List<Region> populateRegions() {
        return regionRepository.findAll();
    }

    /***
     * Find all the product objects and bind them with the model
     * @return
     */
    @ModelAttribute("allProducts")
    public List<Product> populateProducts() {
        return productRepository.findAll();
    }

    @RequestMapping("/customer/delete/{id}")
    public String delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customer";
    }

    @RequestMapping("/customer/toAdd")
    public String add(ModelMap model) {
        model.addAttribute("customer", new Customer());
        return "/customer/add";
    }

    @GetMapping("/customer/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        model.addAttribute("customer", customerService.findCustomer(id));
        return "/customer/edit";
    }

    @PostMapping(value = "/customer/update", params = {"save"})
    public String update(ModelMap model,
                         @Valid Customer customer,
                         BindingResult bindingResult) throws Exception{
        if (bindingResult.hasErrors()) {
            return "/customer/edit";
        } else {
            customerService.saveCustomer(customer);
            return "redirect:/customer?q=" + URLEncoder.encode(customer.getName(), "utf-8");
        }
    }

    @PostMapping(value = "/customer/update", params = {"addProduct"})
    public String addProductForUpdate(final Customer customer,
                                      final BindingResult bindingResult) {
        customer.getProducts().add(new Product());
        return "/customer/edit";
    }

    @PostMapping(value = "/customer/update", params = {"removeProduct"})
    public String removeProductForUpdate(final Customer customer,
                                         final BindingResult bindingResult,
                                         final HttpServletRequest request) {
        int rowIndex = Integer.valueOf(request.getParameter("removeProduct"));
        customer.getProducts().remove(rowIndex);
        return "/customer/edit";
    }

    @PostMapping(value = "/customer/add", params = {"save"})
    public String save(final ModelMap model,
                              @Valid final Customer customer,
                              final BindingResult bindingResult) throws Exception{
        if (bindingResult.hasErrors()) {
            return "/customer/add";
        } else {
            customerService.saveCustomer(customer);
            return "redirect:/customer?q=" + URLEncoder.encode(customer.getName(), "utf-8");
        }
    }

    @PostMapping(value = "/customer/add", params = {"addProduct"})
    public String addProduct(final Customer customer,
                             final BindingResult bindingResult) {
        customer.getProducts().add(new Product());
        return "/customer/add";
    }

    @PostMapping(value = "/customer/add", params = {"removeProduct"})
    public String removeProduct(final Customer customer,
                                final BindingResult bindingResult,
                                final HttpServletRequest request) {
        int rowIndex = Integer.valueOf(request.getParameter("removeProduct"));
        customer.getProducts().remove(rowIndex);
        return "/customer/add";
    }

    @PostMapping("/customer/imp")
    public String imp(String content, ModelMap model) {
        int importedCustomerNum = customerService.importCustomers(Arrays.asList(content.split("\n")));
        model.addAttribute("content", content);
        model.addAttribute("importedCustomers", importedCustomerNum);
        return "/customer/import";
    }

    @GetMapping("/customer/toImport")
    public String imp() {
        return "/customer/import";
    }

    @RequestMapping("/customer/exp")
    public ResponseEntity<ByteArrayResource> exp(ModelMap model,
                                                 @RequestParam(value = "q", defaultValue = "") String q) throws IOException {
        byte[] data = customerService.findAllAsByteArray(q);
        ByteArrayResource resource = new ByteArrayResource(data);
        String fileName = "CUSTOMER_" + DateFormatter.nowAsyyyyMMddHHmmss() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    @RequestMapping(value = "/customer/chart", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<ChartsResponse> viewChart(){
        List<ChartsResponse> chartsResponses = new ArrayList<>();
        chartsResponses.add(customerService.getChartData());
        return chartsResponses;
    }

    @GetMapping("/customer/toProduct")
    public String category() {
        return "/customer/product";
    }

    @PostMapping(value = "/customer/product", params = {"save"})
    public String category(ModelMap model,
                           @RequestParam(value = "newValues", defaultValue = "") String[] newValues,
                           @RequestParam(value = "values", defaultValue = "") String[] values,
                           @RequestParam(value = "ids", defaultValue = "")String[] ids){
        List<Product> products = new ArrayList<>();
        Product product = null;

        for (int i = 0; i < ids.length; i++){
            product = productRepository.findOne(Long.valueOf(ids[i]));
            product.setName(values[i]);
            products.add(product);
        }

        for (String value : newValues){
            Product newProduct = new Product(value);
            product = productRepository.findOne(Example.of(newProduct));
            if (null == product){
                product = newProduct;
                products.add(product);
            }
        }

        productRepository.save(products);

        return "redirect:/customer/toProduct";

    }

    @PostMapping(value = "/customer/product", params = {"delete"})
    public String category(ModelMap model,
                           @RequestParam(value = "del", defaultValue = "")String[] del){
        if (del.length > 0) {
            for (String delId : del) {
                long id = Long.valueOf(delId);
                Product product = productRepository.getOne(id);
                if (null == product) {
                    model.addAttribute("errors", "Delete failed - object not existed!");
                    return "forward:/customer/toProduct";
                } else if (!product.getCustomers().isEmpty()) {
                    model.addAttribute("errors", "Delete failed - object in use!");
                    return "forward:/customer/toProduct";
                } else {
                    productRepository.delete(id);
                }
            }
        }
        return "redirect:/customer/toProduct";
    }
}
