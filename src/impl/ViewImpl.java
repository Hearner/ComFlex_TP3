package impl;

import interfaces.IEvent;
import interfaces.IUser;

public class ViewImpl extends View {
	private int etat;
	
	@Override
	protected IEvent make_event() {

		return new IEvent() {
			@Override
			public void notifier() {
				etat = requires().read().read();
			}
		};
	}

	@Override
	protected IUser make_user() {
		
		return new IUser() {
			
			@Override
			public void sendValue(int val) {
				requires().write().write(val);
			}
			
			@Override
			public void printValue() {
				System.out.println(etat);
			}
		};
	}

}
