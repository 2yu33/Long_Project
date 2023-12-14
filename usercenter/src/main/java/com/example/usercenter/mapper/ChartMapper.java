package com.example.usercenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.model.Chart;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.yupi.springbootinit.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {

    List<Chart> queryChartData(String querySql);
}




