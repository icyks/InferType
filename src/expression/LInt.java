package expression;

import type.TCon;

public class LInt extends Lit{
	private Integer value;

	public LInt(int i) {
		this.value=i;
	}
	@Override
	public TCon getType() {
		return new TCon("Int");
	}
}
