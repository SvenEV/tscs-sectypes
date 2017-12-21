package de.upb.cs.swt.sectypes.qual;

import org.checkerframework.framework.qual.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The low-security qualifier
 */
@SubtypeOf(High.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@ImplicitFor(literals = LiteralKind.ALL)
@DefaultQualifierInHierarchy
public @interface Low {}
