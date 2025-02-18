package com.mortal.wms.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//手写分页
@Data
public class PageResult {

    @Schema(name = "总条数")
    private int count;
    @Schema(name = "每页的条数")
    private int pageSize;

    // 页数（总共几页）
    private long totalPages;

    // 查询数据库里面对应的数据有多少条
    private long total;// 从数据库查处的总记录数

    // 每页查5条
    private int Pagetotal;


    //从哪条开始查
    private long current;

    //数据
    private List records;


    //返回map
    public Map ckptPageUtil(Integer page, Integer limit, List list) {
        Map myPageMap = new HashMap();
        try {
            // 手动分页
            int total = list.size(); //总数量
            int fromIndex = (Integer.valueOf(page) - 1) * Integer.valueOf(limit); //第几行数据开始
            int toIndex = fromIndex + Integer.valueOf(limit); //第几行结束
            if (toIndex > total) {
                toIndex = total;
            }
            if (fromIndex <= total) {
                List pageList = list.subList(fromIndex, toIndex);
                pageList.forEach(System.out::println);
                myPageMap.put("total", total);
                myPageMap.put("current", page);
                myPageMap.put("Pagetotal", limit);
                // 这个公式获取对应的page总数
                myPageMap.put("totalPages", (total - 1) / Integer.valueOf(limit) + 1);
                myPageMap.put("records", pageList);
            }
        } catch (Exception e) {


        }
        return myPageMap;
    }

    //返回对象
    public static PageResult ckptPageUtilList(Integer page, Integer limit, List list) {
        PageResult pageResult = new PageResult();
        try {
            // 手动分页
            int total = list.size(); //总数量
            int fromIndex = (Integer.valueOf(page) - 1) * Integer.valueOf(limit); //第几行数据开始
            int toIndex = fromIndex + Integer.valueOf(limit); //第几行结束
            if (toIndex > total) {
                toIndex = total;
            }
            if (fromIndex <= total) {
                List pageList = list.subList(fromIndex, toIndex);

                pageResult.setTotal(total);
                pageResult.setCurrent(page);
                pageResult.setPagetotal(limit);
                pageResult.setTotalPages((total - 1) / Integer.valueOf(limit) + 1);
                pageResult.setRecords(pageList);
            }
        } catch (Exception e) {

        }
        return pageResult;
    }

}
