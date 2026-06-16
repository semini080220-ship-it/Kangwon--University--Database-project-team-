package com.samcheok.ecotour.demo;

import java.net.URI;

/**
 * 기본 브라우저로 URI를 여는 부작용을 추상화한 인터페이스.
 * 운영에서는 {@link DemoConfig}가 java.awt.Desktop 구현을 주입하고,
 * 테스트에서는 호출을 가로채는 람다를 주입한다.
 */
@FunctionalInterface
public interface BrowserOpener {
	void open(URI uri) throws Exception;
}
