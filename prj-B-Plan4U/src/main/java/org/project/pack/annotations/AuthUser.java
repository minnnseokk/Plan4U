package org.project.pack.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Retention(RUNTIME)
@Target(PARAMETER)
@AuthenticationPrincipal(
	expression = "#this == 'anonymousUser' ? null : user"
)
public @interface AuthUser {}
