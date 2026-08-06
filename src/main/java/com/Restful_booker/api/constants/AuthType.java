package com.Restful_booker.api.constants;

import com.Restful_booker.api.specs.SpecFactory;

/**
 * Authentication schemes supported by {@link SpecFactory}.
 * Extend here (e.g. BEARER, OAUTH2) when targeting a new API.
 */
public enum AuthType {
    NONE,
    TOKEN,
    BASIC
}
