package com.nunegal.Matchly.validation;

import com.nunegal.Matchly.util.StringUtil;
import org.springframework.stereotype.Component;

@Component
public class InputValidator {
    private static String MESG_EXCEPTION_EMPTY = "El ID no puede estar vacío";

    /**
     * Valida que el ID no sea nulo ni vacío.
     */
    public void validateInputId(String id) {
        if (StringUtil.isNullOrEmpty(id)) {
            throw new IllegalArgumentException(MESG_EXCEPTION_EMPTY);
        }
    }
}
