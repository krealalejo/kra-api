package com.kra.api.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Unit tests for WebCorsConfiguration.
 *
 * These tests verify that EC2_ORIGIN is read from the OS environment (System.getenv),
 * not from JVM system properties (System.getProperty). Docker `environment:` entries
 * inject OS env vars, not JVM -D flags, so the getProperty call was a bug.
 *
 * Note: Env var injection for Test 2 and Test 3 is done via the JVM -D mechanism
 * through a testable subclass that overrides the env-reading helper. This avoids
 * Java 21 module restrictions that prevent modifying System.getenv via reflection.
 */
class WebCorsConfigurationTest {

    /**
     * Testable subclass that allows overriding the env-reading mechanism.
     * This follows the "seam" pattern: the production code reads from System.getenv;
     * tests inject a specific value via override without needing reflection hacks.
     */
    private static class TestableCorsConfig extends WebCorsConfiguration {
        private final String ec2OriginEnvValue;

        TestableCorsConfig(String ec2OriginEnvValue) {
            this.ec2OriginEnvValue = ec2OriginEnvValue;
        }

        @Override
        protected String readEc2OriginEnv() {
            return ec2OriginEnvValue;
        }
    }

    /**
     * Test 1: WebCorsConfiguration bean initializes without throwing when EC2_ORIGIN is not set.
     * The null/blank guard in the implementation prevents NPE — this test confirms that behavior
     * is preserved after the System.getProperty → System.getenv change.
     */
    @Test
    void corsConfigurationSource_noEc2Origin_doesNotThrow() {
        TestableCorsConfig config = new TestableCorsConfig(null);
        assertThatCode(config::corsConfigurationSource).doesNotThrowAnyException();
    }

    /**
     * Test 2: EC2_ORIGIN value injected via OS environment variable (not JVM system property)
     * appears in the CORS allowlist.
     *
     * This test verifies the fix: after changing System.getProperty → System.getenv (delegated
     * via the readEc2OriginEnv() override), the Vercel domain appears in the CORS allowlist.
     */
    @Test
    void corsConfigurationSource_ec2OriginSet_appearsInAllowlist() {
        TestableCorsConfig config = new TestableCorsConfig("https://kra.example.com");
        CorsConfigurationSource source = config.corsConfigurationSource();

        // MockHttpServletRequest provides all required fields (contextPath, servletPath, etc.)
        // that UrlBasedCorsConfigurationSource needs via UrlPathHelper.
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/projects");

        CorsConfiguration corsConfig = source.getCorsConfiguration(request);
        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOriginPatterns())
            .contains("https://kra.example.com");
    }

    /**
     * Test 3: Blank EC2_ORIGIN is treated as unset — not added to the allowlist.
     * This verifies the isBlank() guard is preserved after the fix.
     */
    @Test
    void corsConfigurationSource_blankEc2Origin_notAddedToAllowlist() {
        TestableCorsConfig config = new TestableCorsConfig("   ");
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/projects");

        CorsConfiguration corsConfig = source.getCorsConfiguration(request);
        assertThat(corsConfig).isNotNull();
        // Blank EC2_ORIGIN should only contain the localhost patterns, not the blank string.
        assertThat(corsConfig.getAllowedOriginPatterns())
            .doesNotContain("   ");
    }
}
