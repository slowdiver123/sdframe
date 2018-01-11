/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sd.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.sd.entity.SdTest;
import com.jeeplus.modules.sd.service.SdTestService;

/**
 * sdtestController
 * @author sd
 * @version 2018-01-10
 */
@Controller
@RequestMapping(value = "${adminPath}/sd/sdTest")
public class SdTestController extends BaseController {

	@Autowired
	private SdTestService sdTestService;
	
	@ModelAttribute
	public SdTest get(@RequestParam(required=false) String id) {
		SdTest entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sdTestService.get(id);
		}
		if (entity == null){
			entity = new SdTest();
		}
		return entity;
	}
	
	/**
	 * sdtest列表页面
	 */
	@RequiresPermissions("sd:sdTest:list")
	@RequestMapping(value = {"list", ""})
	public String list(SdTest sdTest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SdTest> page = sdTestService.findPage(new Page<SdTest>(request, response), sdTest); 
		model.addAttribute("page", page);
		return "modules/sd/sdTestList";
	}

	/**
	 * 查看，增加，编辑sdtest表单页面
	 */
	@RequiresPermissions(value={"sd:sdTest:view","sd:sdTest:add","sd:sdTest:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SdTest sdTest, Model model) {
		model.addAttribute("sdTest", sdTest);
		return "modules/sd/sdTestForm";
	}

	/**
	 * 保存sdtest
	 */
	@RequiresPermissions(value={"sd:sdTest:add","sd:sdTest:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(SdTest sdTest, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, sdTest)){
			return form(sdTest, model);
		}
		if(!sdTest.getIsNewRecord()){//编辑表单保存
			SdTest t = sdTestService.get(sdTest.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(sdTest, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			sdTestService.save(t);//保存
		}else{//新增表单保存
			sdTestService.save(sdTest);//保存
		}
		addMessage(redirectAttributes, "保存sdtest成功");
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
	}
	
	/**
	 * 删除sdtest
	 */
	@RequiresPermissions("sd:sdTest:del")
	@RequestMapping(value = "delete")
	public String delete(SdTest sdTest, RedirectAttributes redirectAttributes) {
		sdTestService.delete(sdTest);
		addMessage(redirectAttributes, "删除sdtest成功");
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
	}
	
	/**
	 * 批量删除sdtest
	 */
	@RequiresPermissions("sd:sdTest:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			sdTestService.delete(sdTestService.get(id));
		}
		addMessage(redirectAttributes, "删除sdtest成功");
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("sd:sdTest:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(SdTest sdTest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "sdtest"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SdTest> page = sdTestService.findPage(new Page<SdTest>(request, response, -1), sdTest);
    		new ExportExcel("sdtest", SdTest.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出sdtest记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sd:sdTest:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SdTest> list = ei.getDataList(SdTest.class);
			for (SdTest sdTest : list){
				try{
					sdTestService.save(sdTest);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条sdtest记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条sdtest记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入sdtest失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
    }
	
	/**
	 * 下载导入sdtest数据模板
	 */
	@RequiresPermissions("sd:sdTest:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "sdtest数据导入模板.xlsx";
    		List<SdTest> list = Lists.newArrayList(); 
    		new ExportExcel("sdtest数据", SdTest.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sd/sdTest/?repage";
    }
	
	
	

}