package impl;

public class RootImpl extends Root {

	@Override
	protected Model make_model() {
		return new ModelImpl();
	}

	@Override
	protected View make_view() {
		return new ViewImpl();
	}

	@Override
	protected Controller make_controller() {
		return new ControllerImpl();
	}

}
