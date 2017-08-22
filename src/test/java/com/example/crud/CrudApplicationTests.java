package com.example.crud;

import com.example.crud.domain.Product;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrudApplicationTests {

	@Test
	public void contextLoads() {
		Product p1 = new Product("A");
		p1.setId(1l);
		Product p2 = new Product("A");
		p2.setId(2l);

		Set<Product> ps = new HashSet<>();
		ps.add(p1);
		Assert.assertSame(1, ps.size());
		ps.add(p2);
		Assert.assertSame(1, ps.size());
	}

}
