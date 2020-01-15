package com.fymod.swagger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

@Api(value="/show", tags="这是模块名称")
@RestController
public class ShowController {

	@ApiOperation(value="这是名称", notes = "这个备注")
    @PostMapping("/test")
    public String addUser(@RequestBody MyParam param, HttpServletRequest request){
		return param.getOther() + "---" + param.getKeyword() + "---" + request.getHeader("token");
    }
	
}

/**
 * 请求参数，真实场景会是单独的一个文件
 */
class MyParam {
	
	@ApiModelProperty("关键字")
	private String keyword;
	
	@ApiModelProperty("其他内容")
	private String other;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
}
