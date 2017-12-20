package de.upb.cs.swt.sectypes;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.Tree;
import de.upb.cs.swt.sectypes.qual.Low;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.javacutil.TypesUtils;

/**
 * You can implement the specific rules for the type checker in this class
 */
public class SecTypeVisitor extends BaseTypeVisitor{
    public SecTypeVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    protected SecTypeVisitor(BaseTypeChecker checker, GenericAnnotatedTypeFactory typeFactory) {
        super(checker, typeFactory);
    }

    @Override
    protected void commonAssignmentCheck(AnnotatedTypeMirror varType, AnnotatedTypeMirror valueType, Tree valueTree, @CompilerMessageKey String errorKey) {
        System.out.println("Assignment Check - Value of type " + valueType + " assigned to variable of type " + varType + ".");
        if(varType.hasAnnotation(Low.class))
            System.out.println("Caution! Variable is of type low.");
        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }
}

