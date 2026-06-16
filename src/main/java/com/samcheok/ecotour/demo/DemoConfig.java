package com.samcheok.ecotour.demo;

import java.awt.Desktop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 운영용 {@link BrowserOpener} 빈. java.awt.Desktop으로 기본 브라우저를 연다.
 * 람다는 호출 시점에만 Desktop을 건드리므로(지연 평가) 헤드리스 환경에서도
 * 빈 생성 자체는 안전하다.
 */
@Configuration
public class DemoConfig {

	@Bean
	public BrowserOpener browserOpener() {
		return uri -> {
			if (Desktop.isDesktopSupported()
					&& Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(uri);
			} else {
				throw new UnsupportedOperationException("이 환경은 Desktop 브라우저 열기를 지원하지 않습니다");
			}
		};
	}
}
