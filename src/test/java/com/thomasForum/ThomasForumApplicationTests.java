package com.thomasForum;

import com.thomasForum.config.AlphaConfig;
import com.thomasForum.dao.AlphaDao;
import com.thomasForum.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = ThomasForumApplication.class)
class ThomasForumApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext(){
		AlphaDao alphaDao = applicationContext.getBean("alphaDaoHibernateImp",AlphaDao.class);
		alphaDao.select();
	}
	@Test
	public void testApplicationManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
	@Autowired
	@Qualifier("alphaDaoHibernateImp")
	private AlphaDao alphaDao;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Autowired
	private AlphaService alphaService;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(simpleDateFormat);
		System.out.println(alphaService);
	}


}
