/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.rest.webmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.hal.HalLinkDiscoverer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@Import(AffordanceTests.TestConfiguration.class)
public class AffordanceTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	private HalLinkDiscoverer linkDiscoverer;

	@Autowired
	EmployeeRepository repository;

	@Before
	public void setUp() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		this.linkDiscoverer = new HalLinkDiscoverer();

		this.repository.save(new Employee(1, "Frodo", "ring bearer"));
		this.repository.save(new Employee(2, "Bilbo", "burglar"));
	}

	@Test
	public void test() throws Exception {

		String rootResults = this.mockMvc.perform(get("").accept(MediaTypes.HAL_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn()
			.getResponse().getContentAsString();

		Link profileLink = this.linkDiscoverer.findLinkWithRel("profile", rootResults);
	}

	@Configuration
	@Import(RepositoryRestMvcConfiguration.class)
	static class TestConfiguration {

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Employee {
		@Id int id;
		private String name;
		private String role;
	}

	@RepositoryRestResource
	interface EmployeeRepository extends CrudRepository<Employee, Integer> {

	}

}
