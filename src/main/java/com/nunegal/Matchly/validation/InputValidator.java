package com.nunegal.Matchly.validation;

import com.nunegal.Matchly.util.StringUtil;
import org.springframework.stereotype.Component;

@Component
public class InputValidator {
    /**
     * Valida que el ID no sea nulo ni vacío.
     */
    public void validateInputId(String id) {
        if (StringUtil.isNullOrEmpty(id)) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
    }
}
