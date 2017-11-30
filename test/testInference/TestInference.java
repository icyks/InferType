package testInference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import exception.UnboundVariableException;
import expression.App;
import expression.Expr;
import expression.LBool;
import expression.LInt;
import expression.Lam;
import expression.Let;
import expression.Var;

import org.junit.Assert;
import solver.Solve;
import solver.Unifier;
import susbstitution.Subst;
import type.Constraint;
import type.Infer;
import type.InferState;
import type.TArr;
import type.TCon;
import type.TVar;
import type.Type;
import type.TypeEnv;

public class TestInference {
	private TCon tBool = new TCon("Bool");
	private TCon tInt = new TCon("Int");
	
	/**
	 * Test LBool is of type Bool 
	 */
	@Test
	public void simpleInferBool() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());
		Expr boolExp = new LBool(true);
        Type t = boolExp.infer(infer);
        Unifier u = new Unifier(new Subst(),infer.constraints);
		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		Assert.assertEquals(finalType, tBool);
	}
	/**
	 * Test LInt is of type Int
	 */
	@Test
	public void simpleInferInt() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());
		Expr boolInt = new LInt(1);
        Type t = boolInt.infer(infer);
        Unifier u = new Unifier(new Subst(),infer.constraints);
		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		Assert.assertEquals(finalType, tInt);
	}
	
	/**
	 * Test Lambda (\x->x) 
	 */
	@Test
	public void lambdaInfer() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());
		Var x = new Var("x");
	    Lam lamExp = new Lam(x,x);
	    Type t = lamExp.infer(infer);
        Unifier u = new Unifier(new Subst(),infer.constraints);
		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		assertTrue(finalType instanceof TArr);
		assertEquals(((TArr)finalType).typeLeft, ((TArr)finalType).typeRight);
		assertTrue(((TArr)finalType).typeLeft instanceof TVar);
	}
	
	/**
	 * Test App (\x->true) 1
	 */
	@Test
	public void lambdaBoolInfer() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());
		Expr boolExp = new LBool(true);
	    Lam lamExp = new Lam(new Var("x"),boolExp);
	    App appExp = new App(lamExp, new LInt(1));
	    Type t = appExp.infer(infer);
        Unifier u = new Unifier(new Subst(),infer.constraints);
		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		Assert.assertEquals(finalType, tBool);
	}
	
	/**
	 * Test App (\x->x) 1
	 */
	@Test
	public void lambdaIntInfer() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());
		Var x = new Var("x");
	    Lam lamExp = new Lam(x,x);
	    App appExp = new App(lamExp, new LInt(1));
	    Type t = appExp.infer(infer);
        Unifier u = new Unifier(new Subst(),infer.constraints);
		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		Assert.assertEquals(finalType, tInt);
	}
	
	/**
	 * Test let f = (\x->x) in (\ab->b) (f true) (f 1)
	 */
	@Test
	public void letInfer() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());		
		Var x = new Var("x");
		Var a = new Var("a");
		Var b = new Var("b");
		Lam l = new Lam(x, x); //(\x->x)
		Lam l2 = new Lam(a,new Lam(b,b)); //(\ab->b)
		
		Var f = new Var("f");
		App a1 = new App(f, new LBool(true)); //(f true)
		App a2 = new App(f, new LInt(1)); // (f 1)
		
		App superApp = new App(new App(l2,a1),a2);
		
		Let let = new Let(f, l, superApp);

		Type t = let.infer(infer);

		Unifier u = new Unifier(new Subst(),infer.constraints);

		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
		assertEquals(finalType, tInt);
	}
	
	/**
	 * Test error (\f -> (\ab->b) (f true) (f 1))(\x->x)
	 */
	@Test(expected = UnboundVariableException.class)
	public void letErrorInfer() {
		Infer infer = new Infer(new TypeEnv(), new ArrayList<Constraint>(), new InferState());		
		Var x = new Var("x");
		Var a = new Var("a");
		Var b = new Var("b");
		Var f = new Var("f");
		Lam l = new Lam(x, x); //(\x->x)
		Lam l2 = new Lam(a,new Lam(b,b)); //(\ab->b)
		Lam l3 = new Lam(f,l2); //(\f->(\ab->b))
		App a1 = new App(f, new LBool(true)); //(f true)
		App a2 = new App(f, new LInt(1)); // (f 1)
		
		App app = new App(new App(l3,a1),a2);
		App superApp = new App(app,l);
		
		Type t = superApp.infer(infer);

		Unifier u = new Unifier(new Subst(),infer.constraints);

		Subst s = Solve.solver(u);
		Type finalType = Solve.runSolve(s,t);
	}
	
	
}
