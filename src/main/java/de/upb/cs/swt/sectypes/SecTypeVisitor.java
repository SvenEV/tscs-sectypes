package de.upb.cs.swt.sectypes;

import com.sun.source.tree.*;
import com.sun.tools.javac.tree.JCTree;
import de.upb.cs.swt.sectypes.qual.High;
import de.upb.cs.swt.sectypes.qual.Low;
import jdk.nashorn.internal.ir.Assignment;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.javacutil.TypesUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.swing.tree.TreePath;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

        //System.out.println("Assignment Check - Value of type " + valueType + " assigned to variable of type " + varType + ": " + valueTree);

        if (varType.hasAnnotation(Low.class) && valueType.hasAnnotation(High.class)) {
                //valueTree.getKind().asInterface() != LiteralTree.class) {
            checker.report(Result.failure("Assignment of a high value to a low variable"), valueTree);
        }

        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }

    @Override
    public Object visitIf(IfTree node, Object o) {

        AnnotatedTypeMirror conditionType = getTypeFactory().getAnnotatedType(node.getCondition());

        if (true || conditionType.hasAnnotation(High.class)) {
            ((BlockTree)node.getThenStatement()).getStatements().forEach(s -> checkIfStatementIsAssignmentToLowVar(s, "then"));
            ((BlockTree)node.getElseStatement()).getStatements().forEach(s -> checkIfStatementIsAssignmentToLowVar(s, "else"));
        }

        return super.visitIf(node, o);
    }

    private void checkIfStatementIsAssignmentToLowVar(StatementTree statement, String branchName) {
        if (statement instanceof ExpressionStatementTree) {
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree)statement;
            if (expressionStatement.getExpression() instanceof AssignmentTree) {
                AssignmentTree assignment = (AssignmentTree)expressionStatement.getExpression();
                AnnotatedTypeMirror varType = getTypeFactory().getAnnotatedType(assignment.getVariable());

                if (varType.hasAnnotation(Low.class))
                    checker.report(Result.failure(
                            "Interference: Assignment to a low variable insight a " + branchName +
                            "-branch might leak sensitive data because the if-condition is high"), assignment);
            }
        }
    }
}