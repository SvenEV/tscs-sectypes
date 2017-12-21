package de.upb.cs.swt.sectypes;

import com.sun.source.tree.*;
import de.upb.cs.swt.sectypes.qual.High;
import de.upb.cs.swt.sectypes.qual.Low;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

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
        if (isComparisonWithHigh(node.getCondition())) {
            checkBlockForAssignmentToLowVar((BlockTree)node.getThenStatement(), "then-branch");
            checkBlockForAssignmentToLowVar((BlockTree)node.getElseStatement(), "else-branch");
        }
        return super.visitIf(node, o);
    }

    @Override
    public Object visitWhileLoop(WhileLoopTree node, Object o) {
        if (isComparisonWithHigh(node.getCondition())) {
            checkBlockForAssignmentToLowVar((BlockTree)node.getStatement(), "while-loop");
        }
        return super.visitWhileLoop(node, o);
    }

    @Override
    public Object visitDoWhileLoop(DoWhileLoopTree node, Object o) {
        if (isComparisonWithHigh(node.getCondition())) {
            checkBlockForAssignmentToLowVar((BlockTree)node.getStatement(), "do-while-loop");
        }
        return super.visitDoWhileLoop(node, o);
    }

    @Override
    public Object visitForLoop(ForLoopTree node, Object o) {
        if (isComparisonWithHigh(node.getCondition())) { // TODO: this doesn't seem to work
            checkBlockForAssignmentToLowVar((BlockTree)node.getStatement(), "for-loop");
            node.getUpdate().forEach(s -> {
                if (isAssignmentToLowVar(s))
                    reportInterference("for-update", s);
            });
        }
        return super.visitForLoop(node, o);
    }

    private boolean isComparisonWithHigh(ExpressionTree tree) {
        boolean result = false;

        if (tree instanceof ParenthesizedTree) {
            ParenthesizedTree parens = (ParenthesizedTree) tree;
            if (parens.getExpression() instanceof BinaryTree) {
                BinaryTree comparison = (BinaryTree) parens.getExpression();

                AnnotatedTypeMirror leftType = getTypeFactory().getAnnotatedType(comparison.getLeftOperand());
                AnnotatedTypeMirror rightType = getTypeFactory().getAnnotatedType(comparison.getRightOperand());
                result = leftType.hasAnnotation(High.class) || rightType.hasAnnotation(High.class);
            }
        }
        return result;
    }

    private void checkBlockForAssignmentToLowVar(BlockTree block, String locationName) {
        block.getStatements().forEach(s -> {
            if (isAssignmentToLowVar(s))
                reportInterference(locationName, s);
        });
    }

    private boolean isAssignmentToLowVar(StatementTree statement) {
        if (statement instanceof ExpressionStatementTree) {
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree)statement;
            if (expressionStatement.getExpression() instanceof AssignmentTree) {
                AssignmentTree assignment = (AssignmentTree)expressionStatement.getExpression();
                AnnotatedTypeMirror varType = getTypeFactory().getAnnotatedType(assignment.getVariable());

                if (varType.hasAnnotation(Low.class))
                    return true;
            }
        }
        return false;
    }

    private void reportInterference(String locationName, Tree location) {
        checker.report(Result.failure(
                "Assignment to a low variable inside a " + locationName +
                " might leak sensitive data because the if-condition is high"), location);
    }
}