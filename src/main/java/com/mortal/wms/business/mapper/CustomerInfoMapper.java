package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerInfoMapper extends BaseMapper<CustomerInfo> {
    // 自定义查询：根据公司名称查询客户
    @Select("SELECT * FROM customer_info WHERE company_name = #{companyName} AND deleted_time IS NULL")
    CustomerInfo selectByCompanyName(@Param("companyName") String companyName);

    // 自定义查询：根据联系电话查询客户
    @Select("SELECT * FROM customer_info WHERE contact_phone = #{phone} AND deleted_time IS NULL")
    CustomerInfo selectByContactPhone(@Param("phone") String phone);

    @Select("SELECT * FROM customer_info WHERE fax = #{fax} AND deleted_time IS NULL")
    CustomerInfo selectByFax(@Param("fax") String fax);

    @Select("SELECT * FROM customer_info WHERE email = #{email} AND deleted_time IS NULL")
    CustomerInfo selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM customer_info WHERE office_phone = #{officePhone} AND deleted_time IS NULL")
    CustomerInfo selectByOfficePhone(@Param("officePhone") String officePhone);
}
