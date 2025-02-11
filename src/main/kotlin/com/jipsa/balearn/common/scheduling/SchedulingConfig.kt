package com.jipsa.balearn.common.scheduling

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling // ✅ 스케줄링 활성화
class SchedulingConfig