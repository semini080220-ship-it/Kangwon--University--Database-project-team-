package com.samcheok.ecotour.demo;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 앱이 완전히 기동된 직후, 데모 모드일 때 기본 브라우저로 홈페이지를 자동으로 연다.
 *
 * <p>{@code demo.open-browser} 속성으로 게이트하며 기본값은 {@code false} 이므로
 * 팀의 일반 실행/테스트에는 영향이 없다. 배포된 .exe 데모만 이 값을 켠다.
 */
@Component
public class DemoBrowserLauncher {

	private final boolean enabled;
	private final int port;
	private final BrowserOpener opener;

	public DemoBrowserLauncher(
			@Value("${demo.open-browser:false}") boolean enabled,
			@Value("${server.port:8080}") int port,
			BrowserOpener opener) {
		this.enabled = enabled;
		this.port = port;
		this.opener = opener;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openOnReady() {
		if (!enabled) {
			return;
		}
		URI home = URI.create("http://localhost:" + port + "/");
		try {
			opener.open(home);
		} catch (Exception e) {
			// 브라우저 자동 열기는 데모 편의 기능일 뿐 — 절대 기동을 깨뜨리지 않는다.
			System.out.println("[demo] 브라우저 자동 열기 실패 — 직접 " + home + " 로 접속하세요. (" + e.getMessage() + ")");
		}
	}
}
