package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.JwtIgnore;
import com.mortal.wms.business.dto.InfoCategoriesRequest;
import com.mortal.wms.business.entity.InfoCategories;
import com.mortal.wms.business.mapper.InfoCategoriesMapper;
import com.mortal.wms.business.service.InfoCategoriesService;
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
    InfoCategoriesService infoCategoriesService;

    @Operation(summary = "字典列表")
    @GetMapping("/list")
    private ResultResponse list(InfoCategoriesRequest request) {
        return infoCategoriesService.list(request);
    }


}
