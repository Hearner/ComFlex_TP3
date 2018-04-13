package impl;

import interfaces.IRead;
import interfaces.IUpdate;

public class ModelImpl extends Model {
	private int val;
	
	public void setVal(int val) {
		this.val = val;
	}
	
	public int getVal() {
		return this.val;
	}
	
	@Override
	protected IUpdate make_update() {
		return new IUpdate() {
			
			@Override
			public void update(int i) {
				setVal(i);
			}
		};
	}

	@Override
	protected IRead make_read() {
		return new IRead() {
			
			@Override
			public int read() {
				return getVal();
			}
		};
	}

}
