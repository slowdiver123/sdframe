/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sd.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.sd.entity.SdTest;
import com.jeeplus.modules.sd.dao.SdTestDao;

/**
 * sdtestService
 * @author sd
 * @version 2018-01-10
 */
@Service
@Transactional(readOnly = true)
public class SdTestService extends CrudService<SdTestDao, SdTest> {

	public SdTest get(String id) {
		return super.get(id);
	}
	
	public List<SdTest> findList(SdTest sdTest) {
		return super.findList(sdTest);
	}
	
	public Page<SdTest> findPage(Page<SdTest> page, SdTest sdTest) {
		return super.findPage(page, sdTest);
	}
	
	@Transactional(readOnly = false)
	public void save(SdTest sdTest) {
		super.save(sdTest);
	}
	
	@Transactional(readOnly = false)
	public void delete(SdTest sdTest) {
		super.delete(sdTest);
	}
	
	
	
	
}