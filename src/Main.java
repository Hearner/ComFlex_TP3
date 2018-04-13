import impl.Root;
import impl.RootImpl;


public class Main {

	public static void main(String[] args) {
		Root.Component root = new RootImpl().newComponent();
		root.user().sendValue(14);
		root.user().sendValue(10);

		root.user().printValue();
	}

}
