package com.mortal.wms.business.controller;
import com.mortal.wms.business.dto.InfoCategoriesRequest;
import com.mortal.wms.business.entity.InfoCategories;
import com.mortal.wms.business.service.InfoCategoriesService;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "新增字典")
    @PostMapping("/add")
    private ResultResponse add(@RequestBody InfoCategories request) {
        return infoCategoriesService.add(request);
    }

}
