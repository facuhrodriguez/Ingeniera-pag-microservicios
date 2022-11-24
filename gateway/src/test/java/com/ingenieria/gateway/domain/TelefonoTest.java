package com.ingenieria.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingenieria.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelefonoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Telefono.class);
        Telefono telefono1 = new Telefono();
        telefono1.setId(1L);
        Telefono telefono2 = new Telefono();
        telefono2.setId(telefono1.getId());
        assertThat(telefono1).isEqualTo(telefono2);
        telefono2.setId(2L);
        assertThat(telefono1).isNotEqualTo(telefono2);
        telefono1.setId(null);
        assertThat(telefono1).isNotEqualTo(telefono2);
    }
}
