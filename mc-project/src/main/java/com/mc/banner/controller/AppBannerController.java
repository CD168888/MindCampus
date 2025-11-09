package com.mc.banner.controller;

import com.mc.banner.domain.Banner;
import com.mc.banner.service.IBannerService;
import com.mc.common.annotation.Anonymous;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * APP端轮播图Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@Tag(name = "APP端轮播图")
@RestController
@RequestMapping("/app/banner")
@RequiredArgsConstructor
public class AppBannerController extends BaseController {

  private final IBannerService bannerService;

  /**
   * 获取轮播图列表（首页展示）
   * 只返回状态为正常且在有效期内的轮播图，按显示顺序排序
   */
  @Anonymous
  @Operation(summary = "获取轮播图列表")
  @GetMapping("/list")
  public R<List<Banner>> getBannerList() {
    // 查询状态为正常的轮播图
    Banner query = new Banner();
    query.setStatus("0"); // 0-正常
    List<Banner> list = bannerService.selectBannerList(query);

    // 过滤：只返回在有效期内的轮播图（当前时间在开始时间和结束时间之间）
    Date now = new Date();
    list = list.stream()
        .filter(banner -> {
          // 如果没有设置开始时间，则认为立即生效
          if (banner.getStartTime() != null && banner.getStartTime().after(now)) {
            return false;
          }
          // 如果没有设置结束时间，则认为永久有效
          if (banner.getEndTime() != null && banner.getEndTime().before(now)) {
            return false;
          }
          return true;
        })
        .collect(Collectors.toList());

    return R.ok(list);
  }
}
