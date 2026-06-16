package com.samcheok.ecotour.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DemoBrowserLauncherTest {

	@Test
	void opensBrowserAtServerPort_whenEnabled() {
		List<URI> opened = new ArrayList<>();
		DemoBrowserLauncher launcher = new DemoBrowserLauncher(true, 8080, opened::add);

		launcher.openOnReady();

		assertThat(opened).containsExactly(URI.create("http://localhost:8080/"));
	}

	@Test
	void usesConfiguredPort_whenEnabled() {
		List<URI> opened = new ArrayList<>();
		DemoBrowserLauncher launcher = new DemoBrowserLauncher(true, 9000, opened::add);

		launcher.openOnReady();

		assertThat(opened).containsExactly(URI.create("http://localhost:9000/"));
	}

	@Test
	void doesNotOpen_whenDisabled() {
		List<URI> opened = new ArrayList<>();
		DemoBrowserLauncher launcher = new DemoBrowserLauncher(false, 8080, opened::add);

		launcher.openOnReady();

		assertThat(opened).isEmpty();
	}

	@Test
	void swallowsOpenerFailure_soStartupNeverBreaks() {
		DemoBrowserLauncher launcher = new DemoBrowserLauncher(true, 8080, uri -> {
			throw new RuntimeException("no display");
		});

		// must not throw — auto-open is a demo convenience, never fatal
		launcher.openOnReady();
	}
}
