package type;

import inference.Subst;

import java.util.HashSet;

public class TCon implements Type{
	private String name;
	
	public TCon(String name) {
		this.name=name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TCon) {
			return this.name.equals(((TCon) obj).name);
		}
		return false ;
	}

	@Override
	public Type substitute(Subst s) {
		return this;
	}

	@Override
	public HashSet<TVar> ftv() {
		return new HashSet<>();
	}
}
