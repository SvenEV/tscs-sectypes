package de.upb.cs.swt.sectypes.qual;

import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.Unqualified;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The low-security qualifier
 */
@SubtypeOf(Unqualified.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Low {}
