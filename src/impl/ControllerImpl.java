package impl;

import interfaces.IWrite;

public class ControllerImpl extends Controller{

	@Override
	protected IWrite make_write() {
		
		return new IWrite() {
			
			@Override
			public void write(int i) {
				requires().update().update(i);
				requires().event().notifier();
			}
		} ;
	}

}
