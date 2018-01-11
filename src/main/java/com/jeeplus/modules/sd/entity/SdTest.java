/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sd.entity;


import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * sdtestEntity
 * @author sd
 * @version 2018-01-10
 */
public class SdTest extends DataEntity<SdTest> {
	
	private static final long serialVersionUID = 1L;
	
	public SdTest() {
		super();
	}

	public SdTest(String id){
		super(id);
	}

}