/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.web;

import com.example.crud.domain.Region;
import com.example.crud.domain.RegionRepository;
import com.example.crud.util.DateFormatter;
import com.example.crud.util.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class RegionController {
    @Autowired
    private RegionRepository regionRepository;

    /***
     * /region?page=1&size=10&q=keyword
     * @param model
     * @param page
     * @param size
     * @param q
     * @return
     */
    @RequestMapping("/region")
    public String index(ModelMap model,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size,
                        @RequestParam(value = "q", defaultValue = "") String q) {
        PageWrapper<Region> pageWrapper = new PageWrapper<>(
                regionRepository.findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
                        q,
                        q,
                        q,
                        q,
                        new PageRequest(page, size)),
                "/region?q=" + q
        );
        model.put("page", pageWrapper);
        model.put("list", pageWrapper.getContent());
        model.put("region", new Region());
        model.put("q", q);
        return "/region/index";
    }


    @RequestMapping("/region/delete/{id}")
    public String delete(@PathVariable Long id, ModelMap model) {
        Region region = regionRepository.findOne(id);
        if (region == null || ! region.getCustomers().isEmpty()){
            if (null == region) {
                model.addAttribute("errors", "Delete failed, object not existed!");
            } else {
                model.addAttribute("errors", "Delete failed, object in use!");
            }

            // return "forward:/hello" => 转发到能够匹配 /hello 的 controller 上
            // return "hello" => 实际上还是转发，只不过是框架会找到该逻辑视图名对应的 View 并渲染
            // return "/hello" => 同 return "hello"
            return "forward:/region";
        } else {
            regionRepository.delete(id);
        }
        return "redirect:/region";
    }

    @RequestMapping("/region/toAdd")
    public String add(ModelMap model) {
        model.addAttribute("region", new Region());
        return "/region/add";
    }

    @GetMapping("/region/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        model.addAttribute("region", regionRepository.findOne(id));
        return "/region/edit";
    }

    @PostMapping(value = "/region/update", params = {"save"})
    public String update(ModelMap model,
                         @Valid Region region,
                         BindingResult bindingResult) throws Exception{
        if (bindingResult.hasErrors()) {
            return "/region/edit";
        } else {
            regionRepository.save(region);
            return "redirect:/region?q=" + URLEncoder.encode(region.getName(), "utf-8");
        }
    }

    @PostMapping(value = "/region/add", params = {"save"})
    public String save(final ModelMap model,
                       @Valid final Region region,
                       final BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "/region/add";
        } else {
            regionRepository.save(region);
            return "redirect:/region?q=" + URLEncoder.encode(region.getName(), "utf-8");
        }
    }

    @PostMapping("/region/imp")
    public String imp(String content, ModelMap model) {
        int imported = 0;
        List<String> lines = Arrays.asList(content.split("\n"));
        if (lines.isEmpty()) { // 没有数据则退出
            imported = 0;
        } else {
            Set<Region> regions = new HashSet<>();
            for (String line : lines) {
                line = line.trim();
                // #NAME,LOCALNAME,CODE,TELCODE,TIMEZONE
                // 如果行中包含TAB，则以TAB分割，否则以『,』分割
                String[] fields = line.split(line.contains("\t") ? "\t" : ",");

                // 解析到的字段个数不够5个，则错误退出
                if (fields.length != 5) imported = -1;

                Region region = new Region();
                region.setName(fields[0].trim());
                region.setLocalName(fields[1].trim());
                region.setCode(fields[2].trim());
                region.setTelCode(fields[3].trim());
                region.setTimeZone(fields[4].trim());
                regions.add(region);
            }

            // 持久化到数据库中
            imported = regionRepository.save(regions).size();
        }
        model.addAttribute("content", content);
        model.addAttribute("imported", imported);
        return "/region/import";
    }

    @GetMapping("/region/toImport")
    public String imp() {
        return "/region/import";
    }

    @RequestMapping("/region/exp")
    public ResponseEntity<ByteArrayResource> exp(ModelMap model,
                                                 @RequestParam(value = "q", defaultValue = "") String q) throws IOException {
        List<Region> regions = regionRepository.findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
                q,
                q,
                q,
                q);
        StringBuilder content = new StringBuilder("");
        for (Region region : regions){
            // #NAME,LOCALNAME,CODE,TELCODE,TIMEZONE
            content.append(region.getName()).append("\t")
                    .append(region.getLocalName()).append("\t")
                    .append(region.getCode()).append("\t")
                    .append(region.getTelCode()).append("\t")
                    .append(region.getTimeZone()).append("\n");
        }
        byte[] data =  content.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);
        String fileName = "REGIONS_" + DateFormatter.nowAsyyyyMMddHHmmss() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }
}
