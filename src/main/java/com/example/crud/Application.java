package com.example.crud;

import com.example.crud.domain.Region;
import com.example.crud.domain.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// same as @Configuration @EnableAutoConfiguration @ComponentScan
@SpringBootApplication
@EnableCaching //开启缓存功能
public class Application {

	public static void main(String[] args) {
		//System.setProperty("spring.devtools.restart.enabled", "false");
		//SpringApplication application = new SpringApplication(Application.class);
		//application.setBannerMode(Banner.Mode.OFF);
		//application.run(args);
		SpringApplication.run(Application.class, args);
	}

	public Application(RegionRepository regionRepository) {
		this.regionRepository = regionRepository;
		try {
			//regionRepository.deleteAllInBatch();
			//registerRegions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Autowired
	private RegionRepository regionRepository;
	public void registerRegions() throws IOException {
		Resource resource = new ClassPathResource("/regions.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
		String line = null;
		Region r = null;
		List<Region> regionList = new ArrayList<>();
		while ((line = br.readLine()) != null) {
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
}
