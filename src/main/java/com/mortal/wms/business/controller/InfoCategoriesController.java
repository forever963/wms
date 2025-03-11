package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.JwtIgnore;
import com.mortal.wms.business.entity.InfoCategories;
import com.mortal.wms.business.mapper.InfoCategoriesMapper;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/infoCategories")
@Tag(name = "字典")
public class InfoCategoriesController {
    @Autowired
    InfoCategoriesMapper infoCategoriesMapper;

    @Operation(summary = "字典列表")
    @GetMapping("/list/{type}")
    private ResultResponse list(@PathVariable String type) {
        List<InfoCategories> list = infoCategoriesMapper.list(type);
        return ResultResponse.success(list);
    }

    @GetMapping("/test")
    @Operation(summary = "字典测试")
    @JwtIgnore
    public ResultResponse test(HttpServletRequest request, HttpServletResponse response){
        return ResultResponse.success(infoCategoriesMapper.selectList(null));
    }
}
