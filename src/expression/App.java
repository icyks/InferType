package expression;

import inference.Infer;
import type.TArr;
import type.TVar;
import type.Type;

public class App extends Expr{
	private Expr e1,e2;

	public App(Expr e1,Expr e2) {
		this.e1=e1;
		this.e2=e2;
	}
	
	@Override
	public Type infer(Infer infer) {
		Type t1 = e1.infer(infer);
		Type t2 = e2.infer(infer);
		TVar tv = infer.inferState().fresh();
		infer.uni(t1, new TArr(t2, tv));
		return tv ;
	}
	
	@Override
	public String toString() {
		return "App(e1:"+e1+" e2:"+e2+")";
	}
}
