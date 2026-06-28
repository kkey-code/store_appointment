package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.JwtClaimsConstant;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.EmployeeDTO;
import com.wkr.store_appointment.pojo.DTO.EmployeePageQueryDTO;
import com.wkr.store_appointment.pojo.entity.User;
import com.wkr.store_appointment.pojo.vo.EmployeeVO;
import com.wkr.store_appointment.pojo.vo.UserVO;
import com.wkr.store_appointment.properties.JwtProperties;
import com.wkr.store_appointment.service.EmpService;
import com.wkr.store_appointment.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@Api(tags = "员工相关接口")
public class EmpController {

    @Autowired
    private EmpService empService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     */
    @GetMapping("/login")
    @ApiOperation("登录")
    public Result<UserVO> login(@RequestParam String username, @RequestParam String password) {

        log.info("用户登录");
        User user = empService.login(username, password);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, user.getId());
        claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
        claims.put(JwtClaimsConstant.ROLE, user.getRole());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .token(token)
                .role(user.getRole())
                .build();
        return Result.success(userVO);
    }

    /**
     * 分页查询员工
     */
    @GetMapping("/employees")
    @ApiOperation("分页查询员工")
    public Result<PageResult> list(EmployeePageQueryDTO employeePageQuery) {

        log.info("分页查询员工，参数：{}", employeePageQuery);
        PageResult pageResult = empService.list(employeePageQuery);
        return Result.success(pageResult);
    }

    /**
     * 新增员工
     */
    @PostMapping("/employees")
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {

        log.info("新增员工，员工数据：{}", employeeDTO);
        empService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/employees/{id}")
    @ApiOperation("删除员工")
    public Result delete(@PathVariable Long id) {

        log.info("删除员工，员工id为：{}", id);
        empService.delete(id);
        return Result.success();
    }

    /**
     * 根据id查询员工
     */
    @GetMapping("/employees/{id}")
    @ApiOperation("根据id查询员工")
    public Result<EmployeeVO> getById(@PathVariable Long id) {

        log.info("根据id查询员工，员工id为：{}", id);

        EmployeeVO employeeVO = empService.getById(id);

        return Result.success(employeeVO);
    }

    /**
     * 修改员工
     */
    @PutMapping("/employees")
    @ApiOperation("修改员工")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {

        log.info("修改员工，员工数据：{}", employeeDTO);
        empService.update(employeeDTO);
        return Result.success();
    }

}
