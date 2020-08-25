package org.openpaas.servicebroker.cubrid.service.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.servicebroker.cubrid.CubridConfiguration;
import org.openpaas.servicebroker.cubrid.exception.CubridServiceException;
import org.openpaas.servicebroker.cubrid.model.CubridServiceInstance;
import org.openpaas.servicebroker.cubrid.model.CubridServiceInstanceBinding;
import org.openpaas.servicebroker.util.JSchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CubridConfiguration.class)
@Ignore
public class CubridAdminServiceTest {
	
	@Autowired
	private JSchUtil jsch;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private CubridAdminService cubridAdminService;
	
	@Before
	public void initialize() {
		jsch.enableDebug();
		cubridAdminService = new CubridAdminService(jsch, jdbcTemplate);
		
	}
	
	@After
	public void closer() {
		
	}
	
	@Test
	public void createDatabase() throws CubridServiceException {
		CubridServiceInstance serviceInstance = new CubridServiceInstance();
		serviceInstance.setServiceInstanceId("test_createdb_A");
		serviceInstance.setDatabaseName("test_createdb_A");
		serviceInstance.setPlanId("cubrid-plan-A");
		cubridAdminService.createDatabase(serviceInstance );
	}
	
	@Test
	public void deleteDatabase() throws CubridServiceException {
		CubridServiceInstance serviceInstance = new CubridServiceInstance();
		serviceInstance.setDatabaseName("87e6aeb685613f06");
		
		cubridAdminService.deleteDatabase(serviceInstance );
	}
	
	@Test
	public void createUser() throws CubridServiceException {
		cubridAdminService.createUser("testdb", "testuser2", "testpass2");
	}
	
	@Test
	public void deleteUser() throws CubridServiceException {
		cubridAdminService.deleteUser("testdb", "testuser2");
	}
	
	@Test
	public void getConnectionString() {
		String database="test_db_name";
		String username="test_user";
		String password="test_passwd";
		
		String expected = "jdbc:cubrid:localhost:30000:"+database+":"+username+":"+password+":";
		assertEquals(expected, cubridAdminService.getConnectionString(database, username, password));
	}
	
	@Test
	public void isExistsService() throws CubridServiceException {
		CubridServiceInstance instance = new CubridServiceInstance();
		
		instance.setDatabaseName("test_db_name");
		assertTrue(cubridAdminService.isExistsService(instance));
		
		instance.setDatabaseName("test_db_name_not_exist");
		assertFalse(cubridAdminService.isExistsService(instance));
	}
	
	@Test
	public void isExistsUser() throws CubridServiceException {
		
		assertTrue(cubridAdminService.isExistsUser("test_db_name", "test_db_user_name"));
		assertFalse(cubridAdminService.isExistsUser("test_db_name", "test_db_user_name_not_exist"));
	}
	
	@Test
	public void findById() throws CubridServiceException {
		String service_instance_id = "test_service_instance_id";
		String plan_id = "test_plan_id";
		String db_name = "test_db_name";
		
		CubridServiceInstance serviceInstance = cubridAdminService.findById(service_instance_id);
		
		assertEquals(service_instance_id, serviceInstance.getServiceInstanceId());
		assertEquals(plan_id, serviceInstance.getPlanId());
		assertEquals(db_name, serviceInstance.getDatabaseName());
		
	}
	
	@Test
	public void findBindById() throws CubridServiceException {
		String service_instancne_binding_id = "test_service_instance_binding_id";
		String service_instance_id = "test_service_instance_id";
		String db_user_name = "test_db_user_name";
		
		CubridServiceInstanceBinding serviceInstanceBinding = cubridAdminService.findBindById(service_instancne_binding_id);
		
		assertEquals(service_instancne_binding_id, serviceInstanceBinding.getId());
		assertEquals(service_instance_id, serviceInstanceBinding.getServiceInstanceId());
		assertEquals(db_user_name, serviceInstanceBinding.getDatabaseUserName());
		
	}
	
	@Test public void getServiceAddress() {
		StringBuilder builder = new StringBuilder();
		
		DriverManagerDataSource ds = (DriverManagerDataSource) jdbcTemplate.getDataSource();
		
		String[] split = ds.getUrl().split(":");
		
		builder.append(jsch.getHostname());
		builder.append(":");
		builder.append("30000");
		builder.append(":");
		
		System.out.println(split[3]);
	}

}
