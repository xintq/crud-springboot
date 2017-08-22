package com.example.crud.web;

import com.example.crud.domain.*;
import com.example.crud.util.ChartsResponse;
import com.example.crud.util.Dataset;
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

import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class LinkController {

    @Autowired
    private LinkRepository  linkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /***
     * /link?page=1&size=10&q=keyword
     * @param model
     * @param page
     * @param size
     * @param q
     * @return
     */
    @RequestMapping("/link")
    public String index(ModelMap model,
                      @RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "10") int size,
                      @RequestParam(value = "q", defaultValue = "") String q) {
        PageWrapper<Link> pageWrapper = new PageWrapper<>(
                linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(
                        q,
                        q,
                        new PageRequest(page, size)),
                "/link?q=" + q
        );
        model.put("page", pageWrapper);
        model.put("list", pageWrapper.getContent());
        model.put("link", new Link());
        model.put("q", q);
        return "/link/index";
    }

    @ModelAttribute("allCategories")
    public List<Category> populateCategories() {
        return categoryRepository.findAll();
    }

    @RequestMapping("/link/delete/{id}")
    public String delete(@PathVariable Long id, ModelMap model) {
        Link link = linkRepository.findOne(id);
        if (link == null){
            model.addAttribute("errors", "Delete failed, object not existed!");
            // return "forward:/hello" => 转发到能够匹配 /hello 的 controller 上
            // return "hello" => 实际上还是转发，只不过是框架会找到该逻辑视图名对应的 View 并渲染
            // return "/hello" => 同 return "hello"
            return "forward:/link";
        } else {
            linkRepository.delete(id);
        }
        return "redirect:/link";
    }

    @RequestMapping("/link/toAdd")
    public String add(ModelMap model) {
        model.addAttribute("link", new Link());
        return "/link/add";
    }

    @GetMapping("/link/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        model.addAttribute("link", linkRepository.findOne(id));
        return "/link/edit";
    }

    @PostMapping(value = "/link/update", params = {"save"})
    public String update(ModelMap model,
                         @Valid Link link,
                         BindingResult bindingResult) throws Exception{
        if (bindingResult.hasErrors()) {
            return "/link/edit";
        } else {
            linkRepository.save(link);
            return "redirect:/link?q=" + URLEncoder.encode(link.getName(), "utf-8");
        }
    }

    @PostMapping(value = "/link/add", params = {"save"})
    public String save(final ModelMap model,
                              @Valid final Link link,
                              final BindingResult bindingResult) throws Exception{
        if (bindingResult.hasErrors()) {
            return "/link/add";
        } else {
            linkRepository.save(link);
            return "redirect:/link?q=" + URLEncoder.encode(link.getName(), "utf-8");
        }
    }

    @PostMapping("/link/imp")
    public String imp(String content, ModelMap model) {
        int imported = 0;
        List<String> lines = Arrays.asList(content.split("\n"));
        if (lines.isEmpty()) { // 没有数据则退出
            imported = 0;
        } else {
            Set<Link> links = new HashSet<>();
            for (String line : lines) {
                line = line.trim();
                // #NAME,URL,CATEGORY
                // 如果行中包含TAB，则以TAB分割，否则以『,』分割
                String[] fields = line.split(line.contains("\t") ? "\t" : ",");

                // 解析到的字段个数不够3个，则错误退出
                if (fields.length != 3) imported = -1;

                Link link = new Link();
                link.setName(fields[0].trim());
                link.setUrl(fields[1].trim());
                String categoryName = fields[2].trim();
//                Category category = categoryRepository.findByName(categoryName);
                Category category = categoryRepository.findOne(Example.of(new Category(categoryName)));
                if (null == category) category = new Category(categoryName);
                link.setCategory(category);
                links.add(link);
            }

            // 持久化到数据库中
            imported = linkRepository.save(links).size();
        }
        model.addAttribute("content", content);
        model.addAttribute("imported", imported);
        return "/link/import";
    }

    @GetMapping("/link/toImport")
    public String imp() {
        return "/link/import";
    }

    @GetMapping("/link/toCategory")
    public String category() {
        return "/link/category";
    }

    @RequestMapping("/link/exp")
    public ResponseEntity<ByteArrayResource> exp(ModelMap model,
                                                 @RequestParam(value = "q", defaultValue = "") String q) throws IOException {
        List<Link> links = linkRepository.findByNameContainingOrUrlContainingAllIgnoringCase(
                q,
                q);
        StringBuilder content = new StringBuilder("");
        for (Link link : links){
            // NAME,URL,CATEGORY
            content.append(link.getName()).append("\t")
                    .append(link.getUrl()).append("\t")
                    .append(link.getCategory().getValue()).append("\n");
        }
        byte[] data =  content.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);
        String fileName = "LINKS_" + DateFormatter.nowAsyyyyMMddHHmmss() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping(value = "/link/category", params = {"save"})
    public String category(ModelMap model,
                           @RequestParam(value = "newValues", defaultValue = "") String[] newValues,
                           @RequestParam(value = "values", defaultValue = "") String[] values,
                           @RequestParam(value = "ids", defaultValue = "")String[] ids){
        List<Category> categories = new ArrayList<>();
        Category category = null;

        for (int i = 0; i < ids.length; i++){
            category = categoryRepository.findOne(Long.valueOf(ids[i]));
            category.setValue(values[i]);
            categories.add(category);
        }

        for (String value : newValues){
            Category newCatagory = new Category(value);
            category = categoryRepository.findOne(Example.of(newCatagory));
            if (null == category){
                category = newCatagory;
                categories.add(category);
            }
        }

        categoryRepository.save(categories);

        return "redirect:/link/toCategory";

    }

    @PostMapping(value = "/link/category", params = {"delete"})
    public String category(ModelMap model,
                           @RequestParam(value = "del", defaultValue = "")String[] del){
        if (del.length > 0) {
            for (String delId : del) {
                long id = Long.valueOf(delId);
                Category category = categoryRepository.getOne(id);
                if (null == category) {
                    model.addAttribute("errors", "Delete failed - object not existed!");
                    return "forward:/link/toCategory";
                } else if (!category.getLinks().isEmpty()) {
                    model.addAttribute("errors", "Delete failed - object in use!");
                    return "forward:/link/toCategory";
                } else {
                    categoryRepository.delete(id);
                }
            }
        }
        return "redirect:/link/toCategory";
    }

    @RequestMapping(value = "/link/chart", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<ChartsResponse> viewChart(){
        List<ChartsResponse> chartsResponses = new ArrayList<>();

        ChartsResponse chartsResponse = new ChartsResponse();
        chartsResponse.setChartName("Link Chart");

        List<String> lables = new ArrayList<>();
        List<Dataset> datasets = new ArrayList<>();
        Dataset dataset = new Dataset();
        List<Integer> data = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            lables.add(category.getValue());
            data.add(category.getLinks().size());
        }
        chartsResponse.setLabels(lables);
        dataset.setLabel("Links");
        //dataset.setFillColor("rgba(255, 99, 132, 0.2)");
        //dataset.setStrokeColor("rgba(255,99,132,1)");
        dataset.setData(data);
        datasets.add(dataset);

        chartsResponse.setDatasets(datasets);

        chartsResponses.add(chartsResponse);
        return chartsResponses;
    }
}
