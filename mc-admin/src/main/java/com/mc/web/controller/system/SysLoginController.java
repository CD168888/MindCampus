package com.mc.web.controller.system;

import com.mc.common.constant.Constants;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.domain.entity.SysMenu;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.core.domain.model.LoginBody;
import com.mc.common.core.domain.model.LoginUser;
import com.mc.common.core.text.Convert;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.common.utils.StringUtils;
import com.mc.framework.web.service.SysLoginService;
import com.mc.framework.web.service.SysPermissionService;
import com.mc.framework.web.service.TokenService;
import com.mc.system.service.ISysConfigService;
import com.mc.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 登录验证
 *
 * @author caidu
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
//    @PostMapping("/login")
//    public AjaxResult login(@RequestBody LoginBody loginBody) {
//        AjaxResult ajax = AjaxResult.success(); // 生成令牌
//        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
//        ajax.put(Constants.TOKEN, token); return ajax;
//    }

    /**
     * 管理员/辅导员登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();

        // 允许 00 和 02 登录 00 代表管理员  02 代表辅导员
        Set<String> allowTypes = new HashSet<>(Arrays.asList("00", "02"));

        String token = loginService.loginWithUserType(
                loginBody.getUsername(),
                loginBody.getPassword(),
                loginBody.getCode(),
                loginBody.getUuid(),
                allowTypes
        );

        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 学生登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/student/login")
    public AjaxResult studentLogin(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();

        Set<String> allowTypes = new HashSet<>(Arrays.asList("01"));
        String token = loginService.loginWithUserType(
                loginBody.getUsername(),
                loginBody.getPassword(),
                loginBody.getCode(),
                loginBody.getUuid(),
                allowTypes // 学生用户
        );

        ajax.put(Constants.TOKEN, token);
        return ajax;
    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
